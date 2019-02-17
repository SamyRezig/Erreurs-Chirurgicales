import java.util.List;
import java.util.Scanner;

public abstract class Conflit {

	private Chirurgie firstChirurgie;
	private Chirurgie secondChirurgie;


	public abstract boolean persiste();

	public abstract boolean ressourcesSuffisantes(List<Chirurgien> lc, List<Salle> ls);

	public abstract void modifierChirurgie(List<Chirurgien> lc, List<Salle> ls); // Mettre le code de
																					// resolutionClonflit() dedans

	public Conflit(Chirurgie first, Chirurgie second) {
		this.firstChirurgie = first;
		this.secondChirurgie = second;

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

	private void reordonner() {
		Chirurgie tmp = null;
		if (! this.getPremiereChirurgie().commenceAvant(this.getSecondeChirurgie())) {
			tmp = this.getPremiereChirurgie();
			this.firstChirurgie = this.getSecondeChirurgie();
			this.secondChirurgie = tmp;
		}
	}


    public void resoudreConflit(List<Chirurgien> lc, List<Salle> ls) {
		this.reordonner();

		if (!this.persiste()) {
			System.out.println("Ce conflit n'existe plus.");
			return;
		}
		System.out.println("RESOLUTION DU CONFLIT avec :");
		System.out.println(lc);
		System.out.println(ls);
		this.visualiser();

		// Nomalisation des deux chirurgies : de sorte a ce qu'elle ne commence plus
		// ou se termine a des horaires suspectes
		Correcteur.normaliserFin(this.getPremiereChirurgie());
		Correcteur.normaliserDebut(this.getSecondeChirurgie());

		// Resolution par decoupage
		if (this.persiste() && (this.getPremiereChirurgie().dureeSuspecte() || this.getSecondeChirurgie().dureeSuspecte())) {
			System.out.println("----Decoupage des chirurgies");
			Correcteur.couperDuree(this.getPremiereChirurgie(), this.getSecondeChirurgie());
		} else {
			System.out.println("----Pas de decoupage de chirurgies");
		}

		// Resolution par modification des ressources
		if (this.persiste() && this.ressourcesSuffisantes(lc, ls)) {
			System.out.println("----Modification de la ressource est possible");
			this.modifierChirurgie(lc, ls);
		} else {
			System.out.println("----Pas de modification de ressource envisageable");
		}



		// Resolution par decalage
		if (this.persiste()) {
			System.out.println("----Decalage d'une chirurgie");
			Correcteur.decalageChirurgie(this.getPremiereChirurgie(), this.getSecondeChirurgie());
		} else {
			System.out.println("----Pas de decalage de chirurgie");
		}

		System.out.println("Voici le resultat final : ");
		this.visualiser();
		//(new Scanner(System.in)).nextLine();

	}

	// Pour resoudre un conflit :
	// les deux chirurgies sont toujours en conflit ?
	// non : return;
	// On examine les durees des deux chirurgies
	// Changer celle qui sont suspectes
	// Verifier si le conflit persiste toujours
	// si oui : changer de salles/chirurgiens

}
