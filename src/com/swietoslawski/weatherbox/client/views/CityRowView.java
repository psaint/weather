package com.swietoslawski.weatherbox.client.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.swietoslawski.weatherbox.client.WeatherController;

public class CityRowView extends Composite {

	private static final Binder binder = GWT.create(Binder.class);
	private final WeatherController weather_controller;
	private final ListView parent;
	
	@UiField Label city;
	@UiField Button delete;

	interface Binder extends UiBinder<Widget, CityRowView> {
	}

	public CityRowView(ListView parent, WeatherController weather_controller) {
		initWidget(binder.createAndBindUi(this));
		
		this.weather_controller = weather_controller;
		this.parent = parent;
	}

	
	
	@UiHandler("delete")
	public void onClickDelete(ClickEvent event) {
		Button btn = (Button) event.getSource();
		String row_nr  = btn.getTitle();
		weather_controller.removeCity(Integer.parseInt(row_nr));
		
		// Re-render page
		// IMPORTANT: This is a must to make sure that row numbers (indexes)
		//            of tracked elements will be re-calculated again.
		parent.render();
	}
}
