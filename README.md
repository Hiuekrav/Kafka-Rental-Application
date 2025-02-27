# Kafka Rental Application
This repository contains a solution for a Kafka-based rental application. The application is built on top of the NBD-Rental-Mongo-and-Redis project (https://github.com/wiktoriaBi/NBD-Rental-Mongo-and-Redis), which has been extended to include Kafka producers and consumers. The project is divided into two modules: producer and consumer, each responsible for sending and receiving rental data, respectively.

## Overview
The application consists of the following components:
- **Kafka Cluster:** A three-node Kafka cluster is set up to handle the messaging.
- **Topic:** A Kafka topic named rents is created with 3 partitions.
- **Producer:** A Kafka producer that sends rental data to the rents topic. Each rental message includes the rental details and the name of the rental agency.
- **Consumer Group:** A consumer group with at least two consumers that subscribe to the rents topic. The consumers read the messages and store the rent data in a MongoDB database.
- **Testing:** The application includes tests to verify that the producer sends messages correctly and that the consumers read and store the data as expected.

## Project Structure
The project is divided into two modules:
- **producer:** Contains the Kafka producer code.
- **consumer:** Contains the Kafka consumer code and the MongoDB integration.

### Producer Module
- **Producer Class:** Initializes the Kafka producer and sends messages to the rents topic.

### Consumer Module
- **Consumer Class:** Initializes the Kafka consumer group, reads messages from the rents topic, and stores the rent data in MongoDB.
- **KafkaMessage Class:** Represents the rental data and rental agency name.

## Setup and Testing the Project
### Prerequisites
- Java 21
- Docker
- Maven

### Setup
- Clone this repository.
- Set up MongoDB replica set, Redis and Kafka Cluster by running services directly from docker-compose using IDE or by executing ***docker-compose up -d*** command.
- Create the rents topic (use the terminal on any kafka node): ***/opt/bitnami/kafka/bin/kafka-topics.sh --create --topic rents --bootstrap-server kafka1:9192 --partitions 3 --replication-factor 3***

### Testing
1. **Run the Consumer tests separately and the Producer test:**
    - Go to ConsumerTest in your IDE.
    - Run readMessages1 test method.
    - Run readMessages2 test method right after.
    - Within 30 seconds run sendRentTest test method from ProducerTest class.

You should see 3 different Run tabs with readMessages1, readMessages2 and sendRentTest test results after tests completed.
2. **Verify Data in MongoDB:**
    - Check the MongoDB database to ensure that the rental data has been stored correctly.

## Additional Notes
Useful Kafka commands:
- Describe Consumer Group: ***kafka-consumer-groups.sh --bootstrap-server kafka1:9192 --describe --group clients***
- Start reading messages from the beginning of the topic: ***kafka-console-consumer.sh --bootstrap-server kafka1:9192 --topic rents --from-beginning***

## Authors
### Wiktoria Bilecka
### Grzegorz Janasek
