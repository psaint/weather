package com.swietoslawski.weatherbox.client;

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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.swietoslawski.weatherbox.client.widgets.AddCityDialog;
import com.swietoslawski.weatherbox.shared.City;
import com.swietoslawski.weatherbox.shared.Weather;

public class Main extends Composite {

	private static final Binder binder = GWT.create(Binder.class);
	private final WeatherMainController main_controller;
	private AddCityDialog addCityDialog;
	
	@UiField PushButton prev;
	@UiField PushButton next;
	@UiField PushButton home;
	@UiField PushButton add;
	@UiField PushButton list;
	@UiField PushButton help;
	@UiField FlowPanel content;
	
	interface Binder extends UiBinder<Widget, Main> {
	}

	public Main(WeatherMainController main_controller) {
		initWidget(binder.createAndBindUi(this));
		
		this.main_controller = main_controller;
		
		//hidePrevNextButton();
		renderWeatherCast();
		//showHelpView();
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
		main_controller.decIndex();
		
		// Update weather
		main_controller.updateCurrentWeather();
		
		renderWeatherCast();
	}
	
	
	@UiHandler("next")
	public void onClickNext(ClickEvent event) {
		// We don't have to check what position 
		// in the list we are now as button is
		// only available when user can advance
		main_controller.incIndex();
		
		// Update weather
		main_controller.updateCurrentWeather();
		
		renderWeatherCast();
		
	}
	
	protected void renderWeatherCast() {	
		
		// TODO Once new weather cast is added we need to re-render UI
		if (addCityDialog != null) {
			addCityDialog.hide();
		}

		// Render weather cast if user has any saved
		if (main_controller.getCities().size() > 0) {
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
		
		if (main_controller.getWeatherField().size() > 0) {
			
			// Weather for today
			Weather weather = main_controller.getWeatherField().get(0);
			
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
		for (int i = 1; i < main_controller.getWeatherField().size(); i++) {
			Weather weather = main_controller.getWeatherField().get(i);
			
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
		addCityDialog = new AddCityDialog(main_controller);
		
		// Show the dialog box rather than attach it to RootLayout
		addCityDialog.show();
	}
	
	private void updatePrevNextVisibility() {
		if (main_controller.getIndex() != -1 && main_controller.getCities() != null) {
			
			if (main_controller.getCities().size() <= 1) {
				hidePrevNextButton();
			}
			// We are at the beginning of weathercast stack
			// so we only shows next button
			else if (main_controller.getCities().size() > 1) {
				
				// At the beginning of a list
				if (main_controller.getIndex() == 0) {
					prev.setVisible(false);
					next.setVisible(true);
				} 
				// At the end of list
				else if (main_controller.getIndex() == main_controller.getCities().size() - 1) {
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
	
	private void showListView() {
		if (main_controller.getCities().size() == 0) {
			renderWeatherCast();
			return;
		}
		
		// TODO Another, prolly better, way to do this would be to use ListDataProvider 
		//		together with CellTable. However this would be a lot more complicated
		//      than this simple solution where we track index of element listed in 
		//      html's Title attribute.
		VerticalPanel vPanel = new VerticalPanel();
		int row_nr = 0;
		for (City city : main_controller.getCities()) {
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
					main_controller.removeCity(Integer.parseInt(row_nr));
					
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

	/**
	 * Hide buttons.
	 */
	private void hidePrevNextButton() {
		prev.setVisible(false);
		next.setVisible(false);
	}
	
}
