package br.com.laminarsoft.jazzforms.process.menu.handlers;

import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

import br.com.laminarsoft.jazzforms.jbpm.archetypes.ErrorMessage;
import br.com.laminarsoft.jazzforms.process.menu.JFPMMenuInMDB;

public class ErrorLogHandler implements WorkItemHandler{

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		ErrorMessage errorMessage = (ErrorMessage)workItem.getParameter("mensagem");

		for (String mensagem : errorMessage.messages) {
			JFPMMenuInMDB.LOGGER.error("c�digo: " + errorMessage.codigo + " - mensagem: " + mensagem);
		}
		
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("msgSaida", "sucesso");
		manager.completeWorkItem(workItem.getId(), results);		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		
	}

	
}
