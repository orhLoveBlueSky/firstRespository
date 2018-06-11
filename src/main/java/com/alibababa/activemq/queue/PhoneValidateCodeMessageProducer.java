package com.alibababa.activemq.queue;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import org.apache.activemq.command.ActiveMQQueue;



/**
 * 点对点消息生产者
 *
 */
@Component
public class PhoneValidateCodeMessageProducer {
    private Logger logger = LoggerFactory.getLogger(PhoneValidateCodeMessageProducer.class);
    
    /**
     * spring消息发送模版
     */
    @Resource(name="queueJmsTemplate")
    private JmsTemplate jmsTemplate;

    /**
     * 消息目的地
     */
    @Resource(name = "queueDestination")
    private ActiveMQQueue defaultDestination;

    /**
     * 发送消息
     * 

     *
     */
    public void sendQueueMessage(String msg) {
        // getJmsTemplate().convertAndSend(personInfo);//如果配置文件中指定了目的地，可以使用这句话发送消息。
        logger.info("{}","QueueMessageProducer 消息生产者开始发送消息...");

//          <!-- 点对点消息目的地 -->
//    <bean id="queueDestination" class="org.apache.activemq.command.ActiveMQQueue">
//        <!-- 名称可以随意命名 -->
//        <constructor-arg value="MY.queue" />
//    </bean>

        ApplicationContext context=new FileSystemXmlApplicationContext("classpath:applicationContext.xml");
//        student student=context.getBean("student",student.class);
        JmsTemplate jmsTemplate = (JmsTemplate)context.getBean("queueJmsTemplate");
        ActiveMQQueue destination = (ActiveMQQueue)context.getBean("queueDestination");
        //目的地、模版，都是通过注入方式引入，并不是通过配置bean的方式引入.
        if(jmsTemplate==null){
            System.out.println("jmdTemplate is null");
        }
        jmsTemplate.convertAndSend(destination, msg);
    }

    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public ActiveMQQueue getDefaultDestination() {
        return defaultDestination;
    }

    public void setDefaultDestination(ActiveMQQueue defaultDestination) {
        this.defaultDestination = defaultDestination;
    }

}
