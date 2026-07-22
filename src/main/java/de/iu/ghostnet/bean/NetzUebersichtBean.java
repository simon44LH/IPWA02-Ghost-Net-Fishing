package de.iu.ghostnet.bean;

import de.iu.ghostnet.dao.GeisternetzDAO;
import de.iu.ghostnet.dao.PersonDAO;
import de.iu.ghostnet.entity.BergendePerson;
import de.iu.ghostnet.entity.Geisternetz;
import de.iu.ghostnet.entity.MeldendePerson;
import de.iu.ghostnet.entity.NetzStatus;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class NetzUebersichtBean implements Serializable {

    @Inject
    private GeisternetzDAO geisternetzDAO;

    @Inject
    private PersonDAO personDAO;

    private List<Geisternetz> netze;
    private List<BergendePerson> bergendePersonen;

    private Long ausgewaehlteNetzId;
    private Long ausgewaehltePersonId;

    private String neuerPersonName;
    private String neuerPersonTelefon;

    @PostConstruct
    public void init() {
        laden();
    }

    private void laden() {
        netze = geisternetzDAO.findeAlle();
        bergendePersonen = personDAO.findeBergendePersonen();
    }

    public String fuerBergungEintragen(Geisternetz netz) {
        BergendePerson person;

        if (ausgewaehltePersonId != null) {
            person = personDAO.findeBergendeNachId(ausgewaehltePersonId);
        } else if (neuerPersonName != null && !neuerPersonName.isBlank()) {
            person = new BergendePerson();
            person.setName(neuerPersonName);
            person.setTelefonnummer(neuerPersonTelefon);
            personDAO.speichern(person);
        } else {
            meldung(FacesMessage.SEVERITY_WARN, "Bitte bergende Person auswaehlen oder anlegen.");
            return null;
        }

        netz.setBergendePerson(person);
        netz.setStatus(NetzStatus.BERGUNG_BEVORSTEHEND);
        geisternetzDAO.speichern(netz);

        meldung(FacesMessage.SEVERITY_INFO, "Bergung wurde angekuendigt.");
        neuerPersonName = null;
        neuerPersonTelefon = null;
        laden();
        return null;
    }

    public String alsGeborgenMelden(Geisternetz netz) {
        netz.setStatus(NetzStatus.GEBORGEN);
        geisternetzDAO.speichern(netz);
        meldung(FacesMessage.SEVERITY_INFO, "Geisternetz als geborgen vermerkt.");
        laden();
        return null;
    }

    public String alsVerschollenMelden(Geisternetz netz) {
        boolean vorhandenGewaehlt = ausgewaehltePersonId != null;
        boolean neueAngegeben = neuerPersonName != null && !neuerPersonName.isBlank();
        boolean personBereitsAmNetz = netz.getBergendePerson() != null
                || netz.getMeldendePerson() != null;

        if (!vorhandenGewaehlt && !neueAngegeben && !personBereitsAmNetz) {
            meldung(FacesMessage.SEVERITY_WARN,
                    "Verschollen-Meldungen sind nicht anonym moeglich. "
                    + "Bitte eine Person auswaehlen oder anlegen.");
            return null;
        }

        MeldendePerson melder = null;
        if (neueAngegeben) {
            melder = new MeldendePerson();
            melder.setName(neuerPersonName);
            melder.setTelefonnummer(neuerPersonTelefon);
            personDAO.speichern(melder);
        } else if (vorhandenGewaehlt) {
            BergendePerson gewaehlt = personDAO.findeBergendeNachId(ausgewaehltePersonId);
            melder = new MeldendePerson();
            melder.setName(gewaehlt.getName());
            melder.setTelefonnummer(gewaehlt.getTelefonnummer());
            personDAO.speichern(melder);
        }

        if (melder != null && netz.getMeldendePerson() == null) {
            netz.setMeldendePerson(melder);
        }

        netz.setStatus(NetzStatus.VERSCHOLLEN);
        geisternetzDAO.speichern(netz);
        meldung(FacesMessage.SEVERITY_INFO, "Geisternetz als verschollen vermerkt.");
        neuerPersonName = null;
        neuerPersonTelefon = null;
        laden();
        return null;
    }

    private void meldung(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, text, null));
    }

    public boolean istBergbar(Geisternetz netz) {
        return netz.getStatus() == NetzStatus.GEMELDET;
    }

    public boolean istGeborgenMeldbar(Geisternetz netz) {
        return netz.getStatus() == NetzStatus.BERGUNG_BEVORSTEHEND;
    }

    public boolean istVerschollenMeldbar(Geisternetz netz) {
        return netz.getStatus() == NetzStatus.GEMELDET
                || netz.getStatus() == NetzStatus.BERGUNG_BEVORSTEHEND;
    }

    public List<Geisternetz> getNetze() { return netze; }
    public List<BergendePerson> getBergendePersonen() { return bergendePersonen; }

    public Long getAusgewaehlteNetzId() { return ausgewaehlteNetzId; }
    public void setAusgewaehlteNetzId(Long v) { this.ausgewaehlteNetzId = v; }

    public Long getAusgewaehltePersonId() { return ausgewaehltePersonId; }
    public void setAusgewaehltePersonId(Long v) { this.ausgewaehltePersonId = v; }

    public String getNeuerPersonName() { return neuerPersonName; }
    public void setNeuerPersonName(String v) { this.neuerPersonName = v; }

    public String getNeuerPersonTelefon() { return neuerPersonTelefon; }
    public void setNeuerPersonTelefon(String v) { this.neuerPersonTelefon = v; }
}
