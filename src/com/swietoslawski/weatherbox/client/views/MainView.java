package com.swietoslawski.weatherbox.client.views;

import org.apache.jasper.tagplugins.jstl.core.Out;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.swietoslawski.weatherbox.client.WeatherController;

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
	@UiField PushButton update;
	@UiField HTML pager;
	@UiField FlowPanel content;
	
	interface Binder extends UiBinder<Widget, MainView> {
	}

	public MainView(WeatherController weather_controller) {
		initWidget(binder.createAndBindUi(this));
		
		this.weather_controller = weather_controller;
		
		showHomeView();
	}	
	
	@UiHandler("home")
	public void onClickHome(ClickEvent event) {
		showHomeView();
	}
		
	@UiHandler("add")
	public void onClickAdd(ClickEvent event) {
		showAddCityDialog();
	}

	@UiHandler("list")	
	public void onClickList(ClickEvent event) {
		showListView();
	}
	
	@UiHandler("update")	
	public void onClickUpdate(ClickEvent event) {
		weather_controller.getWeather();
		renderWeatherCast();
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
		
	public void showHomeView() {
		// Render weather cast if user has any saved
		if (weather_controller.getCities().size() > 0) {
			list.setEnabled(true);
			renderWeatherCast();
		}
		else {
			list.setEnabled(false);
			showHelpView();
		}
	}
	
	public void showAddCityDialog() {
		addCityDialog = new AddCityDialogView(weather_controller);
		
		// Show the dialog box rather than attach it to RootLayout
		addCityDialog.show();
	}
	
	public void showListView() {
		hidePrevNextButton();
		list_view = new ListView(this);
		list_view.render();
	}
	
	public void showHelpView() {
		hidePrevNextButton();
		
		HTML info = new HTML("<h1>Wather Box</h1><p>Looks like you don't have any weathercasts.</p><p>Click on <strong>+</strong> button below to add new city.</p>");
		HTML indicator = new HTML();
		
		content.clear();
		content.add(info);
		content.add(indicator);
	}
	
	public void showErrorView() {
		hidePrevNextButton();
		
		HTML error = new HTML("<h1>Error</h1><p>We could not connect to Wunderground weather service.</p><p>Please check your Internet connection.</p>");
		
		content.clear();
		content.add(error);
	}
	
	public void renderWeatherCast() {	
		WeatherCastView weather = new WeatherCastView(weather_controller); 
		
		content.clear();
		content.add(weather);
		
		// Render indicator of how many weathercasts we have and which are we viewing now
		StringBuilder output = new StringBuilder();
		
		for (int i = 0; i < weather_controller.getCities().size(); i++) {
			
			if (i == weather_controller.getIndex()) {
				output.append("<span class='active'>&nbsp</span>");
			} else {
				output.append("<span>&nbsp</span>");
			}
		}
		pager.setHTML(output.toString());
		
		updatePrevNextVisibility();
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

	/**
	 * Hide buttons.
	 */
	private void hidePrevNextButton() {
		prev.setVisible(false);
		next.setVisible(false);
	}
	
}
