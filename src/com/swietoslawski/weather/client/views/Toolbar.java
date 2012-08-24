/**
 * 
 */
package com.swietoslawski.weather.client.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author peter.swietoslawski
 *
 */
public class Toolbar extends Composite implements HasText {

	private static ToolbarUiBinder uiBinder = GWT.create(ToolbarUiBinder.class);

	interface ToolbarUiBinder extends UiBinder<Widget, Toolbar> {
	}

	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	public Toolbar() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField Button add;
	@UiField Button list;
	@UiField Button config;
	@UiField Button help;

	public Toolbar(String label_add, String label_list, String label_config, String label_help) {
		initWidget(uiBinder.createAndBindUi(this));

		// Can access @UiField after calling createAndBindUi
		add.setHTML(label_add);
		list.setHTML(label_list);
		config.setHTML(label_config);
		help.setHTML(label_help);
	}

	@UiHandler("add") 
	void onClickAdd(ClickEvent e) {
		Window.alert("Hello!");
	}
	
	@UiHandler("list") 
	void onClickList(ClickEvent e) {
		Window.alert("Hello!");
	}
	
	@UiHandler("config") 
	void onClickConfig(ClickEvent e) {
		Window.alert("Hello!");
	}
	
	@UiHandler("help") 
	void onClickHelp(ClickEvent e) {
		Window.alert("Hello!");
	}

	
	/*
	 * WTF do we need these functions to be implemented as a part of contract with hasText interface??? 
	 */
	public void setText(String text) {
		
	}

	/**
	 * Gets invoked when the default constructor is called
	 * and a string is provided in the ui.xml file.
	 */
	public String getText() {
		return "";
	}

}
