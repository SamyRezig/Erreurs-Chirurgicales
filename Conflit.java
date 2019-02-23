import java.util.List;
import java.util.Scanner;
import java.util.Random;

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
		this.firstChirurgie.setCorrige();
		this.secondChirurgie.setCorrige();
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

	private double tauxSuperposition() {
		double dureeInter = this.getPremiereChirurgie().dureeIntersection(this.getSecondeChirurgie());
		double premierTaux = dureeInter / (double) this.getPremiereChirurgie().duree();
		double deuxiemeTaux = dureeInter / (double) this.getSecondeChirurgie().duree();

		// Prend le minimun des deux taux
		double resultat = (premierTaux > deuxiemeTaux) ? deuxiemeTaux : premierTaux;

		return resultat;
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
		double ts = this.tauxSuperposition();
		if (this.persiste() && this.tauxSuperposition() < 0.8 && (this.getPremiereChirurgie().dureeSuspecte() || this.getSecondeChirurgie().dureeSuspecte()) && (!this.getPremiereChirurgie().courte() || !this.getSecondeChirurgie().courte())) {
			System.out.println("----Decoupage des chirurgies -- ts = " + ts);
			Correcteur.couperDuree(this.getPremiereChirurgie(), this.getSecondeChirurgie());
			Statistiques.nbDecoupage++;
		} else {
			System.out.println("----Pas de decoupage de chirurgies -- ts = " + ts);
		}

		// Resolution par modification des ressources
		if (this.persiste() && this.ressourcesSuffisantes(lc, ls) && ((new Random()).nextDouble() <= 0.85 ) ) {
			System.out.println("----Modification de la ressource est possible");
			this.modifierChirurgie(lc, ls);
			Statistiques.nbRess++;
		} else {
			System.out.println("----Pas de modification de ressource envisageable");
		}

		// Resolution par decalage
		if (this.persiste()) {
			System.out.println("----Decalage d'une chirurgie");
			Correcteur.decalageChirurgie(this.getPremiereChirurgie(), this.getSecondeChirurgie());
			Statistiques.nbDecalage++;
		} else {
			System.out.println("----Pas de decalage de chirurgie");
		}

		System.out.println("Voici le resultat final : ");
		this.visualiser();

	}

}
