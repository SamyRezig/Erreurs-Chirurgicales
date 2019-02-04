import java.util.List;

public class Chevauchement extends Conflit {

	
	
	
	public Chevauchement(Chirurgie first, Chirurgie second) {
		super(first, second);
	}

	@Override
	public void resoudreConflit(List<Chirurgien> lc, List<Salle> ls) {
		Chirurgien tmpChirurgien = null;
		for(Chirurgien c : lc) {
			if(!this.getPremiereChirurgie().getChirurgien().equals(c)) {
				tmpChirurgien = c;
				break;
			}
		}
		Salle tmpSalle = null;
		for(Salle s : ls) {
			if(!this.getPremiereChirurgie().getSalle().equals(s)) {
				tmpSalle = s;
			}
		}
		
		Correcteur.changerChirurgien(getSecondeChirurgie(), tmpChirurgien);
		Correcteur.changerSalle(getSecondeChirurgie(), tmpSalle);
	}

}
