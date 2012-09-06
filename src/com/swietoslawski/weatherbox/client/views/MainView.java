package com.swietoslawski.weatherbox.client.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.swietoslawski.weatherbox.client.WeatherController;
import com.swietoslawski.weatherbox.shared.City;

public class MainView extends Composite {

	private static final Binder binder = GWT.create(Binder.class);
	private final WeatherController weather_controller;
	private AddCityDialogView addCityDialog;
	private ListView list_view;
	
	@UiField PushButton prev;
	@UiField PushButton next;
	@UiField PushButton home;
	@UiField PushButton add;
	@UiField PushButton list;
	@UiField PushButton help;
	@UiField FlowPanel content;
	
	interface Binder extends UiBinder<Widget, MainView> {
	}

	public MainView(WeatherController weather_controller) {
		initWidget(binder.createAndBindUi(this));
		
		this.weather_controller = weather_controller;
		
		renderWeatherCast();
	}	
	
	@UiHandler("home")
	public void onClickHome(ClickEvent event) {
		renderWeatherCast();
	}
		
	@UiHandler("add")
	public void onClickAdd(ClickEvent event) {
		showAddCityDialog();
	}

	@UiHandler("list")	
	public void onClickList(ClickEvent event) {
		showListView();
	}
	
	@UiHandler("help")	
	public void onClickHelp(ClickEvent event) {
		showHelpView();
	}
	
	@UiHandler("prev")
	public void onClickPrev(ClickEvent event) {
		// We don't have to check what position 
		// in the list we are now as button is
		// only available when user can move back
		weather_controller.decIndex();
		
		// Update weather
		weather_controller.updateCurrentWeather();
		
		renderWeatherCast();
	}
	
	
	@UiHandler("next")
	public void onClickNext(ClickEvent event) {
		// We don't have to check what position 
		// in the list we are now as button is
		// only available when user can advance
		weather_controller.incIndex();
		
		// Update weather
		weather_controller.updateCurrentWeather();
		
		renderWeatherCast();
		
	}
	
	public WeatherController getController() {
		return weather_controller;
	}
	
	public void renderWeatherCast() {	
		
		if (addCityDialog != null) {
			addCityDialog.hide();
		}

		// Render weather cast if user has any saved
		if (weather_controller.getCities().size() > 0) {
			list.setEnabled(true);
			
			WeatherCastView weather = new WeatherCastView(weather_controller); 
			
			content.clear();
			content.add(weather);
			
			updatePrevNextVisibility();
		}
		else {
			list.setEnabled(false);
			showHelpView();
		}
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
		addCityDialog = new AddCityDialogView(weather_controller);
		
		// Show the dialog box rather than attach it to RootLayout
		addCityDialog.show();
	}
	
	private void updatePrevNextVisibility() {
		if (weather_controller.getIndex() != -1 && weather_controller.getCities() != null) {
			
			if (weather_controller.getCities().size() <= 1) {
				hidePrevNextButton();
			}
			// We are at the beginning of weathercast stack
			// so we only shows next button
			else if (weather_controller.getCities().size() > 1) {
				
				// At the beginning of a list
				if (weather_controller.getIndex() == 0) {
					prev.setVisible(false);
					next.setVisible(true);
				} 
				// At the end of list
				else if (weather_controller.getIndex() == weather_controller.getCities().size() - 1) {
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
	
	public void showListView() {
		if (weather_controller.getCities().size() == 0) {
			renderWeatherCast();
			return;
		}
		
		// TODO Another, prolly better, way to do this would be to use ListDataProvider 
		//		together with CellTable. However this would be a lot more complicated
		//      than this simple solution where we track index of element listed in 
		//      html's Title attribute.
		list_view = new ListView(this);
		list_view.render();

		hidePrevNextButton();
	}

	/**
	 * Hide buttons.
	 */
	private void hidePrevNextButton() {
		prev.setVisible(false);
		next.setVisible(false);
	}
	
}
