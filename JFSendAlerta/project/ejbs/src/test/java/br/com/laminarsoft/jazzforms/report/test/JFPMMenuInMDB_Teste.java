/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.laminarsoft.jazzforms.report.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.junit.Ignore;
import org.junit.Test;

import br.com.laminarsoft.jazzforms.logging.types.InfoEnvioRelatorioMSG;


public class JFPMMenuInMDB_Teste {
    private static final Logger log = Logger.getLogger(JFPMMenuInMDB_Teste.class.getName());

    // Set up all the default values
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_DESTINATION_1 = "jms/queue/reportQ";
    private static final String DEFAULT_MESSAGE_COUNT = "1";
    private static final String DEFAULT_USERNAME = "jmsuser";
    private static final String DEFAULT_PASSWORD = "jm$_p@$$w0rd";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    //private static final String PROVIDER_URL = "remote://localhost:4447";
    private static final String PROVIDER_URL = "remote://tjsw701v.tribunal.tjdft.jus.br:4447";
    
    
    
    @Test
    public void teste_1() throws Exception {

        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        Destination destination = null;
        ObjectMessage message = null;
        Context context = null;
// ag. 43133 cc. 7523x
        try {
            // Set up the context for the JNDI lookup
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, PROVIDER_URL);
            env.put(Context.SECURITY_PRINCIPAL, DEFAULT_USERNAME);
            env.put(Context.SECURITY_CREDENTIALS, DEFAULT_PASSWORD);
            env.put("jboss.naming.client.ejb.context", true);
            env.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
            env.setProperty("java.naming.factory.url.pkgs", "org.jboss.naming");
            context = new InitialContext(env);

            // Perform the JNDI lookups
            String connectionFactoryString = System.getProperty("connection.factory", DEFAULT_CONNECTION_FACTORY);
            System.out.println("Attempting to acquire connection factory \"" + connectionFactoryString + "\"");
            connectionFactory = (ConnectionFactory) context.lookup(connectionFactoryString);
            System.out.println("Found connection factory \"" + connectionFactoryString + "\" in JNDI");

            String destinationString = System.getProperty("destination", DEFAULT_DESTINATION_1);
            System.out.println("Attempting to acquire destination \"" + destinationString + "\"");
            destination = (Destination) context.lookup(destinationString);
            System.out.println("Found destination \"" + destinationString + "\" in JNDI");

            // Create the JMS connection, session, producer, and consumer
            connection = connectionFactory.createConnection(DEFAULT_USERNAME, DEFAULT_PASSWORD);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            connection.start();

            int count = Integer.parseInt(System.getProperty("message.count", DEFAULT_MESSAGE_COUNT));
            
            InfoEnvioRelatorioMSG msg = new InfoEnvioRelatorioMSG();
            msg.assunto = "Email de teste";
            msg.conteudo = "Esse é um email para teste de envio de relatório";
            msg.destinatario = "alison.silva@tjdft.jus.br";
            msg.loginUsuario = "asilva";
            msg.nomeRelatorio = "relatorio_juizados";
            msg.parametrosPesquisa.put("codigoCircunscricao", "1");
            msg.parametrosPesquisa.put("codigoOrgao", "1");
            msg.parametrosPesquisa.put("codigoVara", "1");
            msg.parametrosPesquisa.put("dataInicial", "2014-07-31");
            msg.parametrosPesquisa.put("dataFinal", "2014-12-31");
            
            System.out.println("Sending " + count + " messages with content: " + msg);

            // Send the specified number of messages
            for (int i = 0; i < count; i++) {
                message = session.createObjectMessage(msg);
                producer.send(message);
            }

        } catch (Exception e) {
        	e.printStackTrace();
            log.severe(e.getMessage());
            throw e;
        } finally {
            if (context != null) {
                context.close();
            }

            // closing the connection takes care of the session, producer, and consumer
            if (connection != null) {
                connection.close();
            }
        }
    }
   
}

