import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
  * Classe representant un chevauchement entre deux chirurgies. Cela signifie que
  * les deux chirurgies s'intersectent, partagent la meme salle et le chirurgien.
  * @author Samy Rezig
  * @author Yves Tran
  * @see Conflit
  * @see Ubiquite
  * @see Interference
  */
public class Chevauchement extends Conflit {

	/**
	  * Appelle le constructeur de la classe Conflit seulement
	  */
	public Chevauchement(Chirurgie first, Chirurgie second) {
		super(first, second);
	}

	@Override
	public boolean persiste() {
		return this.getPremiereChirurgie().estChevauchement(this.getSecondeChirurgie());
	}

	@Override
	public boolean ressourcesSuffisantes(List<Chirurgien> lc, List<Salle> ls) {
		// Les listes de chirurgiens/salles ne sont plus censees contenir le chirurgien / la salle a modifier
		return (lc.size() >= 1) || (ls.size() >= 1);
	}

	@Override
	/**
	  * Cherche le chirurgien et la salle de remplcement depuis les listes donnes.
	  * @param lc la liste de chirurgiens utilisables.
	  * @param ls la liste de salles utilisables.
	  */
	public void modifierChirurgie(List<Chirurgien> lc, List<Salle> ls) {
		Chirurgien tmpChirurgien = null;
        Salle tmpSalle = null;

		// Recherche du chirurgien
		if(lc.size() >= 2){
			for(Chirurgien c : lc) {
				if(!this.getPremiereChirurgie().getChirurgien().equals(c)) {
					tmpChirurgien = c;
					break;
				}
			}
		}

		// Recherche de la salle
		if (ls.size() >= 2) {
			for(Salle s : ls) {
				if(!this.getPremiereChirurgie().getSalle().equals(s)) {
					tmpSalle = s;
					break;
				}
			}
		}
		if (tmpSalle != null)	Correcteur.changerSalle(this.getSecondeChirurgie(), tmpSalle);
		if (tmpChirurgien != null)	Correcteur.changerChirurgien(this.getSecondeChirurgie(), tmpChirurgien);

	}

}
