package com.swietoslawski.weatherbox.client.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Image;
import com.swietoslawski.weatherbox.shared.Weather;

public class WeatherCastRowView extends Composite {

	private static final Binder binder = GWT.create(Binder.class);
	@UiField Label temp_l;
	@UiField Label temp_h;
	@UiField Label weekday;
	@UiField Image icon;

	interface Binder extends UiBinder<Widget, WeatherCastRowView> {
	}

	public WeatherCastRowView(Weather weather) {
		initWidget(binder.createAndBindUi(this));
		
		icon.setUrl(weather.getIcon());
		icon.setHeight("48px");
		weekday.setText(weather.getWeekday());
		temp_h.setText(weather.getTemp_h() + "\u00B0");
		temp_l.setText(weather.getTemp_l() + "\u00B0");
	}

}
