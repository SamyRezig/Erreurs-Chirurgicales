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
    public void resoudreConflit(List<Chirurgien> lc, List<Salle> ls) {
        if (!this.persiste())   return ;

        Salle tmpSalle = null;

        if (lc.size() == 1 || ls.size() == 1) {
            long dureeChevauchement = Duration.between(this.getPremiereChirurgie().getDatesOperation().getDateFin(),
                                                this.getSecondeChirurgie().getDatesOperation().getDateDebut()).toMinutes();
            Correcteur.translater(this.getSecondeChirurgie(), dureeChevauchement);

        } else {
            for (Salle s : ls) {
                if (!this.getPremiereChirurgie().getSalle().equals(s)) {
                    tmpSalle = s;
                }
            }
            if (tmpSalle != null)	Correcteur.changerSalle(this.getSecondeChirurgie(), tmpSalle);
        }
    }

}
