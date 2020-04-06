package no.difi.sdp.client2.internal.kvittering;

import no.difi.begrep.sdp.schema_v10.SDPAvsender;
import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.difi.begrep.sdp.schema_v10.SDPDigitalPostInfo;
import no.difi.begrep.sdp.schema_v10.SDPFeil;
import no.difi.begrep.sdp.schema_v10.SDPFlyttetDigitalPost;
import no.difi.begrep.sdp.schema_v10.SDPFysiskPostInfo;
import no.difi.begrep.sdp.schema_v10.SDPKvittering;
import no.difi.begrep.sdp.schema_v10.SDPMelding;
import no.difi.begrep.sdp.schema_v10.SDPMottaker;
import no.difi.begrep.sdp.schema_v10.SDPVarslingfeilet;
import no.digipost.xsd.types.DigitalPostformidling;

import java.util.Arrays;

public class SimpleSBDMessage {

    private final Object any;

    public SimpleSBDMessage(final Object any) {
        this.any = any;
    }

    public boolean erKvittering() {
        return any instanceof SDPKvittering;
    }

    public boolean erDigitalPost() {
        return any instanceof SDPDigitalPost;
    }

    public boolean erFlyttetDigitalPost() {
        return any instanceof SDPFlyttetDigitalPost;
    }

    public boolean erFeil() {
        return any instanceof SDPFeil;
    }


    public Object getUnderlyingDoc() {
        return any;
    }

    public SDPFeil getFeil() {
        return (SDPFeil) any;
    }

    public SimpleKvittering getKvittering() {
        return new SimpleKvittering((SDPKvittering) any);
    }

    public SimpleDigitalPostformidling getDigitalPostformidling() {
        return new SimpleDigitalPostformidling((DigitalPostformidling) any);
    }

    public SDPMelding getMelding() {
        return (SDPMelding) any;
    }

    public static class SimpleDigitalPostformidling {

        public final Type type;
        private final DigitalPostformidling digitalPostformidling;

        public SimpleDigitalPostformidling(final DigitalPostformidling digitalPostformidling) {
            type = Type.of(digitalPostformidling);
            this.digitalPostformidling = digitalPostformidling;
        }

        public SDPDigitalPost getDigitalPost() {
            return (SDPDigitalPost) Type.NY_POST.validateInstance(digitalPostformidling);
        }

        public SDPFlyttetDigitalPost getFlyttetDigitalPost() {
            return (SDPFlyttetDigitalPost) Type.FLYTTET.validateInstance(digitalPostformidling);
        }

        public boolean kreverAapningsKvittering() {
            SDPDigitalPostInfo postinfo = getDigitalPostInfo();
            return postinfo != null ? postinfo.getAapningskvittering() : false;
        }

        public SDPAvsender getAvsender() {
            return digitalPostformidling.getAvsender();
        }

        public SDPMottaker getMottaker() {
            return digitalPostformidling.getMottaker();
        }

        public SDPDigitalPostInfo getDigitalPostInfo() {
            return digitalPostformidling.getDigitalPostInfo();
        }

        public boolean erDigitalPostTilFysiskLevering() {
            return digitalPostformidling instanceof SDPDigitalPost && ((SDPDigitalPost) digitalPostformidling).getFysiskPostInfo() != null;
        }

        public SDPFysiskPostInfo getFysiskPostInfo() {
            return ((SDPDigitalPost) digitalPostformidling).getFysiskPostInfo();
        }

        public boolean erAlleredeAapnet() {
            return type == Type.FLYTTET && getFlyttetDigitalPost().isAapnet();
        }

        public static enum Type {
            NY_POST(SDPDigitalPost.class),
            FLYTTET(SDPFlyttetDigitalPost.class);

            private final Class<? extends DigitalPostformidling> associatedClass;

            Type(final Class<? extends DigitalPostformidling> associatedClass) {
                this.associatedClass = associatedClass;
            }

            public static Type of(final DigitalPostformidling melding) {
                for (Type type : values()) {
                    if (type.isInstance(melding)) {
                        return type;
                    }
                }
                throw new IllegalArgumentException(
                        DigitalPostformidling.class.getSimpleName() + " av type " + melding.getClass().getName() +
                                "ble ikke gjenkjent som noen av " + Arrays.toString(values()));
            }

            public boolean isInstance(final DigitalPostformidling melding) {
                return associatedClass.isInstance(melding);
            }

            public <T extends DigitalPostformidling> T validateInstance(final T candidate) {
                if (isInstance(candidate)) {
                    return candidate;
                } else {
                    Type type = Type.of(candidate);
                    throw new IllegalArgumentException(
                            candidate.getClass().getName() + " er ikke av forventet type " +
                                    this + ", men ble gjenkjent som " + type);
                }
            }
        }

    }

    public class SimpleKvittering {

        public final SDPKvittering kvittering;

        public SimpleKvittering(final SDPKvittering kvittering) {
            this.kvittering = kvittering;
        }

        public boolean erMottak() {
            return kvittering.getMottak() != null;
        }

        public boolean erLevertTilPostkasse() {
            return kvittering.getLevering() != null;
        }

        public boolean erAapnet() {
            return kvittering.getAapning() != null;
        }

        public boolean erVarslingFeilet() {
            return kvittering.getVarslingfeilet() != null;
        }

        public boolean erReturpost() {
            return kvittering.getReturpost() != null;
        }

        public SDPVarslingfeilet getVarslingFeilet() {
            return kvittering.getVarslingfeilet();
        }

    }

}
