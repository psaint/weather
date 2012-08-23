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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.swietoslawski.weather.shared.WeatherWrapper;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Weather implements EntryPoint {
	private TextBox txBox;
	private Button btnSubmit;
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
		
		// Create widget for HTML output
		weatherHtml = new HTML();
		
		RootPanel.get("output-container").add(weatherHtml);
		
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
		// Hide existing weather report
		hideHtml();
		
		// Setup callback
		AsyncCallback<WeatherWrapper> callback = new AsyncCallback<WeatherWrapper>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error: " + caught.getMessage());
				txBox.setEnabled(true);
			}

			@Override
			public void onSuccess(WeatherWrapper result) {
				
				@SuppressWarnings("unused")
				StringBuilder html = new StringBuilder();
				
				// Get main weather cast info
				
				// Get current weather condition
				
				// Loop through forecasts
				
//				for (Weather w : result) {
//					html.setLength(0);
//					html.append(w.getDayOfWeek() + ": ");
//					html.append(w.getHigh()+ " " + w.getLow() + " " + w.getCondition());
//					String url = "http://www.google.com" + w.getIcon();
//					Image icon = new Image(url);
//					Label label = new Label(html.toString());
//					RootPanel.get().add(label);
//					RootPanel.get().add(icon);
//					
//				}
				
				// Show new weather report
				//displayHtml(html.toString());
			}
		};
		
		// Call remote service and define callback behavior
		weatherService.getWeather(city, callback);
		
	}

	private void displayHtml(String html) {
		weatherHtml.setHTML(html);
		RootPanel.get("output-container").setVisible(true);
	}

	private void hideHtml() {
		RootPanel.get("output-container").setVisible(false);
	}
}
