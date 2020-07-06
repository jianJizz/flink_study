package com.usc.flink;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;


public class WindowWordCount {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<Tuple2<String, Integer>> windowCounts = env.socketTextStream("localhost", 9999)
                .flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
                    @Override
                    public void flatMap(String s, Collector<Tuple2<String, Integer>> out) throws Exception {
                        for (String value : s.split("\\s")) {
                            out.collect(Tuple2.of(value, 1));
                        }
                    }
                })
                .keyBy(0)
                .timeWindow(Time.seconds(5))
                .sum(1);

        windowCounts.print();

        env.execute("Window Count");
    }
}
