package br.com.laminarsoft.jazzforms.report.test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.client.RestTemplate;



public class TesteBase {

	protected static ClassPathXmlApplicationContext springFactory = null;

	protected RestTemplate restTemplate;
	protected Client client;

	@BeforeClass
	@SuppressWarnings("all")
	public static void initClass() {
		try {
			springFactory = new ClassPathXmlApplicationContext(
					new String[] { "applicationContext-persist-test.xml" });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Before
	public void inicializaObjeto() { 
		try {
			restTemplate = (RestTemplate)springFactory.getBean("restTemplate");
			client = ClientBuilder.newClient();		
		} catch (BeansException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked" })
	protected <T> T getTargetObject(Object proxy, Class<T> targetClass){
//		if (AopUtils.isJdkDynamicProxy(proxy)) {
//			return (T) ((Advised) proxy).getTargetSource().getTarget();
//		} else {
//			return (T) proxy; // expected to be cglib proxy then, which is
//								// simply a specialized class
//		}
		
		return null;
	}

	@AfterClass
	public static void destroyClass() {
//		springFactory.close();
	}

}
