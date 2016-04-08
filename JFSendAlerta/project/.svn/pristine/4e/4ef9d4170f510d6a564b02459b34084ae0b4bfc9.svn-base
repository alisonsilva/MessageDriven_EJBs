package br.com.laminarsoft.jazzforms.alerta.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;


@Controller
@SuppressWarnings("all")
public class PropertiesServiceController implements Serializable {
	private static final long serialVersionUID = 1L;
	private static PropertiesServiceController PROPS;
	
	public Properties prop;
	
	public PropertiesServiceController() {
		prop = new Properties();
		try {
			prop.load(this.getClass().getResourceAsStream("/cosist.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}				
	}
	
	public static PropertiesServiceController getInstance() {
		if (PROPS == null) {
			PROPS = new PropertiesServiceController();
		}
		return PROPS;
	}
	
	public String getProperty(String propriedade) {
		return prop.getProperty(propriedade);
	}
}
