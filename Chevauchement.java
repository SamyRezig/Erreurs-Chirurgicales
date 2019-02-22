import java.time.Duration;
import java.util.List;
import java.util.Random;

public class Chevauchement extends Conflit {

	public Chevauchement(Chirurgie first, Chirurgie second) {
		super(first, second);
	}

	@Override
	public boolean persiste() {
		return this.getPremiereChirurgie().estChevauchement(this.getSecondeChirurgie());
	}

	@Override
	public boolean ressourcesSuffisantes(List<Chirurgien> lc, List<Salle> ls) {
		return (lc.size() >= 3) || (ls.size() >= 3);
	}



	public void modifierChirurgie(List<Chirurgien> lc, List<Salle> ls) {
		Chirurgien tmpChirurgien = null;
        Salle tmpSalle = null;

		if(lc.size() >= 2){
			for(Chirurgien c : lc) {
				if(!this.getPremiereChirurgie().getChirurgien().equals(c)) {
					tmpChirurgien = c;
					lc.remove(tmpChirurgien);
					lc.add(tmpChirurgien);
					break;
				}
			}
		}

		if (ls.size() >= 2) {
			for(Salle s : ls) {
				if(!this.getPremiereChirurgie().getSalle().equals(s)) {
					tmpSalle = s;
					ls.remove(tmpSalle);
					ls.add(tmpSalle);
					break;
				}
			}
		}
		if (tmpSalle != null)	Correcteur.changerSalle(this.getSecondeChirurgie(), tmpSalle);
		if (tmpChirurgien != null)	Correcteur.changerChirurgien(this.getSecondeChirurgie(), tmpChirurgien);

	}

}
