package br.com.laminarsoft.jazzforms.process.menu.test;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import br.com.laminarsoft.jazzforms.persistencia.model.ProcessModel;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoProcessModel;


@SuppressWarnings("all")
public class ProcessModelServiceTest extends TesteBase {

	@Ignore
	public void findById() {
//		ProcessModel pmodel = new ProcessModel();
//		pmodel.setProcessId("br.com.laminarsoft.jazzforms.processo.gerenciamentopedido.gerenciamentopedido.pm_GerenciamentoPedido");
//		pmodel.setProcessName("pm_GerenciamentoPedido");
//		
//		ProcessNode node = new ProcessNode();
//		node.setNodeId("task_1");
//		node.setNodeName("node 1");
//		pmodel.getNodes().add(node);
//		
//		node = new ProcessNode();
//		node.setNodeId("task_2");
//		node.setNodeName("node 2");
//		pmodel.getNodes().add(node);
//		
//		node = new ProcessNode();
//		node.setNodeId("task_3");
//		node.setNodeName("node 3");
//		pmodel.getNodes().add(node);
//		
//		try {
//			JAXBContext jaxbContext = JAXBContext.newInstance(ProcessModel.class);
//			Marshaller marshaller = jaxbContext.createMarshaller();
//			
//			StringWriter strwriter = new StringWriter();
//			marshaller.marshal(pmodel, strwriter);
//			
//			System.out.println(strwriter);
//		} catch (JAXBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	
	@Test
	public void insertProcessModel() {
		ProcessModel processModel = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ProcessModel.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			InputStream f = this.getClass().getResourceAsStream("/pm_limpatrilhoDescriptor.xml");
			
			processModel = (ProcessModel)jaxbUnmarshaller.unmarshal(f);
			
			
			byte[] b = Files.readAllBytes((new File("C:\\trabalho\\LaminarSoft\\JazzForms\\JFPMMenu\\project\\ejbs\\src\\main\\resources\\pictures\\pm_limpatrilho2.jpg")).toPath());
			processModel.setProcessImage(b);
			
        } catch (Exception e) {
        	e.printStackTrace();
        	return;
        }

		
		InfoRetornoProcessModel info = null;
		try {
			Client client = ClientBuilder.newClient();
			info = client.target("http://localhost:8080/jazzforms/servicos/processModelService/processmodel/updateModel" + getParametrosExtenso())
					.request()
					.post(Entity.entity(processModel, MediaType.APPLICATION_XML), InfoRetornoProcessModel.class);			
			
//			WebResource webResource = client.resource("http://localhost:8080/jazzforms/servicos/processModelService/processmodel/updateModel" + getParametrosExtenso());
//			webResource.accept(MediaType.APPLICATION_XML);
//			info = webResource.post(InfoRetornoProcessModel.class, processModel);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		

		

		Assert.assertNotNull("O process model não foi encontrado", info);
		Assert.assertTrue("O process model não foi encontrado", info.codigo == 0);
		
	}
	
	private String getParametrosExtenso() {
		String parametros = "?" + "j_username=asilva";
		parametros += "&" + "j_password=123455";
		parametros += "&" + "timestamp=" + System.currentTimeMillis();
		return parametros;
	}
	
	
}
