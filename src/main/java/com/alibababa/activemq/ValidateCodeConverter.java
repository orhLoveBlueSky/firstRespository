package com.alibababa.activemq;

import com.alibababa.activemq.PersonInfo;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/**
 * 消息转换器
 * 
 *
 */
@Component("validateCodeConverter")
public class ValidateCodeConverter implements MessageConverter {

    /**
     * 将发送的实体bean对象封装为Message消息
     *  对已经实现MessageListener接口的消费者产生作用
     *
     *
     *
     * @param obj
     * @param session
     * @return
     * @throws JMSException
     * (non-Javadoc)
     * @see MessageConverter#toMessage(Object, Session)
     */
    public Message toMessage(Object obj, Session session) throws JMSException {
        System.out.println("validateCodeConverter");
        System.out.println("将实体bean对象转换为Message消息: " + obj);

        //发送的消息对象类型是PersonInfo
        if (obj instanceof String) {
            System.out.println("obj instanceof String");
            ActiveMQObjectMessage msg = (ActiveMQObjectMessage) session.createObjectMessage();
            msg.setObject((String) obj);
            return msg;
        } else {//这里可以指定其他的消息类型
            System.out.println("Object:[" + obj + "] is not a instance of String.");
            throw new JMSException("Object:[" + obj + "] is not a instance of PersonInfo.");
        }
    }

    /**
     * 将消息对象转换为对应的实体bean并返回
     *  只对未实现MessageListener接口的消息消费者产生作用，其他的已经实现MessageListener接口的，不会执行该方法
     *
     *
     * 日期：2015年9月28日 上午10:46:59
     * @param message
     * @return
     * @throws JMSException
     * (non-Javadoc)
     * @see MessageConverter#fromMessage(Message)
     */
    public Object fromMessage(Message message) throws JMSException {
        System.out.println();
        System.out.println("执行validateCodeMQMessageConverter消息转换器 ,message信息如下: " + message.toString());
        if (message instanceof ObjectMessage) {
            ObjectMessage oMsg = (ObjectMessage) message;
            if (oMsg instanceof ActiveMQObjectMessage) {
                ActiveMQObjectMessage aMsg = (ActiveMQObjectMessage) oMsg;
                try {

                    String msg = (String) aMsg.getObject();

                    //这里可以对实体bean的属性进行其他处理
                    System.out.println("对实体bean进行二次加工后的结果:" + msg);

                    //对消息二次加工之后，返回最终的消息
                    return msg;

                } catch (Exception e) {
                    System.out.println("Message:[" + message + "] is not a instance of personInfo!");
                    throw new JMSException("Message:[" + message + "] is not a instance of personInfo...");
                }
            } else {
                System.out.println("Message:[" + message + "] is not " + "a instance of ActiveMQObjectMessage[personInfo].");
                throw new JMSException("Message:[" + message + "] is not " + "a instance of ActiveMQObjectMessage[personInfo].");
            }
        } else {
            System.out.println("Message:[" + message + "] is not a instance of ObjectMessage.");
            throw new JMSException("Message:[" + message + "] is not a instance of ObjectMessage.");
        }
    }

    
}
