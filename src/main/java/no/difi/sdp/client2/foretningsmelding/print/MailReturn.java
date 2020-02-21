package no.difi.sdp.client2.foretningsmelding.print;

import no.difi.sdp.client2.domain.fysisk_post.Returhaandtering;

public class MailReturn {

    public PostAddress mottaker;

    public Returhaandtering returhaandtering;

    public MailReturn(PostAddress mottaker, Returhaandtering returhaandtering) {
        this.mottaker = mottaker;
        this.returhaandtering = returhaandtering;
    }

    public MailReturn() {
    }

    public String toString() {
        return "MailReturn(mottaker=" + this.mottaker + ", returhaandtering=" + this.returhaandtering + ")";
    }
}
