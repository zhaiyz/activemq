package com.zhaiyz.activemq;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

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
	
	private static ConcurrentMap<String, ReplyMessage> concurrentMap = new ConcurrentHashMap<String, ReplyMessage>();

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

	public String sendMessage(final String message) {
		ReplyMessage replyMessage = new ReplyMessage();
		final String correlationID = UUID.randomUUID().toString();
		concurrentMap.put(correlationID, replyMessage);
		
		jmsTemplate.send(requestDestination, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				Message msg = session.createTextMessage(message);
				msg.setJMSCorrelationID(correlationID);
				msg.setJMSReplyTo(replyDestination);
				return msg;
			}
		});
		
		try {
			boolean isReceiveMessage = replyMessage.getSemaphore().tryAcquire(10, TimeUnit.MILLISECONDS);
			
			ReplyMessage result = concurrentMap.get(correlationID);
			
			if (isReceiveMessage && null != result) {
				Message msg = result.getMessage();
				if (null != msg) {
					TextMessage textMessage = (TextMessage) msg;
					return textMessage.getText();
				}
			}
			
		} catch (InterruptedException e) {
			LOGGER.error("中断出错", e);
		} catch (JMSException e) {
			LOGGER.error("获取信息出错", e);
		}
		return null;
	}

	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			try {
				concurrentMap.get(textMessage.getJMSCorrelationID()).setMessage(textMessage);
				concurrentMap.get(textMessage.getJMSCorrelationID()).getSemaphore().release();
				LOGGER.info(textMessage.getText());
			} catch (JMSException e) {
				LOGGER.error("接收信息出错", e);
			}
		}
	}
}
