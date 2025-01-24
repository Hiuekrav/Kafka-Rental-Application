package org.example.kafka;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.kafka.Consumer.consumerGroup;

public class ConsumerTest {


    @Test
    public void readMessages1() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Consumer.initConsumer();

        for (KafkaConsumer<UUID, String> consumer : consumerGroup) {
            executorService.execute(() -> {Consumer.consume(consumer);});
        }

        Thread.sleep(50000);
        for (KafkaConsumer<UUID, String> consumer : consumerGroup) {
            consumer.wakeup();
        }
        executorService.shutdown();
    }


    @Test
    public void readMessages2() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Consumer.initConsumer();

        for (KafkaConsumer<UUID, String> consumer : consumerGroup) {
            executorService.execute(() -> {Consumer.consume(consumer);});
        }

        Thread.sleep(30000);
        for (KafkaConsumer<UUID, String> consumer : consumerGroup) {
            consumer.wakeup();
        }
        executorService.shutdown();
    }
}
