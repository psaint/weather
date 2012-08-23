package com.swietoslawski.weather_yahoo.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.swietoslawski.weather_yahoo.shared.Weather;

public interface WeatherServiceAsync {

	void getWeather(String city,
			AsyncCallback<Weather> callback);

}
