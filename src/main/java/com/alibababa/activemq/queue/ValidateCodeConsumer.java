package com.alibababa.activemq.queue;

import javax.jms.JMSException;


import com.alibababa.chuanglanMessageService.SmsSend;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;


/**
 * 点对点消息消费者3 —— 未实现MessageListener接口，通过在spring-activemq-ptp.xml文件中配置进行相应的处理
 * 
 *
 */
public class ValidateCodeConsumer {
    private Logger logger = LoggerFactory.getLogger(ValidateCodeConsumer.class);
    /**
     * receive方法的参数类型是PersonInfo，为何呢？
     *  因为：UserMessageConverter类中的toMessage方法，已经将消息转换为PersonInfo类型了，所以，这里只需要直接指定消息实体bean的类型即可.与QueueMessageReceiver2、QueueMessageReceiver还是有些不同的.
     * 

     * @param msg
     * @throws JMSException
     */
    public void receive(String msg) throws JMSException {
        System.out.println("validateCodeConsumer");
        
        String[] msgs = msg.split(",");
        logger.info("【消费者ValidateCodeConsumer】收到queue的消息—->phone: "+ msgs[0]+", code: " + msgs[1]);
        System.out.println("【消费者ValidateCodeConsumer】收到queue的消息—->phone: "+ msgs[0]+", code: " + msgs[1]);

        try {
            SmsSend.send(msgs[0],msgs[1]);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
