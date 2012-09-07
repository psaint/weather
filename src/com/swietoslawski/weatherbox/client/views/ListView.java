package com.swietoslawski.weatherbox.client.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.swietoslawski.weatherbox.shared.City;

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
		root.clear();
		
		if (!parent.getController().getCities().isEmpty()) {
		
			// TODO Another, prolly better, way to do this would be to use ListDataProvider 
			//		together with CellTable. However this would be a lot more complicated
			//      than this simple solution where we track index of element listed in 
			//      html's Title attribute.
			for (City city : parent.getController().getCities()) {
				CityRowView city_row = new CityRowView(this, parent.getController());
				city_row.city.setText(city.getCity() + " " + city.getState());
				city_row.delete.setTitle(String.valueOf(row_nr));
				
				root.add(city_row);
				
				row_nr++;
			}
			parent.content.clear();
			parent.content.add(root);
		}
		// We deleted last item
		else {
			parent.showHelpView();
		}
	}

}
