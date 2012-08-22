package com.swietoslawski.weather_yahoo.client;

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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.swietoslawski.weather_yahoo.shared.WeatherWrapper;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Weather_Yahoo implements EntryPoint {
	private TextBox txBox;
	private Button btnSubmit;
	private RadioButton ucRadio;
	private RadioButton ufRadio;
	HTML weatherHtml;
	
	// Proxy of weather service class
	private WeatherServiceAsync weatherService = GWT.create(WeatherService.class);
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		HorizontalPanel inputPanel = new HorizontalPanel();
		
		// Align child widgets along middle of panel
		inputPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		Label lbl = new Label("5-digit zipcode: ");
		inputPanel.add(lbl);
		
		txBox = new TextBox();
		txBox.setVisibleLength(20);
		inputPanel.add(txBox);
		
		// Radio button group to select units in C or F
		Panel radioPanel = new VerticalPanel();
		
		ucRadio = new RadioButton("units", "Celcius");
		ufRadio = new RadioButton("units", "Fahrenheit");
		
		// Default to Celcius
		ucRadio.setValue(true);
		
		inputPanel.add(ucRadio);
		inputPanel.add(ufRadio);
		
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
		
		// Create widget for HTML output
		weatherHtml = new HTML();
		
		RootPanel.get("output-container").add(weatherHtml);
		
	}

	protected void validateAndSubmit() {
		String zip = txBox.getText().trim();
		
		if (!ZipValidator.isValid(zip)) {
			Window.alert("Zip-code must have 5 digits");
			return;
		}
		
		// Disable the text box
		txBox.setEnabled(false);
		
		// Get choice of temperature
		boolean celcius = ucRadio.getValue();
		fetchWeatherHtml(zip, celcius);
	}

	private void fetchWeatherHtml(String zip, boolean isCelcius) {
		// Hide existing weather report
		hideHtml();
		
		// Setup callback
		AsyncCallback<WeatherWrapper[]> callback = new AsyncCallback<WeatherWrapper[]>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error: " + caught.getMessage());
				txBox.setEnabled(true);
			}

			@Override
			public void onSuccess(WeatherWrapper[] result) {
				
				StringBuilder html = new StringBuilder();
				
				for (WeatherWrapper w : result) {
					html.append(w.getCity() + " ");
				}
				
				// Show new weather report
				displayHtml(html.toString());
			}
		};
		
		// Call remote service and define callback behavior
		weatherService.getWeatherHtml(zip, isCelcius, callback);
		
	}

	private void displayHtml(String html) {
		weatherHtml.setHTML(html);
		RootPanel.get("output-container").setVisible(true);
	}

	private void hideHtml() {
		RootPanel.get("output-container").setVisible(false);
	}
}
