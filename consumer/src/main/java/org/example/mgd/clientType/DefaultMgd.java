package org.example.mgd.clientType;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.model.clientType.Default;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@BsonDiscriminator(key = DatabaseConstants.BSON_DISCRIMINATOR_KEY, value = DatabaseConstants.DEFAULT_DISCRIMINATOR)
public class DefaultMgd extends ClientTypeMgd {

    @BsonCreator
    public DefaultMgd (
            @BsonProperty(DatabaseConstants.ID) UUID id,
            @BsonProperty(DatabaseConstants.CLIENT_TYPE_DISCOUNT) Double discount,
            @BsonProperty(DatabaseConstants.CLIENT_TYPE_MAX_VEHICLES) Integer maxVehicles) {
        super(id, discount, maxVehicles);
    }

    public DefaultMgd(Default defaultObj) {
        super(defaultObj);
    }
    public DefaultMgd(Document document) {
        super(
            document.get(DatabaseConstants.ID, UUID.class),
            document.getDouble(DatabaseConstants.CLIENT_TYPE_DISCOUNT),
            document.getInteger(DatabaseConstants.CLIENT_TYPE_MAX_VEHICLES)
        );
    }
}
