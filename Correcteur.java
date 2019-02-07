public class Correcteur {
	// Methodes pour corriger une chirurgie.

	// Translater l'intervalle de temps d'une chirurgie
	public static void translater(Chirurgie courante, long biaisMinutes) {
		courante.getDatesOperation().translater(biaisMinutes);
	}

	// Reduire l'intervalle de temps d'une chirurgie par la fin
	public static void reduireFin(Chirurgie courante, long biaisMinutes) {
		courante.reduireFin(biaisMinutes);
	}

	// Reduire l'intervalle de temps par la fin
	public static void reduireDebut(Chirurgie courante, long biaisMinutes) {
		courante.reduireDebut(biaisMinutes);
	}

	// Changer le chirurgien

	public static void changerChirurgien(Chirurgie courante, Chirurgien ch) {
		courante.setChirurgien(ch);
	}

	// Changer la salle

	public static void changerSalle(Chirurgie courante, Salle s) {
		courante.setSalle(s);
	}
}
