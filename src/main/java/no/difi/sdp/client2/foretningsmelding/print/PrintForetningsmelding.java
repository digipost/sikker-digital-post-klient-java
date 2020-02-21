package no.difi.sdp.client2.foretningsmelding.print;

import no.difi.sdp.client2.domain.fysisk_post.Posttype;
import no.difi.sdp.client2.domain.fysisk_post.Utskriftsfarge;
import no.difi.sdp.client2.foretningsmelding.ForretningsMelding;

import static no.difi.sdp.client2.foretningsmelding.ForretningMeldingsType.PRINT;

public class PrintForetningsmelding  extends ForretningsMelding {

    public PostAddress mottaker;
    public Utskriftsfarge utskriftsfarge;

    public Posttype posttype;
    public MailReturn retur;

    public PrintForetningsmelding() {
        super(PRINT);
    }
}
