package vn.iotstar.configs;

import com.mongodb.DBRef;
import com.mongodb.MongoClientSettings;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "doancuoiky"; // Thay thế bằng tên database của bạn
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        CodecRegistry defaultRegistry = MongoClientSettings.getDefaultCodecRegistry();
        CodecRegistry dbRefRegistry = CodecRegistries.fromCodecs(new DBRefCodec());
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(dbRefRegistry, defaultRegistry);
        
        builder.codecRegistry(codecRegistry);
    }

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new DBRefReadConverter());
        converters.add(new DBRefWriteConverter());
        return new MongoCustomConversions(converters);
    }

    // Converter để đọc DBRef
    private static class DBRefReadConverter implements Converter<DBRef, DBRef> {
        @Override
        public DBRef convert(DBRef source) {
            return source;
        }
    }

    // Converter để ghi DBRef
    private static class DBRefWriteConverter implements Converter<DBRef, DBRef> {
        @Override
        public DBRef convert(DBRef source) {
            return source;
        }
    }
    
    // Custom codec cho DBRef
    private static class DBRefCodec implements Codec<DBRef> {
        @Override
        public void encode(BsonWriter writer, DBRef value, EncoderContext encoderContext) {
            writer.writeStartDocument();
            writer.writeString("$ref", value.getCollectionName());
            writer.writeName("$id");
            if (value.getId() instanceof ObjectId) {
                writer.writeObjectId((ObjectId) value.getId());
            } else {
                writer.writeString(value.getId().toString());
            }
            if (value.getDatabaseName() != null) {
                writer.writeString("$db", value.getDatabaseName());
            }
            writer.writeEndDocument();
        }

        @Override
        public Class<DBRef> getEncoderClass() {
            return DBRef.class;
        }

        @Override
        public DBRef decode(BsonReader reader, DecoderContext decoderContext) {
            reader.readStartDocument();
            String collection = reader.readString("$ref");
            reader.readName("$id");
            Object id;
            BsonType type = reader.getCurrentBsonType();
            if (type == BsonType.OBJECT_ID) {
                id = reader.readObjectId();
            } else {
                id = reader.readString();
            }
            String database = null;
            if (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                database = reader.readString("$db");
            }
            reader.readEndDocument();
            return new DBRef(database, collection, id);
        }
    }
}
