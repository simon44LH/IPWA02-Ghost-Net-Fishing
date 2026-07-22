package de.iu.ghostnet.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("BERGEND")
public class BergendePerson extends Person {

    @OneToMany(mappedBy = "bergendePerson")
    private List<Geisternetz> zugewieseneNetze = new ArrayList<>();

    public List<Geisternetz> getZugewieseneNetze() { return zugewieseneNetze; }
    public void setZugewieseneNetze(List<Geisternetz> netze) { this.zugewieseneNetze = netze; }
}
