package de.iu.ghostnet.bean;

import de.iu.ghostnet.dao.GeisternetzDAO;
import de.iu.ghostnet.dao.PersonDAO;
import de.iu.ghostnet.entity.Geisternetz;
import de.iu.ghostnet.entity.MeldendePerson;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class NetzMeldenBean implements Serializable {

    @Inject
    private GeisternetzDAO geisternetzDAO;

    @Inject
    private PersonDAO personDAO;

    private Double breitengrad;
    private Double laengengrad;
    private Double geschaetzteGroesse;
    private boolean anonym = true;
    private String name;
    private String telefonnummer;

    public String melden() {
        Geisternetz netz = new Geisternetz();
        netz.setBreitengrad(breitengrad);
        netz.setLaengengrad(laengengrad);
        netz.setGeschaetzteGroesse(geschaetzteGroesse);

        if (!anonym) {
            MeldendePerson person = new MeldendePerson();
            person.setName(name);
            person.setTelefonnummer(telefonnummer);
            personDAO.speichern(person);
            netz.setMeldendePerson(person);
        }

        geisternetzDAO.speichern(netz);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Geisternetz erfolgreich gemeldet.", null));

        zuruecksetzen();
        return null;
    }

    private void zuruecksetzen() {
        breitengrad = null;
        laengengrad = null;
        geschaetzteGroesse = null;
        anonym = true;
        name = null;
        telefonnummer = null;
    }

    public Double getBreitengrad() { return breitengrad; }
    public void setBreitengrad(Double v) { this.breitengrad = v; }

    public Double getLaengengrad() { return laengengrad; }
    public void setLaengengrad(Double v) { this.laengengrad = v; }

    public Double getGeschaetzteGroesse() { return geschaetzteGroesse; }
    public void setGeschaetzteGroesse(Double v) { this.geschaetzteGroesse = v; }

    public boolean isAnonym() { return anonym; }
    public void setAnonym(boolean v) { this.anonym = v; }

    public String getName() { return name; }
    public void setName(String v) { this.name = v; }

    public String getTelefonnummer() { return telefonnummer; }
    public void setTelefonnummer(String v) { this.telefonnummer = v; }
}
