
public class Main {

	public static void main(String[] args) {
		Agenda a = new Agenda("Chirurgies_v2.csv");

		System.out.println(a.getPlanning() + "\n");

		System.out.println("----------CONFLITS-------------");
		a.montrerConflits();

		//System.out.println(a.extraireConflits());

		System.out.println("moyenne : " + a.stats.getDureeMoyenne());
		System.out.println("premier quartile : " + a.stats.getPremierQuartile());
		System.out.println("mediane : " + a.stats.getMediane());
		System.out.println("dernier quartile : " + a.stats.getDernierQuartile());
		System.out.println();

        a.resolutionCommentee();
        a.verifierChirurgies();
        
        

	}
}
