package no.difi.sdp.client2.internal;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class KvitteringTestUtil {
    public static String Feilmelding() {
        return TilXmlDokument("Feilmelding.xml");
    }

    public static String Leveringskvittering() {
        return TilXmlDokument("Leveringskvittering.xml");
    }

    public static String Mottakskvittering() {
        return TilXmlDokument("Mottakskvittering.xml");
    }

    public static String Returpostkvittering() {
        return TilXmlDokument("Returpostkvittering.xml");
    }

    public static String VarslingFeiletKvittering() {
        return TilXmlDokument("VarslingFeiletKvittering.xml");
    }

    public static String Åpningskvittering() {
        return TilXmlDokument("Åpningskvittering.xml");
    }

    public static String TilXmlDokument(String kvittering) {
        try {
            return IOUtils.toString(KvitteringTestUtil.class.getResourceAsStream("/kvitteringer/" + kvittering), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
