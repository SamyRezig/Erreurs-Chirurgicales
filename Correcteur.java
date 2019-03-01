import java.time.Duration;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalTime;

public class Correcteur {
	private static int dureePause = 15;
	private static Log historiqueChirurgies = new Log();

	public static Log getHistoriqueChirurgies() {
		return Correcteur.historiqueChirurgies;
	}

	// Translater l'intervalle de temps d'une chirurgie
	private static void translater(Chirurgie courante, long biaisMinutes) {
		Correcteur.historiqueChirurgies.ajouter(courante);
		courante.getDatesOperation().translater(biaisMinutes);
		Correcteur.historiqueChirurgies.ajouter(courante);
	}

	// Reduire l'intervalle de temps d'une chirurgie par la fin
	private static void reduireFin(Chirurgie courante, long biaisMinutes) {
		Correcteur.historiqueChirurgies.ajouter(courante);
		courante.reduireFin(biaisMinutes);
		Correcteur.historiqueChirurgies.ajouter(courante);
	}

	// Reduire l'intervalle de temps par la fin
	private static void reduireDebut(Chirurgie courante, long biaisMinutes) {
		Correcteur.historiqueChirurgies.ajouter(courante);
		courante.reduireDebut(biaisMinutes);
		Correcteur.historiqueChirurgies.ajouter(courante);
	}

	// Changer le chirurgien
	public static void changerChirurgien(Chirurgie courante, Chirurgien ch) {
		Correcteur.historiqueChirurgies.ajouter(courante);
		courante.setChirurgien(ch);
		Correcteur.historiqueChirurgies.ajouter(courante);
	}

	// Changer la salle
	public static void changerSalle(Chirurgie courante, Salle s) {
		Correcteur.historiqueChirurgies.ajouter(courante);
		courante.setSalle(s);
		Correcteur.historiqueChirurgies.ajouter(courante);
	}

	public static void couperDuree(Chirurgie premiere, Chirurgie seconde) {
			long dureeInter = premiere.dureeIntersection(seconde);

			// Calcul des taux de suspection pour determiner la chirurgie a decouper
			double tauxSuspect1 = premiere.tauxSuspect(dureeInter);
			double tauxSuspect2 = seconde.tauxSuspect(dureeInter);

            if (tauxSuspect1 > tauxSuspect2) {
                Correcteur.reduireFin(premiere, dureeInter + Correcteur.dureePause);
                System.out.println("--------La chirurgie " + premiere.getId() + " a ete decoupee");
                System.out.println("--------Reduction par la fin");
				Statistiques.plusDureeDecoupage(dureeInter + Correcteur.dureePause);

            } else {
                Correcteur.reduireDebut(seconde, dureeInter + Correcteur.dureePause);
                System.out.println("--------La chirurgie " + seconde.getId() + " a ete decoupee");
                System.out.println("--------Reduction par le debut");
				Statistiques.plusDureeDecoupage(dureeInter + Correcteur.dureePause);
            }

			if (seconde.duree() <= 0 || premiere.duree() <= 0)	throw new RuntimeException();
	}

    public static void decalageChirurgie(Chirurgie premiere, Chirurgie seconde){
		long duree;
		long dureeTranslation;

        if (premiere.estImbrique(seconde) || seconde.estImbrique(premiere)){

            if (premiere.duree() > seconde.duree()){
                duree = Duration.between(premiere.getDatesOperation().getDateDebut(),
											seconde.getDatesOperation().getDateFin()).toMinutes();
                dureeTranslation = duree + Correcteur.dureePause;
                Correcteur.translater(premiere, dureeTranslation);
                Statistiques.mettreAJourDureeTotaleDecalage(dureeTranslation);
            } else {
                duree = Duration.between(seconde.getDatesOperation().getDateDebut(),
											premiere.getDatesOperation().getDateFin()).toMinutes();
                dureeTranslation = duree + Correcteur.dureePause;
                Correcteur.translater(seconde, dureeTranslation);
                Statistiques.mettreAJourDureeTotaleDecalage(dureeTranslation);
        	}

        } else {
			Correcteur.translation(premiere, seconde);
        }
    }

	public static List<LocalTime> heuresIndesirables() {
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

		return indesirables;
	}

	public static void normaliserDebut(Chirurgie courante) {
		LocalTime debut = LocalTime.from(courante.getDatesOperation().getDateDebut());

		// LocalTime indesirables
		List<LocalTime> indesirables = Correcteur.heuresIndesirables();

		// Couper la duree
		if (indesirables.contains(debut) && courante.dureeSuspecte()) {
			// MAJ Stats
			Statistiques.plusNormalisation();

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
		List<LocalTime> indesirables = Correcteur.heuresIndesirables();

		// Couper la duree
		if (indesirables.contains(fin) && courante.dureeSuspecte()) {
			// MAJ Stats
			Statistiques.plusNormalisation();

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

}
