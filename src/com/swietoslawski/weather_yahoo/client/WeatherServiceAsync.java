package com.swietoslawski.weather_yahoo.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface WeatherServiceAsync {

	void getWeatherHtml(String zip, boolean isCelcius, AsyncCallback<String> callback);

}
