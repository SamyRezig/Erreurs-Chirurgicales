import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Main {

	public static void main(String [] args) throws IOException {
		Agenda a = new Agenda("MiniBase(1).csv");


        a.resolution();
		//a.verifierChirurgies();
		a.descriptionCourante();

		System.out.println("Nombre de normalisation : \t" + Statistiques.nbNormalisation);
		System.out.println("Nombre de decoupages : \t\t" + Statistiques.nbDecoupage);
		System.out.println("Nombre de modifs ressources : \t" + Statistiques.nbRess);
		System.out.println("Nombre de decalages : \t\t" + Statistiques.nbDecalage);
		System.out.println(" === Nombre de corrections : \t" + Statistiques.nbCorrection);

		a.creerNouveauFichier();

		Map<String, List<Integer>> map = a.dataConflits();
		System.out.println("Chevauchement :\t" + map.get("Chevauchement"));
		System.out.println("Interference : \t" + map.get("Interference"));
		System.out.println("Ubiquite : \t" + map.get("Ubiquite"));
		System.out.println("Total : \t" + map.get("Total"));

		a.afficherJoursConflit();

		Graphique g = new Graphique();
		Graphique.valeurs = map;
		g.afficher(args);



	}

}
