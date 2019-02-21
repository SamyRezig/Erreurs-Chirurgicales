
public class Salle {
	private String nom;
	
	public Salle(String nom) {
		this.nom = nom;
	}
	
	public String getNom() {
		return this.nom;
	}
	
        public boolean estUrgence() {
            return this.nom.split("-")[1].startsWith("U");
        }

         
	@Override
	public String toString() {
		return this.nom;
	}
}
