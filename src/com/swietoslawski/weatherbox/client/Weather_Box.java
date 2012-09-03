package com.swietoslawski.weatherbox.client;


import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.swietoslawski.weatherbox.shared.City;
import com.swietoslawski.weatherbox.shared.Weather;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Weather_Box implements EntryPoint {
	
	private List<City> cities;
	private TextBox city;
	private Button submit;
	private ListBox cities_list = new ListBox();
	
	private String selected_city;
	
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final WeatherServiceAsync weatherService = GWT.create(WeatherService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		city = new TextBox();
		submit = new Button("Find");
		
		submit.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				weatherService.findCityLike(city.getValue(), new AsyncCallback<List<City>>() {
					
					@Override
					public void onSuccess(List<City> cities) {
						cities_list.clear();
						cities_list.addChangeHandler(new ChangeHandler() {
							
							@Override
							public void onChange(ChangeEvent event) {
								int index = cities_list.getSelectedIndex();
								String selected = cities_list.getItemText(index);
								if (!selected.equals("Select city:")) {
									selected_city = cities_list.getValue(index);
									System.out.println(selected_city);
									addWeatherCast(selected_city);
									
								} 
								else {
									System.out.println("Nothing was selected");
								}
								
							}
						});
						
						cities_list.addItem("Select city:");
						
						for (City city : cities) {
							System.out.println(city.toString());
							cities_list.addItem(city.toString(), city.toURL());
						}
						RootPanel.get().add(cities_list);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						System.out.println(caught.getMessage());
					}
				});
			}
		});
		
		RootPanel.get().add(city);
		RootPanel.get().add(submit);
	
	}
	
	private void addWeatherCast(String path) {
		// Break down / delimited string of Country/State/City
		path = path.replace("%20", " ");
		String[] url = path.split("/");
		String country = "";
		String state = "";
		String city = "";
		
		// We only have country and city
		if (url.length == 2) {
			country = url[0];
			state = "";
			city = url[1];
		}
		// Adding state for US
		else if (url.length == 3) {
			country = url[0];
			state = url[1];
			city = url[2];
		}
		else {
			// This shoud throw exception as something is wrong with path
		}
		
		callWeatherService(country, state, city);
	}
	
	private void callWeatherService(String country, String state, String city) {
		weatherService.getWeatherFor(country, state, city, new AsyncCallback<List<Weather>>() {
			
			@Override
			public void onSuccess(List<Weather> weather_cast) {
				
				StringBuilder response = new StringBuilder();
				for (Weather elem : weather_cast) {
					response.append(elem.toString());
				}
				System.out.println(response.toString());
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println(caught.getMessage());
				
			}
		});
	}
}
