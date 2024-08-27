package com.felixzh;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;


/**
 * @author FelixZh
 * @desc 不限制Kafka数据格式
 */
public class Kafka2Console {
    public static void main(String[] args) throws Exception {
        SparkConf sparkConf = new SparkConf().setAppName("StructuredKafka2Console");
        SparkSession sparkSession = SparkSession.builder().config(sparkConf).getOrCreate();

        Dataset<Row> df = sparkSession
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "felixzh:9092")
                .option("startingOffsets", "latest")
                .option("kafka.group.id", "felixzh")
                .option("subscribe", "topic1")
                .load();

        Dataset<Row> kafkaDF = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)");
        StreamingQuery streamingQuery = kafkaDF.writeStream()
                .outputMode(OutputMode.Append())
                .format("console")
                .start();

        streamingQuery.awaitTermination();
    }
}
