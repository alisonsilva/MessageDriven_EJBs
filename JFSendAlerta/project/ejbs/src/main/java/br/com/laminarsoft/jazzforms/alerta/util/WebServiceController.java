package br.com.laminarsoft.jazzforms.alerta.util;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.sun.jersey.api.client.Client;

@Controller
public class WebServiceController {
	private static WebServiceController CONTROLLER;
	private Client client;
	
	@Autowired private PropertiesServiceController properties;
	
	private WebServiceController() {
		client = Client.create();
		client.setConnectTimeout(120*1000);
		client.setReadTimeout(120*1000);
		
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new  HostnameVerifier() {
			
			@Override
			public boolean verify(String hostName, SSLSession sslSession) {
				if(hostName.equalsIgnoreCase("localhost")) {
					return true;
				}
				return false;
			}
		});
	}

	public static WebServiceController getInstance() {
		if(CONTROLLER  == null) {
			CONTROLLER = new WebServiceController();
			if(CONTROLLER.properties == null) {
				CONTROLLER.properties = PropertiesServiceController.getInstance();
			}
		}
		return CONTROLLER;
	}
	
	

}
