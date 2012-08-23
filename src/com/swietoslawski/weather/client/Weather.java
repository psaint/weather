package com.swietoslawski.weather.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.swietoslawski.weather.shared.CurrentConditions;
import com.swietoslawski.weather.shared.Forecast;
import com.swietoslawski.weather.shared.ForecastInformation;
import com.swietoslawski.weather.shared.WeatherWrapper;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Weather implements EntryPoint {
	private TextBox txBox;
	private Button btnSubmit;
	HTML weatherHtml;
	
	// TODO This is duplicated in WeatherServiceImpl class. We need to solve this
	//      kludge. 
	private final String weatherProviderURL = "http://www.google.com";
	
	// Proxy of weather service class
	private WeatherServiceAsync weatherService = GWT.create(WeatherService.class);
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		HorizontalPanel inputPanel = new HorizontalPanel();
		
		// Align child widgets along middle of panel
		inputPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		Label lbl = new Label("City: ");
		inputPanel.add(lbl);
		
		txBox = new TextBox();
		txBox.setVisibleLength(20);
		inputPanel.add(txBox);
		
				
		// Submit button
		btnSubmit = new Button("Submit", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				validateAndSubmit();
			}
		});
		
		txBox.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				// Check for Enter key
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					validateAndSubmit();
				}
			}
		});
		
		// Add button to inputs, aligned to bottom
		inputPanel.add(btnSubmit);
		inputPanel.setCellVerticalAlignment(btnSubmit, HasVerticalAlignment.ALIGN_BOTTOM);
		
		
		RootPanel.get("input-container").add(inputPanel);
	}

	protected void validateAndSubmit() {
		String city = txBox.getText().trim();
		
		if (!ZipValidator.isValid(city)) {
			Window.alert("Zip-code must have 5 digits");
			return;
		}
		
		// Disable the text box
		txBox.setEnabled(false);
		
		// Get choice of temperature
		fetchWeatherHtml(city);
	}

	private void fetchWeatherHtml(String city) {
		
		
		// Setup callback
		AsyncCallback<WeatherWrapper> callback = new AsyncCallback<WeatherWrapper>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error: " + caught.getMessage());
				txBox.setEnabled(true);
			}

			@Override
			public void onSuccess(WeatherWrapper weather) {
				
				// Extract data from weather cast
				ForecastInformation forecastInformation = weather.getForecastInformation();
				CurrentConditions currentConditions = weather.getCurrentConditions();
				Forecast[] forecasts = weather.getForecastConditions();
				
				// Update forecast
				updateForecast(forecastInformation, currentConditions, forecasts);
			}
		};
		
		// Call remote service and define callback behavior
		weatherService.getWeather(city, callback);
		
	}

	protected void updateForecast(ForecastInformation forecastInformation,
			CurrentConditions currentConditions, Forecast[] forecasts) {
		
		// Remove current forecast
		RootPanel.get("forecast").clear();
		
		// Render City
		updateCity(forecastInformation.getCity());
		
		// Render current condition icon and description
		updateCondition(currentConditions.getIcon(), currentConditions.getCondition());
		
		// Render Current temperature in Fahrenheit and Celsius
		updateTemperature(currentConditions.getTemp_f(), currentConditions.getTemp_c());
		
		// Render forecast for four days
		// Each forecast in one row with:
		//   - condition icon
		//   - Week day name
		//   - High and low temperature in Fahrenheit 
		updateForecast(forecasts);
		
		// Reenable textbox
		txBox.setEnabled(true);
		
	}

	private void updateForecast(Forecast[] forecasts) {
		
		
		// Forecast for following days will be rendered in rows
		VerticalPanel vPanel = new VerticalPanel();
		
		for (Forecast forecast : forecasts) {
			Image img = new Image(weatherProviderURL + forecast.getIcon());
			Label wday = new Label(forecast.getDay_of_week());
			Label high = new Label(forecast.getHigh());
			Label low = new Label(forecast.getLow());
			
			HorizontalPanel hPanel = new HorizontalPanel();
			hPanel.add(img);
			hPanel.add(wday);
			hPanel.add(high);
			hPanel.add(low);
			
			vPanel.add(hPanel);	
		}
		
		RootPanel.get("forecast").add(vPanel);
	}

	private void updateTemperature(String temp_f, String temp_c) {
		RootPanel.get("f").add(new Label(temp_f));
		RootPanel.get("c").add(new Label(temp_c));
	}

	private void updateCondition(String icon, String condition) {
		Image image = new Image(weatherProviderURL + icon);
		RootPanel.get("icon").add(image);	
	}

	private void updateCity(String city) {
		RootPanel.get("city").add(new Label(city));
	}

}
