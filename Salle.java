
public class Salle {
	private String nom;	// Nom de la salle.

	/**
	  * Constructeur principal.
	  * @param nom nom de la salle.
	  */
	public Salle(String nom) {
		this.nom = nom;
	}

	/**
	  * Getter pour le nom de la salle
	  */
	public String getNom() {
		return this.nom;
	}

	/**
	  * @return true si la salle est reservee aux urgence et false sinon.
	  */
    public boolean estUrgence() {
        return this.nom.split("-")[1].startsWith("U");
    }

	@Override
	public int hashCode() {
		return this.nom.hashCode();
	}

	@Override
	/**
	  * Egalite sur le nom de la salle
	  */
	public boolean equals(Object o) {
		if (o != null && o.getClass().equals(this.getClass())) {
			Salle s = (Salle) o;
			return s.nom.equals(this.nom);
		}
		return false;
	}

	@Override
	public String toString() {
		return this.nom;
	}
}
