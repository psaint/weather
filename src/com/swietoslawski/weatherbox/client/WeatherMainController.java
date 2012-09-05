package com.swietoslawski.weatherbox.client;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.swietoslawski.weatherbox.shared.City;
import com.swietoslawski.weatherbox.shared.Weather;

// TODO Add timer to poll weather every 1-5 minutes
// TODO Move the UI code to separate UX Builder classes
// TODO Tidy up code
// TODO Add styling
//TODO Test on iPhone
// TODO Add gestures support at least swipe for moving between weathers.

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WeatherMainController implements EntryPoint {
		
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
	private Main main;
	 
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		main = new Main(this);
		
		//loadFromLocalStorage();
		RootLayoutPanel.get().add(main);
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
				
				StringBuilder response = new StringBuilder();
				for (Weather elem : weather_cast) {
					response.append(elem.toString());
				}
				System.out.println(response.toString());
				
				// TODO Change definition of service so it passes back the city for which we get the weathercast
				//City city = new City(weather_cast.get(0).getCity());
				weather_casts.add(weather_cast);
				weather = weather_cast;
				
				main.renderWeatherCast();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println(caught.getMessage());
				
			}
		});
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
	
	
	
	
	private void loadFromLocalStorage() {
		if (cities.size() == 0) {
			Storage storage = Storage.getLocalStorageIfSupported();
			if (storage != null) {
				
				for (int i = 0; i < storage.getLength(); i++) {
					String key = storage.key(i);
					//String item = storage.getItem(key);
					City city = new City(key);
					
					addCity(city);
				}
			}
		}
	}
}