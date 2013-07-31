package com.zhaiyz.activemq;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * 消息消费者
 * 
 * @author zhaiyz
 */
public class Consumer implements MessageListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);
	
	private JmsTemplate jmsTemplate;
	
	/**
	 * @return the jmsTemplate
	 */
	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	/**
	 * @param jmsTemplate the jmsTemplate to set
	 */
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			try {
				LOGGER.info(textMessage.getText());
				Destination destination = textMessage.getJMSReplyTo();
				final String jmsCorrelationID = textMessage.getJMSCorrelationID();
				jmsTemplate.send(destination, new MessageCreator() {
					
					@Override
					public Message createMessage(Session session) throws JMSException {
						Message msg = session.createTextMessage("由消息消费者返回的信息");
						msg.setJMSCorrelationID(jmsCorrelationID);
						return msg;
					}
				});
			} catch (JMSException e) {
				LOGGER.error("接收信息出错", e);
			}
		}
	}
}
