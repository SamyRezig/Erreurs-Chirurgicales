import java.time.Duration;
import java.util.List;

public class Interference extends Conflit {
	
	public Interference(Chirurgie first, Chirurgie second) {
		super(first, second);
	}

	@Override
	public void resoudreConflit(List<Chirurgien> lc, List<Salle> ls) {
                long dureeChevauchement = Duration.between(this.getPremiereChirurgie().getDatesOperation().getDateFin(), this.getSecondeChirurgie().getDatesOperation().getDateDebut()).toMinutes();
                Salle tmpSalle = null;
		for(Salle s : ls) {
			if(!this.getPremiereChirurgie().getSalle().equals(s)) {
				tmpSalle = s;
			}
		}
		Correcteur.changerSalle(getSecondeChirurgie(), tmpSalle);
	}

}
