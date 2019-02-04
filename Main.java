public class Main {

	public static void main(String[] args) {
		Agenda a = new Agenda("MiniBase(1).csv");

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
		System.out.println("dernier quartile : " + a.stats.getPremierQuartile());

		System.out.println("tout :");
		a.stats.afficheTout();

	}
}