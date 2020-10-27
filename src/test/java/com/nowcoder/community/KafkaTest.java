package com.nowcoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTest {

    @Autowired
    private kafkaProducer kafkaProducer;

    @Test
    public void testKafka(){
        kafkaProducer.sendMessage("test","你好，吃了吗");
        kafkaProducer.sendMessage("test","吃月饼了吗");
        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

@Component
class kafkaProducer{
    @Autowired
    private KafkaTemplate kafkaTemplate;
    //生产者  传输消息
    public void sendMessage(String topic,String content){
           kafkaTemplate.send(topic,content);
    }
}

@Component
class kafkaConsumer{
    /**
     *   @KafkaListener(topics = {"test"})  消费者 会自动监听主题里的消息 查看偏移量 是否有为被读取的数据  若有就立刻读取
     * @param record      将读取到的数据封装成ConsumerRecord类
     */
    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord record){
        System.out.println(record.value());
    }
}