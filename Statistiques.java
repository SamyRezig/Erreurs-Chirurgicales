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

public class Statistiques {
	private int nbConflits;
	private long dureeMoyenne; // Duree moyenne d'une operation
	private long premierQuartile;
	private long mediane;
	private long dernierQuartile;

	private Map<LocalTime, Integer> heuresConflits;
	private Map<Chirurgien, Double> dureeParChirurgien;
	private Map<Salle, Double> dureeParSalle;
	private Map<LocalDate, Long> dureeJournees;

	private double ecartTypeSalles;
	private double ecartTypeChirurgiens;

	private static long dureeTotaleDecalage = 0;
	private static long dureeTotaleDecoupage = 0;
	private static int nbNormalisation = 0;
	private static int nbDecoupage = 0;
	private static int nbRess = 0;
	private static int nbDecalage = 0;
	private static int nbCorrection = 0;

	public static List<Integer> nombresUbiquite = new ArrayList<>();
	public static List<Integer> nombresInterference = new ArrayList<>();
	public static List<Integer> nombresChevauchement = new ArrayList<>();
	public static List<Integer> nombresConflits = new ArrayList<>();
	public static List<Integer> nombresConflitsCorriges = new ArrayList<>();

	public Statistiques(Agenda a) {
        List<Chirurgie> listeBase = a.getListeChirurgies();
        List<Conflit> listeConflits = a.extraireConflits();
        NavigableMap<LocalDate, PlanningJournee> planning = a.getPlanning();

		this.nbConflits = listeConflits.size();

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
					System.out.print("\t*");

				} else if (medecin.censeTravailler(jour) && !a.getPlanning().get(jour).travaille(medecin)) {
					System.out.print("\t+");

				} else if (!medecin.censeTravailler(jour) && a.getPlanning().get(jour).travaille(medecin)) {
					System.out.print("\t?");

				} else {
					System.out.print("\t|");
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
					System.out.print("\t*");

				} else {
					System.out.print("\t|");
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

	private Map<LocalDate, Long> dureeJournees(Map<LocalDate, PlanningJournee> planning) {
		Map<LocalDate, Long> resultat = new HashMap<>();
		PlanningJournee contenuJour;

		for (LocalDate jour : planning.keySet()) {
			contenuJour = planning.get(jour);
			resultat.put(jour, contenuJour.dureeTotale());
		}

		return resultat;
	}

	public static void plusDureeDecoupage(long duree) {
		Statistiques.dureeTotaleDecoupage += duree;
	}

	public static void plusDecoupe() {
		Statistiques.nbDecoupage++;
		Statistiques.nbCorrection++;
	}

	public static void plusModifRessource() {
		Statistiques.nbRess++;
		Statistiques.nbCorrection++;
	}

	public static void plusDecalage() {
		Statistiques.nbDecalage++;
		Statistiques.nbCorrection++;
	}

	public static void plusNormalisation() {
		Statistiques.nbNormalisation++;
		Statistiques.nbCorrection++;
	}

	private long calculerDureeMoyenne(Set<Chirurgie> operationsSansConflit) {
		long sommeDurees = operationsSansConflit.stream().mapToLong(chrg -> chrg.duree()).sum();
		return sommeDurees / operationsSansConflit.size();
		// Possible perte de donnees avec division par 2 long
	}

	private long calculerPremierQuartile(Set<Chirurgie> operationsSansConflit) {
		OptionalLong ol = operationsSansConflit.stream().mapToLong(chrg -> chrg.duree()).sorted()
				.skip(operationsSansConflit.size() / 4).findFirst();
		long premierQuartile = ol.getAsLong();

		return premierQuartile;
	}

	private long calculerMediane(Set<Chirurgie> operationsSansConflit) {
		OptionalLong ol = operationsSansConflit.stream().mapToLong(chrg -> chrg.duree()).sorted()
				.skip(operationsSansConflit.size() / 2).findFirst();
		long mediane = ol.getAsLong();

		return mediane;
	}

	private long calculerDernierQuartile(Set<Chirurgie> operationsSansConflit) {
		OptionalLong ol = operationsSansConflit.stream()
											.mapToLong(chrg -> chrg.duree()).sorted()
											.skip(operationsSansConflit.size() * 3 / 4)
											.findFirst();
		long dernierQuartile = ol.getAsLong();

		return dernierQuartile;
	}

	private Map<LocalTime, Integer> topHeuresConflits(List<Conflit> listeConflits) {
		Map<LocalTime, Integer> tableFrequences = new HashMap<>();
		Integer frequence;

		List<LocalTime> listeTemps = new ArrayList<>();
		for (Conflit conflitCourant : listeConflits) {
			listeTemps.add(conflitCourant.getPremiereChirurgie().getDatesOperation().getDateDebut().toLocalTime());
			listeTemps.add(conflitCourant.getPremiereChirurgie().getDatesOperation().getDateFin().toLocalTime());
			listeTemps.add(conflitCourant.getSecondeChirurgie().getDatesOperation().getDateDebut().toLocalTime());
			listeTemps.add(conflitCourant.getSecondeChirurgie().getDatesOperation().getDateFin().toLocalTime());
		}

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
																		.reversed())
																		.collect(Collectors.toMap(
			Map.Entry::getKey, Map.Entry::getValue, (ancienneValeur, nouvelleValeur) -> ancienneValeur, LinkedHashMap::new));

		return tableFrequences;
	}

	public long getDureeMoyenne() {
		return this.dureeMoyenne;
	}

	public long getPremierQuartile() {
		return this.premierQuartile;
	}

	public long getMediane() {
		return this.mediane;
	}

	public long getDernierQuartile() {
		return this.dernierQuartile;
	}

	public Map<Chirurgien, Double> getDureeParChirurgien() {
		return this.dureeParChirurgien;
	}

	public Map<Salle, Double> getDureeParSalle() {
		return this.dureeParSalle;
	}

	public List<LocalTime> getHeuresConflits() {
		return new ArrayList<>(this.heuresConflits.keySet());
	}

	public Map<Salle, Double> dureeParSalle(Agenda a) {
		List<Salle> salles = new ArrayList<>(a.getListeSalles());
		salles.addAll(a.getListeSallesUrgence());

		Map<Salle, Double> dureeSalles = new HashMap<>();
		long sum;
		long card;

		for (Salle salleCourante : salles) {
			sum = a.getListeChirurgies().stream()
										.filter(x -> x.getSalle().equals(salleCourante))
										.mapToLong(x -> x.duree())
										.sum();
			card = a.getListeChirurgies().stream()
										.filter(x -> x.getSalle().equals(salleCourante))
										.count();
			dureeSalles.put(salleCourante, (double) sum / (double) card);
		}
		return dureeSalles;
	}

	public Map<Chirurgien, Double> dureeParChirurgien(Agenda a) {
		Map<Chirurgien, Double> dureeChirurgien = new HashMap<>();
		long sum;
		long card;

		for (Chirurgien chgCourante : a.getListeChirurgiens()) {
			sum = a.getListeChirurgies().stream()
										.filter(x -> x.getChirurgien().equals(chgCourante))
										.mapToLong(x -> x.duree())
										.sum();
			card = a.getListeChirurgies().stream()
										.filter(x -> x.getChirurgien().equals(chgCourante))
										.count();
			dureeChirurgien.put(chgCourante, (double) sum / (double) card);
		}
		return dureeChirurgien;
	}

	// Moyenne des ecarts au carre entre les durees d'utilisation des salles avant
	// correction et apres correction de la base de donnees
	public double ecartSalles(Map<Salle, Double> realisationSalles) {
		Map<Salle, Double> dureeSallesCorrectes = this.dureeParSalle;
		double somme = 0;

		for (Salle courant : realisationSalles.keySet()) {
			somme += Math.pow((dureeSallesCorrectes.get(courant) - realisationSalles.get(courant)), 2);
		}

		return Math.sqrt(somme / (double) realisationSalles.keySet().size());
	}

	// Moyenne des ecarts au carre entre les durees de travail des chirurgiens avant
	// correction et apres correction de la base de donnees
	public double ecartChirurgiens(Map<Chirurgien, Double> realisationChirurgiens) {
		Map<Chirurgien, Double> dureesChirurgiensCorrectes = this.dureeParChirurgien;
		double somme = 0;

		for (Chirurgien courant : realisationChirurgiens.keySet()) {
			somme += Math.pow((dureesChirurgiensCorrectes.get(courant) - realisationChirurgiens.get(courant)), 2);
		}

		return Math.sqrt(somme / (double) realisationChirurgiens.keySet().size());
	}

	// Calcule l'ecart-type entre les valeurs passees en parametre
	public double ecartType(Collection<Double> valeurs) {
		double moyenne = 0.0;
		double sommeCarrees = 0.0;

		for (Double v : valeurs) {
			moyenne += v;
		}
		moyenne = moyenne / (double) valeurs.size();

		for (Double v : valeurs) {
			sommeCarrees += Math.pow((v - moyenne), 2);
		}
		return Math.sqrt(sommeCarrees / (double) valeurs.size());
	}

	public double getEcartTypeSalles() {
		return this.ecartTypeSalles;
	}

	public double getEcartTypeChirurgiens() {
		return this.ecartTypeChirurgiens;
	}

	private double ecartMoyenAllongement(Map<LocalDate, Long> planningApres) {
		double ecartMoyen = 0;
		int nbJours = 0;

		for (LocalDate jour : planningApres.keySet()) {
			ecartMoyen += (planningApres.get(jour) - this.dureeJournees.get(jour));
			nbJours++;
		}

		return ecartMoyen / (double) nbJours;
	}

	@Deprecated
	public void afficherJoursChirurgiensInsuffisants(NavigableMap<LocalDate, PlanningJournee> planning) {
		PlanningJournee contenuJour;
		int seuilChirurgiens;
		int nbChirurgiens;

		for (LocalDate jour : planning.keySet()) {
			contenuJour = planning.get(jour);
			seuilChirurgiens = contenuJour.getListeChirurgies().size() / 4; // 4 = nombre de chirurgies qu'un seul
																			// chirurgien peut gerer
			nbChirurgiens = contenuJour.nbChirurgiensDispos();

			if (nbChirurgiens < seuilChirurgiens) {
				System.out.println(jour + " : " + nbChirurgiens + " chirurgiens disponibles pour "
						+ contenuJour.getListeChirurgies().size() + " chirurgies.");
			}
		}
	}

	public static Map<String, List<Integer>> dataConflits() {
		Map<String, List<Integer>> data = new HashMap<>();

		data.put("Ubiquite", Statistiques.nombresUbiquite);
		data.put("Interference", Statistiques.nombresInterference);
		data.put("Chevauchement", Statistiques.nombresChevauchement);
		data.put("Total", Statistiques.nombresConflits);

		return data;
	}

	public void afficherConflitsTotaux() {
		Map<String, List<Integer>> map = Statistiques.dataConflits();
		System.out.println("Chevauchement :\t\t" + map.get("Chevauchement"));
		System.out.println("Interference : \t\t" + map.get("Interference"));
		System.out.println("Ubiquite : \t\t" + map.get("Ubiquite"));
		System.out.println("Total : \t\t" + map.get("Total"));
		System.out.println("Conflits corriges:\t" + Statistiques.nombresConflitsCorriges);
		System.out.println();
		System.out.println("Taux d'existence des chevauchements : \t" + this.tauxExistence(Statistiques.nombresChevauchement) + " pourcent du temps");
		System.out.println("Taux d'existence des interferences : \t" + this.tauxExistence(Statistiques.nombresInterference) + " pourcent du temps");
		System.out.println("Taux d'existence des ubiquites : \t" + this.tauxExistence(Statistiques.nombresUbiquite) + " pourcent du temps");

		int nbIterNecessaires = (map.get("Total").get( map.get("Total").size() - 1 ).equals(0)) ?
										map.get("Total").size() - 1
										:
										map.get("Total").size();

		String plus = (map.get("Total").get( map.get("Total").size() - 1 ) == 0) ? "" : "+";	// Cas ou il fallait plus d'iteration pour resoudre tous les conflits
		System.out.println("Nombre d'iterations necessaires : \t" + nbIterNecessaires + plus);
	}

	public void comparer(Statistiques apresStats) {
		System.out.println("Statistiques \t\t AVANT correction \t APRES correction");
		System.out.println("Duree moyenne : \t\t" + this.dureeMoyenne + "\t\t\t" + apresStats.getDureeMoyenne());
		System.out.println("Duree mediane : \t\t" + this.mediane + "\t\t\t" + apresStats.getMediane());
		System.out.println("Premier quartile : \t\t" + this.premierQuartile + "\t\t\t" + apresStats.getPremierQuartile());
		System.out.println("Dernier quartile : \t\t" + this.dernierQuartile + "\t\t\t" + apresStats.getDernierQuartile());

		System.out.println("Ecart-type duree des salles : :\t"
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

		System.out.println("Duree moyenne de decalage : \t"
				+ Statistiques.dureeMoyenneDecalage()
				+ " minutes par decalage soit un decalage de "
				+ (float) Statistiques.dureeTotaleDecalage / 60 + " heures");

		System.out.println("Duree moyenne de decoupage : \t"
				+ Statistiques.dureeMoyenneDecoupage()
				+ " minutes par decoupage soit "
				+ (float) Statistiques.dureeTotaleDecoupage / 60
				+ " heures decoupees hors normalisation");

		System.out.println("Pertinance de la correction : \t" + this.mesurerPertinance(apresStats)
				+ " conflits corriges par correction");
	}

	public void afficherNombreCorrections() {
		System.out.println("Nombre de normalisation : \t" + Statistiques.nbNormalisation);
		System.out.println("Nombre de modifs ressources : \t" + Statistiques.nbRess);
		System.out.println("Nombre de decoupages : \t\t" + Statistiques.nbDecoupage);
		System.out.println("Nombre de decalages : \t\t" + Statistiques.nbDecalage);
		System.out.println(" === Nombre de corrections : \t" + Statistiques.nbCorrection);
	}

	private static double dureeMoyenneDecoupage() {
		return (double) Statistiques.dureeTotaleDecoupage / (double) Statistiques.nbDecoupage;
	}

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

	public static void nouvelleIteration() {
		Statistiques.nombresUbiquite.add(0);
		Statistiques.nombresInterference.add(0);
		Statistiques.nombresChevauchement.add(0);
		Statistiques.nombresConflits.add(0);
		Statistiques.nombresConflitsCorriges.add(0);
	}

	public static void mettreAJourDureeTotaleDecalage(long dureeTranslation) {
		Statistiques.dureeTotaleDecalage += dureeTranslation;
	}

	public static void setNombresConflitsCorriges(int nombreConflitsCorriges) {
		Statistiques.nombresConflitsCorriges.remove(Statistiques.nombresConflitsCorriges.size() - 1);
		Statistiques.nombresConflitsCorriges.add(nombreConflitsCorriges);
	}

	public static double dureeMoyenneDecalage() {
		return (double) Statistiques.dureeTotaleDecalage / Statistiques.nbDecalage;
	}

	public double tauxExistence(List<Integer> nombres) {
		long survie = nombres.stream()
													.filter( x -> !x.equals(0) )
													.count();
		return (double) survie / (double) nombres.size();
	}

	public long nombreChirurgiesNormalesSoir(Collection<Chirurgie> chirurgies) {
		return chirurgies.stream()
						.filter( x -> !x.dansJournee() && !x.estUrgente())
						.count();
	}

	public void afficherChirurgiesSoir(Collection<Chirurgie> chirurgies) {
		chirurgies.stream()
					.filter( x->!x.dansJournee() && !x.estUrgente())
					.forEach(System.out::print);
		System.out.println("Nombre chirurgies non urgente le soir : " + this.nombreChirurgiesNormalesSoir(chirurgies));
	}

	public double mesurerPertinance(Statistiques apresCorrection) {
		return ((double) (this.nbConflits - apresCorrection.nbConflits)) / ((double) Statistiques.nbCorrection);
	}

	public void afficherGraphique(String [] args) {
		Map<String, List<Integer>> map = Statistiques.dataConflits();
		Graphique g = new Graphique();

		Graphique.valeurs = map;

		g.afficher(args, Statistiques.nombresConflits.size() - 1, 45);
	}
}
