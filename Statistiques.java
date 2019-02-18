import java.util.List;
import java.util.OptionalLong;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalTime;
import java.util.stream.Collectors;
import java.util.Collection;

public class Statistiques {
	private Set<Chirurgie> operationsSansConflit;
	private long dureeMoyenne; // Duree moyenne d'une operation
	private long premierQuartile;
	private long mediane;
	private long dernierQuartile;
	private Map<LocalTime, Integer> heuresConflits;
	private Map<Chirurgien, Double> dureeParChirurgien;
	private Map<Salle, Double> dureeParSalle;

	public Statistiques(List<Chirurgie> listeBase, List<Conflit> listeConflits) {

		// Extraction des chirurgies en conflits
		Set<Chirurgie> enConflit = new HashSet<>(); // implementer hashCode ?
		for (Conflit conflit : listeConflits) {
			enConflit.add(conflit.getPremiereChirurgie());
			enConflit.add(conflit.getSecondeChirurgie());
		}

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
		
		System.out.println("----Calcul des durees moyennes par chirurgien...");
		this.dureeParChirurgien = this.dureeParChirurgien();
		
		System.out.println("----Calcul des durees moyennes par salle...");
		this.dureeParSalle = this.dureeParSalle();
		
		System.out.println("Fin du chargement des outils statistiques.");
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
		return this.dureeParSalle(this.operationsSansConflit);
    }

	public Map<Salle, Double> dureeParSalle(Collection<Chirurgie> chirurgies) {
		List<Salle> salles = this.operationsSansConflit.stream()
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
		return this.dureeParChirurgien(this.operationsSansConflit);
    }

	public Map<Chirurgien, Double> dureeParChirurgien(Collection<Chirurgie> chirurgies) {
		List<Chirurgien> chirurgiens = this.operationsSansConflit.stream()
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

	/*public void afficheTout() {
		this.operationsSansConflit.stream().mapToLong(chrg -> chrg.duree()).sorted().forEach(System.out::println);
	}*/

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

	// Mesure plusieurs indicateurs de qualite au sujet de la correction de la base de donnees
	public void qualite(List<Chirurgie> listeChirurgies) {
		Map<Salle, Double> realisationsSalles = this.dureeParSalle(listeChirurgies);
		Map<Chirurgien, Double> realisationsChirurgiens = this.dureeParChirurgien(listeChirurgies);

		double ecartSalles = this.ecartSalles(realisationsSalles);
		double ecartChirurgiens = this.ecartChirurgiens(realisationsChirurgiens);

		System.out.println("Ecart par salles : " + ecartSalles);
		System.out.println("Ecart par chirurgiens : " + ecartChirurgiens);
		System.out.println("Ecart-type salles : " + this.ecartType(realisationsSalles.values()));
		System.out.println("Ecart-type chirurgiens : " + this.ecartType(realisationsChirurgiens.values()));

		System.out.println(this.dureeParSalle);
		System.out.println(this.dureeParChirurgien);
	}
}
