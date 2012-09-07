package com.swietoslawski.weatherbox.client.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.swietoslawski.weatherbox.client.WeatherController;
import com.swietoslawski.weatherbox.shared.City;
import com.swietoslawski.weatherbox.shared.Weather;

public class WeatherCastView extends Composite {

	private static final Binder binder = GWT.create(Binder.class);
	private final WeatherController weather_controller;
	
	@UiField Label city;
	@UiField Image icon;
	@UiField Label temp_h;
	@UiField Label temp_l;
	@UiField InlineLabel humidity;
	@UiField VerticalPanel weather_casts;

	interface Binder extends UiBinder<Widget, WeatherCastView> {
	}

	public WeatherCastView(WeatherController weather_controller) {
		initWidget(binder.createAndBindUi(this));
		
		this.weather_controller = weather_controller;
		
		render();
	}
	
	private void render() {
		if (weather_controller.getWeatherField().size() > 0) {
			
			// Weather for today
			Weather weather = weather_controller.getWeatherField().get(0);
			
			City city_name = new City(weather.getCity());
			
			city.setText(city_name.getCity() + " " + city_name.getState());
			temp_h.setText("Hi " + weather.getTemp_h() + "\u00B0 F");
			temp_l.setText("Lo " + weather.getTemp_l() + "\u00B0 F");
			icon.setUrl(weather.getIcon());
			humidity.setText(weather.getHumidity() + "%");
			
			// Add weathercasts
			for (int i = 1; i < weather_controller.getWeatherField().size(); i++) {
				Weather weather_cast = weather_controller.getWeatherField().get(i);
				
				WeatherCastRowView row = new WeatherCastRowView(weather_cast);
				weather_casts.add(row);
			}
		}
	}

}
