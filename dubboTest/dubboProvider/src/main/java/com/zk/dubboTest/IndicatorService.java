package com.zk.dubboTest;

import com.zk.dubboTest.dto.UserDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@RestController
public class IndicatorService {
    private Logger LOG = LoggerFactory.getLogger(IndicatorService.class);

    private final KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private CacheUtils cacheUtils;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @RequestMapping("sendMsg")
    public String sendMsg(){
        UserDTO u = new UserDTO();
        u.setName("zk");
        u.setSex("man");
        redisTemplate.opsForValue().set(u.getName()+"_str",u.getSex());
        redisTemplate.opsForValue().set(u.getName(),u);
        UserDTO a = (UserDTO)redisTemplate.opsForValue().get(u.getName());
        String sex = (String)redisTemplate.opsForValue().get(u.getName()+"_str");
        redisTemplate.delete(u.getName());
        UserDTO b = (UserDTO)redisTemplate.opsForValue().get(u.getName());
        sendMessage("zk","kafka");
        return "ok";
    }

    /**
     * 注入KafkaTemplate
     * @param kafkaTemplate kafka模版类
     */
    @Autowired
    public IndicatorService(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @KafkaListener(topics = "#{kafkaTopicName}", groupId = "#{topicGroupId}")
    public void processMessage(ConsumerRecord<Integer, String> record) {
        LOG.info("kafka processMessage start");
        LOG.info("processMessage, topic = {}, msg = {}", record.topic(), record.value());
        // do something ...

        LOG.info("kafka processMessage end");
    }

    public void sendMessage(String topic, String data) {
        LOG.info("kafka sendMessage start");
        ListenableFuture<SendResult<Integer, String>> future = kafkaTemplate.send(topic, data);
        future.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                LOG.error("kafka sendMessage error, ex = {}, topic = {}, data = {}", ex, topic, data);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                LOG.info("kafka sendMessage success topic = {}, data = {}",topic, data);
            }
        });
        LOG.info("kafka sendMessage end");
    }

    class user implements Serializable {

        private String name;

        private String sex;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        @Override
        public String toString() {
            return "user{" +
                    "name='" + name + '\'' +
                    ", sex='" + sex + '\'' +
                    '}';
        }
    }
}
