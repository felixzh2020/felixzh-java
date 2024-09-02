package com.felixzh;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.*;

import java.util.*;

/**
 * @author FelixZh
 * @desc 测试数据schema id,name,part
 * beeline 创建 Hive表
 * create table test(value string) partitioned by (part string) stored as orc;
 */
public class Kafka2HiveV1 {
    public static void main(String[] args) throws Exception {
        SparkConf sparkConf = new SparkConf().setAppName("kafka2HiveV1");
        SparkSession sparkSession = SparkSession.builder().config(sparkConf)
                .enableHiveSupport() // Enables Hive support, including connectivity to a persistent Hive metastore,
                // support for Hive serdes, and Hive user-defined functions.
                .getOrCreate();
        sparkSession.sqlContext().setConf("hive.exec.dynamic.partition.mode", "nonstrict");

        JavaStreamingContext javaStreamingContext = new JavaStreamingContext(sparkConf, Durations.seconds(60));

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
            JavaRDD<Row> rowRDD = rdd.map((Function<ConsumerRecord<String, String>, Row>) value -> {
                String[] fieldValueArr = value.value().split(",");
                return RowFactory.create(fieldValueArr[0], fieldValueArr[1], fieldValueArr[2]);
            });

            // 定义schema
            List<StructField> fields = new ArrayList<>();
            fields.add(DataTypes.createStructField("id", DataTypes.IntegerType, true));
            fields.add(DataTypes.createStructField("name", DataTypes.StringType, true));
            fields.add(DataTypes.createStructField("part", DataTypes.StringType, true));
            StructType schema = DataTypes.createStructType(fields);

            // 将schema与数据转为DataFrame
            Dataset<Row> df = sparkSession.createDataFrame(rowRDD, schema);

            // 将数据写入到Hive
            // coalesce：非shuffle 大->小   repartition：shuffle 大->小 或者 小->大
            df.repartition(1).write().partitionBy("part").format("hive").mode(SaveMode.Append).saveAsTable("test");

            // 提交offset
            ((CanCommitOffsets) dStream.inputDStream()).commitAsync(offsetRanges);
        });

        javaStreamingContext.start();
        javaStreamingContext.awaitTermination();
    }
}