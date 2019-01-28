import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Ubiquite extends Conflit {

	

	public Ubiquite(Chirurgie first, Chirurgie second) {
		super(first, second);
  	}

	@Override
	public void resoudreConflit(Chirurgie first, Chirurgie second, List<Chirurgien> lc, List<Salle> ls) {
		LocalDate ld = first.getDatesOperation().getDateDebut().toLocalDate();
		List<Chirurgien> chirurgienDispo = new ArrayList<>();
		//chirurgienDispo=
		
		
	}

	
	/**
	 * Modifier le chirurgien
	 * Modifier l'heure
	 */
}
