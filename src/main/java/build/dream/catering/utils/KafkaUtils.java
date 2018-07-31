package build.dream.catering.utils;

import build.dream.common.utils.ApplicationHandler;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.converter.MessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Map;

public class KafkaUtils {
    private static KafkaTemplate<String, String> kafkaTemplate;

    public KafkaTemplate<String, String> obtainKafkaTemplate() {
        if (kafkaTemplate == null) {
            kafkaTemplate = ApplicationHandler.getBean(KafkaTemplate.class);
        }
        return kafkaTemplate;
    }

    public String getDefaultTopic() {
        return obtainKafkaTemplate().getDefaultTopic();
    }

    public void setDefaultTopic(String defaultTopic) {
        obtainKafkaTemplate().setDefaultTopic(defaultTopic);
    }

    public void setProducerListener(ProducerListener<String, String> producerListener) {
        obtainKafkaTemplate().setProducerListener(producerListener);
    }

    public MessageConverter getMessageConverter() {
        return obtainKafkaTemplate().getMessageConverter();
    }

    public void setMessageConverter(RecordMessageConverter messageConverter) {
        obtainKafkaTemplate().setMessageConverter(messageConverter);
    }

    public boolean isTransactional() {
        return obtainKafkaTemplate().isTransactional();
    }

    public ListenableFuture<SendResult<String, String>> sendDefault(String data) {
        return send(getDefaultTopic(), data);
    }

    public ListenableFuture<SendResult<String, String>> sendDefault(String key, String data) {
        return obtainKafkaTemplate().sendDefault(key, data);
    }

    public ListenableFuture<SendResult<String, String>> sendDefault(Integer partition, String key, String data) {
        return obtainKafkaTemplate().sendDefault(partition, key, data);
    }

    public ListenableFuture<SendResult<String, String>> sendDefault(Integer partition, Long timestamp, String key, String data) {
        return obtainKafkaTemplate().sendDefault(partition, timestamp, key, data);
    }

    public ListenableFuture<SendResult<String, String>> send(String topic, String data) {
        return obtainKafkaTemplate().send(topic, data);
    }

    public ListenableFuture<SendResult<String, String>> send(String topic, String key, String data) {
        return obtainKafkaTemplate().send(topic, key, data);
    }

    public ListenableFuture<SendResult<String, String>> send(String topic, Integer partition, String key, String data) {
        return obtainKafkaTemplate().send(topic, partition, key, data);
    }

    public ListenableFuture<SendResult<String, String>> send(String topic, Integer partition, Long timestamp, String key, String data) {
        return obtainKafkaTemplate().send(topic, partition, timestamp, key, data);
    }


    public ListenableFuture<SendResult<String, String>> send(ProducerRecord<String, String> record) {
        return obtainKafkaTemplate().send(record);
    }

    public ListenableFuture<SendResult<String, String>> send(Message<?> message) {
        return obtainKafkaTemplate().send(message);
    }

    public List<PartitionInfo> partitionsFor(String topic) {
        return obtainKafkaTemplate().partitionsFor(topic);
    }

    public Map<MetricName, ? extends Metric> metrics() {
        return obtainKafkaTemplate().metrics();
    }

    public <T> T execute(KafkaOperations.ProducerCallback<String, String, T> callback) {
        return obtainKafkaTemplate().execute(callback);
    }

    public <T> T executeInTransaction(KafkaOperations.OperationsCallback<String, String, T> callback) {
        return obtainKafkaTemplate().executeInTransaction(callback);
    }

    public void flush() {
        obtainKafkaTemplate().flush();
    }


    public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> offsets) {
        obtainKafkaTemplate().sendOffsetsToTransaction(offsets);
    }

    public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> offsets, String consumerGroupId) {
        obtainKafkaTemplate().sendOffsetsToTransaction(offsets, consumerGroupId);
    }
}
