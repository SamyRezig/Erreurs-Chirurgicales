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

	public boolean ressourcesSuffisantes(List<Chirurgien> lc, List<Salle> ls) {
		return (lc.size() >= 3);
	}


	public void modifierChirurgie(List<Chirurgien> lc, List<Salle> ls) {
		//LocalDate ld = super.getPremiereChirurgie().getDatesOperation().getDateDebut().toLocalDate();

		  /*Chirurgie tmpChirurgie = this.getSecondeChirurgie().clone();
		  if(tmpChirurgie.dureeSuspecte()){
		  		long dureeReduite = tmpChirurgie.duree() - 104; // 104 = dureeMoyenne
		  		Correcteur.reduireDebut(tmpChirurgie, dureeReduite);
		  		if(this.getPremiereChirurgie().estUbiquite(tmpChirurgie)){
		  			//On modifie le chirurgien ou la décale
		  		}else{
		  			//Copie des infos de tmpChirurgie
		 			this.getSecondeChirurgie().setSalle(tmpChirurgie.getSalle());
		 			this.getSecondeChirurgie().setChirurgien(tmpChirurgie.getChirurgien());
		 			this.getSecondeChirurgie().getDatesOperation().reduireDebut(dureeReduite);

		  		}
		  }*/

		Chirurgien tmpChirurgien = null;
		//Change de chirurgien
		for(Chirurgien c : lc) {
			if(!this.getPremiereChirurgie().getChirurgien().equals(c)) {
				tmpChirurgien = c;
				lc.remove(tmpChirurgien);
				lc.add(tmpChirurgien);
				break;
			}
		}
		if (tmpChirurgien != null)	Correcteur.changerChirurgien(this.getSecondeChirurgie(), tmpChirurgien);


	}


}
