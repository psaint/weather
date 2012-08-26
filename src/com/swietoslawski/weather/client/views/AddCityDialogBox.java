package com.swietoslawski.weather.client.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.swietoslawski.weather.client.WeatherMainPresenter;
import com.swietoslawski.weather.client.WeatherServiceAsync;
import com.swietoslawski.weather.shared.JSO.Weather;

public class AddCityDialogBox extends DialogBox {

	private static final Binder binder = GWT.create(Binder.class);

	interface Binder extends UiBinder<Widget, AddCityDialogBox> {
	}
	
	private WeatherMainPresenter controller;
	
	protected Weather weatherCast;
	
	@UiField TextBox city;
	@UiField Button search;
	@UiField HTML message;
	@UiField Button add;
	@UiField Button cancel;
	
	
	public AddCityDialogBox(WeatherMainPresenter controller) {
		
		this.controller = controller;
		
		// Initialize widget and it's bindings
		setWidget(binder.createAndBindUi(this));
		
		// Size and position
		int width = 300;
		int height = 500;
		
		setWidth(width + "pt");
        setHeight(height + "pt");
		
        int left = (Window.getClientWidth() - width) / 3;
        int top = (Window.getClientHeight() - height) / 3;
        setPopupPosition(left, top);
        
        // Set the dialog box's caption.
        setText("Search for weathercast");

        // Enable animation.
        setAnimationEnabled(true);

        // Enable glass background.
        //setGlassEnabled(true);

        // We'll re-attach Add button only if we were able to find a 
        // weather cast for the city user typed in
        message.setVisible(false);
        add.setVisible(false);
	}
	
	@UiHandler("city")
	void onClickCity(ClickEvent event) {
		city.setText("");
	}
	
	@UiHandler("city")
    void onPressEnter(KeyPressEvent event) {
		if (event.getCharCode() == KeyCodes.KEY_ENTER)
		searchHandler();
    }
	
	@UiHandler("search")
	void onClickSearch(ClickEvent event) {
		searchHandler();
	}
	
	@UiHandler("cancel")
    void onClickCancel(ClickEvent event) {
    	hide();
    }
	
	@UiHandler("add")
    void onClickAdd(ClickEvent event) {
		// Let controller update it's list of cities
		if (city.getValue() != "") {
			controller.addWeatherCast(weatherCast);
		}
		
    	hide();
    }
	
	private void searchHandler() {
		// Setup callback
		AsyncCallback<Weather> callback = new AsyncCallback<Weather>() {

			@Override
			public void onFailure(Throwable caught) {
				
				// Delete saved weather cast
				weatherCast = null;
				
				message.setHTML("Error: " + caught.getMessage());
				message.setVisible(true);
			}

			@Override
			public void onSuccess(Weather weather) {
				
				// Save weather cast
				weatherCast = weather;
				
				message.setHTML(weather.getForecastInformation().getCity());
				message.setVisible(true);
				add.setVisible(true);
			}
		};
		
		
		// Call weather service
		WeatherServiceAsync ws = controller.getWeatherService();
		String c = city.getText();
		ws.getWeather(c, callback);	
	}
}
