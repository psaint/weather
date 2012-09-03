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

import com.swietoslawski.weatherbox.shared.City;
import com.swietoslawski.weatherbox.shared.Weather;


public class WeatherController {
	
	private final static String URL = "http://api.wunderground.com/api/";
	private final static String API = "a780a20159174771";
	private final static String SERVICE = "/forecast/geolookup/q/";
	private final static String RESPONSE_FORMAT = ".xml";
	
	// String location = "CA/San_Francisco";
	//private String location = "/San_Francisco";
				
    private URL url;
    
    private List<Weather> weather_cast = new ArrayList<Weather>();
    
    public WeatherController() {
    }
    
    public List<Weather> getWeatherCast() {
    	return weather_cast;
    }
    
	public List<City> findCity(String name) {
		
		// We use Tree to have the keep the map sorted
		List<City> cities = new ArrayList<City>();
		
		/*
		 * Parsing returned XML
		 */
		try {
			
			// Build URL with query
			url = buildURL("", "", name);
			
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
	
	public void extractWeatherFor(String country, String state, String city) {
				
		url = buildURL(country, state, city);
		
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
					addWeatherCast(forecastdayNode);
				}
			}
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void addWeatherCast(Node forecastdayNode) {
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
		
		weather_cast.add(new Weather(weekday, time, temp_h, temp_l, condition, icon, humidity, wind));
	}

	private Document parseXMLtoDOM() 
			throws ParserConfigurationException, SAXException, IOException {

		// Create a builder factory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder document_builder = factory.newDocumentBuilder();
		Document doc = document_builder.parse(url.openStream());
		
		return doc;
	}
	
	private String encodeSpaces(String string) {
		return string.replace(" ", "%20");
	}
	
	private URL buildURL(String country, String state, String city) {
		URL url = null;
		try {
			country = encodeSpaces(country);
			state = encodeSpaces(state);
			city = encodeSpaces(city);
			
			String query = (!country.equals("") ? country : "") + (!state.equals("") ? "/" + state : "") + "/" + city;
			
			url = new URL(URL + API + SERVICE + query + RESPONSE_FORMAT);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
	}

}

