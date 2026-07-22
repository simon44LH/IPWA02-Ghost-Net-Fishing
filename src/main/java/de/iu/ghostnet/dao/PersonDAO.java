package de.iu.ghostnet.dao;

import de.iu.ghostnet.entity.BergendePerson;
import de.iu.ghostnet.entity.MeldendePerson;
import de.iu.ghostnet.entity.Person;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class PersonDAO implements Serializable {

    @PersistenceContext(unitName = "ghostnetPU")
    private EntityManager em;

    @Transactional
    public void speichern(Person person) {
        if (person.getId() == null) {
            em.persist(person);
        } else {
            em.merge(person);
        }
    }

    public List<BergendePerson> findeBergendePersonen() {
        return em.createQuery(
                "SELECT p FROM BergendePerson p ORDER BY p.name", BergendePerson.class)
                .getResultList();
    }

    public List<MeldendePerson> findeMeldendePersonen() {
        return em.createQuery(
                "SELECT p FROM MeldendePerson p ORDER BY p.name", MeldendePerson.class)
                .getResultList();
    }

    public BergendePerson findeBergendeNachId(Long id) {
        return em.find(BergendePerson.class, id);
    }
}
