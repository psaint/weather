package com.swietoslawski.weather.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.swietoslawski.weather.client.views.AddCityDialogBox;
import com.swietoslawski.weather.client.views.ConfigView;
import com.swietoslawski.weather.client.views.HelpView;
import com.swietoslawski.weather.client.views.ListOfCitiesView;
import com.swietoslawski.weather.shared.CurrentConditions;
import com.swietoslawski.weather.shared.Forecast;
import com.swietoslawski.weather.shared.ForecastInformation;
import com.swietoslawski.weather.shared.WeatherWrapper;

// TODO Consider using mGWT http://code.google.com/p/mgwt/wiki/GettingStarted


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Weather implements EntryPoint {
	
	// Eight weather casts should initially be more than enough
	private ArrayList<String> cities = new ArrayList<String>(8);
	
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
	
	private Listener[] listeners;
	
	// Store weather objects
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
		
		// TODO We need to delegate UI management to a separate class

		
		// We will create RootLayoutPanel that will give us ability to respond to resizing
		// Then we add DockLayoutPanel that will allow to define content and toolbar
		// areas and will fill in whole available space
		// Then we add FlowPanel to centr area to stretch left right top bottom
		// Then we add toolbar to south area
		// Content area will either present user with welcome screen if no cities are 
		// stored or weather cast for first city on the stred list
		// West and East regions will hold navigation buttons (if user has more than
		// one city in the list.
		// In a center area below content we will show indicator rendering dots for number
		// of cities user have
		
		// We will provide four buttons in a toolbar: adding new city, showing list of cities,
		// showing config (update time etc), accessing help page
		
		// All the pages (views) will be generated in separate functions
		
		// adding city will be shown as dialog box where on success we will render name of city
		// and a state for which we were able to pull weather and provide Add and Cancel buttons
		// on failure we will provide message of either not being able to conenct to service, 
		// or not being able to find city
		
		// Function calling service fetching weatcher will update fields related to weathercast
		// in main controller (this class)
		// However because this is an aynchronous call and we have callbacks for success
		// we need to have ability to provide different callbacks depending what object
		// is calling service. For instance when the home page is calling service the success
		// callback should result in re-rendering currently displayed weather cast.
		// On the other hand when the dialog box window for addding new city calls service 
		// it should result in updating html on the box itself
		
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
		
		prev = new Button("<");
		next = new Button(">");
		
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
				forecastInformation = weather.getForecastInformation();
				currentConditions = weather.getCurrentConditions();
				forecasts = weather.getForecastConditions();
				
				// TODO we should save the weather info in fields, reset failure falg
				// and not update forecasts updating forecast should be invoked 
				// only when user is viewing home page
				
				// Update forecast
				//updateForecast();
				
				broadcastEvent("weatherFetched");
			}
		};
		
		// Call remote service and define callback behavior
		weatherService.getWeather(city, callback);
		
	}

	protected void broadcastEvent(String event) {
		for (Listener listener : listeners) {
			listener.getNotified(event);
		}		
	}

	protected void updateForecast() {
		
		// Remove current forecast
		RootPanel.get("content").clear();
		RootPanel.get("content").clear();
		
		// Re-render city search box
		//showAddCityView();
		
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

	public void addCity(String city) {
		cities.add(city);
		
		// At that point we should also re-render view
	}

}
