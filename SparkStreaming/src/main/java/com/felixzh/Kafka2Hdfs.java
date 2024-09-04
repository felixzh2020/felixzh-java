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
 */
public class Kafka2Hdfs {
    public static void main(String[] args) throws Exception {
        SparkConf sparkConf = new SparkConf().setAppName("kafka2Hdfs");
        JavaStreamingContext javaStreamingContext = new JavaStreamingContext(sparkConf, Durations.seconds(10));
        SparkSession sparkSession = SparkSession.builder().config(sparkConf).getOrCreate();

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

            // Spark UI Output Op Duration 与 Job Duration 之和 相差巨大  测试
            // 原因：Output Op Duration = Driver端耗时 +  Job Duration 之和
            // Driver端耗时：往Driver端拉取数据的算子、shuffle数据量大(blockManager数据量大)、其他耗时(如sparkSession.sql)等等
            Thread.sleep(30_000);

            // 将schema与数据转为DataFrame
            Dataset<Row> df = sparkSession.createDataFrame(rowRDD, schema);

            // 将数据写入到HDFS
            // coalesce：非shuffle 大->小   repartition：shuffle 大->小 或者 小->大
            df.repartition(1).write().partitionBy("part").mode(SaveMode.Append).parquet("/tmp/parquet");

            // 提交offset
            ((CanCommitOffsets) dStream.inputDStream()).commitAsync(offsetRanges);
        });

        javaStreamingContext.start();
        javaStreamingContext.awaitTermination();
    }
}