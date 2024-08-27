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
public class Kafka2Hdfs {
    public static void main(String[] args) throws Exception {
        SparkConf sparkConf = new SparkConf().setAppName("StructuredKafka2Hdfs");
        SparkSession sparkSession = SparkSession.builder().config(sparkConf).getOrCreate();

        Dataset<Row> kafkaDF = sparkSession
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "felixzh:9092")
                .option("startingOffsets", "latest")
                .option("kafka.group.id", "felixzh")
                .option("subscribe", "topic1")
                .load()
                .selectExpr("CAST(value AS STRING)");

        StructType structType = new StructType()
                .add("user", StringType)
                .add("age", IntegerType);

        Dataset<Row> dataDF = kafkaDF.select(from_json(col("value"), structType).as("data"))
                .selectExpr("data.user AS user", "data.age AS age");

        StreamingQuery streamingQuery = dataDF.coalesce(1)
                .writeStream()
                .queryName("kafka2hdfs")
                .format("orc")
                .option("path", "/tmp/orc")
                .option("checkpointLocation", "/tmp/checkpoint")
                .outputMode(OutputMode.Append())
                .trigger(Trigger.ProcessingTime(10_000))
                .start();
        streamingQuery.awaitTermination();
    }
}
