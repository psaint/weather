package com.swietoslawski.weatherbox.client;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.swietoslawski.weatherbox.client.views.MainView;
import com.swietoslawski.weatherbox.shared.City;
import com.swietoslawski.weatherbox.shared.Weather;

// TODO Add timer to poll weather every 1-5 minutes
// TODO Add styling
// TODO Test on iPhone
// TODO Add gestures support at least swipe for moving between weathers.

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WeatherController implements EntryPoint {
		
	/**
	 * Create a remote service proxy to talk to the server-side Weather service.
	 */
	private final WeatherServiceAsync weatherService = GWT.create(WeatherService.class);
	
	private List<City> cities = new ArrayList<City>(8);
	private List<List<Weather>> weather_casts = new ArrayList<List<Weather>>();
	
	// Keep track of current city city on weather casts stack
	private City city = new City();
	private List<Weather> weather = new ArrayList<Weather>();
	
	// Keep track of position of current city in weather casts stack
	private int index = -1;
	private MainView main_view;
	 
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		loadFromLocalStorage();
		
		main_view = new MainView(this);
		RootLayoutPanel.get().add(main_view);
	}
		
	public List<City> getCities() {
		return cities;
	}
	
	public void decIndex() {
		if (index > 1) {
			index--;
		}
		else {
			index = 0;
		}
	}
	
	public void incIndex() {
		// TODO Add limiting condition
		index++;
	}
	
	public void updateCurrentWeather() {
		weather = weather_casts.get(index);
	}
	
	public int getIndex() {
		return index;
	}
	
	public List<Weather> getWeatherField() {
		return weather;
	}
	
	public WeatherServiceAsync getWeatherService() {
		return weatherService;
	}
		
	public void getWeather() {
		
		weatherService.getWeatherFor(city, new AsyncCallback<List<Weather>>() {
			
			@Override
			public void onSuccess(List<Weather> weather_cast) {
								
				weather_casts.add(weather_cast);
				weather = weather_cast;
				
				main_view.renderWeatherCast();
				System.out.println("weather fetched");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println(caught.getMessage());
				main_view.showErrorView();
				
			}
		});
	}
	
	public void addCity(City city) {
		
		// Save city to local storage...if supported
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
		
		// Remove from local storage
		Storage storage = Storage.getLocalStorageIfSupported();
		if (storage != null) {
			storage.removeItem(cities.get(row_nr).toURL());
		}

		// Remove from list of cities
		cities.remove(row_nr);		
		
		// Remove related weather cast
		weather_casts.remove(row_nr);
		
		// Reset index to last element
		index = cities.size() - 1;
					
		// Make sure that we update current weather
		if (index >= 0) updateCurrentWeather();
		else weather = null;
	}
	
	
	private void loadFromLocalStorage() {
		if (cities.size() == 0) {
			Storage storage = Storage.getLocalStorageIfSupported();
			if (storage != null) {
				for (int i = 0; i < storage.getLength(); i++) {
					addCity(new City(storage.key(i)));
				}
			}
		}
	}
}