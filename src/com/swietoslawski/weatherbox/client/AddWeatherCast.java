package com.swietoslawski.weatherbox.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.swietoslawski.weatherbox.shared.City;
import com.swietoslawski.weatherbox.shared.Weather;

public class AddWeatherCast extends DialogBox {


	private static final Binder binder = GWT.create(Binder.class);
	
	private String selected_city;
	private final WeatherMainController main_controller;
	
	@UiField TextBox city_text;
	@UiField ListBox cities_list;
	

	interface Binder extends UiBinder<Widget, AddWeatherCast> {
	}
	
	public AddWeatherCast(WeatherMainController mainController) {
		setWidget(binder.createAndBindUi(this));
		
		this.main_controller = mainController;
		
		// Hide drop down as we don't have any cities loaded into yet
		cities_list.setVisible(false);
		
		city_text.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				
				// Process only if user hits Enter
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					
					main_controller.getWeatherService().findCityLike(city_text.getValue(), new AsyncCallback<List<City>>() {
						
						@Override
						public void onSuccess(List<City> cities) {
							cities_list.clear();
							cities_list.addItem("Found cities:");
							
							for (City city : cities) {
								System.out.println(city.toString());
								cities_list.addItem(city.toString(), city.toURL());
							}
							
							if (cities_list.getItemCount() < 2) {
								cities_list.clear();
								cities_list.addItem("No cities found");
							}
							
							cities_list.setVisible(true);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							System.out.println(caught.getMessage());
						}
					});
				}
			}
		});
		
		cities_list.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				int index = cities_list.getSelectedIndex();
				String selected = cities_list.getItemText(index);
				
				if (!selected.equals("Found cities:")) {
					selected_city = cities_list.getValue(index);
					System.out.println(selected_city);
					addWeatherCast(selected_city);
					
				} 
				else {
					System.out.println("Nothing was selected");
				}
				
			}
		});
	}
	
	private void addWeatherCast(String path) {
		
		// Instantiate city from url path
		City city = new City(path);
		
		main_controller.getWeatherService().getWeatherFor(city, new AsyncCallback<List<Weather>>() {
			
			@Override
			public void onSuccess(List<Weather> weather_cast) {
				
				StringBuilder response = new StringBuilder();
				for (Weather elem : weather_cast) {
					response.append(elem.toString());
				}
				System.out.println(response.toString());
				
				// TODO Add this weather cast to the main controller
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println(caught.getMessage());
				
			}
		});
	}
	
}
