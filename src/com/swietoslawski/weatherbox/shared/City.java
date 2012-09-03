package com.swietoslawski.weatherbox.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is a POJO object for mapping XML nodes for matching cities returned by web service
 * @author peter.swietoslawski
 *
 */
public class City implements IsSerializable {
	
	private String city;
	private String state;
	private String country;
	
	
	public City() {}
	
	public City(String city, String state, String country) {
		this.city = city;
		this.state = state;
		this.country = country;
	}

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	@Override
	public String toString() {
		return city + ", " + state + " " + country;
	}
	
	public String toURL() {
		String country_str = country.replace(" ", "%20");
		String state_str = (!state.equals("")) ? "/" + state : "";
		String city_str = "/" + city.replace(" ", "%20");
		
		return country_str + state_str + city_str;
	}
}
