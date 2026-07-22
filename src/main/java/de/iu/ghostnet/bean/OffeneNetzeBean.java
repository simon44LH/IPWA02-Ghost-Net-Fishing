package de.iu.ghostnet.bean;

import de.iu.ghostnet.dao.GeisternetzDAO;
import de.iu.ghostnet.entity.Geisternetz;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class OffeneNetzeBean implements Serializable {

    @Inject
    private GeisternetzDAO geisternetzDAO;

    private List<Geisternetz> offeneNetze;

    @PostConstruct
    public void init() {
        offeneNetze = geisternetzDAO.findeOffeneNetze();
    }

    public List<Geisternetz> getOffeneNetze() { return offeneNetze; }
}
