import java.util.List;
import java.util.OptionalLong;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalTime;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.Scanner;
import java.util.NavigableMap;
import java.time.LocalDate;
import java.util.Collections;

/**
  * Classe contenant des statistiques a un moment donne d'un agenda. Elle contient
  * l'evolution des conflits dans des variables statiques.
  * @author Samy Rezig
  * @author Yves Tran
  * @see Agenda
  * @see Correction
  */
public class Statistiques {

	private final int nbConflits;				// Nombre de conflits
	private long dureeMoyenne;			// Duree moyenne d'une operation
	private long premierQuartile;		// Premier quartile des durees des chirurgies sans conflit
	private long mediane;				// Mediane des durees des chirurgies sans conflit
	private long dernierQuartile;		// Dernier quartile des durees des chirurgies sans conflit

	private Map<LocalTime, Integer> heuresConflits;			// Frequence des horaires en conflit
	private Map<Chirurgien, Double> dureeParChirurgien;		// Duree de travail par chirurgien
	private Map<Salle, Double> dureeParSalle;				// Duree de travail par salle
	private Map<LocalDate, Long> dureeJournees;				// Duree entre la premiere et la derniere chirurgie de chaque journee.

	private double ecartTypeSalles;			// Ecart type des durees de travail  des salles.
	private double ecartTypeChirurgiens;	// Ecart type des durees de travail des chirurgiens

	private static long dureeTotaleDecalage = 0;	// Duree totale des decalages pour corriger les conflits.
	private static long dureeTotaleDecoupage = 0;	// Duree totale des decoupages pour corriger les conflits
	private static int nbNormalisation = 0;			// Nombre de normalisation effectuees.
	private static int nbDecoupage = 0;				// Nombre de decoupages effectues.
	private static int nbRess = 0;					// Nombre de changements de ressources effectues.
	private static int nbDecalage = 0;				// Nombre de decalage effectues.
	private static int nbCorrection = 0;			// Nombre de correction totales effctuees = nomralisation + decalage + changement de ressources + decoupage.

	public static List<Integer> nombresUbiquite = new ArrayList<>();		// Evolution du nombre d'ubuiquites en fonction des iteratinons.
	public static List<Integer> nombresInterference = new ArrayList<>();	// Evolution du nombre d'interferences en fonction des iteratinons.
	public static List<Integer> nombresChevauchement = new ArrayList<>();	// Evolution du nombre de chevauchements en fonction des iteratinons.
	public static List<Integer> nombresConflits = new ArrayList<>();		// Evolution du nombre de conflits en fonction des iteratinons.
	public static List<Integer> nombresConflitsCorriges = new ArrayList<>();// Evolution du nombre de conflits corriges en fonction des iteratinons.


	/**
	  * Constructeur principal.
	  * @param a l'agenda sur lequel se repose les statistiques.
	  */
	public Statistiques(Agenda a) {
        List<Chirurgie> listeBase = a.extraireListeChirurgies();			// Liste de toutes les chirurgies.
        List<Conflit> listeConflits = a.extraireConflits();					// Liste de tous les conflits.
        NavigableMap<LocalDate, PlanningJournee> planning = a.getPlanning();// Planning par jour

		this.nbConflits = listeConflits.size();				// Setting du nombre de conflits.

		// Extraction des chirurgies en conflits
		Set<Chirurgie> enConflit = new HashSet<>();
		for (Conflit conflit : listeConflits) {
			enConflit.add(conflit.getPremiereChirurgie());
			enConflit.add(conflit.getSecondeChirurgie());
		}

		// Difference
		Set<Chirurgie> operationsSansConflit = new HashSet<>(listeBase);
		operationsSansConflit.removeAll(enConflit);

		// Remplissage des attributs
		System.out.println("Chargement des outils statistiques...");
		System.out.println("----Calcul des moyennes...");
		this.dureeMoyenne = this.calculerDureeMoyenne(operationsSansConflit);

		System.out.println("----Calcul des quartiles/mediane...");
		this.premierQuartile = this.calculerPremierQuartile(operationsSansConflit);
		this.mediane = this.calculerMediane(operationsSansConflit);
		this.dernierQuartile = this.calculerDernierQuartile(operationsSansConflit);

		System.out.println("----Calcul des heures des conflits les plus frequentes...");
		this.heuresConflits = this.topHeuresConflits(listeConflits);

		System.out.println("----Calcul des durees moyennes par chirurgien...");
		this.dureeParChirurgien = this.dureeParChirurgien(a);

		System.out.println("----Calcul des durees moyennes par salle...");
		this.dureeParSalle = this.dureeParSalle(a);

		System.out.println("----Calcul des ecart-types");
		this.ecartTypeSalles = this.ecartType(this.dureeParSalle.values());
		this.ecartTypeChirurgiens = this.ecartType(this.dureeParChirurgien.values());

		System.out.println("----Calcul des durees pour chaque journee.");
		this.dureeJournees = this.dureeJournees(planning);

		System.out.println("Fin du chargement des outils statistiques.\n");
	}

	/**
	  * Afficher en ligne de commande l'organisation de la disponibilites des
	  * chirurgiens. Il est possible de distinguer les jours ajouter a chaque chirurgien
	  * apres analyse des chirurgies.
	  * @param a l'agenda a analyser.
	  */
	public void afficherJoursTravailPlannifie(Agenda a) {
		char lettre; // Stocke chaque lettre des noms des chirurgiens pour leur affichage
		List<LocalDate> tousJours = new ArrayList<>(a.getPlanning().keySet());
		List<Chirurgien> listeChirurgiens = a.getListeChirurgiens();

		// Trie de la liste dans lo'rdre alphabetique
		Collections.sort(listeChirurgiens, (x, y) -> x.getNom().compareTo(y.getNom()));

		// Afficher leur jour de travail
		for (LocalDate jour : tousJours) {
			System.out.print(jour + " : ");
			for (Chirurgien medecin : listeChirurgiens) {

				if (medecin.censeTravailler(jour) && a.getPlanning().get(jour).travaille(medecin)) {
					System.out.print("\t*");	// chirurgien disponible et a travaille

				} else if (medecin.censeTravailler(jour) && !a.getPlanning().get(jour).travaille(medecin)) {
					System.out.print("\t+");	// chirurgien disponible mais n'a pas travaille

				} else if (!medecin.censeTravailler(jour) && a.getPlanning().get(jour).travaille(medecin)) {
					System.out.print("\t?");	// chirurgien non disponible mais a travaille

				} else {
					System.out.print("\t|");	// chirurgien non disponible et n'a pas travaille
				}
			}
			System.out.print("  " + a.getPlanning().get(jour).getListeChirurgies().size());
			System.out.println();
		}

		// Afficher le nom des chirurgiens
		System.out.println();
		for (int i = 0; i < 16; i++) {
			System.out.print("\t\t");
			for (Chirurgien medecin : listeChirurgiens) {
				lettre = (i < medecin.toString().length()) ? medecin.toString().charAt(i) : ' ';
				System.out.print(lettre + "\t");
			}
			System.out.println();
		}
		// Legende
		System.out.println("Legende : ");
		System.out.println("\t * : chirurgien disponible et a travaille");
		System.out.println("\t + : chirurgien disponible mais n'a pas travaille");
		System.out.println("\t ? : chirurgien non disponible mais a travaille");
		System.out.println("\t | : chirurgien non disponible et n'a pas travaille");
		System.out.println("\tLes nombres sur le cote correspondent au nombre de chirurgies dans la journees.");
	}

	/**
	  * Afficher en ligne de commande les salles utilisees.
	  * @param a l'agenda a analyser.
	  */
	public void afficherJoursTravailSalles(Agenda a) {
		char lettre; // Stocke chaque lettre des noms des chirurgiens pour leur affichage
		List<Salle> listeSalles = a.getListeSalles();
		listeSalles.addAll(a.getListeSallesUrgence());

		// Trie de la liste dans lo'rdre alphabetique
		Collections.sort(listeSalles, (x, y) -> x.getNom().compareTo(y.getNom()));

		// Afficher leur jour de travail
		for (LocalDate jour : a.getPlanning().keySet()) {
			System.out.print(jour + " : ");
			for (Salle bloc : listeSalles) {

				if (a.getPlanning().get(jour).occupe(bloc)) {
					System.out.print("\t*");	// salle occupee

				} else {
					System.out.print("\t|");	// salle libre
				}
			}
			System.out.print("  " + a.getPlanning().get(jour).getListeChirurgies().size());
			System.out.println();
		}

		// Afficher le nom des chirurgiens
		System.out.println();
		for (int i = 0; i < 9; i++) {
			System.out.print("\t\t");
			for (Salle bloc : listeSalles) {
				lettre = (i < bloc.toString().length()) ? bloc.toString().charAt(i) : ' ';
				System.out.print(lettre + "\t");
			}
			System.out.println();
		}
		// Legende
		System.out.println("Legende : ");
		System.out.println("\t * : salle occupee");
		System.out.println("\t | : salle libre");
		System.out.println("\tLes nombres sur le cote correspondent au nombre de chirurgies dans la journees.");
	}

	/**
	  * @return une map avec les durees de la journee en minutes en fonction des jours.
	  */
	private Map<LocalDate, Long> dureeJournees(Map<LocalDate, PlanningJournee> planning) {
		Map<LocalDate, Long> resultat = new HashMap<>();
		PlanningJournee contenuJour;

		for (LocalDate jour : planning.keySet()) {
			contenuJour = planning.get(jour);
			resultat.put(jour, contenuJour.dureeTotale());
		}

		return resultat;
	}

	/**
	  * Mise a jour des durees en minute de decoupage.
	  * @param duree duree en minute a ajouter.
	  */
	public static void plusDureeDecoupage(long duree) {
		Statistiques.dureeTotaleDecoupage += duree;
	}

	/**
	  * Mise a jour du nombre de decoupages.
	  */
	public static void plusDecoupe() {
		Statistiques.nbDecoupage++;
		Statistiques.nbCorrection++;
	}

	/**
	  * Mise a jour du nombre de changements de ressources
	  */
	public static void plusModifRessource() {
		Statistiques.nbRess++;
		Statistiques.nbCorrection++;
	}

	/**
	  * Mise a jour du nombre de decalages
	  */
	public static void plusDecalage() {
		Statistiques.nbDecalage++;
		Statistiques.nbCorrection++;
	}

	/**
	  * Mise a jour du nombre de normalisations
	  */
	public static void plusNormalisation() {
		Statistiques.nbNormalisation++;
		Statistiques.nbCorrection++;
	}

	/**
	  * @return la duree moyenne en minutes des chirurgies donnees.
	  * @param operationsSansConflit la liste des chirurgies a consideree.
	  */
	private long calculerDureeMoyenne(Set<Chirurgie> operationsSansConflit) {
		long sommeDurees = operationsSansConflit.stream().mapToLong(chrg -> chrg.duree()).sum();
		return sommeDurees / operationsSansConflit.size();
		// Possible perte de donnees avec division par 2 long
	}

	/**
	  * @return premier quartile de la duree en minutes des chirurgies donnees.
	  * @param operationsSansConflit la liste des chirurgies a consideree.
	  */
	private long calculerPremierQuartile(Set<Chirurgie> operationsSansConflit) {
		OptionalLong ol = operationsSansConflit.stream().mapToLong(chrg -> chrg.duree()).sorted()
				.skip(operationsSansConflit.size() / 4).findFirst();
		long premierQuartile = ol.getAsLong();

		return premierQuartile;
	}

	/**
	  * @return mediane de la duree en minutes des chirurgies donnees.
	  * @param operationsSansConflit la liste des chirurgies a consideree.
	  */
	private long calculerMediane(Set<Chirurgie> operationsSansConflit) {
		OptionalLong ol = operationsSansConflit.stream().mapToLong(chrg -> chrg.duree()).sorted()
				.skip(operationsSansConflit.size() / 2).findFirst();
		long mediane = ol.getAsLong();

		return mediane;
	}

	/**
	  * @return dernier quartile de la duree en minutes des chirurgies donnees.
	  * @param operationsSansConflit la liste des chirurgies a consideree.
	  */
	private long calculerDernierQuartile(Set<Chirurgie> operationsSansConflit) {
		OptionalLong ol = operationsSansConflit.stream()
											.mapToLong(chrg -> chrg.duree()).sorted()
											.skip(operationsSansConflit.size() * 3 / 4)
											.findFirst();
		long dernierQuartile = ol.getAsLong();

		return dernierQuartile;
	}

	/**
	  * @return une map avec la frequence de chaque horaire retrouve parmi les
	  * chirurgies en conflit. Cette map est triee dans l'ordre decroissante des valeurs.
	  * @param listeConflit la liste des conflits contenat les chirurgies a consideree.
	  */
	private Map<LocalTime, Integer> topHeuresConflits(List<Conflit> listeConflits) {
		Map<LocalTime, Integer> tableFrequences = new HashMap<>();
		Integer frequence;

		List<LocalTime> listeTemps = new ArrayList<>();
		for (Conflit conflitCourant : listeConflits) {
			// Ajout des horaires
			listeTemps.add(conflitCourant.getPremiereChirurgie().getDatesOperation().getDateDebut().toLocalTime());
			listeTemps.add(conflitCourant.getPremiereChirurgie().getDatesOperation().getDateFin().toLocalTime());
			listeTemps.add(conflitCourant.getSecondeChirurgie().getDatesOperation().getDateDebut().toLocalTime());
			listeTemps.add(conflitCourant.getSecondeChirurgie().getDatesOperation().getDateFin().toLocalTime());
		}

		// Calcule des frequences
		for (LocalTime temps : listeTemps) {
			frequence = tableFrequences.get(temps);
			if (frequence == null)
				tableFrequences.put(temps, 1);
			else
				tableFrequences.put(temps, frequence + 1);
		}

		tableFrequences = tableFrequences.entrySet()
										.stream()
										.sorted(Map.Entry.<LocalTime, Integer>comparingByValue()
																		.reversed())				// Tri decroissant
																		.collect(Collectors.toMap(
			Map.Entry::getKey, Map.Entry::getValue, (ancienneValeur, nouvelleValeur) -> ancienneValeur, LinkedHashMap::new));

		return tableFrequences;
	}

	/**
	  * Getter pour la duree moyenne des chirurgies.
	  * @return la duree moyenne en minutes
	  */
	public long getDureeMoyenne() {
		return this.dureeMoyenne;
	}

	/**
	  * Getter pour le premier quartile de la duree des chirurgies.
	  * @return le premier quartile des durees en minutes
	  */
	public long getPremierQuartile() {
		return this.premierQuartile;
	}

	/**
	  * Getter pour la mediane de la duree des chirurgies.
	  * @return la mediane des durees en minutes
	  */
	public long getMediane() {
		return this.mediane;
	}

	/**
	  * Getter pour le dernier quartile de la duree des chirurgies.
	  * @return le dernier quartile des durees en minutes
	  */
	public long getDernierQuartile() {
		return this.dernierQuartile;
	}

	/**
	  * Getter pour la map de chirurgiens et durees moyennes.
	  * @return la map de chirurgiens et durees moyennes.
	  */
	public Map<Chirurgien, Double> getDureeParChirurgien() {
		return this.dureeParChirurgien;
	}

	/**
	  * Getter pour la map de salles et durees moyennes.
	  * @return la map de salles et durees moyennes.
	  */
	public Map<Salle, Double> getDureeParSalle() {
		return this.dureeParSalle;
	}

	/**
	  * Getter pour la liste des horaires triees selon leur frequence d'apparition
	  * parmi les chirurgies en conflit.
	  * @return la liste de horaires triees selon ce critere.
	  */
	public Map<LocalTime, Integer> getHeuresConflits() {
		//return new ArrayList<>(this.heuresConflits.keySet());
		return this.heuresConflits;
	}

	/**
	  * @return une map avec la duree moyenne d'occupation des salles
	  * @param a l'agenda a analyser.
	  */
	public Map<Salle, Double> dureeParSalle(Agenda a) {
		List<Salle> salles = new ArrayList<>(a.getListeSalles());
		salles.addAll(a.getListeSallesUrgence());

		Map<Salle, Double> dureeSalles = new HashMap<>();
		long somme;		// Somme des durees de toutes les chirurgies pour une ceraine salle.
		long card;		// Nombre de chirurgies qui se sont passees dans cette salle.

		for (Salle salleCourante : salles) {
			somme = a.extraireListeChirurgies().stream()
										.filter(x -> x.getSalle().equals(salleCourante))
										.mapToLong(x -> x.duree())
										.sum();
			card = a.extraireListeChirurgies().stream()
										.filter(x -> x.getSalle().equals(salleCourante))
										.count();
			dureeSalles.put(salleCourante, (double) somme / (double) card);
		}
		return dureeSalles;
	}

	/**
	  * @return une map avec la duree moyenne du temps de travail des chirurgiens.
	  * @param a l'agenda a analyser.
	  */
	public Map<Chirurgien, Double> dureeParChirurgien(Agenda a) {
		Map<Chirurgien, Double> dureeChirurgien = new HashMap<>();
		long somme;		// SOmme des durees de toutes les chirurgies pour un certain chirurgiens.
		long card;		// Nombre de chirurgies que ce chirurgien a realise.

		for (Chirurgien chgCourante : a.getListeChirurgiens()) {
			somme = a.extraireListeChirurgies().stream()
										.filter(x -> x.getChirurgien().equals(chgCourante))
										.mapToLong(x -> x.duree())
										.sum();
			card = a.extraireListeChirurgies().stream()
										.filter(x -> x.getChirurgien().equals(chgCourante))
										.count();
			dureeChirurgien.put(chgCourante, (double) somme / (double) card);
		}
		return dureeChirurgien;
	}

	/**
	  * @return l'ecart type entre les valeurs donnees.
	  * @param valeurs une collection des valeurs a analyser.
	  */
	public double ecartType(Collection<Double> valeurs) {
		double moyenne = 0.0;
		double sommeCarrees = 0.0;

		// Calcule de la moyenne des valeurs.
		for (Double v : valeurs) {
			moyenne += v;
		}
		moyenne = moyenne / (double) valeurs.size();

		// Calcule de la variance.
		for (Double v : valeurs) {
			sommeCarrees += Math.pow((v - moyenne), 2);
		}
		return Math.sqrt(sommeCarrees / (double) valeurs.size());
	}

	/**
	  * Getter pour l'ecart type des salles.
	  * @return l'ecart type des salles.
	  */
	public double getEcartTypeSalles() {
		return this.ecartTypeSalles;
	}

	/**
	  * Getter pour l'ecart type des chirurgiens.
	  * @return l'ecart type des chirurgiens..
	  */
	public double getEcartTypeChirurgiens() {
		return this.ecartTypeChirurgiens;
	}

	/**
	  * Calcule de l'ecart moyen d'allongement des journees apres correction.
	  * @param planningApres les plannings apres correction.
	  */
	private double ecartMoyenAllongement(Map<LocalDate, Long> planningApres) {
		double ecartMoyen = 0;
		int nbJours = 0;

		for (LocalDate jour : planningApres.keySet()) {
			ecartMoyen += (planningApres.get(jour) - this.dureeJournees.get(jour));
			nbJours++;
		}

		return ecartMoyen / (double) nbJours;
	}

	/**
	  * Afficher les jours ou le nombre de chirurgiens qui travaillent est insuffisant.
	  * @param planning les plannings des journees.
	  */
	public void afficherJoursChirurgiensInsuffisants(NavigableMap<LocalDate, PlanningJournee> planning) {
		PlanningJournee contenuJour;
		int seuilChirurgiens;
		int nbChirurgiens;

		for (LocalDate jour : planning.keySet()) {
			contenuJour = planning.get(jour);
			seuilChirurgiens = contenuJour.getListeChirurgies().size() / 4; // 4 = nombre de chirurgies qu'un seul
																			// chirurgien peut gerer
			nbChirurgiens = contenuJour.nbChirurgiensDispos();

			// Condition d'affichage.
			if (nbChirurgiens < seuilChirurgiens) {
				System.out.println(jour + " : " + nbChirurgiens + " chirurgiens disponibles pour "
						+ contenuJour.getListeChirurgies().size() + " chirurgies.");
			}
		}
	}

	/**
	  * @return l'evolution des ubiquites, interferences, chevauchement, les conflits
	  * et le nombre de conflits corriges en nue map.
	  */
	public static Map<String, List<Integer>> dataConflits() {
		Map<String, List<Integer>> data = new HashMap<>();

		data.put("Ubiquite", Statistiques.nombresUbiquite);
		data.put("Interference", Statistiques.nombresInterference);
		data.put("Chevauchement", Statistiques.nombresChevauchement);
		data.put("Total", Statistiques.nombresConflits);

		return data;
	}

	/**
	  * Afficher l'evolution des differents types de conflits et les totaux
	  * en ligne de commandes.
	  */
	public void afficherConflitsTotaux() {
		Map<String, List<Integer>> map = Statistiques.dataConflits();
		System.out.println("Chevauchements :\t\t" + map.get("Chevauchement"));
		System.out.println("Interferences : \t\t" + map.get("Interference"));
		System.out.println("Ubiquites : \t\t\t" + map.get("Ubiquite"));
		System.out.println("Total : \t\t\t" + map.get("Total"));
		System.out.println("Conflits corriges:\t\t" + Statistiques.nombresConflitsCorriges);
		System.out.println();
		System.out.println("Taux d'existence des chevauchements : \t" + this.tauxExistence(Statistiques.nombresChevauchement) + " pourcent du temps");
		System.out.println("Taux d'existence des interferences : \t" + this.tauxExistence(Statistiques.nombresInterference) + " pourcent du temps");
		System.out.println("Taux d'existence des ubiquites : \t" + this.tauxExistence(Statistiques.nombresUbiquite) + " pourcent du temps");

		int nbIterNecessaires = (map.get("Total").get( map.get("Total").size() - 1 ).equals(0)) ?
										map.get("Total").size() - 1
										:
										map.get("Total").size();

		String plus = (map.get("Total").get( map.get("Total").size() - 1 ) == 0) ? "" : "+";	// Cas ou il fallait plus d'iterations pour resoudre tous les conflits
		System.out.println("Nombre d'iterations necessaires : \t" + nbIterNecessaires + plus);
	}

	/**
	  * Comparaison entre les statistiques actuelles et les statistiques donnes.
	  * @param apresStats les statistiques a comparer avec celles actuelles.
	  */
	public void comparer(Statistiques apresStats) {
		System.out.println("Statistiques \t\t AVANT correction \t APRES correction");
		System.out.println("Duree moyenne : \t\t" + this.dureeMoyenne + "\t\t\t" + apresStats.getDureeMoyenne());
		System.out.println("Premier quartile : \t\t" + this.premierQuartile + "\t\t\t" + apresStats.getPremierQuartile());
		System.out.println("Duree mediane : \t\t" + this.mediane + "\t\t\t" + apresStats.getMediane());
		System.out.println("Dernier quartile : \t\t" + this.dernierQuartile + "\t\t\t" + apresStats.getDernierQuartile());

		System.out.println("Ecart-type duree des salles : \t"
				+ (float) this.ecartTypeSalles + "\t\t"
				+ (float) apresStats.getEcartTypeSalles());
		System.out.println("Ecart-type duree chirurgiens :\t"
				+ (float) this.ecartTypeChirurgiens + "\t\t"
				+ (float) apresStats.getEcartTypeChirurgiens());

		System.out.println("Nombre de conflits restant :\t"
				+ this.nbConflits + "\t\t\t"
				+ apresStats.nbConflits);

		System.out.println();
		System.out.println("Allongement des journees : \t"
				+ this.ecartMoyenAllongement(apresStats.dureeJournees)
				+ " minutes de plus par jour");

		System.out.println("Duree moyenne des decalages : \t"
				+ Statistiques.dureeMoyenneDecalage()
				+ " minutes par decalage soit un decalage de "
				+ (float) Statistiques.dureeTotaleDecalage / 60 + " heures au total");

		System.out.println("Duree moyenne des decoupages : \t"
				+ Statistiques.dureeMoyenneDecoupage()
				+ " minutes par decoupage soit "
				+ (float) Statistiques.dureeTotaleDecoupage / 60
				+ " heures decoupees hors normalisation");

		System.out.println("Pertinence de la correction : \t" + this.mesurerPertinance(apresStats)
				+ " conflits corriges par correction");
	}

	/**
	  * Affichage du nombre de corrections selon les types de corrections.
	  */
	public void afficherNombreCorrections() {
		System.out.println("Nombre de normalisations : \t" + Statistiques.nbNormalisation);
		System.out.println("Nombre de modifs ressources : \t" + Statistiques.nbRess);
		System.out.println("Nombre de decoupages : \t\t" + Statistiques.nbDecoupage);
		System.out.println("Nombre de decalages : \t\t" + Statistiques.nbDecalage);
		System.out.println(" === Nombre de corrections : \t" + Statistiques.nbCorrection);
	}

	/**
	  * Calcule de la duree moyenne d'allongement des journees.
	  */
	private static double dureeMoyenneDecoupage() {
		return (double) Statistiques.dureeTotaleDecoupage / (double) Statistiques.nbDecoupage;
	}

	/**
	  * Recenser un conflit. Determiner son type pour pouvoir suivre l'evolution
	  * des conflits par iteration.
	  * @param c la conflit a considerer.
	  */
	public static void recenser(Conflit c) {
		Integer nb;		// Variable de stockage

		nb = Statistiques.nombresConflits.remove(Statistiques.nombresConflits.size() - 1) + 1;
		Statistiques.nombresConflits.add(nb);

		if (c.getClass().toString().equals("class Ubiquite")) {
			nb = Statistiques.nombresUbiquite.remove(Statistiques.nombresUbiquite.size() - 1) + 1;
			Statistiques.nombresUbiquite.add(nb);

		} else if (c.getClass().toString().equals("class Interference")) {
			nb = Statistiques.nombresInterference.remove(Statistiques.nombresInterference.size() - 1) + 1;
			Statistiques.nombresInterference.add(nb);

		} else if (c.getClass().toString().equals("class Chevauchement")) {
			nb = Statistiques.nombresChevauchement.remove(Statistiques.nombresChevauchement.size() - 1) + 1;
			Statistiques.nombresChevauchement.add(nb);

		} else {
			System.out.println("Type de conflit inexistant !" + c.getClass().toString());
			(new Scanner(System.in)).nextLine();
		}
	}

	/**
	  * Mise a jour des listes sauvant l'evolution des conflits en rajoutant
	  * une case en plus pour signaler une nouvelle iteration.
	  */
	public static void nouvelleIteration() {
		Statistiques.nombresUbiquite.add(0);
		Statistiques.nombresInterference.add(0);
		Statistiques.nombresChevauchement.add(0);
		Statistiques.nombresConflits.add(0);
		Statistiques.nombresConflitsCorriges.add(0);
	}

	/**
	  * Mise a jour de la duree totale de decalage.
	  * @param dureeTranslation duree a prendre en consideration.
	  */
	public static void mettreAJourDureeTotaleDecalage(long dureeTranslation) {
		Statistiques.dureeTotaleDecalage += dureeTranslation;
	}

	/**
	  * Mise a jour du nombre de conflits corriges.
	  * @param nombreConflitsCorriges le nombre de conflits corriges
	  */
	public static void setNombresConflitsCorriges(int nombreConflitsCorriges) {
		Statistiques.nombresConflitsCorriges.remove(Statistiques.nombresConflitsCorriges.size() - 1);
		Statistiques.nombresConflitsCorriges.add(nombreConflitsCorriges);
	}

	/**
	* Calcule de la duree moyenne de decalage.
	  * @return la duree moyenne de decalage.
	  */
	public static double dureeMoyenneDecalage() {
		return (double) Statistiques.dureeTotaleDecalage / Statistiques.nbDecalage;
	}

	/**
	  * Calcule d'un taux d'existance : le nombre d'entiers positifs dans la liste
	  * par la taille de la lsite.
	  * @param nombres sequence de nombres a etudier
	  * @return le taux d'existance.
	  */
	public double tauxExistence(List<Integer> nombres) {
		long survie = nombres.stream()
													.filter( x -> !x.equals(0) )
													.count();
		return (double) survie / (double) nombres.size();
	}

	/**
	  * @param chirurgies toutes les chirurgies a considerer.
	  * @return le nombre de chirurgies non urgentes qui se deroulent le soir.
	  */
	public long nombreChirurgiesNormalesSoir(Collection<Chirurgie> chirurgies) {
		return chirurgies.stream()
						.filter( x -> !x.dansJournee() && !x.estUrgente())
						.count();
	}

	/**
	  * Afficher les chirurgies non urgentes qui se deroule le soir.
	  * Afficher egalement son nombre.
	  * @param chirurgies toutesl es chirurgies a considerer.
	  */
	public void afficherChirurgiesSoir(Collection<Chirurgie> chirurgies) {
		chirurgies.stream()
					.filter( x->!x.dansJournee() && !x.estUrgente())
					.forEach(System.out::print);
		System.out.println("Nombre chirurgies non urgente le soir : " + this.nombreChirurgiesNormalesSoir(chirurgies));
	}

	/**
	  * Mesurer la pertinance de la correction de la base de donnees chirurgicales.
	  * Il s'agit du nombre de conflits corriges par correction.
	  * @param apresCorrection les statistiques apres correction de la base de donnees.
	  * @return la pertinance souvent comprise entre entre 0 et 1.
	  */
	public double mesurerPertinance(Statistiques apresCorrection) {
		return ((double) (this.nbConflits - apresCorrection.nbConflits)) / ((double) Statistiques.nbCorrection);
	}

	/**
	  * Affichage du graphique montrant l'evolution des differents types de conflits
	  * dans le temps. Cette methode s'utilise qu'une seuele fois car elle lance
	  * la methode start de Application de javaFX.
	  */
	public void afficherGraphique() {
		Map<String, List<Integer>> map = Statistiques.dataConflits();
		Graphique g = new Graphique();

		Graphique.valeurs = map;

		g.afficher(null, Statistiques.nombresConflits.size() - 1, 45);
	}

}
