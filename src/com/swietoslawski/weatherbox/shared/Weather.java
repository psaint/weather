package com.swietoslawski.weatherbox.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is a POJO object for mapping XML nodes for weather cast returned by web service
 * @author peter.swietoslawski
 *
 */
public class Weather implements IsSerializable {
	
	private String weekday;
	private String time;
	private String temp_h;
	private String temp_l;
	private String condition;
	private String icon;
	private String humidity;
	private String wind;
	
	
	public Weather() {}
	
	public Weather(String weekday, String time, String temp_h,
			String temp_l, String condition, String icon_url,
			String avehumidity, String wind) {

		this.weekday = weekday;
		this.time = time;
		this.temp_h = temp_h;
		this.temp_l = temp_l;
		this.condition = condition;
		this.icon = icon_url;
		this.humidity = avehumidity;
		this.wind = wind;
	}
	
	public String getWeekday() {
		return weekday;
	}
	public String getTime() {
		return time;
	}
	public String getTemp_h() {
		return temp_h;
	}
	public String getTemp_l() {
		return temp_l;
	}
	public String getCondition() {
		return condition;
	}
	public String getIcon_url() {
		return icon;
	}
	public String getAvehumidity() {
		return humidity;
	}
	public String getWind() {
		return wind;
	}
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		
		// Normally would use reflection with Field this.getClass().getDeclaredFields()
		// but it's not available on client side 
		final String EOL = "\n";
		
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
