package de.iu.ghostnet.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MELDEND")
public class MeldendePerson extends Person {
}
