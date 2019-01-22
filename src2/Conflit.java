
public abstract class Conflit {
	
	
	private Chirurgie firstChirurgie;
	private Chirurgie secondChirurgie;
	
	public Conflit(Chirurgie first, Chirurgie second) {
		this.firstChirurgie=first;
		this.secondChirurgie=second;
	}
	
	public abstract void resoudreConflit(Chirurgie first,Chirurgie second);
}
