package com.swietoslawski.weatherbox.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is a POJO object for mapping XML nodes for weather cast returned by web service
 * @author peter.swietoslawski
 *
 */
public class Weather implements IsSerializable {
	
	private String city;
	private String weekday;
	private String time;
	private String temp_h;
	private String temp_l;
	private String condition;
	private String icon;
	private String humidity;
	private String wind;
	
	
	public Weather() {}
	
	public Weather(String city, String weekday, String time, String temp_h,
			String temp_l, String condition, String icon_url,
			String avehumidity, String wind) {

		this.city = city;
		this.weekday = weekday;
		this.time = time;
		this.temp_h = temp_h;
		this.temp_l = temp_l;
		this.condition = condition;
		this.icon = icon_url;
		this.humidity = avehumidity;
		this.wind = wind;
	}
	


	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getWeekday() {
		return weekday;
	}

	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTemp_h() {
		return temp_h;
	}

	public void setTemp_h(String temp_h) {
		this.temp_h = temp_h;
	}

	public String getTemp_l() {
		return temp_l;
	}

	public void setTemp_l(String temp_l) {
		this.temp_l = temp_l;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public String getWind() {
		return wind;
	}

	public void setWind(String wind) {
		this.wind = wind;
	}

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		
		// Normally would use reflection with Field this.getClass().getDeclaredFields()
		// but it's not available on client side 
		final String EOL = "\n";
	
		output.append("city: " + city.toString() + EOL);
		output.append("weekday: " + weekday + EOL);
		output.append("time: " + time + EOL);
		output.append("high: " + temp_h + EOL);
		output.append("low: " + temp_l + EOL);
		output.append("condition: " + condition + EOL);
		output.append("icon: " + icon + EOL);
		output.append("humidity: " + humidity + EOL);
		output.append("wind: " + wind + EOL);
		
		return output.toString();
	}
	
}
