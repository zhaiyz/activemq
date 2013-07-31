package com.zhaiyz.activemq;

import java.util.UUID;

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
 * 消息生产者
 * 
 * @author zhaiyz
 */
public class Producer implements MessageListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

	private JmsTemplate jmsTemplate;

	private Destination requestDestination;
	
	private Destination replyDestination;

	/**
	 * @return the jmsTemplate
	 */
	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	/**
	 * @param jmsTemplate
	 *            the jmsTemplate to set
	 */
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	/**
	 * @return the requestDestination
	 */
	public Destination getRequestDestination() {
		return requestDestination;
	}

	/**
	 * @param requestDestination the requestDestination to set
	 */
	public void setRequestDestination(Destination requestDestination) {
		this.requestDestination = requestDestination;
	}

	/**
	 * @return the replyDestination
	 */
	public Destination getReplyDestination() {
		return replyDestination;
	}

	/**
	 * @param replyDestination the replyDestination to set
	 */
	public void setReplyDestination(Destination replyDestination) {
		this.replyDestination = replyDestination;
	}

	public void sendMessage(final String message) {
		jmsTemplate.send(requestDestination, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				Message msg = session.createTextMessage(message);
				msg.setJMSCorrelationID(UUID.randomUUID().toString());
				msg.setJMSReplyTo(replyDestination);
				return msg;
			}
		});
	}

	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			try {
				LOGGER.info(textMessage.getText());
			} catch (JMSException e) {
				LOGGER.error("接收信息出错", e);
			}
		}
	}
}
