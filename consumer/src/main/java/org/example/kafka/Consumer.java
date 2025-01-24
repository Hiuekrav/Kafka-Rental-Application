package org.example.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.example.mgd.RentMgd;
import org.example.model.Rent;
import org.example.repositories.mongo.implementations.RentRepository;
import org.example.repositories.mongo.implementations.VehicleRepository;
import org.example.services.implementations.RentService;
import org.example.utils.consts.DatabaseConstants;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Consumer {

    public static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    public static List<KafkaConsumer<UUID, String>> consumerGroup = new ArrayList<>();
    public static MongoClient mongoClient;
    public static RentRepository rentRepository;

    public static void initConsumer() {
        Properties consumerConfig = new Properties();
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "clients");
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192,kafka2:9292,kafka3:9392");
        consumerConfig.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        connectMongo();
        for (int i = 0; i < 2; i++) {
            KafkaConsumer<UUID, String> kafkaConsumer = new KafkaConsumer<>(consumerConfig);
            kafkaConsumer.subscribe(List.of("rents"));
            consumerGroup.add(kafkaConsumer);
        }
    }

    public static void consume(KafkaConsumer<UUID, String> consumer) {
        String result;
        //List<RentMgd> rents = new ArrayList<>();
        try{
            Duration timeout = Duration.of(100, ChronoUnit.MILLIS);
            while(true) {
                ConsumerRecords<UUID, String> records = consumer.poll(timeout);
                if (records.count() != 0) {
                    System.out.println("Message count: " + records.count());
                }
                for (ConsumerRecord<UUID, String> record: records) {
                    result = record.value();
                    if (!result.isBlank()) {
                        System.out.println("Found rent:" + result);
                        KafkaMessage message = mapper.readValue(result, KafkaMessage.class);
                        try {
                            rentRepository.save(message.getRent());
                        } catch (RuntimeException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }
        }
        catch (WakeupException e) {
            System.out.println("Job finished!");
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public static void connectMongo() {
        ConnectionString connectionString = new ConnectionString(DatabaseConstants.connectionString);

        MongoCredential credential = MongoCredential.createCredential("admin", "admin", "adminpassword".toCharArray());

        CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder()
                .automatic(true)
                .conventions(List.of(Conventions.ANNOTATION_CONVENTION)).build());

        MongoClientSettings settings = MongoClientSettings.builder()
                .credential(credential)
                .applyConnectionString(connectionString)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(
                        CodecRegistries.fromRegistries(
                                MongoClientSettings.getDefaultCodecRegistry(),
                                pojoCodecRegistry
                        ))
                .readConcern(ReadConcern.MAJORITY)
                .writeConcern(WriteConcern.MAJORITY)
                .readPreference(ReadPreference.primary())
                .build();
        mongoClient = MongoClients.create(settings);
        rentRepository = new RentRepository(mongoClient);
    }

}
