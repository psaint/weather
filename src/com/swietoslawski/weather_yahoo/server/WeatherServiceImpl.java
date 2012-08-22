package com.swietoslawski.weather_yahoo.server;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.swietoslawski.weather_yahoo.client.WeatherService;
import com.swietoslawski.weather_yahoo.client.ZipValidator;
import com.swietoslawski.weather_yahoo.shared.WeatherException;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

public class WeatherServiceImpl extends RemoteServiceServlet implements
		WeatherService {

	private static final long serialVersionUID = 1L;
//	private static Log log = LogFactory.getLog(WeatherServiceImpl.class);
	private static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	
	@Override
	public String getWeatherHtml(String zip, boolean isCelcius)
			throws WeatherException {

		@SuppressWarnings("unused")
		int stop = 0;
		stop = 1 + 1;
		
		// Validation of the code in client side does not guarantee that the such
		// validated input was not tampered after by man in the middle attack 
		// For AJAX application the input should be also re-validated on server side!!!
		if (!ZipValidator.isValid(zip)) {
			//log.warn("Invalid zipcode: " + zip);
			throw new WeatherException("Zip-code must have 5 digits");
		}
		
		Document rss = null;
		
		
		// TODO Replace all this exception catch with one generic 
		try {
			rss = getWeatherRssDocument(zip, isCelcius);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String html = getWeatherHtml(rss);
		return html;
	}
	
	private Document getWeatherRssDocument(String zip, boolean isCelcius) 
		throws IOException, ParserConfigurationException, SAXException {
		
		String url_string = getWeatherUrl(zip, isCelcius);
		URL url = new URL(url_string);
		
		// Reading XML file
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		     
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(url.openStream());
		
		return doc;
	}

	private String getWeatherHtml(Document rss) {
		Element item = (Element) rss.getElementsByTagName("item").item(0);
		Element desc = (Element) item.getElementsByTagName("description").item(0);
		
		return desc.getFirstChild().getNodeValue();
	}
	
	private String getWeatherUrl(String zip, boolean isCelcius) {
		final String base = "http://xml.weather.yahoo.com/forecastrss?";
		return base + "p=" + zip + "&u=" + (isCelcius ? "c" : "f");
	}

}
