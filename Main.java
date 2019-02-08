import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Agenda a = new Agenda("Chirurgies_v2.csv");

		// System.out.println(a.getListConflits().size());
		// System.out.println(a.getChirurgiensDispos());
		// System.out.println(a.getListeSalles());

		System.out.println(a.getPlanning() + "\n");

		System.out.println("----------CONFLITS-------------");
		a.montrerConflits();

		System.out.println(a.extraireConflits());

		a.statistiques();
		System.out.println("moyenne : " + a.stats.getDureeMoyenne());
		System.out.println("premier quartile : " + a.stats.getPremierQuartile());
		System.out.println("mediane : " + a.stats.getMediane());
		System.out.println("dernier quartile : " + a.stats.getDernierQuartile());

                //a.resolution();
		a.statistiques();
		a.stats.dureeParChirurgien();


		/*System.out.println("tout :");
		a.stats.afficheTout();*/

                //IntervalleTemps t = new IntervalleTemps("01/01/2019", "12:00:00", "01/01/2019", "11:00:00");
                //System.out.println(t);

	}
}
