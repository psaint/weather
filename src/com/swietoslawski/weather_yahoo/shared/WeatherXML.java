package com.swietoslawski.weather_yahoo.shared;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//Google weather API respond with XML:
/*
 * <xml_api_reply version="1">
		<weather module_id="0" tab_id="0" mobile_row="0" mobile_zipped="1" row="0" section="0">
			<forecast_information>
				<city data="Miami, FL"/>
				<postal_code data="Miami"/>
				<latitude_e6 data=""/>
				<longitude_e6 data=""/>
				<forecast_date data="2012-08-21"/>
				<current_date_time data="2012-08-21 23:53:00 +0000"/>
				<unit_system data="US"/>
			</forecast_information>
			
			<current_conditions>
				<condition data="Mostly Cloudy"/>
				<temp_f data="86"/>
				<temp_c data="30"/>
				<humidity data="Humidity: 67%"/>
				<icon data="/ig/images/weather/mostly_cloudy.gif"/>
				<wind_condition data="Wind: S at 6 mph"/>
			</current_conditions>
			
			<!-- Repeated for four days -->
			<forecast_conditions>
				<day_of_week data="Tue"/>
				<low data="79"/>
				<high data="93"/>
				<icon data="/ig/images/weather/thunderstorm.gif"/>
				<condition data="Thunderstorm"/>
			</forecast_conditions>
			...
		</weather>
	</xml_api_reply>	
 */
public class WeatherXML {
	
	private HashMap<String, String> forecastInformation;
	private HashMap<String, String> currentConditions;
	private ArrayList<HashMap<String, String>> forecastConditions;
	
	public WeatherXML(Document weatherDOM) {
		String key;
		Node node;
		Node condition;
		NodeList forecast_information;
		NodeList current_conditions;
		NodeList forecast_conditions;
		NodeList conditions;
		
		// Fill in forecast information
		forecastInformation = new HashMap<String, String>();
		forecast_information = weatherDOM.getElementsByTagName("forecast_information").item(0).getChildNodes();
		for (int i = 0; i < forecast_information.getLength(); i++) {
			node = forecast_information.item(i);
			key = node.getNodeName();
			
			if (key == "city" || key == "postal_code" || key == "forecast_date") {
				forecastInformation.put(key, getData(node));
			} 
		}
		
		// Fill in current conditions
		currentConditions = new HashMap<String, String>();
		current_conditions = weatherDOM.getElementsByTagName("current_conditions").item(0).getChildNodes();
		for (int i = 0; i < current_conditions.getLength(); i++) {
			node = current_conditions.item(i);
			key = node.getNodeName();
			
			if (key == "condition" || key == "temp_f" || key == "temp_c" || key == "humidity" || key == "icon" || key == "wind_condition") {
				currentConditions.put(key, getData(node));
			} 
		}
		
		// Fill in forecast conditions
		forecastConditions = new ArrayList<HashMap<String,String>>(4);
		forecast_conditions = weatherDOM.getElementsByTagName("forecast_conditions");
		for (int i = 0; i < forecast_conditions.getLength(); i++) {
			node = forecast_conditions.item(i);
			conditions = node.getChildNodes();
			condition = null;
			
			HashMap<String, String> forecast = new HashMap<String, String>();
			for (int j = 0; j < conditions.getLength(); j++) {
				condition = conditions.item(j);
				key = condition.getNodeName();
				
				if (key == "day_of_week" || key == "low" || key == "high" || key == "icon" || key == "condition") {
					forecast.put(key, getData(condition));
				} 
			}
			forecastConditions.add(forecast);
		}
	}
	
	/*
	 * HELPER METHODS
	 */
	private String getData(Node node) {
		// TODO Add a simple check for attribute "data" existence
		return node.getAttributes().getNamedItem("data").getNodeValue();
	}
	
	
	/*
	 * ACCESORS for weather forecast info
	 */
	public String getCity() {
		return forecastInformation.get("city");
	}
	
	public String getDate() {
		return forecastInformation.get("forecast_date");
	}
	
	/*
	 * ACCESORS for current weather conditions
	 */
	public String getIcon() {
		return currentConditions.get("icon");
	}
	
	public String getCondition() {
		return currentConditions.get("condition");
	}
	
	// We prolly even get rid of this as forecast does not
	// provide temperatures in Celcius
	public String getTempF() {
		return currentConditions.get("temp_f");
	}
	
	public String getTempC() {
		return currentConditions.get("temp_c");
	}
	
	public String getHumidity() {
		return currentConditions.get("humidity");
	}

	public String getWind() {
		return currentConditions.get("wind_condition");
	}
	
	
	/*
	 * ACCESORS for forecast conditions
	 */
	public String getDay(int day) {
		// We forecast only 4 days ahead starting from 0
		if (day < 0) day = 0;
		if (day > 3) day = 3;
		
		return forecastConditions.get(day).get("day_of_week");
	}
	
	public String getHight(int day) {
		// We forecast only 4 days ahead starting from 0
		if (day < 0) day = 0;
		if (day > 3) day = 3;
		
		return forecastConditions.get(day).get("high");
	}
	
	public String getLow(int day) {
		// We forecast only 4 days ahead starting from 0
		if (day < 0) day = 0;
		if (day > 3) day = 3;
		
		return forecastConditions.get(day).get("low");
	}
	
	public String getIcon(int day) {
		// We forecast only 4 days ahead starting from 0
		if (day < 0) day = 0;
		if (day > 3) day = 3;
				
		return forecastConditions.get(day).get("icon");
	}
	
	public String getCondition(int day) {
		// We forecast only 4 days ahead starting from 0
		if (day < 0) day = 0;
		if (day > 3) day = 3;
		
		return forecastConditions.get(day).get("condition");
	}

	public Weather asWeather() {
		// TODO assemble Weather objec of: ForecastInformation,
		//      Current conditions and array of Forecasts.
		Weather weather;
		ForecastInformation forecastInformation = new ForecastInformation(getCity(), getDate());
		CurrentConditions currentConditions = new CurrentConditions(getCondition(), Byte.parseByte(getTempF()), Byte.parseByte(getTempC()), getHumidity(), getIcon(), getWind());
		Forecast[] forecasts = new Forecast[4];
		for (byte i = 0; i < forecasts.length; i++) {
			forecasts[i] = new Forecast(getDay(i), getLow(i), getHight(i), getIcon(i), getCondition(i));
		}
		
		weather = new Weather(forecastInformation, currentConditions, forecasts);
		
		return weather;
	}

}
