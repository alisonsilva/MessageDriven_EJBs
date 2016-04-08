package br.com.laminarsoft.jazzforms.process.connector.jfformout.handler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

import br.com.laminarsoft.jazzforms.jbpm.controller.WebServiceController;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoMensagem;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.BPInstanceVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.DeploymentVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.MensagemVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.ValorBPInstanceVO;

@SuppressWarnings("all")
public class JFFormOutHandler implements WorkItemHandler {
	private static Logger LOGGER;
	private static Properties PROP;
	
	private static final String USUARIO = "jbpm";
	private static final String SENHA = "123455";
	
	
	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String jndiName = (String)workItem.getParameter("databaseJNDI");
		String ugroups = workItem.getParameter("userGroups") == null ? null : (String)workItem.getParameter("userGroups");
		String users = workItem.getParameter("users") == null ? null : (String)workItem.getParameter("users");
		String processName = (String)workItem.getParameter("processName");
		String activityName = (String)workItem.getParameter("activityName");
		
		String corpoMensagem = (String)workItem.getParameter("txtCorpoMensagem");
		String tituloMensagem = (String)workItem.getParameter("txtTituloMensagem");
		String usuarioOrigem = workItem.getParameter("txtUsuarioOrigem") == null ? null : (String)workItem.getParameter("txtUsuarioOrigem");
		String strIdSessao = workItem.getParameter("sessionId") == null ? null : (String)workItem.getParameter("sessionId");
		String strIdProcesso = workItem.getParameter("processId") == null ? null : (String)workItem.getParameter("processId");
		
		Map<String, String> parametros = new HashMap<String, String>();
		
		if (workItem.getParameter("valores") != null &&  workItem.getParameter("valores") instanceof IJazzProcessOutParameters ) {
			parametros = ((IJazzProcessOutParameters)workItem.getParameter("valores")).getValores();
			strIdSessao = ((IJazzProcessOutParameters)workItem.getParameter("valores")).getSessionId();
			strIdProcesso = ((IJazzProcessOutParameters)workItem.getParameter("valores")).getProcessId();
		}
		
		String workItemName = workItem.getName();		
//		Long processInstanceId = strIdProcesso == null ? null : Long.valueOf(strIdProcesso);
//		Long sessionId = strIdSessao == null ? null : Long.valueOf(strIdSessao);
		
		if (LOGGER == null) {
			LOGGER = LogManager.getLogger("br.com.laminarsoft.jazzforms.process.jfformout");	
		}	
		
		Connection conn = null;
		try {
			InitialContext iniCtx = new InitialContext();
			DataSource ds = (DataSource)iniCtx.lookup(jndiName);
			conn = ds.getConnection();
			
			DeploymentVO depVo = getDeployment(processName, activityName, conn);
			if(depVo != null) {
				BPInstanceVO instVo = createBPInstance(depVo, strIdProcesso, strIdSessao, parametros, conn);
				MensagemVO mensagem = new MensagemVO();
				mensagem.conteudo = corpoMensagem;
				mensagem.data = new Date();
				mensagem.titulo = tituloMensagem;
				mensagem.deployment = depVo;
				mensagem.bpInstance = instVo;
				mensagem.remetenteUID = "jbpm";
				
				if(users != null && users.length() > 0) {
					StringTokenizer strtok = new StringTokenizer(users, ";");
					while(strtok.hasMoreTokens()) {
						String usr = strtok.nextToken();
						mensagem.destinatariosUids.add(usr);
					}
				}
				if(ugroups != null && ugroups.length() > 0) {
					StringTokenizer strtok = new StringTokenizer(ugroups, ";");
					while(strtok.hasMoreElements()) {
						String grp = strtok.nextToken();
						mensagem.grupos.add(grp);
					}
				}
				WebServiceController controller = WebServiceController.getInstance(LOGGER);
				InfoRetornoMensagem info = controller.novaMensagemBPM(mensagem, USUARIO, SENHA);
				if(info.codigo != 0) {
					LOGGER.error("Erro ao enviar nova mensagem para o bpm: " + info.mensagem);
				} else {
					LOGGER.info("Mensagem enviada com sucesso");
				}
			} else {
				LOGGER.error("Deployment não encontrado: " + processName +", " + workItemName);
			}
			
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
		manager.completeWorkItem(workItem.getId(), null);
	}

	private DeploymentVO getDeployment(String processName, String workItemName, Connection connection) {
		DeploymentVO vo = null;
		
		String slctDeployment = "select id, ativo, removido, dh_publicacao " +
				"from deployment where nome_processo_negocio = ? and nome_atividade_negocio = ?";
		try {
			PreparedStatement pstmt = connection.prepareStatement(slctDeployment);
			pstmt.setString(1, processName);
			pstmt.setString(2, workItemName);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				vo = new DeploymentVO();
				vo.ativo = rs.getBoolean("ativo");
				vo.id = rs.getLong("id");
				vo.removido = rs.getBoolean("removido");
				Timestamp dhPublicado = rs.getTimestamp("dh_publicacao"); 
				vo.dhPublicacao = dhPublicado != null ? dhPublicado.getTime() : 0; 
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			LOGGER.error("Erro ao recuperar deployment do banco de dados", e);
		}
		
		return vo;
	}
	
	private BPInstanceVO createBPInstance(DeploymentVO depVo, String processInstanceId, String sessionId, Map<String, String> parametros, Connection conn) {
		BPInstanceVO bpinstance = null;
		try {
			PreparedStatement pstmt = 
					conn.prepareStatement("insert into BP_INSTANCE (deployment_id, process_instance_id, session_id, idx) values (?,?,?,1)", 
							Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1, depVo.id);
			pstmt.setString(2, processInstanceId);
			pstmt.setString(3, sessionId);
			pstmt.executeUpdate();
			
			ResultSet rsres = pstmt.getGeneratedKeys();
			if(rsres.next()) {
				bpinstance = new BPInstanceVO();
				bpinstance.deploymentId = depVo.id;
				bpinstance.id = rsres.getLong(1);
				bpinstance.processInstanceId = processInstanceId;
				bpinstance.deploymentVo = depVo;
				PreparedStatement pstmtPar = conn.prepareStatement("insert into valor_bp_instance " +
						"(bp_instance_id, id_campo, valor_campo, dh_alteracao, idx) " +
						" values (?,?,?,?,1)");
				
				Iterator<String> iter = parametros.keySet().iterator();
				Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
				while(iter.hasNext()) {
					String chave = iter.next();
					String valor = parametros.get(chave);
					pstmtPar.clearParameters();
					pstmtPar.setLong(1, bpinstance.id);
					pstmtPar.setString(2, chave);
					pstmtPar.setString(3, valor);
					pstmtPar.setTimestamp(4, timeStamp);
					pstmtPar.executeUpdate();
					
					ValorBPInstanceVO vlrBPInst = new ValorBPInstanceVO();
					vlrBPInst.dhAlteracao = new Date(timeStamp.getTime());
					vlrBPInst.idCampo = chave;
					vlrBPInst.valorCampo = valor;
					
					bpinstance.valores.add(vlrBPInst);
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Erro ao inserir nova instancia de business process", e);
		}
		return bpinstance;
	}
}
