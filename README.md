# Message Driven Beans
These are all message driven beans that work as separated services. They are deployed on top of a webserver that makes use of a JMS queue to receive data and being activated.

## JFAuthenticacao
Provides authentication functionalities

## JFError
Deals with errors throughout the JazzIT product. Particularly it logs errors on predefined log files or database.

## JFSendAlerta
Sends alert messages to android and iPhone/iPad devices

# JFSendEmail
Sends e-mail message to provided mail addresses

# JFSendReport
Sends report according to a Jasper Report template previously developed and provided. The report type and its parameters are sent with the message.

# JFFormIn, JFFormOut, JFLogging, JFPMMenu

These are all components to deal with messages sent through JBPM. They are just for testint purposes.


