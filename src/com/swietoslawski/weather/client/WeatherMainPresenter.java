package com.swietoslawski.weather.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.swietoslawski.weather.client.views.AddCityDialogBox;
import com.swietoslawski.weather.client.views.ConfigView;
import com.swietoslawski.weather.client.views.HelpView;
import com.swietoslawski.weather.client.views.ListOfCitiesView;
import com.swietoslawski.weather.shared.JSO.CurrentConditions;
import com.swietoslawski.weather.shared.JSO.Forecast;
import com.swietoslawski.weather.shared.JSO.ForecastInformation;
import com.swietoslawski.weather.shared.JSO.Weather;

// TODO Consider using mGWT http://code.google.com/p/mgwt/wiki/GettingStarted


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WeatherMainPresenter implements EntryPoint {
	
	private Storage storageForecasts = null;
	
	// Eight weather casts should initially be more than enough
	private ArrayList<Weather> weatherCasts = new ArrayList<Weather>(8);
	private int index = -1;
	
	private TextBox txBox;
	HTML weatherHtml;
	
	private Button home;
	private Button add;
	private Button list;
	private Button config;
	private Button help;
	
	private Button prev;
	private Button next;
	private FlowPanel content;
	private FlowPanel navigation;
	
	// Store current weather objects
	private ForecastInformation forecastInformation;
	private CurrentConditions currentConditions;
	private Forecast[] forecasts;
	
	// TODO This is duplicated in WeatherServiceImpl class. We need to solve this
	//      kludge. 
	private final String weatherProviderURL = "http://www.google.com";
	
	// Proxy of weather service class
	private WeatherServiceAsync weatherService = GWT.create(WeatherService.class);
	
	public WeatherServiceAsync getWeatherService() {
		return weatherService;
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		renderUI();
		initEventHandlers();
		
		return;
	}
	
	private void initEventHandlers() {
		
		add.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				showAddCity();
			}
		});
		
		list.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				showListView();
			}
		});
		
		config.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				showConfigView();
			}
		});
		
		help.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				showHelpView();
			}
		});
	}
	
	protected void showAddCity() {
		AddCityDialogBox addCityDialog = new AddCityDialogBox(this);
		addCityDialog.show();
	}
	
	protected void showConfigView() {
		// Create back button
		Button back = new Button("Home");
		navigation.clear();
		navigation.add(back);
		content.clear();
		content.add(new ConfigView());
	}

	protected void showHelpView() {
		Button back = new Button("Home");
		navigation.clear();
		navigation.add(back);
		content.clear();
		content.add(new HelpView());
	}
	
	protected void showListView() {
		Button back = new Button("Home");
		navigation.clear();
		navigation.add(back);
		content.clear();
		content.add(new ListOfCitiesView());
	}

	

	private void renderUI() {
		
		
		// This is a view navigation that will have Home button to 
		// return to app's home screen
		navigation = new FlowPanel();
		
		home = new Button("Home");
		add = new Button("Add");
		list = new Button("List");
		config = new Button("Config");
		help = new Button("Help");
		
		FlowPanel toolbar = new FlowPanel();
		toolbar.add(add);
		toolbar.add(list);
		toolbar.add(config);
		toolbar.add(help);
		
		prev = new Button("<", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// We don't have to check what position 
				// in the list we are now as button is
				// only available when user can move back
				index--;
				render();
				
			}
		});
		prev.setVisible(false);
		next = new Button(">", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// We don't have to check what position 
				// in the list we are now as button is
				// only available when user can advance
				index++;
				render();
				
			}
		});
		next.setVisible(false);
		
		HTML info = new HTML("<h1>Welcome</h1><p>bla bla bla</p>");
		HTML indicator = new HTML();
		content = new FlowPanel();
		content.add(info);
		content.add(indicator);
		
		// Order by which you add to DockLayoutPanel is crucial. Content region always has to be added last.
		// If you want North or South to stretch above West and East they have to be added before West/East.
		// And last we add to center region...has to be the last call
		// otherwise it will fail
		DockLayoutPanel layout = new DockLayoutPanel(Unit.PT);
		layout.addNorth(navigation, 40);
		layout.addSouth(toolbar, 40);
		layout.addWest(prev, 20);
		layout.addEast(next, 20);
		layout.add(content);
		
		RootLayoutPanel root = RootLayoutPanel.get();
		root.add(layout);
		
	}
	
	private void updatePrevNextVisibility() {
		if (index != -1 && weatherCasts != null) {
			
			// We are at the beggining of weathercast stack
			// so we only shows next button
			if (index == 0 && weatherCasts.size() > 1) {
				prev.setVisible(false);
				next.setVisible(true);
			}
			
			// At the end of weather casts stack
			// show only previous button
			else if (index == weatherCasts.size() - 1) {
				prev.setVisible(true);
				next.setVisible(false);
			}
			// In the middle of a stack
			else {
				prev.setVisible(true);
				next.setVisible(true);
			}
		}
	}

	private void refresh() {
		
		
		// Setup callback
		AsyncCallback<Weather> callback = new AsyncCallback<Weather>() {

			@Override
			public void onFailure(Throwable caught) {
				
				// TODO this shoudl set flag in our object so we can then confirm
				//      back to user when he is adding a city that it was found
				
				Window.alert("Error: " + caught.getMessage());
				txBox.setEnabled(true);
			}

			@Override
			public void onSuccess(Weather weather) {
				
			}
		};
		
		
		
		// Call remote service and define callback behavior
		//weatherService.getWeather(city, callback);
		
	}

	public void addWeatherCast(Weather weather) {

		// Save weather cast
		weatherCasts.add(weather);
		
		// And if available save it in browser local storage
		storageForecasts = Storage.getLocalStorageIfSupported();
		 if (storageForecasts != null) {
			 // Serialize data and save in local storage
			 // We will keep serialized data under city_#_key as a key
			 // GWT does not support serialization of an objects on Client side
			 // 	http://stackoverflow.com/questions/6873221/using-rpc-serialization-deserialization-mechanism-built-in-gwt
			 // that's just f...n awesome. If they compile code to JS why they can't just use native JavaScript JSON object to
			 // run these serialization???
			 
			 // Apparently this can be done with JavaScriptObject overlay types:
			 // 	https://developers.google.com/web-toolkit/doc/latest/DevGuideCodingBasicsOverlay
			 // However reading this documentation is not instantly obvious how this 
			 // thing should be coded to make it really works.
			 // Alternatively to overlay types serialization and deserialization
			 // of JSON into Java could be done with with JSON <-> POJO mapping framework Piriti
			 //		http://code.google.com/p/piriti/wiki/Json
			 String city = weather.getForecastInformation().getCity();
			 String data = "";
	
			
			 //storageForecasts.setItem(city, data);
		 }
		
		// Reset index to the last element on the stack
		index = weatherCasts.size() - 1;
				
		// Re-render view
		render();
	}

	protected void render() {
				
		// Update forecast view
		// Extract current weather
		Weather weather = weatherCasts.get(index);
		forecastInformation = weather.getForecastInformation();
		currentConditions = weather.getCurrentConditions();
		forecasts = weather.getForecastConditions();

		FlowPanel current = updateCurrent(forecastInformation.getCity(), currentConditions.getIcon(), 
				currentConditions.getTemp_f(), currentConditions.getTemp_c(), currentConditions.getHumidity()); 
		VerticalPanel forecasts = updateForecast(this.forecasts);
		
		// Clear panel
		content.clear();

		// Render new weather cast
		content.add(current);
		content.add(forecasts);
		
		// Update previous and next buttons visibility
		updatePrevNextVisibility();
	}

	private FlowPanel updateCurrent(String city, String icon, String temp_f, String temp_c, String humidity) {
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
			
		return flowPanel;
	}

	private VerticalPanel updateForecast(Forecast[] forecasts) {
		
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
		
		return vPanel;
	}

}
