package com.swietoslawski.weatherbox.client.widgets;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.swietoslawski.weatherbox.client.WeatherController;
import com.swietoslawski.weatherbox.shared.City;

public class AddCityDialog extends DialogBox {


	private static final Binder binder = GWT.create(Binder.class);
	
	private String selected_city;
	private final WeatherController main_controller;
	
	@UiField TextBox city_text;
	@UiField ListBox cities_list;
	@UiField Button cancel_button;
	

	interface Binder extends UiBinder<Widget, AddCityDialog> {
	}
	
	public AddCityDialog(WeatherController mainController) {
		setWidget(binder.createAndBindUi(this));
		
		this.main_controller = mainController;
		
		// Hide drop down as we don't have any cities loaded into yet
		cities_list.setVisible(false);    
		
		// Center on screen
        centerOnScreen();
		
        // Set the dialog box's caption.
        setText("Search for weathercast");

        // Enable animation.
        setAnimationEnabled(true);

        // Enable glass background.
        setGlassEnabled(true);
        
        // Setting focus for the text box didn't work other than when scheduled
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			
			@Override
			public void execute() {
				city_text.setFocus(true);
			}
		});
	}
	
	private void centerOnScreen() {
		int width = 200;
		int height = 200;
		
		setWidth(width + "pt");
        setHeight(height + "pt");
		
        int left = (Window.getClientWidth() - width) / 2;
        int top = (Window.getClientHeight() - height) / 2;
        
        setPopupPosition(left, top);
	}

	private void addCity(String path) {
		main_controller.addCity(new City(path));
	}
	
	@UiHandler("cancel_button")
	public void onClick(ClickEvent event) {
		this.hide();
	}
	
	@UiHandler("city_text")
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
					cities_list.setFocus(true);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					System.out.println(caught.getMessage());
				}
			});
		}
	}
	
	@UiHandler("cities_list")
	public void onChange(ChangeEvent event) {
		int index = cities_list.getSelectedIndex();
		String selected = cities_list.getItemText(index);
		
		if (!selected.equals("Found cities:")) {
			selected_city = cities_list.getValue(index);
			System.out.println(selected_city);
			addCity(selected_city);
			
		} 
		else {
			System.out.println("Nothing was selected");
		}
	}
	
}
