import java.time.Duration;
import java.util.List;

import java.util.Random;
import java.util.Set;

public class Ubiquite extends Conflit {

	/**
	  * Appelle le constructeur de la classe Conflit seulement
	  */
	public Ubiquite(Chirurgie first, Chirurgie second) {
		super(first, second);
  	}

	@Override
	public boolean persiste() {
		return this.getPremiereChirurgie().estUbiquite(this.getSecondeChirurgie());
	}

	public boolean ressourcesSuffisantes(List<Chirurgien> lc, List<Salle> ls) {
		return (lc.size() >= 1);	// La liste de chirurgiens n'est pas censee contenir le chirurgien qu'on veut changer
	}

	/**
	  * Cherche le chirurgien de remplcement depuis la liste donne.
	  * @param lc la liste de chirurgiens utilisables.
	  */
	public void modifierChirurgie(List<Chirurgien> lc, List<Salle> ls) {
		Chirurgien tmpChirurgien = null;
		//Change de chirurgien
		for(Chirurgien c : lc) {
			if(!this.getPremiereChirurgie().getChirurgien().equals(c)) {
				tmpChirurgien = c;
				break;
			}
		}
		if (tmpChirurgien != null)	Correcteur.changerChirurgien(this.getSecondeChirurgie(),  tmpChirurgien);
	}
}
