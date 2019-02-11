import java.util.List;

public abstract class Conflit {


	private Chirurgie firstChirurgie;
	private Chirurgie secondChirurgie;

	public abstract void resoudreConflit(List<Chirurgien> lc, List<Salle> ls);
	public abstract boolean persiste();
	public abstract void modifierChirurgie(List<Chirurgien> lc, List<Salle> ls);	// Mettre le code de resolutionClonflit() dedans

	public Conflit(Chirurgie first, Chirurgie second) {
		this.firstChirurgie=first;
		this.secondChirurgie=second;
	}

	public String toString() {
		return this.getClass() + " -- " + this.firstChirurgie + " avec " + this.secondChirurgie;
	}

	public Chirurgie getPremiereChirurgie() {
		return this.firstChirurgie;
	}

	public Chirurgie getSecondeChirurgie() {
		return this.secondChirurgie;
	}

	public void visualiser() {
		System.out.print(this.getClass() + "\n" + this.firstChirurgie);
		this.firstChirurgie.visualisation();

		System.out.print(this.secondChirurgie);
		this.secondChirurgie.visualisation();

		System.out.println();
	}

	public void modifierHeuresSuspectes() {
		if (this.firstChirurgie.heureDebutSuspecte()) {
			Correcteur.modifierHoraire(this.firstChirurgie);
		}
	}

	// Pour resoudre un conflit :
		// les deux chirurgies sont toujours en conflit ?
			// non : return;
		// On examine les durees des deux chirurgies
		// Changer celle qui sont suspectes
		// Verifier si le conflit persiste toujours
			// si oui : changer de salles/chirurgiens

}
