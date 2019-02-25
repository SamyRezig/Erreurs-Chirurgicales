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

public class Statistiques {
	private Set<Chirurgie> operations;
	private Set<Chirurgie> operationsSansConflit;
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
	
	private static long dureeTotaleDecalage;
	public static int nbNormalisation = 0;
	public static int nbDecoupage = 0;
	public static int nbRess = 0;
	public static int nbDecalage = 0;
	public static int nbCorrection = 0;

	public static List<Integer> nombresUbiquite = new ArrayList<>();
	public static List<Integer> nombresInterference = new ArrayList<>();
	public static List<Integer> nombresChevauchement = new ArrayList<>();
	public static List<Integer> nombresConflits = new ArrayList<>();

	public Statistiques(List<Chirurgie> listeBase, List<Conflit> listeConflits, NavigableMap<LocalDate, PlanningJournee> planning) {
		this.nbConflits = listeConflits.size();

		// Extraction des chirurgies en conflits
		Set<Chirurgie> enConflit = new HashSet<>(); // implementer hashCode ?
		for (Conflit conflit : listeConflits) {
			enConflit.add(conflit.getPremiereChirurgie());
			enConflit.add(conflit.getSecondeChirurgie());
		}
		// Toutes les operations
		this.operations = new HashSet<>(listeBase);

		// Difference
		this.operationsSansConflit = new HashSet<>(listeBase);
		this.operationsSansConflit.removeAll(enConflit);

		// Remplissage des attributs
		System.out.println("Chargement des outils statistiques...");
		System.out.println("----Calcul des moyennes...");
		this.dureeMoyenne = this.calculerDureeMoyenne();

		System.out.println("----Calcul des quartiles/mediane...");
		this.premierQuartile = this.calculerPremierQuartile();
		this.mediane = this.calculerMediane();
		this.dernierQuartile = this.calculerDernierQuartile();

		System.out.println("----Calcul des heures des conflits les plus frequentes...");
		this.heuresConflits = this.topHeuresConflits(listeConflits);
		//System.out.println(this.heuresConflits);

		/*System.out.println("----Calcul des durees moyennes par chirurgien...");
		this.dureeParChirurgien = this.dureeParChirurgien();

		System.out.println("----Calcul des durees moyennes par salle...");
		this.dureeParSalle = this.dureeParSalle();

		System.out.println("----Calcul des ecart-types");
		this.ecartTypeSalles = this.ecartType(this.dureeParSalle.values());
		this.ecartTypeChirurgiens = this.ecartType(this.dureeParChirurgien.values());*/

		System.out.println("----Calcul des durees pour chaque journee.");
		this.dureeJournees = this.dureeJournees(planning);

		System.out.println("EDT par salle");
		this.afficherJoursTravailSalles(planning);

		System.out.println("Jours avec chirurgiens insuffisants : ");
		this.afficherJoursChirurgiensInsuffisants(planning);

		System.out.println("Fin du chargement des outils statistiques.");
	}

	public void afficherJoursTravail(NavigableMap<LocalDate, PlanningJournee> planning) {
		char lettre;		// Stocke chaque lettre des noms des chirurgiens pour leur affichage
		List<Chirurgien> listeChirurgiens =  this.operations.stream()
                                                .map( x->x.getChirurgien() )
												.distinct()
                                                .collect(Collectors.toList());

		// Afficher le nom des chirurgiens
		for (int i = 0; i < 16; i++) {
			System.out.print("\t\t");
			for (Chirurgien medecin : listeChirurgiens) {
				lettre = (i < medecin.toString().length()) ? medecin.toString().charAt(i) : ' ';
				System.out.print(lettre + "\t");
			}
			System.out.println();
		}

		// Afficher leur jour de travail
		for (LocalDate jour : planning.keySet()) {
			System.out.print(jour + " : ");
			for (Chirurgien medecin : listeChirurgiens) {

				if (planning.get(jour).travaille(medecin)) {
					System.out.print("\t*");

				} else {
					System.out.print("\t|");
				}
			}
			System.out.print("  " + planning.get(jour).getListeChirurgies().size());
			System.out.println();
		}
	}

	public void afficherJoursTravailSalles(NavigableMap<LocalDate, PlanningJournee> planning) {
		char lettre;		// Stocke chaque lettre des noms des chirurgiens pour leur affichage
		List<Salle> listeSalles =  this.operations.stream()
                                                .map( x->x.getSalle() )
												.distinct()
                                                .collect(Collectors.toList());

		// Afficher le nom des chirurgiens
		for (int i = 0; i < 9; i++) {
			System.out.print("\t\t");
			for (Salle bloc : listeSalles) {
				lettre = (i < bloc.toString().length()) ? bloc.toString().charAt(i) : ' ';
				System.out.print(lettre + "\t");
			}
			System.out.println();
		}

		// Afficher leur jour de travail
		for (LocalDate jour : planning.keySet()) {
			System.out.print(jour + " : ");
			for (Salle bloc : listeSalles) {

				if (planning.get(jour).occupe(bloc)) {
					System.out.print("\t*");

				} else {
					System.out.print("\t|");
				}
			}
			System.out.print("  " + planning.get(jour).getListeChirurgies().size());
			System.out.println();
		}
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

	private long calculerDureeMoyenne() {
		long sommeDurees = this.operationsSansConflit.stream().mapToLong(chrg -> chrg.duree()).sum();
		return sommeDurees / this.operationsSansConflit.size();
		// Possible perte de donnees avec division par 2 long
	}

	private long calculerPremierQuartile() {
		OptionalLong ol = this.operationsSansConflit.stream().mapToLong(chrg -> chrg.duree()).sorted()
				.skip(this.operationsSansConflit.size() / 4).findFirst();
		long premierQuartile = ol.getAsLong();

		return premierQuartile;
	}

	private long calculerMediane() {
		OptionalLong ol = this.operationsSansConflit.stream().mapToLong(chrg -> chrg.duree()).sorted()
				.skip(this.operationsSansConflit.size() / 2).findFirst();
		long mediane = ol.getAsLong();

		return mediane;
	}

	private long calculerDernierQuartile() {
		OptionalLong ol = this.operationsSansConflit.stream().mapToLong(chrg -> chrg.duree()).sorted()
				.skip(this.operationsSansConflit.size() * 3 / 4).findFirst();
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
			if (frequence == null)	tableFrequences.put(temps, 1);
			else 					tableFrequences.put(temps, frequence + 1);
		}

		tableFrequences = tableFrequences.entrySet().stream()
										.sorted(Map.Entry.<LocalTime, Integer>comparingByValue().reversed())
										.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
												(oldValue, newValue) -> oldValue, LinkedHashMap::new));

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

	public void afficheHeuresConflits() {
		System.out.println(this.heuresConflits);
	}

	public List<LocalTime> getHeuresConflits() {
		return new ArrayList<>(this.heuresConflits.keySet());
	}

    public Map<Salle, Double> dureeParSalle() {
		return this.dureeParSalle(this.operations);
    }

	public Map<Salle, Double> dureeParSalle(Collection<Chirurgie> chirurgies) {
		List<Salle> salles = chirurgies.stream()
                                                .map( x->x.getSalle() )
                                                .collect(Collectors.toList());
		Map<Salle, Double> dureeSalles = new HashMap<>();
        long sum;
        long card;

        for (Salle salleCourante : salles) {
            sum = chirurgies.stream()
                                    .filter( x->x.getSalle().equals(salleCourante) )
                                    .mapToLong( x->x.duree() )
                                    .sum();
            card = chirurgies.stream()
                                    .filter( x->x.getSalle().equals(salleCourante) )
                                    .count();
            dureeSalles.put(salleCourante, (double)sum / (double)card);
        }

		return dureeSalles;
	}

    public Map<Chirurgien, Double> dureeParChirurgien() {
		return this.dureeParChirurgien(this.operations);
    }

	public Map<Chirurgien, Double> dureeParChirurgien(Collection<Chirurgie> chirurgies) {
		List<Chirurgien> chirurgiens = chirurgies.stream()
                                                .map( x->x.getChirurgien() )
                                                .collect(Collectors.toList());

		Map<Chirurgien, Double> dureeChirurgien = new HashMap<>();
        long sum;
        long card;

        for (Chirurgien chgCourante : chirurgiens) {
            sum = chirurgies.stream()
                                    .filter( x->x.getChirurgien().equals(chgCourante) )
                                    .mapToLong( x->x.duree() )
                                    .sum();
            card = chirurgies.stream()
                                    .filter( x->x.getChirurgien().equals(chgCourante) )
                                    .count();
            dureeChirurgien.put(chgCourante, (double)sum / (double)card);
        }

		return dureeChirurgien;
	}

	public static void repartition(List<Chirurgie> listeChirurgies) {
		Map<Chirurgien, Long> mapChirurgien = new HashMap<>();
		Map<Salle, Long> mapSalle = new HashMap<>();

		Long cpt = null;
		for (Chirurgie courante : listeChirurgies) {
			// MAJ des chirurgiens
			cpt = mapChirurgien.get(courante.getChirurgien());
			if (cpt == null) {
				mapChirurgien.put(courante.getChirurgien(), courante.getDatesOperation().duree());
			} else {
				mapChirurgien.put(courante.getChirurgien(), cpt + courante.getDatesOperation().duree());
			}

			//MAJ des salles
			cpt = mapSalle.get(courante.getSalle());
			if (cpt == null) {
				mapSalle.put(courante.getSalle(), courante.getDatesOperation().duree());
			} else {
				mapSalle.put(courante.getSalle(), cpt + courante.getDatesOperation().duree());
			}
		}

		System.out.println("Repartition par chirurgien en minutes:\n" + mapChirurgien);
		System.out.println("Repartition par salle en minutes:\n" + mapSalle);
	}

	// Moyenne des ecarts au carre entre les durees d'utilisation des salles avant correction et apres correction de la base de donnees
	public double ecartSalles(Map<Salle, Double> realisationSalles) {
		Map<Salle, Double> dureeSallesCorrectes = this.dureeParSalle;
		double somme = 0;

		for (Salle courant : realisationSalles.keySet()) {
			somme += Math.pow((dureeSallesCorrectes.get(courant) - realisationSalles.get(courant)), 2);
		}

		return Math.sqrt(somme / (double) realisationSalles.keySet().size());
	}

	// Moyenne des ecarts au carre entre les durees de travail des chirurgiens avant correction et apres correction de la base de donnees
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

	public int getNbConflits() {
		return this.nbConflits;
	}

	private double ecartMoyenAllongement(Map<LocalDate, Long> planningApres) {
		double ecartMoyen = 0;
		int card = 0;

		for (LocalDate jour : planningApres.keySet()) {
			ecartMoyen += (planningApres.get(jour) - this.dureeJournees.get(jour));
			card++;
		}

		return ecartMoyen / (double) card;
	}

	public void afficherJoursChirurgiensInsuffisants(NavigableMap<LocalDate, PlanningJournee> planning) {
		PlanningJournee contenuJour;
		int seuilChirurgiens;
		int nbChirurgiens;

		for (LocalDate jour : planning.keySet()) {
			contenuJour = planning.get(jour);
			seuilChirurgiens = contenuJour.getListeChirurgies().size() / 4 ;	// 4 = nombre de chirurgies qu'un seul chirurgien peut gerer
			nbChirurgiens = contenuJour.nbChirurgiensDispos();

			if (nbChirurgiens < seuilChirurgiens) {
				System.out.println(jour + " : " + nbChirurgiens + " chirurgiens disponibles pour " + contenuJour.getListeChirurgies().size() + " chirurgies.");
			}

		}
	}

	public void comparer(Statistiques apresStats) {
		System.out.println("Statistiques -- AVANT correction -- APRES correction");
		System.out.println("Duree moyenne : " + this.dureeMoyenne + "\t" + apresStats.getDureeMoyenne());
		System.out.println("Duree mediane : " + this.mediane + "\t" + apresStats.getMediane());
		System.out.println("Premier quartile : " + this.premierQuartile + "\t" + apresStats.getPremierQuartile());
		System.out.println("Dernier quartile : " + this.dernierQuartile + "\t" + apresStats.getDernierQuartile());
		//System.out.println("Ecart-type duree par salle : " + this.ecartTypeSalles + "\t" + apresStats.getEcartTypeSalles());
		//System.out.println("Ecart-type duree par chirurgiens : " + this.ecartTypeChirurgiens + "\t" + apresStats.getEcartTypeChirurgiens());
		System.out.println("Nombre de conflits restant : " + this.nbConflits + "\t" + apresStats.getNbConflits());
		System.out.println("Duree moyenne d'allongement des journees en minute : " + this.ecartMoyenAllongement(apresStats.dureeJournees));
		
		System.out.println("Pertinance de la correction : \t" + this.mesurerPertinance(apresStats) + " conflits corriges par correction");
		System.out.println("Duree moyenne de decalage : \t" + Statistiques.dureeMoyenneDecalage() + " minutes");
	}

	public static void recenser(Conflit c) {
		Integer nb;

		nb = Statistiques.nombresConflits.remove( Statistiques.nombresConflits.size() - 1 ) + 1;
		Statistiques.nombresConflits.add(nb);

		if (c.getClass().toString().equals("class Ubiquite")) {
			nb = Statistiques.nombresUbiquite.remove( Statistiques.nombresUbiquite.size() - 1 ) + 1;
			Statistiques.nombresUbiquite.add(nb);

		} else if (c.getClass().toString().equals("class Interference")) {
			nb = Statistiques.nombresInterference.remove( Statistiques.nombresInterference.size() - 1 ) + 1;
			Statistiques.nombresInterference.add(nb);

		} else if (c.getClass().toString().equals("class Chevauchement")) {
			nb = Statistiques.nombresChevauchement.remove( Statistiques.nombresChevauchement.size() - 1 ) + 1;
			Statistiques.nombresChevauchement.add(nb);

		} else {
			System.out.println("WTF ? " + c.getClass().toString());
			(new Scanner(System.in)).nextLine();
		}
	}

	public static void nouvelleIteration() {
		Statistiques.nombresUbiquite.add(0);
		Statistiques.nombresInterference.add(0);
		Statistiques.nombresChevauchement.add(0);
		Statistiques.nombresConflits.add(0);
	}
	
	public static void mettreAJourDureeTotaleDecalage(long dureeTranslation) {
		Statistiques.dureeTotaleDecalage += dureeTranslation;
	}
	
	public static double dureeMoyenneDecalage() {
		return (double) Statistiques.dureeTotaleDecalage / Statistiques.nbDecalage;
	}
	
	public double mesurerPertinance(Statistiques apresCorrection) {
		return ((double) (this.nbConflits - apresCorrection.nbConflits)) / ((double) Statistiques.nbCorrection);
	}
}
