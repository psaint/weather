package com.swietoslawski.weatherbox.client;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.swietoslawski.weatherbox.client.widgets.AddCityDialog;
import com.swietoslawski.weatherbox.shared.City;
import com.swietoslawski.weatherbox.shared.Weather;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WeatherMainController implements EntryPoint {
		
	/**
	 * Create a remote service proxy to talk to the server-side Weather service.
	 */
	private final WeatherServiceAsync weatherService = GWT.create(WeatherService.class);
	
	private List<City> cities = new ArrayList<City>(8);
//	private ListDataProvider<City> cities_dp = new ListDataProvider<City>(cities);
	
	private List<List<Weather>> weather_casts = new ArrayList<List<Weather>>();
	
	// Keep track of current city city on weather casts stack
	private City city = new City();
	private List<Weather> weather = new ArrayList<Weather>();
	
	// Keep track of position of current city in weather casts stack
	private int index = -1;
	
	private Button home;
	private Button add;
	private Button list;
	private Button help;
	
	private Button prev;
	private Button next;
	private FlowPanel content;
	private FlowPanel navigation;
	
	private AddCityDialog addCityDialog; 
	

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		loadFromLocalStorage();
		
		renderUI();
		
		initEventHandlers();
		
		renderWeatherCast();
	}
	
	
	private void loadFromLocalStorage() {
		if (cities.size() == 0) {
			Storage storage = Storage.getLocalStorageIfSupported();
			if (storage != null) {
				
				// LOAD only first city
				
				for (int i = 0; i < storage.getLength(); i++) {
					String key = storage.key(i);
					//String item = storage.getItem(key);
					City city = new City(key);
					
					addCity(city);
				}
			}
		}
	}
	
	private void initEventHandlers() {
		
		home.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				renderWeatherCast();
			}
		});
		
		add.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				showAddCityDialog();
			}
		});
		
		list.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				showListView();
			}
		});
		
		help.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				showHelpView();
			}
		});
	}
	
	private void renderUI() {
		
		
		// This is a view navigation that will have Home button to 
		// return to app's home screen
		navigation = new FlowPanel();
		
		home = new Button("Home");
		add = new Button("Add");
		list = new Button("List");
		help = new Button("Help");
		
		FlowPanel toolbar = new FlowPanel();
		toolbar.add(home);
		toolbar.add(add);
		toolbar.add(list);
		toolbar.add(help);
		
		prev = new Button("<", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// We don't have to check what position 
				// in the list we are now as button is
				// only available when user can move back
				index--;
				
				// Update weather
				weather = weather_casts.get(index);
				
				renderWeatherCast();
				
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
				
				// Update weather
				weather = weather_casts.get(index);
				
				renderWeatherCast();
				
			}
		});
		next.setVisible(false);
		
		content = new FlowPanel();
		
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
		if (index != -1 && cities != null) {
			
			if (cities.size() <= 1) {
				hidePrevNextButton();
			}
			// We are at the beginning of weathercast stack
			// so we only shows next button
			else if (cities.size() > 1) {
				
				// At the beginning of a list
				if (index == 0) {
					prev.setVisible(false);
					next.setVisible(true);
				} 
				// At the end of list
				else if (index == cities.size() - 1) {
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
	}
	
	
	
	
	
	protected void renderWeatherCast() {		
		
		
		// Render weather cast if user has any saved
		if (cities.size() > 0) {
			list.setEnabled(true);
			
			FlowPanel weatherPanel = updateWeatherPanel(); 
			VerticalPanel forecastPanel = updateForecastPanel();
			
			// Clear panel
			content.clear();
	
			// Render new weather cast
			content.add(weatherPanel);
			content.add(forecastPanel);
			
			// Update previous and next buttons visibility
			updatePrevNextVisibility();
		}
		else {
			list.setEnabled(false);
			showHelpView();
		}
	}

	private FlowPanel updateWeatherPanel() {
		FlowPanel flowPanel = new FlowPanel();
		
		if (this.weather.size() > 0) {
			// Weather for today
			Weather weather = this.weather.get(0);
			
			City city = new City(weather.getCity());
			Label city_name = new Label(city.getCity() + " " + city.getState());
			Label tempH = new Label(weather.getTemp_h());
			Label tempL = new Label(weather.getTemp_l());
			Image icon = new Image(weather.getIcon());
			Label humidity = new Label(weather.getHumidity());
			
			
			flowPanel.add(city_name);
			flowPanel.add(icon);
			flowPanel.add(tempH);
			flowPanel.add(tempL);
			flowPanel.add(humidity);
		}
			
		return flowPanel;
	}

	private VerticalPanel updateForecastPanel() {
		
		// Forecast for following days will be rendered in rows
		VerticalPanel vPanel = new VerticalPanel();
		
		// We start at 1 as the 0 is current weather cast
		for (int i = 1; i < this.weather.size(); i++) {
			Weather weather = this.weather.get(i);
			
			Image icon = new Image(weather.getIcon());
			Label day = new Label(weather.getWeekday());
			Label high = new Label(weather.getTemp_h());
			Label low = new Label(weather.getTemp_l());
			
			HorizontalPanel hPanel = new HorizontalPanel();
			hPanel.add(icon);
			hPanel.add(day);
			hPanel.add(high);
			hPanel.add(low);
			
			vPanel.add(hPanel);	
		}
		
		return vPanel;
	}
	
	
	public void showHelpView() {
		HTML info = new HTML("<h1>Welcome</h1><p>bla bla bla</p>");
		HTML indicator = new HTML();
		
		hidePrevNextButton();
		
		content.clear();
		content.add(info);
		content.add(indicator);
	}
	
	public void showAddCityDialog() {
		addCityDialog = new AddCityDialog(this);
		
		// Show the dialog box rather than attach it to RootLayout
		addCityDialog.show();
	}
	
	public WeatherServiceAsync getWeatherService() {
		return weatherService;
	}
	
	public void addCity(City city) {
		
		// Add city to local storage if available
		// So the next time user will load app her favorite 
		// cities will be persisted
		Storage storage = Storage.getLocalStorageIfSupported();
		if (storage != null) {
			storage.setItem(city.toURL(), city.toURL());
		}
		
		cities.add(city);
		
		// Update current city
		// TODO Get rid of this current city and current weather as we
		// can calculate them dynamically based on the value of index
		// when and where we need them.
		this.city = city;
		
		// update index to point to the last added city
		index = cities.size() - 1;
		getWeather();
	}
	
	public void removeCity(int row_nr) {
		
		// Remove from local storage if available
		Storage storage = Storage.getLocalStorageIfSupported();
		if (storage != null) {
			City city = cities.get(row_nr);
			storage.removeItem(city.toURL());
		}

		cities.remove(row_nr);
		index = cities.size() - 1;
		
			
		// Make sure that we update weather
		if (index >= 0)
			weather = weather_casts.get(index);
		else 
			weather = null;
	}
	
	public void getWeather() {
		
		weatherService.getWeatherFor(city, new AsyncCallback<List<Weather>>() {
			
			@Override
			public void onSuccess(List<Weather> weather_cast) {
				
				StringBuilder response = new StringBuilder();
				for (Weather elem : weather_cast) {
					response.append(elem.toString());
				}
				System.out.println(response.toString());
				
				// TODO Change definition of service so it passes back the city for which we get the weathercast
				//City city = new City(weather_cast.get(0).getCity());
				weather_casts.add(weather_cast);
				weather = weather_cast;
				
				// TODO Once new weather cast is added we need to re-render UI
				if (addCityDialog != null) {
					addCityDialog.hide();
				}
				
				renderWeatherCast();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println(caught.getMessage());
				
			}
		});
	}
	
	private void showListView() {
		if (cities.size() == 0) {
			renderWeatherCast();
			return;
		}
		
		// TODO Another, prolly better, way to do this would be to use ListDataProvider 
		//		together with CellTable. However this would be a lot more complicated
		//      than this simple solution where we track index of element listed in 
		//      html's Title attribute.
		VerticalPanel vPanel = new VerticalPanel();
		int row_nr = 0;
		for (City city : cities) {
			FlowPanel layout = new FlowPanel();
			Label city_name = new Label(city.getCity());
			Button delete = new Button("Delete");
			delete.setTitle(String.valueOf(row_nr));
			row_nr++;
			
			layout.add(city_name);
			layout.add(delete);
			
			delete.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					Button btn = (Button) event.getSource();
					String row_nr  = btn.getTitle();
					removeCity(Integer.parseInt(row_nr));
					
					// Re-render page
					// IMPORTANT: This is a must to make sure that row numbers (indexes)
					//            of tracked elements will be re-calculated again.
					showListView();
				}
			});
			
			vPanel.add(layout);
		}
		
		
		
		hidePrevNextButton();
		
		content.clear();
		content.add(vPanel);
	}


	private void hidePrevNextButton() {
		// Hide buttons
		prev.setVisible(false);
		next.setVisible(false);
	}
}
