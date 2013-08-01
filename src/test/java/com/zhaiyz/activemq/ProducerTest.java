package com.zhaiyz.activemq;

import javax.annotation.Resource;

import junit.framework.Assert;

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
		for (int i = 1; i < 10; i++) {
			String request = "第" + i + "条信息";
			Assert.assertEquals(request + "的应答！", producer.sendMessage(request));
		}
	}

}
