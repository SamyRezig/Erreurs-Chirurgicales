import java.util.List;
import java.util.Scanner;
import java.util.Random;
import java.util.Set;

public abstract class Conflit {
	private Chirurgie premiereChirurgie;	// Chirurgie en conflit avec la seconde chirurgie
	private Chirurgie secondeChirurgie;		// Chirurgie en conflit avec la premiere chirurgie

	/**
	  * @return true si les deux chirurgies sont toujours en conflit
	  * et false sinon.
	  */
	public abstract boolean persiste();

	/**
	  * @return true si la liste de chirurgiens et la liste de salles sont assez
	  * longue pour permettre un changement de ressource selon le type de coonflit
	  */
	public abstract boolean ressourcesSuffisantes(List<Chirurgien> lc, List<Salle> ls);

	/**
	  * @param lc la liste de chirurgiens utilisables pour la modification.
	  * @param ls la lsite de salles utilisables pour la modification.
	  */
	public abstract void modifierChirurgie(List<Chirurgien> lc, List<Salle> ls);

	/**
	  * Constructeur principal. Les parametres sont interchangeables. L'ordre
	  * sera fixe au moment de la correction.
	  / @param first premiere chirurgie en conflit
	  * @param second seconde chirrugie en conflit
	  */
	public Conflit(Chirurgie first, Chirurgie second) {
		this.premiereChirurgie = first;
		this.secondeChirurgie = second;
		//this.premiereChirurgie.setCorrige();
		//this.secondeChirurgie.setCorrige();
	}

	/**
	  * Getter pour la premiere chirurgie.
	  * @return la premiere chirurgie en conflit
	  */
	public Chirurgie getPremiereChirurgie() {
		return this.premiereChirurgie;
	}

	/**
	  * Getter pour la seconde chirurgie.
	  * @return la seconde chirurgie en conflit
	  */
	public Chirurgie getSecondeChirurgie() {
		return this.secondeChirurgie;
	}

	/**
	  * Visualiser un conflit en ligne de commandes.
	  */
	public void visualiser() {
		System.out.print(this.getClass() + "\n" + this.premiereChirurgie);
		this.premiereChirurgie.visualisation();

		System.out.print(this.secondeChirurgie);
		this.secondeChirurgie.visualisation();

		System.out.println();
	}

	/**
	  * Reordonner la premiere et seconde chirurgie en fonction de leur date de debut.
	  * La premiere doit commencer avant la seconde.
	  */
	public void reordonner() {
		Chirurgie tmp = null;	// Varaible de stockage
		if (! this.getPremiereChirurgie().commenceAvant(this.getSecondeChirurgie())) {
			tmp = this.getPremiereChirurgie();
			this.premiereChirurgie = this.getSecondeChirurgie();
			this.secondeChirurgie = tmp;
		}
	}

	/**
	  * Calculer un taux de superposition entre les 2 chirurgies en conflit.
	  * @return minimum entre le ration duree de l'intersection et duree
	  * de la chirurgie des deux operations.
	  */
	private double tauxSuperposition() {
		double dureeInter = this.getPremiereChirurgie().dureeIntersection(this.getSecondeChirurgie());
		double premierTaux = dureeInter / (double) this.getPremiereChirurgie().duree();
		double deuxiemeTaux = dureeInter / (double) this.getSecondeChirurgie().duree();

		// Prend le minimun des deux taux
		double resultat = (premierTaux > deuxiemeTaux) ? deuxiemeTaux : premierTaux;

		return resultat;
	}

	/**
	  * Resoudre le conflit.
	  * @param lc liste de chirurgiens utilisables pour resoudre ce conflit.
	  * @param ls liste de salles tulisables pour resoudre ce conflit.
	  */
    public void resoudreConflit(List<Chirurgien> lc, List<Salle> ls) {
		if (!this.persiste()) {		// Le conflit a pu etre resolu entre temps
			System.out.println("Le conflit entre les chirurgies " + this.getPremiereChirurgie().getId() + " et " + this.getSecondeChirurgie().getId() + " n'existe plus.");
			this.visualiser();
			return;
		}
		System.out.println("RESOLUTION DU CONFLIT entre " + this.getPremiereChirurgie().getId() + " et " + this.getSecondeChirurgie().getId() + " : ");
		System.out.println("Chirurgiens disponibles : \t" + lc + "\n" + "Salles dispnobles : \t\t" + ls);
		this.visualiser();

		// Nomalisation des deux chirurgies : de sorte a ce qu'elle ne commence plus
		// ou se termine a des horaires suspectes
		Correcteur.normaliserFin(this.getPremiereChirurgie());
		Correcteur.normaliserDebut(this.getSecondeChirurgie());

		// Resolution par modification des ressources
		// Les listes de chirurgiens/salles ne sont pas censees contenir le chirurgien / la salle a modifier !
		if (this.persiste() && this.ressourcesSuffisantes(lc, ls)) {
			System.out.println("----Modification de la ressource est possible");
			this.modifierChirurgie(lc, ls);
			Statistiques.plusModifRessource();
		} else {
			System.out.println("----Pas de modification de ressource envisageable");
		}

		// Resolution par decoupage
		double ts = this.tauxSuperposition();
		if (this.persiste() && this.tauxSuperposition() < 0.8
							&& (this.getPremiereChirurgie().dureeSuspecte() || this.getSecondeChirurgie().dureeSuspecte())
							&& (!this.getPremiereChirurgie().courte() || !this.getSecondeChirurgie().courte())) {
			System.out.println("----Decoupage des chirurgies");
			Correcteur.couperDuree(this.getPremiereChirurgie(), this.getSecondeChirurgie());
			Statistiques.plusDecoupe();
		} else {
			System.out.println("----Pas de decoupage de chirurgies");
		}

		// Resolution par decalage
		if (this.persiste()) {
			System.out.println("----Decalage d'une chirurgie");
			Correcteur.decalageChirurgie(this.getPremiereChirurgie(), this.getSecondeChirurgie());
			Statistiques.plusDecalage();
		} else {
			System.out.println("----Pas de decalage de chirurgie");
		}

		System.out.println("\nVoici le resultat final : ");
		this.visualiser();
		System.out.println();
	}

	@Override
	public String toString() {
		return this.getClass() + " -- " + this.premiereChirurgie + " avec " + this.secondeChirurgie;
	}
}
