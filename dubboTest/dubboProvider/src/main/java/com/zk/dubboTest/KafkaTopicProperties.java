package com.zk.dubboTest;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.Arrays;

@ConfigurationProperties("kafka.topic")
public class KafkaTopicProperties implements Serializable {
    private String groupId;
    private String[] topicName;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String[] getTopicName() {
        return topicName;
    }

    public void setTopicName(String[] topicName) {
        this.topicName = topicName;
    }

    @Override
    public String toString() {
        return "KafkaTopicProperties{" +
                "groupId='" + groupId + '\'' +
                ", topicName=" + Arrays.toString(topicName) +
                '}';
    }
}
