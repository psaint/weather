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

	public City(String path) {
		
		path = decodeSpaces(path);
		
		String[] url = path.split("/");
		
		// We only have country and city
		if (url.length == 2) {
			country = url[0];
			state = "";
			city = url[1];
		}
		// Adding state for US
		else if (url.length == 3) {
			country = url[0];
			state = url[1];
			city = url[2];
		}
		else {
			// This shoud throw exception as something is wrong with path
		}
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
		String country_str = (!state.equals("")) ? country : "";
		String state_str = (!state.equals("")) ? "/" + state : "";
		String city_str = "/" + city.replace(" ", "%20");
		
		return encodeSpaces(country_str + state_str + city_str);
	}
	
	private String encodeSpaces(String string) {
		return string.replace(" ", "%20");
	}
	
	private String decodeSpaces(String string) {
		return string.replace("%20", " ");
	}
}
