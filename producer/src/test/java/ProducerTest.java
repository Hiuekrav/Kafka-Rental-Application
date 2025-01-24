import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.example.kafka.KafkaMessage;
import org.example.kafka.Producer;
import org.example.mgd.ClientMgd;
import org.example.mgd.RentMgd;
import org.example.mgd.vehicle.BicycleMgd;
import org.example.mgd.vehicle.CarMgd;
import org.example.model.Client;
import org.example.model.Rent;
import org.example.model.clientType.ClientType;
import org.example.model.clientType.Silver;
import org.example.model.vehicle.Bicycle;
import org.example.model.vehicle.Car;
import org.example.model.vehicle.Vehicle;
import org.example.repositories.mongo.implementations.ClientRepository;
import org.example.repositories.mongo.implementations.RentRepository;
import org.example.repositories.mongo.implementations.VehicleRepository;
import org.example.repositories.mongo.interfaces.IClientRepository;
import org.example.repositories.mongo.interfaces.IRentRepository;
import org.example.repositories.mongo.interfaces.IVehicleRepository;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ProducerTest {

    private IRentRepository rentRepository;
    private IClientRepository clientRepository;
    private IVehicleRepository vehicleRepository;
    private static MongoClient client;

    @BeforeAll
    static void connect() {
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

        client = MongoClients.create(settings);
        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.RENT_ACTIVE_COLLECTION_NAME).drop();
        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.RENT_ARCHIVE_COLLECTION_NAME).drop();
        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME).drop();
        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.CLIENT_TYPE_COLLECTION_NAME).drop();
        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.CLIENT_COLLECTION_NAME).drop();
    }
    @BeforeEach
    void setUp() {
        rentRepository = new RentRepository(client);
        clientRepository = new ClientRepository(client, ClientMgd.class);
        vehicleRepository = new VehicleRepository(client);
        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME).drop();
        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.CLIENT_TYPE_COLLECTION_NAME).drop();
        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.CLIENT_COLLECTION_NAME).drop();
    }

    //@AfterEach
    //void dropDatabase() {
    //
    //}


    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    public void sendRentTest() throws JsonProcessingException, ExecutionException, InterruptedException {
        Producer.initProducer();

        String email = "test@test.com";
        ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
        Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
                email, silver, "Wawa", "Kwiatowa", "15");
        ClientMgd clientMgd = new ClientMgd(client);
        clientRepository.save(clientMgd);

        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
        BicycleMgd bicycleMgd = new BicycleMgd(bicycle);
        vehicleRepository.save(bicycleMgd);

        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
        RentMgd rentMgd = new RentMgd(rent, clientMgd, bicycleMgd);

        KafkaMessage kafkaMessage = new KafkaMessage("rental 1", rentMgd);
        Producer.sendMessage(rent.getId(), mapper.writeValueAsString(kafkaMessage));

        Car car = new Car(UUID.randomUUID(), "AABB123", 100.0,3, Car.TransmissionType.MANUAL);
        CarMgd carMgd = new CarMgd(car);
        vehicleRepository.save(carMgd);

        Rent rent2 = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, car);
        RentMgd rentMgd2 = new RentMgd(rent2, clientMgd, carMgd);
        KafkaMessage kafkaMessage2 = new KafkaMessage("rental 2", rentMgd2);
        Producer.sendMessage(rentMgd2.getId(), mapper.writeValueAsString(kafkaMessage2));

    }
}
