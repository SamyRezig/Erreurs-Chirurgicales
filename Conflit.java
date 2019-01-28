import java.util.List;

public abstract class Conflit {
	
	
	private Chirurgie firstChirurgie;
	private Chirurgie secondChirurgie;
	
	public Conflit(Chirurgie first, Chirurgie second) {
		this.firstChirurgie=first;
		this.secondChirurgie=second;
	}
	
	public String toString() {
		return this.getClass() + " -- " + this.firstChirurgie + " avec " + this.secondChirurgie;
	}
	
	public abstract void resoudreConflit(Chirurgie first,Chirurgie second, List<Chirurgien> lc, List<Salle> ls);
	
	/*
	 * Ubiquite -> changer chirurgien
	 * 			-> deplacer heure chg
	 * 
	 * Interference -> changer salle
	 * 				-> deplacer heure chg
	 * 
	 * Chevauchement -> deplacer chirurgien et salle
	 * */
}
