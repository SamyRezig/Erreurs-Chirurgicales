import java.util.List;
import java.util.OptionalLong;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalTime;
import java.util.stream.Collectors;

public class Statistiques {
	private Set<Chirurgie> operationsSansConflit;
	private long dureeMoyenne; // Duree moyenne d'une operation
	private long premierQuartile;
	private long mediane;
	private long dernierQuartile;
	private Map<LocalTime, Integer> heuresConflits;

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
		this.dureeMoyenne = this.calculerDureeMoyenne();
		this.premierQuartile = this.calculerPremierQuartile();
		this.mediane = this.calculerMediane();
		this.dernierQuartile = this.calculerDernierQuartile();
		topHeuresConflits(listeConflits);
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

	private void topHeuresConflits(List<Conflit> listeConflits) {
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

		this.heuresConflits = tableFrequences;
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

	public void afficheHeuresConflits() {
		System.out.println(this.heuresConflits);
	}

	public List<LocalTime> getHeuresConflits() {
		return new ArrayList<>(this.heuresConflits.keySet());
	}

    public void dureeParSalle() {
        List<Salle> salles = this.operationsSansConflit.stream()
                                                .map( x->x.getSalle() )
                                                .collect(Collectors.toList());
        Map<Salle, Double> dureeSalles = new HashMap<>();
        long sum;
        long card;

        for (Salle salleCourante : salles) {
            sum = this.operationsSansConflit.stream()
                                    .filter( x->x.getSalle().equals(salleCourante) )
                                    .mapToLong( x->x.duree() )
                                    .sum();
            card = this.operationsSansConflit.stream()
                                    .filter( x->x.getSalle().equals(salleCourante) )
                                    .count();
            dureeSalles.put(salleCourante, (double)sum / (double)card);
        }

        System.out.println(dureeSalles);
    }

    public void dureeParChirurgien() {
        List<Chirurgien> chirurgiens = this.operationsSansConflit.stream()
                                                .map( x->x.getChirurgien() )
                                                .collect(Collectors.toList());
        Map<Chirurgien, Double> dureeChirurgien = new HashMap<>();
        long sum;
        long card;

        for (Chirurgien chgCourante : chirurgiens) {
            sum = this.operationsSansConflit.stream()
                                    .filter( x->x.getChirurgien().equals(chgCourante) )
                                    .mapToLong( x->x.duree() )
                                    .sum();
            card = this.operationsSansConflit.stream()
                                    .filter( x->x.getChirurgien().equals(chgCourante) )
                                    .count();
            dureeChirurgien.put(chgCourante, (double)sum / (double)card);
        }

        System.out.println(dureeChirurgien);
    }

	public void afficheTout() {
		this.operationsSansConflit.stream().mapToLong(chrg -> chrg.duree()).sorted().forEach(System.out::println);
	}
}
