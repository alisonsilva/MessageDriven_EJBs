package br.com.laminarsoft.jazzforms.process.connector.jfformin.handler;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

public class JFFormInHandler implements WorkItemHandler {
	private static Logger LOGGER;
	
	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String jndiName = (String)workItem.getParameter("Database JNDI");
		
		if (LOGGER == null) {
			LOGGER = LogManager.getLogger("br.com.laminarsoft.jazzforms.process.jfformin");	
		}		
		
		Connection conn = null;
		try {
			InitialContext iniCtx = new InitialContext();
			DataSource ds = (DataSource)iniCtx.lookup(jndiName);
			conn = ds.getConnection();
			
			String sessionId = (String)workItem.getParameter("SESSION_ID");
			long processInstanceId = workItem.getProcessInstanceId();
			
		} catch (Exception e) {
			LOGGER.error("Erro ao executar atualização no banco", e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
					LOGGER.error("Erro ao fechar conexão com o banco", ex);
				}
			}
		}
	}

}
