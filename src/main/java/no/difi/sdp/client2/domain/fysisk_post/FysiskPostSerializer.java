package no.difi.sdp.client2.domain.fysisk_post;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class FysiskPostSerializer extends StdSerializer<FysiskPost> {
    protected FysiskPostSerializer() {
        super(FysiskPost.class);
    }

    @Override
    public void serialize(FysiskPost value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeObjectField("mottaker", value.getAdresse());

        gen.writeFieldName("retur");
        gen.writeStartObject();
        gen.writeObjectField("mottaker", value.getReturadresse());
        gen.writeEndObject();
    }
}
