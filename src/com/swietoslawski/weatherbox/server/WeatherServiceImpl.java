package com.swietoslawski.weatherbox.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.swietoslawski.weatherbox.client.WeatherService;
import com.swietoslawski.weatherbox.shared.City;
import com.swietoslawski.weatherbox.shared.Weather;

public class WeatherServiceImpl extends RemoteServiceServlet implements WeatherService {

	private static final long serialVersionUID = 1L;
	
	private final static String URL = "http://api.wunderground.com/api/";
	private final static String API = "a780a20159174771";
	private final static String SERVICE = "/forecast/geolookup/q/";
	private final static String RESPONSE_FORMAT = ".xml";
	
    private URL url;
    
	
    /* ********************************************************************* */
	/* 							CONSTRUCTORS 								 */
	/* ********************************************************************* */
	public WeatherServiceImpl() {}

	
	/* ********************************************************************* */
	/* 							PUBLIC API	  								 */
	/* ********************************************************************* */
	public List<City> findCityLike(String name) {
		
		// We use Tree to have the keep the map sorted
		List<City> cities = new ArrayList<City>();
		
		/*
		 * Parsing returned XML
		 */
		try {
			
			// Build URL with query
			buildURL(new City(name, "", ""));
			
			// Parse XML
			Document doc = parseXMLtoDOM();
		    
			// Get results as a list
		    NodeList results = doc.getElementsByTagName("result");
		    for (int i = 0; i < results.getLength(); i++) {
		    	Node result = results.item(i);
		    	
		    	NodeList result_child_nodes = result.getChildNodes();
		    	
		    	String city = "", country = "", state = "";
		    	for (int j = 0; j < result_child_nodes.getLength(); j++) {
		    		Node node = result_child_nodes.item(j);
		    				    		
		    		// Process ONLY for child nodes being elements <xyz></xyz>
		    		if (node.getNodeType() == Node.ELEMENT_NODE) {
		    			if (node.getNodeName() == "city") city = node.getTextContent();
		    			if (node.getNodeName() == "country_name") country = node.getTextContent();
		    			if (node.getNodeName() == "state") state = node.getTextContent();
		    		}
		    	}
		    	cities.add(new City(city, state, country));
		    }
		    
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return cities;
	}
	
	public List<Weather> getWeatherFor(City city) {
				
		List<Weather> weather_casts = new ArrayList<Weather>(4);
		
		buildURL(city);
		
		try {
			Document doc = parseXMLtoDOM();
			
			NodeList simpleforecast = doc.getElementsByTagName("simpleforecast");
			Element forecastdays_elem = ((Element) simpleforecast.item(0));
			NodeList forecastdays = forecastdays_elem.getElementsByTagName("forecastday");
			
			// Loop through all forecasts (today plus 3 days ahead)
			for (int i = 0; i < forecastdays.getLength(); i++) {
				
				Node forecastdayNode = forecastdays.item(i);
				
				// Process if child item we got is a node
				if (forecastdayNode.getNodeType() == Node.ELEMENT_NODE) {
					weather_casts.add(extractWeatherFromNode(forecastdayNode, city));
				}
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return weather_casts;
	}
	
	/* ********************************************************************* */
	/* 							PRIVATE API	  								 */
	/* ********************************************************************* */
	private Document parseXMLtoDOM() 
			throws ParserConfigurationException, SAXException, IOException {

		// Create a builder factory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder document_builder = factory.newDocumentBuilder();
		Document doc = document_builder.parse(url.openStream());
		
		return doc;
	}
	
	private Weather extractWeatherFromNode(Node forecastdayNode, City city) {
		// Forecast has only few fields we are interested in keeping
		//   - date
		//     -  weekday
		//     - datepretty_short (time with timezone)
		//   - high
		//     - fahrenheit
		//   - low
		//     - fahrenheit 
		//   - conditions
		//   - icon_url
		//   - avehumidity
		//   - avewind
		//     - mph
		
		Node date = ((Element) forecastdayNode).getElementsByTagName("date").item(0);
		String weekday = ((Element) date).getElementsByTagName("weekday").item(0).getFirstChild().getTextContent();
		String time = ((Element) date).getElementsByTagName("pretty_short").item(0).getFirstChild().getTextContent();
		
		Node high = ((Element) forecastdayNode).getElementsByTagName("high").item(0);
		String temp_h = ((Element) high).getElementsByTagName("fahrenheit").item(0).getFirstChild().getTextContent();
		
		Node low = ((Element) forecastdayNode).getElementsByTagName("low").item(0);
		String temp_l = ((Element) low).getElementsByTagName("fahrenheit").item(0).getFirstChild().getTextContent();
		

		String condition = ((Element) forecastdayNode).getElementsByTagName("conditions").item(0).getFirstChild().getTextContent();
		String icon = ((Element) forecastdayNode).getElementsByTagName("icon_url").item(0).getFirstChild().getTextContent();
		
		
		String humidity = ((Element) forecastdayNode).getElementsByTagName("avehumidity").item(0).getFirstChild().getTextContent();
		
		Node avewind = ((Element) forecastdayNode).getElementsByTagName("avewind").item(0);
		String wind = ((Element) avewind).getElementsByTagName("mph").item(0).getFirstChild().getTextContent();
		
		return new Weather(city.toURL(), weekday, time, temp_h, temp_l, condition, icon, humidity, wind);
	}
	
	private void buildURL(City city) {
		try {
			url = new URL(URL + API + SERVICE + city.toURL() + RESPONSE_FORMAT);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void saveToFile(List<City> cities) {
		// IMPORTANT: Saving to file system is not supported on Google App Engine 
		// @link http://stackoverflow.com/questions/2693081/does-google-app-engine-allow-creation-of-files-and-folders-on-the-server
		
		// NOTE: To store data on Google App Engine we can use NoSQL schemaless object datastore
		// @link https://developers.google.com/appengine/docs/java/datastore/
		// @link https://developers.google.com/appengine/docs/java/gettingstarted/usingdatastore
	}


	@Override
	public List<City> readFromFile() {
		// TODO Auto-generated method stub
		return null;
	}
}
