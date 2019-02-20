import java.time.Duration;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalTime;

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

			// Si les chirurgies ne se chevauchent pas completement

			long dureeInter = premiere.dureeIntersection(seconde);
			//Sinon priviligié le changement de chirurgien / salle
			double tauxSuspect1 = premiere.tauxSuspect(dureeInter);
			double tauxSuspect2 = seconde.tauxSuspect(dureeInter);
			System.out.println(dureeInter + " -- " + tauxSuspect1 + " -- " + tauxSuspect2);
			
			/*if ((tauxSuspect1 < (double) dureeInter / (double) (15 + dureeInter)) && (tauxSuspect2 < (double) dureeInter / (double) (15 + dureeInter))) {
				System.out.println("--------Decoupage annule");
				(new Scanner(System.in)).nextLine();
				return;
			}*/

                            if (tauxSuspect1 > tauxSuspect2) {
                                    Correcteur.reduireFin(premiere, dureeInter + 15);
                                    System.out.println(premiere);
                                    System.out.println("--------Reduction par la fin");
                                    if (premiere.duree() <= 0)	throw new RuntimeException();

                            } else {
                                    Correcteur.reduireDebut(seconde, dureeInter + 15);
                                    System.out.println(seconde);
                                    System.out.println("--------Reduction par le debut");
                                    if (seconde.duree() <= 0)	throw new RuntimeException();
                            }

	}

        public static void decalageChirurgie (Chirurgie premiere, Chirurgie seconde){
            if(premiere.estImbrique(seconde) || seconde.estImbrique(premiere)){
                if(premiere.duree() > seconde.duree()){
                    long duree = Duration.between(premiere.getDatesOperation().getDateDebut(), seconde.getDatesOperation().getDateFin()).toMinutes();
                    Correcteur.translater(premiere, duree + 15);
                }else{
                    long duree = Duration.between(seconde.getDatesOperation().getDateDebut(), premiere.getDatesOperation().getDateFin()).toMinutes();
                    Correcteur.translater(seconde, duree + 15);
            }
            }else{
				Correcteur.translation(premiere, seconde);
        }
        }

	public static void normaliserDebut(Chirurgie courante) {
		LocalTime debut = LocalTime.from(courante.getDatesOperation().getDateDebut());

		// LocalTime indesirables
		List<LocalTime> indesirables = new ArrayList<>();
		indesirables.add(LocalTime.of(8, 0));
		indesirables.add(LocalTime.of(0, 0));
		indesirables.add(LocalTime.of(14, 0));
		indesirables.add(LocalTime.of(13, 40));
		indesirables.add(LocalTime.of(14, 15));
		indesirables.add(LocalTime.of(11, 15));
		indesirables.add(LocalTime.of(15, 15));
		indesirables.add(LocalTime.of(12, 50));
		indesirables.add(LocalTime.of(13, 50));
		indesirables.add(LocalTime.of(10, 15));
		indesirables.add(LocalTime.of(12, 35));
		indesirables.add(LocalTime.of(14, 50));
		indesirables.add(LocalTime.of(15, 20));
		indesirables.add(LocalTime.of(17, 0));

		// Couper la duree
		if (indesirables.contains(debut) && courante.dureeSuspecte()) {
			System.out.println("----Normalisation du debut : " + debut);
			long dureeTotale = courante.duree();
			long dureeFinale = 134;		// Dernier quartile des durees

			// dureeSuspecte() assure que dureeTotale > dureeFinale
			Correcteur.reduireDebut(courante, dureeTotale - dureeFinale);
		} else {
			System.out.println("----Pas de normalisation a faire sur le debut");
		}
	}

	public static void normaliserFin(Chirurgie courante) {
		LocalTime fin = LocalTime.from(courante.getDatesOperation().getDateFin());

		// LocalTime indesirables
		List<LocalTime> indesirables = new ArrayList<>();
		indesirables.add(LocalTime.of(8, 0));
		indesirables.add(LocalTime.of(0, 0));
		indesirables.add(LocalTime.of(14, 0));
		indesirables.add(LocalTime.of(13, 40));
		indesirables.add(LocalTime.of(14, 15));
		indesirables.add(LocalTime.of(11, 15));
		indesirables.add(LocalTime.of(15, 15));
		indesirables.add(LocalTime.of(12, 50));
		indesirables.add(LocalTime.of(13, 50));
		indesirables.add(LocalTime.of(10, 15));
		indesirables.add(LocalTime.of(12, 35));
		indesirables.add(LocalTime.of(14, 50));
		indesirables.add(LocalTime.of(15, 20));
		indesirables.add(LocalTime.of(17, 0));

		// Couper la duree
		if (indesirables.contains(fin) && courante.dureeSuspecte()) {
			System.out.println("----Normalisation de la fin : " + fin);
			long dureeTotale = courante.duree();
			long dureeFinale = 134;		// Dernier quartile des durees

			// dureeSuspecte() assure que dureeTotale > dureeFinale
			Correcteur.reduireFin(courante, dureeTotale - dureeFinale);
		} else {
			System.out.println("----Pas de normalisation a faire sur la fin");
		}
	}

	public static void translation(Chirurgie premiere, Chirurgie seconde) {
		long dureeChevauchement = premiere.dureeIntersection(seconde);
		Correcteur.translater(seconde, dureeChevauchement + 15);
	}
	
	
	
	/**
	 * Comparer ancienne liste de conflits avec les conflits restant 
	 * Si les conflits restant ne sont pas dans l'ancienne liste de conflits
	 * 	On applique une autre strat de correction
	 * Sinon 
	 * 	On applique correction basique
	 * 
	 */
}
