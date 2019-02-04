import java.util.List;

public class Interference extends Conflit {
	
	public Interference(Chirurgie first, Chirurgie second) {
		super(first, second);
	}

	@Override
	public void resoudreConflit(List<Chirurgien> lc, List<Salle> ls) {
		//LocalDate ld = super.getPremiereChirurgie().getDatesOperation().getDateDebut().toLocalDate();
		Salle tmpSalle = null;
		for(Salle s : ls) {
			if(!this.getPremiereChirurgie().getSalle().equals(s)) {
				tmpSalle = s;
			}
		}
		Correcteur.changerSalle(getSecondeChirurgie(), tmpSalle);
	}

}
