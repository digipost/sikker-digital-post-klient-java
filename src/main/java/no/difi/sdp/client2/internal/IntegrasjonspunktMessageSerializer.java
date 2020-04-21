package no.difi.sdp.client2.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import no.difi.sdp.client2.domain.sbd.StandardBusinessDocument;

import java.io.IOException;

public class IntegrasjonspunktMessageSerializer extends StdSerializer<StandardBusinessDocument> {

    public IntegrasjonspunktMessageSerializer() {
        super(StandardBusinessDocument.class);
    }

    @Override
    public void serialize(StandardBusinessDocument value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeFieldName("standardBusinessDocumentHeader");
        gen.writeObject(value.getStandardBusinessDocumentHeader());
        final String foretningMeldingType = value.getForretningsMelding().getType();
        gen.writeFieldName(foretningMeldingType);
        gen.writeObject(value.getAny());
        gen.writeEndObject();
    }

}
