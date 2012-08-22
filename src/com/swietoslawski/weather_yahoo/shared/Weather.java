package com.swietoslawski.weather_yahoo.shared;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class Weather {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, String> forecastInformation;
	private HashMap<String, String> currentConditions;
	private ArrayList<HashMap<String, String>> forecastConditions;
	
	public Weather(Document doc) {
		Node node = null;
		String key = null;
		
		// Fill in forecast information
		forecastInformation = new HashMap<String, String>();
		NodeList forecast_information = doc.getElementsByTagName("forecast_information").item(0).getChildNodes();
		for (int i = 0; i < forecast_information.getLength(); i++) {
			node = forecast_information.item(i);
			key = node.getNodeName();
			
			if (key == "city" || key == "postal_code" || key == "forecast_date") {
				forecastInformation.put(key, getData(node));
			} 
		}
		
		// Fill in current conditions
		currentConditions = new HashMap<String, String>();
		NodeList current_conditions = doc.getElementsByTagName("current_conditions").item(0).getChildNodes();
		for (int i = 0; i < current_conditions.getLength(); i++) {
			node = current_conditions.item(i);
			key = node.getNodeName();
			
			if (key == "condition" || key == "temp_f" || key == "temp_c" || key == "humidity" || key == "icon" || key == "wind_condition") {
				currentConditions.put(key, getData(node));
			} 
		}
		
		// Fill in forecast conditions
		forecastConditions = new ArrayList<HashMap<String,String>>(4);
		NodeList forecast_conditions = doc.getElementsByTagName("forecast_conditions");
		for (int i = 0; i < forecast_conditions.getLength(); i++) {
			node = forecast_conditions.item(i);
			NodeList conditions = node.getChildNodes();
			Node condition = null;
			
			HashMap<String, String> forecast = new HashMap<String, String>();
			for (int j = 0; j < conditions.getLength(); j++) {
				condition = conditions.item(j);
				key = condition.getNodeName();
				
				if (key == "day_of_week" || key == "low" || key == "high" || key == "icon" || key == "conditon") {
					forecast.put(key, getData(condition));
				} 
			}
			forecastConditions.add(forecast);
		}
	}
	
	private String getData(Node node) {
		
		// Could be a one liner...just keep it verbose as it makes it easy to debug
		String data = node.getAttributes().getNamedItem("data").getNodeValue();
		
		return data;
	}
	
	/*
	 * ACCESORS for current weather conditions
	 */
	public String getIconCondition() {
		return forecastInformation.get("icon");
	}
	
	public String getCity() {
		return forecastInformation.get("city");
	}
	
	public String getDay() {
		// We return today's week day. The same as API is returning for 
		// other day forecasts
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("E");
		String weekday = dateFormat.format(date);
		
		return weekday;
	}
	
	// We prolly even get rif of this as forecast does not
	// provide temperatures in Celcius
	public String getTempF() {
		return currentConditions.get("temp_f");
	}
	
	public String getTempC() {
		return currentConditions.get("temp_c");
	}
	
	public String getHumid() {
		return currentConditions.get("humidity");
	}

	public String getWind() {
		return forecastInformation.get("wind_condition");
	}
	
	public String getDate() {
		return forecastInformation.get("forecast_date");
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
	
	public String getTemp(int day) {
		// We forecast only 4 days ahead starting from 0
		if (day < 0) day = 0;
		if (day > 3) day = 3;
		
		// Just return average temp
		int high = Integer.parseInt(forecastConditions.get(day).get("high"));
		int low = Integer.parseInt(forecastConditions.get(day).get("low"));
		
		return String.valueOf((high + low) / 2);
	}
	
	public String getIconCondition(int day) {
		// We forecast only 4 days ahead starting from 0
		if (day < 0) day = 0;
		if (day > 3) day = 3;
				
		return forecastConditions.get(day).get("icon");
	}
}
