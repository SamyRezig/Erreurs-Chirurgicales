import java.util.List;

public class Chevauchement extends Conflit {

	
	
	
	public Chevauchement(Chirurgie first, Chirurgie second) {
		super(first, second);
	}

	@Override
	public void resoudreConflit(Chirurgie first, Chirurgie second, List<Chirurgien> lc, List<Salle> ls) {
		
	}

}
