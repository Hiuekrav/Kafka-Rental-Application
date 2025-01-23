//package org.example.kafka;
//
//import org.apache.kafka.clients.producer.*;
//import org.apache.kafka.common.serialization.LongSerializer;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.apache.kafka.common.serialization.UUIDSerializer;
//
//import java.util.Properties;
//import java.util.concurrent.ExecutionException;
//
//public class Producer {
//
//    private final static String TOPIC = "my-example-topic";
//    private final static String BOOTSTRAP_SERVERS = "kafka1:9192,kafka2:9292,kafka3:9392";
//
//    private static Producer<Long, String> createProducer() {
//        Properties props = new Properties();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                BOOTSTRAP_SERVERS);
//        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
//                LongSerializer.class.getName());
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
//                StringSerializer.class.getName());
//        return new KafkaProducer<>(props);
//
//    public static void initProducer() throws ExecutionException, InterruptedException {
//        Properties producerConfig = new Properties();
//        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
//                UUIDSerializer.class.getName(
//                ));
//        producerConfig.put(ProducerConfig. VALUE_SERIALIZER_CLASS_CONFIG,
//                StringSerializer.class.getName(
//                ));
//        producerConfig.put(ProducerConfig.CLIENT_ID_CONFIG, "local");
//        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                "kafka1:9192,kafka2:9292,kafka3:9392");
//        producerConfig.put(ProducerConfig.ACKS_CONFIG, "alLl");
//        //
//        producerConfig.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
//        producer = new KafkaProducer(producerConfig);