package no.difi.sdp.client2.domain.fysisk_post;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class FysiskPostSerializer extends StdSerializer<FysiskPost> {
    public FysiskPostSerializer() {
        super(FysiskPost.class);
    }

    @Override
    public void serialize(FysiskPost value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("hoveddokument", value.getHoveddokument());
        gen.writeObjectField("posttype", value.getPosttype());
        gen.writeObjectField("utskriftsfarge", value.getUtskriftsfarge());

        gen.writeObjectField("mottaker", value.getMottaker());

        gen.writeFieldName("retur");
        gen.writeStartObject();
        gen.writeObjectField("mottaker", value.getReturadresse());
        gen.writeObjectField("returhaandtering", value.getReturhaandtering());

        gen.writeEndObject();

        gen.writeEndObject();
    }
}
