package br.com.laminarsoft.jazzforms.process.menu.test;

import java.util.HashMap;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;

import br.com.laminarsoft.jazzit.processo.limpatrilho.type.JFMensagemPedido;

public class TesteBusinessProcess {

	public static void main(String[] args) {
		System.out.println("Loading process pm_GerenciamentoPedido.bpmn2");
		
		
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(new ClassPathResource("pm_GerenciamentoPedido.bpmn"), ResourceType.BPMN2);
		
		if (kbuilder.hasErrors()) {
			KnowledgeBuilderErrors errors = kbuilder.getErrors();

			for (KnowledgeBuilderError error : errors) {
				System.out.println(">>> Error:" + error.getMessage());

			}
			throw new IllegalStateException(">>> Knowledge couldn't be parsed! ");
		}		
		KnowledgeBase kbase = kbuilder.newKnowledgeBase();
		
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        
		
		JFMensagemPedido pedido = new JFMensagemPedido();
        pedido.dhEnvioPedido = System.currentTimeMillis() + "";
        pedido.idProcesso = "1";
        pedido.idUsuario = "asilva";
        
        Map<String, String> valores = new HashMap<String, String>();
        valores.put("key1", "vlr1");
        valores.put("key2", "vlr2");
        valores.put("key3", "vlr3");
        valores.put("key4", "vlr4");
        
        pedido.itensPedidos = valores;
		
		Map<String, Object> inputVariables = new HashMap<String, Object>();
		inputVariables.put("_pedido", pedido);
		
		ProcessInstance processInstance = ksession.createProcessInstance("br.com.laminarsoft.jazzforms.processo.gerenciamentopedido.pm_GerenciamentoPedido", inputVariables);
		pedido.idProcesso = String.valueOf(processInstance.getId());
		ksession.startProcessInstance(processInstance.getId());		
	}
	
	private static StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) {
		return kbase.newStatefulKnowledgeSession();
	}	

}
