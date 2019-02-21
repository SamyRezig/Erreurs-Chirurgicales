
public class Chirurgien {
	private String nom;
	
	public Chirurgien(String nom) {
		this.nom = nom;
	}
	
	public String getNom() {
		return this.nom;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this.getClass() == o.getClass()) {
			return this.nom.equals(((Chirurgien)o).nom);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return this.nom;
	}
}
