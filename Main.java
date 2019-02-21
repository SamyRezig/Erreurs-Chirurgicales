import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Main {

	public static void main(String [] args) {
		Agenda a = new Agenda("Chirurgies_v2.csv");

		System.out.println(a.getPlanning() + "\n");

		String dernier = a.derniereChirurgie().toString();

		System.out.println("moyenne : " + a.stats.getDureeMoyenne());
		System.out.println("premier quartile : " + a.stats.getPremierQuartile());
		System.out.println("mediane : " + a.stats.getMediane());
		System.out.println("dernier quartile : " + a.stats.getDernierQuartile());
		System.out.println();

        a.resolutionCommentee();
        a.verifierChirurgies();

		System.out.println("Nombre de normalisation : " + Statistiques.nbNormalisation);
		System.out.println("Nombre de decoupages : " + Statistiques.nbDecoupage);
		System.out.println("Nombre de modifs ressources : " + Statistiques.nbRess);
		System.out.println("Nombre de decalages : " + Statistiques.nbDecalage);

		System.out.println("\nLa derniere chirurgie :");
		System.out.println("Avant correction : " + dernier);
		System.out.println("Apres correction : " + a.derniereChirurgie());

		//Graphique.afficher(args);
		Map<String, List<Integer>> map = a.dataConflits();
		System.out.println(map);

		Graphique g = new Graphique();
		Graphique.valeurs = map;
		g.afficher(args);
		//Fenetre.graphe = g;

		//Fenetre f = new Fenetre();
		//f.afficher(args);

	}

}
