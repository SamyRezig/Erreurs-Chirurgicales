
public class Salle {
	String nom;
	
	public Salle(String nom) {
		this.nom = nom;
	}
	
	public String getNom() {
		return this.nom;
	}
	
	@Override
	public String toString() {
		return this.nom;
	}
}
