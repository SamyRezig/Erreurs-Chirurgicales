import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Main {

	public static void main(String [] args) throws IOException {
		Agenda a = new Agenda("Chirurgies_v2.csv");

        a.resolution();
		//a.verifierChirurgies();
        
        // Description statistique
		a.descriptionCourante();

		System.out.println("Nombre de normalisation : \t" + Statistiques.nbNormalisation);
		System.out.println("Nombre de decoupages : \t\t" + Statistiques.nbDecoupage);
		System.out.println("Nombre de modifs ressources : \t" + Statistiques.nbRess);
		System.out.println("Nombre de decalages : \t\t" + Statistiques.nbDecalage);
		System.out.println(" === Nombre de corrections : \t" + Statistiques.nbCorrection);

		a.creerNouveauFichier();

		// Liste avec les nombres de conflits a chaque iteration
		a.afficherConflitsTotaux();

		// Le nombre de conflits restant par journee
		a.afficherJoursConflit();

		// Appeler la methode pour afficher le graphique !
		
		/*Graphique g = new Graphique();
		Graphique.valeurs = map;
		g.afficher(args);*/



	}

}
