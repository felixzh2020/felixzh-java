package com.felixzh;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.*;

import java.util.*;

public class Kafka2Kafka {
    public static void main(String[] args) throws Exception {
        SparkConf sparkConf = new SparkConf().setAppName("kafka2kafka");
        JavaStreamingContext javaStreamingContext = new JavaStreamingContext(sparkConf, Durations.seconds(10));

        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "felixzh:9092");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "felixzh");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

        Collection<String> topics = Collections.singletonList("in");
        JavaInputDStream<ConsumerRecord<String, String>> dStream = KafkaUtils.createDirectStream
                (javaStreamingContext, LocationStrategies.PreferConsistent(), ConsumerStrategies.Subscribe(topics, kafkaParams));
        dStream.foreachRDD(rdd -> {
            if (rdd.isEmpty()) {
                return;
            }

            // 获取当前offset
            OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();

            // 数据处理
            Producer<String, String> producer = new KafkaProducer<>(kafkaParams);
            rdd.foreachPartition(consumerRecords -> {
                while (consumerRecords.hasNext()) {
                    producer.send(new ProducerRecord<>("out", consumerRecords.next().value()));
                }
            });
            producer.close();

            // 提交offset
            ((CanCommitOffsets) dStream.inputDStream()).commitAsync(offsetRanges);
        });

        javaStreamingContext.start();
        javaStreamingContext.awaitTermination();
    }
}