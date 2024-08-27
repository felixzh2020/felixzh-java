package com.felixzh;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.Trigger;
import org.apache.spark.sql.types.StructType;

import static org.apache.spark.sql.types.DataTypes.*;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;

/**
 * @author FelixZh
 * @desc Kafka数据格式：{"user":"felixzh","age":100}
 */
public class Kafka2Kafka {
    public static void main(String[] args) throws Exception {
        SparkConf sparkConf = new SparkConf().setAppName("StructuredKafka2Kafka");
        SparkSession sparkSession = SparkSession.builder().config(sparkConf).getOrCreate();

        Dataset<Row> kafkaDF = sparkSession
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "felixzh:9092")
                .option("startingOffsets", "latest")
                .option("kafka.group.id", "felixzh")
                .option("subscribe", "topic1")
                .load();

        Dataset<Row> dataDF = kafkaDF.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)");

        StreamingQuery streamingQuery = dataDF
                .writeStream()
                .queryName("kafka2kafka")
                .option("kafka.bootstrap.servers", "felixzh:9092")
                .option("topic", "out")
                .option("checkpointLocation", "/tmp/checkpoint1")
                .trigger(Trigger.ProcessingTime(10_000))
                .start();
        streamingQuery.awaitTermination();
    }
}