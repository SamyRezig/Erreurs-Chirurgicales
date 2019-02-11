import java.time.Duration;
import java.util.List;

import java.util.Random;

public class Ubiquite extends Conflit {

	public Ubiquite(Chirurgie first, Chirurgie second) {
		super(first, second);
  	}

	@Override
	public boolean persiste() {
		return this.getPremiereChirurgie().estUbiquite(this.getSecondeChirurgie());
	}

	@Override
	public void resoudreConflit(List<Chirurgien> lc, List<Salle> ls) {
		if (!this.persiste())   return ;

		System.out.println(this);
		this.modifierChirurgie(lc,ls);
		System.out.println(this);
	}
	
	public void modifierChirurgie(List<Chirurgien> lc, List<Salle> ls) {
		//LocalDate ld = super.getPremiereChirurgie().getDatesOperation().getDateDebut().toLocalDate();
		Chirurgien tmpChirurgien = null;
		if(lc.size() == 1) {
			//Deplace les horaires
			long duree = this.getSecondeChirurgie().duree();
			long dureeChevauchement = Duration.between(this.getPremiereChirurgie().getDatesOperation().getDateFin(), this.getSecondeChirurgie().getDatesOperation().getDateDebut()).toMinutes();
			// heure fin first - heure debut seconde
			Correcteur.translater(getSecondeChirurgie(), dureeChevauchement + 15); // + temps de pause

		} else {
			//Change de chirurgien
			for(Chirurgien c : lc) {
				if(!this.getPremiereChirurgie().getChirurgien().equals(c)) {
					tmpChirurgien = c;
					break;
				}
			}
			if (tmpChirurgien != null)	Correcteur.changerChirurgien(getSecondeChirurgie(), tmpChirurgien);
		}
	}




	/**
	 * Modifier le chirurgien
	 * Modifier l'heure
	 */
}
