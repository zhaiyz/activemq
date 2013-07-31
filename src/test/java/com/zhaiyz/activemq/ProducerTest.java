package com.zhaiyz.activemq;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 消息生产者测试类
 * 
 * @author zhaiyz
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-jms.xml")
public class ProducerTest {
	
	@Resource
	private Producer producer;

	/**
	 * @return the producer
	 */
	public Producer getProducer() {
		return producer;
	}

	/**
	 * @param producer the producer to set
	 */
	public void setProducer(Producer producer) {
		this.producer = producer;
	}

	/**
	 * Test method for
	 * {@link com.zhaiyz.activemq.Producer#sendMessage(java.lang.String)}.
	 */
	@Test
	public void testSendMessage() {
		producer.sendMessage("Hello, world!");
	}

}
