package com.swietoslawski.weatherbox.client.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.swietoslawski.weatherbox.shared.City;
import com.google.gwt.user.client.ui.FlowPanel;

public class ListView extends Composite {

	private static final Binder binder = GWT.create(Binder.class);
	private final MainView parent;
	
	@UiField FlowPanel root;

	interface Binder extends UiBinder<Widget, ListView> {
	}

	public ListView(MainView parent) {
		initWidget(binder.createAndBindUi(this));
		
		this.parent = parent;
	}
	
	public void render() {
		int row_nr = 0;
		for (City city : parent.getController().getCities()) {
			CityRowView city_row = new CityRowView(this, parent.getController());
			city_row.city.setText(city.getCity());
			city_row.delete.setTitle(String.valueOf(row_nr));
			
			root.add(city_row);
			
			row_nr++;
		}
		parent.content.clear();
		parent.content.add(root);
	}

}
