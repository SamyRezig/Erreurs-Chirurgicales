import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Main {

	public static void main(String [] args) throws IOException {
		Agenda a = new Agenda("Chirurgies_v2.csv");

		System.out.println(a.getPlanning() + "\n");

		String dernier = a.derniereChirurgie().toString();

        a.resolution();
		//a.verifierChirurgies();
		a.descriptionCourante();

		System.out.println("Nombre de normalisation : " + Statistiques.nbNormalisation);
		System.out.println("Nombre de decoupages : " + Statistiques.nbDecoupage);
		System.out.println("Nombre de modifs ressources : " + Statistiques.nbRess);
		System.out.println("Nombre de decalages : " + Statistiques.nbDecalage);

		a.creerNouveauFichier();

		Map<String, List<Integer>> map = a.dataConflits();
		System.out.println(map);

		Graphique g = new Graphique();
		Graphique.valeurs = map;
		g.afficher(args);

	}

}
