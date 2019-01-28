import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Ubiquite extends Conflit {

	

	public Ubiquite(Chirurgie first, Chirurgie second) {
		super(first, second);
  	}

	@Override
	public void resoudreConflit( List<Chirurgien> lc, List<Salle> ls) {
		LocalDate ld = super.getFirst().getDatesOperation().getDateDebut().toLocalDate();
		if(lc.size() == 1) {
			//Deplace les horaires
		}else {
			//Change de chirurgien
		}
		
		
	}

	
	/**
	 * Modifier le chirurgien
	 * Modifier l'heure
	 */
}
