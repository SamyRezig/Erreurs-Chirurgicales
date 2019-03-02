import java.util.List;
import java.io.IOException;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {

	// Demander un nombre entre deux entiers.
	private static int demanderNombreEntre(int inf, int sup) {
		Scanner demande = new Scanner(System.in);
		int reponse = -1;

		while (reponse == -1) {
			System.out.print("Votre reponse : ");
			try {
				reponse = demande.nextInt();
				if (reponse < inf || reponse > sup) {	// Le nombre n'est pas dans l'intervalle.
					System.out.println("Ce nombre n'est pas compris dans l'intervalle.");
					reponse = -1;
				}

			} catch (InputMismatchException e) {
				// Un caractere a ete saisi
				demande.nextLine();
				reponse = -1;
				System.out.println("Ceci n'est pas un nombre !");
			}
		}

		return reponse;
	}
	private static Agenda corrigerFichier(int choix) throws IOException {
		Agenda a = null;
		// Choix du fichier
		switch (choix) {
			case 1:
				a = new Agenda("MiniBase(1)a.csv");
				break;
			case 2:
				a = new Agenda("Chirurgies_v2.csv");
				break;
		}

		// Affichage des tatistiques et creation d'un nouveau fichier
		// avec les chirurgies corrigees. Un graphique s'affiche avec l'evolution des conflits.
		if (a != null) {
			a.resolution();

			List<Chirurgie> listeChirurgies = a.extraireListeChirurgies();
			a.creerNouveauFichier(listeChirurgies);

			Main.afficherStatistiques(a);

			System.out.println("\nFermer le graphique pour continuer.");
			a.stats.afficherGraphique(null);
		}
		return a;
	}

	// Afficher les de premier plan statistiques.
	private static void afficherStatistiques(Agenda a) throws IOException {
		System.out.println();
		a.comparaisonStats();					// Les comparaisons avant-apres corrections.
		System.out.println();
		a.stats.afficherNombreCorrections();	// Les nombres des differents types de corrections.
		System.out.println();
		a.stats.afficherConflitsTotaux();		// L'evolution des conflits en liste.
	}

	// Afficher les statistiques de second plan.
	private static void afficherStatistiquesPlus(Agenda a) {
		System.out.println("Classement des heures avec le plus de conflits avant correction :");
		a.stats.getHeuresConflits().stream()
									.limit(20)
									.forEach( x -> System.out.println("\t" + x));

		System.out.println("\nLa duree moyenne de travail de chaque chirurgien en minutes pour une chirurgie : ");
		a.stats.getDureeParChirurgien().entrySet().stream()
												.forEach( x -> System.out.println("\t" + x.getKey() + " = " + x.getValue() + " min"));

		System.out.println("\nLa duree moyenne de travail dans chaque salle en minutes pour une chirurgie : ");
		a.stats.getDureeParSalle().entrySet().stream()
												.forEach( x -> System.out.println("\t" + x.getKey() + " = " + x.getValue() + " min"));;
	}

	// Lancer une action sur l'agenda en fonctino de l'entier donne.
	private static void lancerAction(int numero, Agenda a) throws IOException {
		switch (numero) {
			case 1:	// Reafficher les statistiques
				Main.afficherStatistiques(a);
				break;
			case 2:	// Afficher l'evolution des corrections des chirurgies
				Correcteur.getHistoriqueChirurgies().afficher();
				break;
			case 3:	// Afficher les chirurgies suspectes (trop courte ou trop longue
				a.verifierChirurgies();
				break;
			case 4:	// Afficher le planning des chirurgiens
				a.stats.afficherJoursTravailPlannifie(a);
				break;
			case 5:	// Afficher le planning des salles utilisees
				a.stats.afficherJoursTravailSalles(a);
				break;
			case 6:	// Afficher des statistiques complementaires
				Main.afficherStatistiquesPlus(a);
				break;
			case 7:	 // quitter l'application : il n'y a rien a faire
				System.out.println("Fermeture de l'application.");
				break;
		}
	}

	public static void main(String [] args) throws IOException {
		Agenda a;
		int reponse = -1;

		// Choix du fichier.
		System.out.println("\nChoisissez un fichier :");
		System.out.println("\t1- MiniBase(1).csv");
		System.out.println("\t2- Chirurgies_v2.csv");

		reponse = Main.demanderNombreEntre(1, 2);		// Demander la reponse
		a = Main.corrigerFichier(reponse);				// Execution de la reponse

		// Choix des actions.
		reponse = -1;
		while (reponse != 7) {
			System.out.println("\n=======================================================================================================");
			System.out.println("\nChoisissez une action : ");
			System.out.println("\t1- Reafficher les statistiques");
			System.out.println("\t2- Afficher l'evolution des corrections des chirurgies");
			System.out.println("\t3- Afficher les chirurgies suspectes (trop courte ou trop longue)");
			System.out.println("\t4- Afficher le planning des chirurgiens");
			System.out.println("\t5- Afficher le planning des salles utilisees");
			System.out.println("\t6- Afficher des statistiques complementaires");
			System.out.println("\t7- Quitter.");

			reponse = Main.demanderNombreEntre(1, 7);		// Demander la reponse
			System.out.println();
			Main.lancerAction(reponse, a);					// Execution de la reponse
		}
	}

}
