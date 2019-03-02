import java.time.Duration;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalTime;

/**
  * Classe representant comportant uniquement des methodes statiques
  * pour la resolution des conflits. Elle contient des outils pour traiter les
  * chirurgies.
  * @author Samy Rezig
  * @author Yves Tran
  * @see Log
  * @see Conflit
  * @see Chirurgie
  */
public class Correcteur {

	private static int dureePause = 15;						// Duree interchirurgicale.
	private static Log historiqueChirurgies = new Log();	// Historique des differents etats des chirurgies modifiees.


	/**
	  * Getter pour avoir l'historique des chirurgies modifiees.
	  * @return les donnes des chirurgies et leur differents etats.
	  */
	public static Log getHistoriqueChirurgies() {
		return Correcteur.historiqueChirurgies;
	}

	/**
	  * Translater l'intervalle de temps d'une chirurgie.
	  * @param courante la chirurgie consideree pour la modification.
	  * @param biaisMinutes nombre de minutes a translater
	  */
	private static void translater(Chirurgie courante, long biaisMinutes) {
		Correcteur.historiqueChirurgies.ajouter(courante);		// Mise a jour de l'historique
		courante.getDatesOperation().translater(biaisMinutes);
		Correcteur.historiqueChirurgies.ajouter(courante);		// Mise a jour de l'historique
	}

	/**
	  * Reduire l'intervalle de temps d'une chirurgie par la fin
	  * @param courante la chirurgie consideree pour la modification.
	  * @param biaisMinutes nombre de minutes a reduire
	  */
	private static void reduireFin(Chirurgie courante, long biaisMinutes) {
		Correcteur.historiqueChirurgies.ajouter(courante);	// Mise a jour de l'historique
		courante.reduireFin(biaisMinutes);
		Correcteur.historiqueChirurgies.ajouter(courante);	// Mise a jour de l'historique
	}

	/**
	  * Reduire l'intervalle de temps par le debut
	  * @param courante la chirurgie consideree pour la modification.
	  * @param biaisMinutes nombre de minutes a reduire
	  */
	private static void reduireDebut(Chirurgie courante, long biaisMinutes) {
		Correcteur.historiqueChirurgies.ajouter(courante);	// Mise a jour de l'historique
		courante.reduireDebut(biaisMinutes);
		Correcteur.historiqueChirurgies.ajouter(courante);	// Mise a jour de l'historique
	}

	/**
	  * Changer le chriurgien de la chirurgie donnee.
	  * @param courante la chirurgie consideree pour la modification.
	  * @param ch le nouveau chirurgien.
	  */
	public static void changerChirurgien(Chirurgie courante, Chirurgien ch) {
		Correcteur.historiqueChirurgies.ajouter(courante);	// Mise a jour de l'historique
		courante.setChirurgien(ch);
		Correcteur.historiqueChirurgies.ajouter(courante);	// Mise a jour de l'historique
	}

	/**
	  * Changer la salle de la chirurgie donnee.
	  * @param courante la chirurgie consideree pour la modification.
	  * @param s la nouvelle salle.
	  */
	public static void changerSalle(Chirurgie courante, Salle s) {
		Correcteur.historiqueChirurgies.ajouter(courante);	// Mise a jour de l'historique
		courante.setSalle(s);
		Correcteur.historiqueChirurgies.ajouter(courante);	// Mise a jour de l'historique
	}

	/**
	  * Couper la duree d'une des deux chirurgies donnees pour resoudre un conflit.
	  * @param premiere premiere chirurgie du conflit
	  * @param deuxieme chirurgie du conflit
	  */
	public static void couperDuree(Chirurgie premiere, Chirurgie seconde) {
		// Duree d'intersection des deux chirurgies.
		long dureeInter = premiere.dureeIntersection(seconde);

		// Calcul des taux de suspection pour determiner la chirurgie a decouper
		double tauxSuspect1 = premiere.tauxSuspect(dureeInter);
		double tauxSuspect2 = seconde.tauxSuspect(dureeInter);
		// La chirurgie avec un taux de suspection maximale est choisie.

        if (tauxSuspect1 > tauxSuspect2) {
			// Couper la premiere chirurgie.
            Correcteur.reduireFin(premiere, dureeInter + Correcteur.dureePause);
            System.out.println("--------La chirurgie " + premiere.getId() + " a ete decoupee");
            System.out.println("--------Reduction par la fin");
			Statistiques.plusDureeDecoupage(dureeInter + Correcteur.dureePause);

        } else {
			// Couper la seconde chirurgie.
            Correcteur.reduireDebut(seconde, dureeInter + Correcteur.dureePause);
            System.out.println("--------La chirurgie " + seconde.getId() + " a ete decoupee");
            System.out.println("--------Reduction par le debut");
			Statistiques.plusDureeDecoupage(dureeInter + Correcteur.dureePause);
        }

		// Cas ou decoupage a ete abusee. La duree est devenue negative.
		if (seconde.duree() <= 0 || premiere.duree() <= 0)	{
			throw new RuntimeException(premiere.toString() + "\n" + seconde.toString());
		}
	}

	/**
	  * Decaler une des deux chirurgies donnees pour resoudre un conflit.
	  * @param premiere premiere chirurgie du conflit
	  * @param deuxieme chirurgie du conflit
	  */
    public static void decalageChirurgie(Chirurgie premiere, Chirurgie seconde){
		long duree;				// Stocker la duree d'une chriurgie.
		long dureeTranslation;	// La duree a translater.

		// Si l'une des chirurgie est imbriquee dans l'autre.
        if (premiere.estImbrique(seconde) || seconde.estImbrique(premiere)){

			// La chirurgie a translater est celle avec la duree la plus longue
			// car la luree de translation sera egale a la duree de la chirurgie
			//  la plus courte (plus le temps de pause)
            if (premiere.duree() > seconde.duree()){
                duree = Duration.between(premiere.getDatesOperation().getDateDebut(),
											seconde.getDatesOperation().getDateFin()).toMinutes();
                dureeTranslation = duree + Correcteur.dureePause;
                Correcteur.translater(premiere, dureeTranslation);

				// Mettre a jour les statistiques sur le decalage de chirurgies.
                Statistiques.mettreAJourDureeTotaleDecalage(dureeTranslation);

            } else {
                duree = Duration.between(seconde.getDatesOperation().getDateDebut(),
											premiere.getDatesOperation().getDateFin()).toMinutes();
                dureeTranslation = duree + Correcteur.dureePause;
                Correcteur.translater(seconde, dureeTranslation);

				// Mettre a jour les statistiques sur le decalage de chirurgies.
                Statistiques.mettreAJourDureeTotaleDecalage(dureeTranslation);
        	}

        } else {	// Les deux chirurgies ne sont pas imbriquees.
			Correcteur.translation(premiere, seconde);
			// La mise a jour des statistiques de decalage sont dans la methode translater(Chirurgie, Chirurgie).
        }
    }

	/**
	  * @return la liste des heures de debut et fin de chirurgies avec le plus
	  * de conflits. Les horaires sont determinees manuellement apres analyse
	  * par les developpeurs des heures depuis la classe Statistiques.
	  */
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

	/**
	  * Couper la duree de la chirurgie donne par le debut si elle commence par
	  * un horaire frequent chez les chirurgies en conflit.
	  * @param courante la chirurgie a normaliser s'il le faut.
	  */
	public static void normaliserDebut(Chirurgie courante) {
		// Horaire de debut de la chirurgie.
		LocalTime debut = LocalTime.from(courante.getDatesOperation().getDateDebut());

		// LocalTime indesirables
		List<LocalTime> indesirables = Correcteur.heuresIndesirables();

		// Couper la duree
		if (indesirables.contains(debut) && courante.dureeSuspecte()) {
			// MAJ Stats sur le nombre de normalisations
			Statistiques.plusNormalisation();

			System.out.println("----Normalisation du debut : " + debut);
			long dureeTotale = courante.duree();	// Duree de la chirurgie.
			long dureeFinale = 134;		// Dernier quartile des durees

			// dureeSuspecte() assure que dureeTotale > dureeFinale
			Correcteur.reduireDebut(courante, dureeTotale - dureeFinale);
		} else {
			System.out.println("----Pas de normalisation a faire sur le debut");
		}
	}

	/**
	  * Couper la duree de la chirurgie donne par la fin si elle se termine par
	  * un horaire frequent chez les chirurgies en conflit.
	  * @param courante la chirurgie a normaliser s'il le faut.
	  */
	public static void normaliserFin(Chirurgie courante) {
		// Horaire de fin de la chirurgie
		LocalTime fin = LocalTime.from(courante.getDatesOperation().getDateFin());

		// LocalTime indesirables
		List<LocalTime> indesirables = Correcteur.heuresIndesirables();

		// Couper la duree
		if (indesirables.contains(fin) && courante.dureeSuspecte()) {
			// MAJ Stats sur le nombre de normalisations
			Statistiques.plusNormalisation();

			System.out.println("----Normalisation de la fin : " + fin);
			long dureeTotale = courante.duree();	// Duree de la chirurgie
			long dureeFinale = 134;		// Dernier quartile des durees

			// dureeSuspecte() assure que dureeTotale > dureeFinale
			Correcteur.reduireFin(courante, dureeTotale - dureeFinale);

		} else {
			System.out.println("----Pas de normalisation a faire sur la fin");
		}
	}

	/**
	  * Translater la seconde chirurgie de la duree de conflit.
	  * @param premiere chirurgie fixe de base.
	  * @param seconde chirurgie a modifier.
	  */
	public static void translation(Chirurgie premiere, Chirurgie seconde) {
		long dureeChevauchement = premiere.dureeIntersection(seconde);	// Duree d'intersection des 2 chirurgies.
		long dureeTranslation = dureeChevauchement + 15;				// Duree a decaler.
		Correcteur.translater(seconde, dureeTranslation);			// Par defaut, on translate la seconde chirurgie.

		// Mise a jour des statistiques de la duree de decalage.
		Statistiques.mettreAJourDureeTotaleDecalage(dureeTranslation);
	}

}
