package com.zhaiyz.activemq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息消费者
 * 
 * @author zhaiyz
 */
public class Consumer implements MessageListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);
	
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
