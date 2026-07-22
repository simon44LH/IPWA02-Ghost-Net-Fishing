package de.iu.ghostnet.dao;

import de.iu.ghostnet.entity.Geisternetz;
import de.iu.ghostnet.entity.NetzStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class GeisternetzDAO implements Serializable {

    @PersistenceContext(unitName = "ghostnetPU")
    private EntityManager em;

    @Transactional
    public void speichern(Geisternetz netz) {
        if (netz.getId() == null) {
            em.persist(netz);
        } else {
            em.merge(netz);
        }
    }

    public Geisternetz findeNachId(Long id) {
        return em.find(Geisternetz.class, id);
    }

    public List<Geisternetz> findeAlle() {
        return em.createQuery(
                "SELECT g FROM Geisternetz g ORDER BY g.id", Geisternetz.class)
                .getResultList();
    }

    public List<Geisternetz> findeNachStatus(NetzStatus status) {
        return em.createQuery(
                "SELECT g FROM Geisternetz g WHERE g.status = :status ORDER BY g.id",
                Geisternetz.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Geisternetz> findeOffeneNetze() {
        return em.createQuery(
                "SELECT g FROM Geisternetz g WHERE g.status IN (:gemeldet, :bevorstehend) ORDER BY g.id",
                Geisternetz.class)
                .setParameter("gemeldet", NetzStatus.GEMELDET)
                .setParameter("bevorstehend", NetzStatus.BERGUNG_BEVORSTEHEND)
                .getResultList();
    }
}
