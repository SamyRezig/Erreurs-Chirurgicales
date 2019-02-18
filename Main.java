
public class Main {

	public static void main(String[] args) {
		Agenda a = new Agenda("Chirurgies_v2.csv");

		// System.out.println(a.getListConflits().size());
		// System.out.println(a.getChirurgiensDispos());
		// System.out.println(a.getListeSalles());

		System.out.println(a.getPlanning() + "\n");

		System.out.println("----------CONFLITS-------------");
		a.montrerConflits();

		//System.out.println(a.extraireConflits());

		System.out.println("moyenne : " + a.stats.getDureeMoyenne());
		System.out.println("premier quartile : " + a.stats.getPremierQuartile());
		System.out.println("mediane : " + a.stats.getMediane());
		System.out.println("dernier quartile : " + a.stats.getDernierQuartile());
		System.out.println();

        a.resolution();

		a.stats.dureeParSalle();
		a.stats.dureeParChirurgien();


        /*List<Conflit> lc = a.extraireConflits();
        System.out.println(lc.size());

		Conflit c = lc.get(0);

		System.out.println(c);
		c.resoudreConflit(null, null);
		System.out.println("Conflit resolu !!!!!!!!!!!!!!!");
		System.out.println(c);*/

		/*Chirurgie c1 = lc.get(2).getPremiereChirurgie();
		Chirurgie c2 = lc.get(2).getSecondeChirurgie();

		if (c1.getDatesOperation().getDateDebut().isBefore(c2.getDatesOperation().getDateDebut())) {
			lc.get(2).visualiser();
			Correcteur.decalageChirurgie(c1, c2);
			lc.get(2).visualiser();
		}else {
			lc.get(2).visualiser();
			Correcteur.decalageChirurgie(c2, c1);
			lc.get(2).visualiser();
		}*/


	}
}
