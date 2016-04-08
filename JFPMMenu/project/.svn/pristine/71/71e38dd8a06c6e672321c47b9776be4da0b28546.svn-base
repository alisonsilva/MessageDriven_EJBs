package br.com.laminarsoft.jazzforms.process.menu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.impl.EnvironmentFactory;
import org.drools.io.Resource;
import org.drools.io.impl.ClassPathResource;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jboss.ejb3.annotation.Pool;
import org.jboss.logging.Logger;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import br.com.laminarsoft.jazzforms.process.connector.jfformout.handler.JFFormOutHandler;
import br.com.laminarsoft.jazzforms.process.menu.handlers.WIHNotificacaoAndamentoHandler;
import br.com.laminarsoft.jazzit.processo.limpatrilho.type.JFMensagemPedido;


/**
 * Message-Driven Bean implementation class for: TratamentoErrosSubscriber
 */
@MessageDriven(name = "JFPMMenuResultadoPedidoMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/limpaTrilhoPedidoProcQ"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
@Pool("error-model-max-pool")
public class JFPMMenuResultadoPedidoMDB implements MessageListener {

	/**
	 * The internal ksession where the processes will run.
	 */
	protected static StatefulKnowledgeSession session;
	public static Logger LOGGER;
	public static Properties PROPS;
	
	private PoolingDataSource ds = new PoolingDataSource();

	/**
	 * Default constructor.
	 */
	public JFPMMenuResultadoPedidoMDB() {
        ds.setUniqueName("jdbc/testDS1");
	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {
		try {
			if (message != null && message instanceof ObjectMessage && ((ObjectMessage)message).getObject() instanceof JFMensagemPedido) {
				JFMensagemPedido pedido = ((JFMensagemPedido)((ObjectMessage)message).getObject());
				if (session == null) {
					initializeSession(Integer.valueOf(pedido.getSessionId()));
				}
				int processid = Integer.parseInt(pedido.getProcessId());
				
				
				setupWorkItemHandlers();
				if (LOGGER == null) {
					LOGGER = Logger.getLogger("br.com.laminarsoft.jazzforms.menu");	
				}
				if (PROPS == null) {
					PROPS = new Properties();
					try {
						PROPS.load(this.getClass().getResourceAsStream("/config.properties"));
					} catch (IOException e) {
						LOGGER.error("Erro ao carregar arquivo com configurações", e);
						return;
					}
				}
		
				ProcessInstance processIntance = session.getProcessInstance(processid);
				processIntance.signalEvent("Message-mensagem_pedido", pedido);
				
//				session.signalEvent("Message-mensagem_pedido", pedido, processid);
//				session.dispose();		
//				session = null;
			} else {
				LOGGER.warn("A mensagem será descartada, pois não é do formato: br.com.laminarsoft.jazzforms.jbpm.archetypes.JFMensagemPedido");
			}
		} catch (JMSException e) {
			LOGGER.error("Erro na recuperação da mensagem enviada: " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Erro genérico na recuperação do processo: " + e.getMessage());
		}
	}

	private void initializeSession(int sessionid) {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(new ClassPathResource("pm_limpatrilho2.bpmn2"), ResourceType.BPMN2);
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		
		for (Map.Entry<Resource, ResourceType> entry : this.getResources().entrySet()) {
			kbuilder.add(entry.getKey(), entry.getValue());
		}

		if (kbuilder.hasErrors()) {
			KnowledgeBuilderErrors errors = kbuilder.getErrors();

			for (KnowledgeBuilderError error : errors) {
				System.out.println(">>> Error:" + error.getMessage());

			}
			throw new IllegalStateException(">>> Knowledge couldn't be parsed! ");
		}


		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        Environment env = EnvironmentFactory.newEnvironment();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("JFPMMenu_PU");
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        env.set(EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager());
        session= JPAKnowledgeService.loadStatefulKnowledgeSession(sessionid, kbase, null, env);
	}

	public void setupWorkItemHandlers() {
		session.getWorkItemManager().registerWorkItemHandler("JFFormOut",
				new JFFormOutHandler());
		session.getWorkItemManager().registerWorkItemHandler(
				"JFNotificaAndamento", new WIHNotificacaoAndamentoHandler());
	}

	private Map<Resource, ResourceType> getResources() {
		// return the resources used by this test.
		Map<Resource, ResourceType> resources = new HashMap<Resource, ResourceType>();
//		resources.put(ResourceFactory
//				.newClassPathResource("TratamentoErrosProcess.bpmn2"),
//				ResourceType.BPMN2);
		return resources;
	}
}
