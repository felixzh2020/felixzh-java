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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author FelixZh
 * @desc 测试数据无要求
 * 如果不同Topic处理逻辑不同,比如过滤/去重/ETL等等,可以为不同Topic单独创建DirectStream
 */
public class KafkaUnion {
    public static void main(String[] args) throws Exception {
        SparkConf sparkConf = new SparkConf().setAppName("kafkaUnion");
        JavaStreamingContext javaStreamingContext = new JavaStreamingContext(sparkConf, Durations.seconds(10));

        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "felixzh:9092");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "felixzh");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

        Collection<String> topics1 = Collections.singletonList("topic1");
        Collection<String> topics2 = Collections.singletonList("topic2");
        JavaInputDStream<ConsumerRecord<String, String>> dStream1 = KafkaUtils.createDirectStream
                (javaStreamingContext, LocationStrategies.PreferConsistent(), ConsumerStrategies.Subscribe(topics1, kafkaParams));
        JavaInputDStream<ConsumerRecord<String, String>> dStream2 = KafkaUtils.createDirectStream
                (javaStreamingContext, LocationStrategies.PreferConsistent(), ConsumerStrategies.Subscribe(topics2, kafkaParams));

        // 这里进行dStream1和dStream2 不同的处理逻辑
        // ........

        // 两个相同interval的数据流，进行union合并
        dStream1.union(dStream2).foreachRDD(rdd -> {
            if (rdd.isEmpty()) {
                return;
            }

            rdd.foreachPartition(consumerRecords -> {
                while (consumerRecords.hasNext()) {
                    System.out.println(consumerRecords.next().value());
                }
            });
        });

        javaStreamingContext.start();
        javaStreamingContext.awaitTermination();
    }
}