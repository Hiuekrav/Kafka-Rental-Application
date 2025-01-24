package org.example.kafka;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.mgd.RentMgd;
import org.example.model.Rent;

@NoArgsConstructor
@Getter @Setter
public class KafkaMessage {

    private String name;
    private RentMgd rent;

    public KafkaMessage(String name, RentMgd rent) {
        this.name = name;
        this.rent = rent;
    }
}
