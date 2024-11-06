/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.rocketmq.flink.legacy.example;

import org.apache.rocketmq.flink.source.RocketMQSource;
import org.apache.rocketmq.flink.source.reader.deserializer.RocketMQValueOnlyDeserializationSchemaWrapper;
import org.apache.rocketmq.flink.source.reader.deserializer.SimpleStringSchema;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class RocketMQSourceExample {
    private static String topic = "test_flink_rmq";
    private static String consumerGroup = "cg_test";
    private static String nameServerAddress = "192.168.111.117:9876";

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(30000L);

        RocketMQSource<String> source =
                RocketMQSource.<String>builder()
                        .setNameServerAddress(nameServerAddress)
                        .setTopic(topic)
                        .setConsumerGroup(consumerGroup)
                        .setStartFromEarliest()
                        .setDeserializer(
                                new RocketMQValueOnlyDeserializationSchemaWrapper<>(
                                        new SimpleStringSchema()))
                        .build();

        DataStreamSource<String> newSource =
                env.fromSource(source, WatermarkStrategy.noWatermarks(), "new source")
                        .setParallelism(4);

        newSource.print().setParallelism(1);

        env.execute();
    }
}
