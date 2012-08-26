package com.swietoslawski.weather.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.swietoslawski.weather.shared.JSO.Weather;

public interface WeatherServiceAsync {

	void getWeather(String city,
			AsyncCallback<Weather> callback);

}
