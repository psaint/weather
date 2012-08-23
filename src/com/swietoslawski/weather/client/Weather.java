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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
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
		
		// TODO We need to delegate UI management to a separate class
		
		
		showContentView();
		showToolbarView();
		
		return;
	}

	/**
	 * This method renders main view with first of the cities from favorite list
	 * and if user has not added any cities yet will prompt user with dialog box
	 * to do so
	 */
	private void showContentView() {
		
		// Check if user has list of cities
		if (hasCities()) {
			
			// Render forecast for first city on the list
			showForecastView(0);
		}
		else {
			// Render boilerplate page with instructions how
			// to add cities for weather forecast
			showHelpView();
		}
	}

	private void showHelpView() {
		StringBuilder html = new StringBuilder();
		html.append("<div id=\"info-page\">");
		html.append("<h1>Welcome</h1>");
		html.append("<p>Your list of cities to report weather on is empty!.</p>");
		html.append("<p>Click the plus button below to add city.</p>");
		html.append("<p>Enjoy!</p>");
		html.append("</div>");
		
		RootPanel.get("content").clear();
		RootPanel.get("content").add(new HTML(html.toString()));
	}
	
	private void showAddCityView() {
		HorizontalPanel inputPanel = new HorizontalPanel();
		
		// Align child widgets along middle of panel
		inputPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		Label lbl = new Label("City: ");
		inputPanel.add(lbl);
		
		// TODO Replace text box with suggest box widget (typeahead) 
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
		
		
		RootPanel.get("content").add(inputPanel);
	}
	
	private void showForecastView(int n) {
		// TODO Add code to render forecast for n-th city on the list
		
	}

	private boolean hasCities() {
		// TODO Add logic to figure out if user has favorite cities
		return false;
	}

	/**
	 * This function renders bottom toolbar
	 */
	private void showToolbarView() {
		
		// Four buttons:
		//   - add new city (plus icon)
		//   - view list of cities (bullets icon) - will allow to delete city
		//   - help (i icon) will show help of how to use app
		//   - config (wrench icon) will display page allowing to configure some settings
		Button add = new Button("Add", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				clearContent();
				showAddCityView();
			}
		});
		
		Button list = new Button("List", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Button config = new Button("Config", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Button help = new Button("Help", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(add);
		hPanel.add(list);
		hPanel.add(config);
		hPanel.add(help);
		hPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_JUSTIFY);
		hPanel.setWidth("100%");
		
		// Add panel to page
		RootPanel.get("toolbar").add(hPanel);
	}

	protected void clearContent() {
		// TODO replace calls to RootPanel for content element with instance variable
		RootPanel.get("content").clear();
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
				
				// TODO this shoudl set flag in our object so we can then confirm
				//      back to user when he is adding a city that it was found
				
				Window.alert("Error: " + caught.getMessage());
				txBox.setEnabled(true);
			}

			@Override
			public void onSuccess(WeatherWrapper weather) {
				
				// Extract data from weather cast
				ForecastInformation forecastInformation = weather.getForecastInformation();
				CurrentConditions currentConditions = weather.getCurrentConditions();
				Forecast[] forecasts = weather.getForecastConditions();
				
				// TODO we should save the weather info in fields, reset failure falg
				// and not update forecasts updating forecast should be invoked 
				// only when user is viewing home page
				
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
		RootPanel.get("content").clear();
		RootPanel.get("content").clear();
		
		// Re-render city search box
		showAddCityView();
		
		// Update current forecast
		updateCurrent(forecastInformation.getCity(), currentConditions.getIcon(), currentConditions.getTemp_f(), currentConditions.getTemp_c(), currentConditions.getHumidity());
				
		// Render forecast for four days
		// Each forecast in one row with:
		//   - condition icon
		//   - Week day name
		//   - High and low temperature in Fahrenheit 
		updateForecast(forecasts);
		
		// Reenable textbox
		txBox.setEnabled(true);
		
	}

	private void updateCurrent(String city, String icon, String temp_f, String temp_c, String humidity) {
		Label c = new Label(city);
		Label t_f = new Label(temp_f);
		Label t_c = new Label(temp_c);
		Image image = new Image(weatherProviderURL + icon);
		Label h = new Label(humidity);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(c);
		flowPanel.add(image);
		flowPanel.add(t_f);
		flowPanel.add(t_c);
		flowPanel.add(h);
			
		RootPanel.get("content").add(flowPanel);
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
		
		RootPanel.get("content").add(vPanel);
	}

}
