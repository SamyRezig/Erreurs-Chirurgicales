import java.time.Duration;

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
	
	// premiere doit commencer avant seconde
	public static void couperDuree(Chirurgie premiere, Chirurgie seconde) {
		long dureeInter = Duration.between(premiere.getDatesOperation().getDateFin(),
								seconde.getDatesOperation().getDateDebut()).toMinutes();
		
		double tauxSuspect1 = premiere.tauxSuspectFin(dureeInter);
		double tauxSuspect2 = seconde.tauxSuspectDebut(dureeInter);
		
		if (tauxSuspect1 > tauxSuspect2) {
			Correcteur.reduireFin(premiere, dureeInter);
		} else {
			Correcteur.reduireDebut(seconde, dureeInter);
		}
	}
	
	public static void modifierHoraire(Chirurgie courante) {
		
	}
}
