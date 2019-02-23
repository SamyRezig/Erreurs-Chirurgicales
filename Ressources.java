import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Collections;

public class Ressources {
    private List<Chirurgien> listeChirurgiens;
    private List<Salle> listeSalles;
    private List<Salle> listeSallesUrgence;

    public Ressources(List<Chirurgien> chirurgiens, List<Salle> salles, List<Salle> sallesUrgence) {
        this.listeChirurgiens = chirurgiens;
        this.listeSalles = salles;
        this.listeSallesUrgence = sallesUrgence;
    }

    public List<Chirurgien> getListeChirurgiens() {
        return this.listeChirurgiens;
    }

    public List<Salle> getListeSalles() {
        return this.listeSalles;
    }

    public List<Salle> getListeSallesUrgence() {
        return this.listeSallesUrgence;
    }

    /*public void trierListes(List<Chirurgie> listeChirurgies) {
        Collections.shuffle(this.listeChirurgiens);
        Collections.shuffle(this.listeSalles);
        Collections.shuffle(this.listeSallesUrgence);
    }*/

    public void trierListes(List<Chirurgie> listeChirurgies) {
		Map<Chirurgien, Integer> mapChirurgiens = new HashMap<>();
		Map<Salle, Integer> mapSalles = new HashMap<>();
		Map<Salle, Integer> mapSallesUrgentes = new HashMap<>();
		Integer nbChrg = null;

		for (Chirurgie courante : listeChirurgies) {
			// Gestion des chirurgiens
			nbChrg = mapChirurgiens.get(courante.getChirurgien());
			if (nbChrg == null) {
				mapChirurgiens.put(courante.getChirurgien(), 1);
			} else {
				mapChirurgiens.put(courante.getChirurgien(), nbChrg + 1);
			}

			// Gestion des salles
			if (!courante.estUrgente()) {
				// Gestion des salles classiques
				nbChrg = mapSalles.get(courante.getSalle());
				if (nbChrg == null) {
					mapSalles.put(courante.getSalle(), 1);
				} else {
					mapSalles.put(courante.getSalle(), nbChrg + 1);
				}

			} else {
				// Gestion des salles urgentes
				nbChrg = mapSallesUrgentes.get(courante.getSalle());
				if (nbChrg == null) {
					mapSallesUrgentes.put(courante.getSalle(), 1);
				} else {
					mapSallesUrgentes.put(courante.getSalle(), nbChrg + 1);
				}
			}
		}
		// Conversion des chirurgiens en une liste
		this.listeChirurgiens = mapChirurgiens.entrySet().stream()
									.sorted(Map.Entry.comparingByValue())
									.map( x->x.getKey() )
									.collect(Collectors.toList());
		// Conversion des salles classiques en une liste
		this.listeSalles = mapSalles.entrySet().stream()
									.sorted(Map.Entry.comparingByValue())
									.map(x->x.getKey())
									.collect(Collectors.toList());
		// Conversion des salles d'urgence en une liste
		this.listeSallesUrgence = mapSallesUrgentes.entrySet().stream()
										.sorted(Map.Entry.comparingByValue())
										.map(x->x.getKey())
										.collect(Collectors.toList());
	}

    public void trierListes3(List<Chirurgie> listeChirurgies) {
		Map<Chirurgien, Long> mapChirurgiens = new HashMap<>();
		Map<Salle, Long> mapSalles = new HashMap<>();
		Map<Salle, Long> mapSallesUrgentes = new HashMap<>();
		Long duree = null;

		for (Chirurgie courante : listeChirurgies) {
			// Gestion des chirurgiens
			duree = mapChirurgiens.get(courante.getChirurgien());
			if (duree == null) {
				mapChirurgiens.put(courante.getChirurgien(), courante.getDatesOperation().duree());
			} else {
				mapChirurgiens.put(courante.getChirurgien(), duree + courante.getDatesOperation().duree());
			}

			// Gestion des salles
			if (!courante.estUrgente()) {
				// Gestion des salles classiques
				duree = mapSalles.get(courante.getSalle());
				if (duree == null) {
					mapSalles.put(courante.getSalle(), courante.getDatesOperation().duree());
				} else {
					mapSalles.put(courante.getSalle(), courante.getDatesOperation().duree() + duree);
				}

			} else {
				// Gestion des salles urgentes
				duree = mapSallesUrgentes.get(courante.getSalle());
				if (duree == null) {
					mapSallesUrgentes.put(courante.getSalle(), courante.getDatesOperation().duree());
				} else {
					mapSallesUrgentes.put(courante.getSalle(), courante.getDatesOperation().duree() + duree);
				}
			}
		}
		// Conversion des chirurgiens en une liste
		this.listeChirurgiens = mapChirurgiens.entrySet().stream()
									.sorted(Map.Entry.comparingByValue())
									.map( x->x.getKey() )
									.collect(Collectors.toList());
		// Conversion des salles classiques en une liste
		this.listeSalles = mapSalles.entrySet().stream()
									.sorted(Map.Entry.comparingByValue())
									.map(x->x.getKey())
									.collect(Collectors.toList());
		// Conversion des salles d'urgence en une liste
		this.listeSallesUrgence = mapSallesUrgentes.entrySet().stream()
										.sorted(Map.Entry.comparingByValue())
										.map(x->x.getKey())
										.collect(Collectors.toList());
	}

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();

        strb.append(this.listeChirurgiens);
        strb.append("\n");
        strb.append(this.listeSalles);
        strb.append("\n");
        strb.append(this.listeSallesUrgence);

        return strb.toString();
    }
}
