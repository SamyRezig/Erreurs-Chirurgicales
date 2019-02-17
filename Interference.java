import java.time.Duration;
import java.util.List;

import java.util.Random;

public class Interference extends Conflit {

    public Interference(Chirurgie first, Chirurgie second) {
	super(first, second);
    }

    @Override
	public boolean persiste() {
		return this.getPremiereChirurgie().estInterference(this.getSecondeChirurgie());
	}

    @Override
    public boolean ressourcesSuffisantes(List<Chirurgien> lc, List<Salle> ls) {
		return (ls.size() >= 3);
	}

    @Override
    public void modifierChirurgie(List<Chirurgien> lc, List<Salle> ls) {
    	Salle tmpSalle = null;
        for (Salle s : ls) {
            if (!this.getPremiereChirurgie().getSalle().equals(s)) {
                tmpSalle = s;
                ls.remove(tmpSalle);
                ls.add(tmpSalle);
                break;
            }
        }
        if (tmpSalle != null)	Correcteur.changerSalle(this.getSecondeChirurgie(), tmpSalle);

    }

}
