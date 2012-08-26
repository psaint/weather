package com.swietoslawski.weather.server;

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
import com.swietoslawski.weather.client.WeatherService;
import com.swietoslawski.weather.shared.WeatherException;
import com.swietoslawski.weather.shared.WeatherXML;
import com.swietoslawski.weather.shared.JSO.Weather;

public class WeatherServiceImpl extends RemoteServiceServlet implements
		WeatherService {

	private static final long serialVersionUID = 1L;
	private final String weatherURL = "http://www.google.com/ig/api?";
	
	@Override
	public Weather getWeather(String city)
			throws WeatherException {

		// Validation of the code in client side does not guarantee that the such
		// validated input was not tampered after by man in the middle attack 
		// For AJAX application the input should be also re-validated on server side!!!
		// TODO Add some validation
		
		Document weatherDOM = null;
		
		
		// TODO Replace all this exception catch with one generic 
		try {
			weatherDOM = getWeatherDOM(city);
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
		
		WeatherXML weatherXML = getWeather(weatherDOM);
		
		return weatherXML.asWeather();
	}
	
	private Document getWeatherDOM(String city) 
		throws IOException, ParserConfigurationException, SAXException {
		
		// TODO This fetching of XML and parsing is a joke. I have to have a
		// look in RequestBuilder
		
		String url_string = getWeatherUrl(city);
		URL url = new URL(url_string);
		
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
	     
	     // Google API will block requests (randomly if this string is not set)
	     // I used to get error:
	     //		Unsupported API
	     // Let's pretend that the request are coming from Chrome
	     conn.setRequestProperty( "User-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_4) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1");
		 
		 BufferedReader rd = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
		 String line;

		 while ((line = rd.readLine()) != null)
		 {
		  responseBuilder.append(line + '\n');
		 }
		
		//Document doc = builder.parse(in);
		StringReader str = new StringReader(responseBuilder.toString());
		InputSource in = new InputSource(str);
		
		// Reading XML file
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(in);
		
		return doc;
	}

	private WeatherXML getWeather(Document weatherRSS) {
		
		WeatherXML weatherXML = new WeatherXML(weatherRSS);
		
		// We need to extract info from WeatherXML and wrap it 
		// into serializable class Weather that will be simple to 
		// use and work with on client side
		
		
		return weatherXML;
	}
	
	private String getWeatherUrl(String city) {		
		return weatherURL + "weather=" + city;
	}

}
