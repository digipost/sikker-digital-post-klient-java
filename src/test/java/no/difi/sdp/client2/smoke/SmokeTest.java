package no.difi.sdp.client2.smoke;

import no.difi.sdp.client2.ObjectMother;
import no.difi.sdp.client2.domain.AktoerOrganisasjonsnummer;
import no.difi.sdp.client2.domain.Miljo;
import org.junit.jupiter.api.Test;


//@Disabled("This test runs the client against a deployed backed, and thus needs correct keys set up. " +
//        "Run it, and it will tell you how to set things up!")
public class SmokeTest {

    private static final AktoerOrganisasjonsnummer avsenderOrgnr = ObjectMother.POSTEN_ORGNR;

    @Test
    public void send_simple_digital_message() {

        new SmokeTestHelper(Miljo.FUNKSJONELT_TESTMILJO)
                .create_digital_forsendelse(ObjectMother.avsender(avsenderOrgnr))
                .send()
                .fetch_receipt()
                .expect_receipt_to_be_leveringskvittering()
                .confirm_receipt();
    }


    @Test
    public void send_simple_fysisk_post_message() {
        new SmokeTestHelper(Miljo.FUNKSJONELT_TESTMILJO)
            .create_print_forsendelse()
            .send()
            .fetch_receipt()
            .expect_receipt_to_be_leveringskvittering()
            .confirm_receipt();
    }


    @Test
    public void send_ehf_message() {
        new SmokeTestHelper(Miljo.FUNKSJONELT_TESTMILJO)
            .create_ehf_forsendelse(ObjectMother.avsender(avsenderOrgnr))
            .send()
            .fetch_receipt()
            .expect_receipt_to_be_leveringskvittering()
            .confirm_receipt();
    }

}
