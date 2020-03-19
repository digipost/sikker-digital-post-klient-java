package no.difi.sdp.client2.domain;

import java.util.Optional;

public interface MedDokumentEgenskaper {
    String getFileName();
    byte[] getBytes();
    String getMimeType();

    default Optional<String> getDokumentTittel(){
        return Optional.empty();
    }

}
