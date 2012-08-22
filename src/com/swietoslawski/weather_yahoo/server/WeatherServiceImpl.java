package com.swietoslawski.weather_yahoo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.swietoslawski.weather_yahoo.client.WeatherService;
import com.swietoslawski.weather_yahoo.client.ZipValidator;
import com.swietoslawski.weather_yahoo.shared.Weather;
import com.swietoslawski.weather_yahoo.shared.WeatherException;
import com.swietoslawski.weather_yahoo.shared.WeatherWrapper;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

public class WeatherServiceImpl extends RemoteServiceServlet implements
		WeatherService {

	private static final long serialVersionUID = 1L;
//	private static Log log = LogFactory.getLog(WeatherServiceImpl.class);
//	private static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	
	@Override
	public WeatherWrapper[] getWeatherHtml(String zip, boolean isCelcius)
			throws WeatherException {

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
		
		WeatherWrapper[] weather = getWeatherHtml(rss);
		return weather;
	}
	
	private Document getWeatherRssDocument(String zip, boolean isCelcius) 
		throws IOException, ParserConfigurationException, SAXException {
		
		String url_string = getWeatherUrl(zip, isCelcius);
		URL url = new URL(url_string);
		
		// Reading XML file
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		     
		DocumentBuilder builder = factory.newDocumentBuilder();
		
		// The below which should in theory be feed to DocumentBuilder.parse
		// results in error thus we need to do this ridiculous
		// implicity URL connection opening, type casting to HTTP 
		// connection and then manually read content of the
		// returned xml into string then convert string to xml
		// which is another joke (3 classes!!?!!) and finally
		// we are able to pass this as InputSource type to
		// DocumentBuilder.parse ... or shall I say farce
		//InputStream in = url.openStream();
		
		
		StringBuilder responseBuilder = new StringBuilder();
		
		 // Create a URLConnection object for a URL
		 URLConnection conn = url.openConnection();
		 HttpURLConnection httpConn;
		 httpConn = (HttpURLConnection)conn;
		 
		 // Set up a request.
	     conn.setConnectTimeout( 10000 );    // 10 sec
	     conn.setReadTimeout( 10000 );       // 10 sec
	     //conn.setInstanceFollowRedirects( true );
	     
	     // Google API will block requests (randomly if this string is not set)
	     // I used to get error of Not supported APIs?!!?
	     conn.setRequestProperty( "User-agent", "spider" );
		 
		 BufferedReader rd = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
		 String line;

		 while ((line = rd.readLine()) != null)
		 {
		  responseBuilder.append(line + '\n');
		 }
		
		//Document doc = builder.parse(in);
		StringReader str = new StringReader(responseBuilder.toString());
		InputSource in = new InputSource(str);
		Document doc = builder.parse(in);
		
		return doc;
	}

	private WeatherWrapper[] getWeatherHtml(Document doc) {
		
		Weather weather = new Weather(doc);
		WeatherWrapper[] forecasts = new WeatherWrapper[5];
		
		// We need to convert Weather object into nice WeatherWrappers
		// bundle them as array and send them back
		
		// City is always the same for each day we just add it for the sake
		// of keeping the WeatherWrapper made of objects with the same fields
		final String city = weather.getCity();
		
		String temp;
		String condition;
		String day;
		
		// We'll have 
		for (int i = 0; i < forecasts.length - 1; i++) {
			// Because results returned from Google API suck big we need to 
			// loop through them bearing in mind that in first loop we 
			// pull elements from Weather  forecastInformation and currentConditions 
			// while subsequent iterations are relying on forecastConditions
//			if (i == 0) {
//				temp = weather.getTempF();
//				condition = weather.getIconCondition();
//				day = weather.getDay();
//			}
			//else {
				temp = weather.getTemp(i);
				condition = weather.getIconCondition(i);
				day = weather.getDay(i);
			//}
			
			WeatherWrapper forecast = new WeatherWrapper(city, temp , condition, day);
			forecasts[i] = forecast;
		}
		
		return forecasts;
	}
	
	private String getWeatherUrl(String city, boolean isCelcius) {
		final String base = "http://www.google.com/ig/api?";
		return base + "weather=" + city;
	}

}
