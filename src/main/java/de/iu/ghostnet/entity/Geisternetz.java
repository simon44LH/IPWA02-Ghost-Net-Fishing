package de.iu.ghostnet.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "geisternetz")
public class Geisternetz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double breitengrad;

    @Column(nullable = false)
    private Double laengengrad;

    @Column(name = "groesse_qm")
    private Double geschaetzteGroesse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NetzStatus status = NetzStatus.GEMELDET;

    @ManyToOne
    @JoinColumn(name = "meldende_person_id")
    private MeldendePerson meldendePerson;

    @ManyToOne
    @JoinColumn(name = "bergende_person_id")
    private BergendePerson bergendePerson;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getBreitengrad() { return breitengrad; }
    public void setBreitengrad(Double breitengrad) { this.breitengrad = breitengrad; }

    public Double getLaengengrad() { return laengengrad; }
    public void setLaengengrad(Double laengengrad) { this.laengengrad = laengengrad; }

    public Double getGeschaetzteGroesse() { return geschaetzteGroesse; }
    public void setGeschaetzteGroesse(Double groesse) { this.geschaetzteGroesse = groesse; }

    public NetzStatus getStatus() { return status; }
    public void setStatus(NetzStatus status) { this.status = status; }

    public MeldendePerson getMeldendePerson() { return meldendePerson; }
    public void setMeldendePerson(MeldendePerson p) { this.meldendePerson = p; }

    public String getMelderAnzeige() {
        return meldendePerson == null ? "anonym" : meldendePerson.getName();
    }

    public BergendePerson getBergendePerson() { return bergendePerson; }
    public void setBergendePerson(BergendePerson p) { this.bergendePerson = p; }
}
