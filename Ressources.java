import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Scanner;

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

    public int nbChirurgiensDispos() {
        return this.listeChirurgiens.size();
    }

    public void tl(List<Chirurgie> listeChirurgies) {
        Map<Chirurgien, Long> mapChirurgiens = new HashMap<>();
        Chirurgien medecin = null;
        Long duree = null;

        for (Chirurgie chrg : listeChirurgies) {
            // Si le chirurgien est cense etre disponible
            if (this.listeChirurgiens.contains(chrg.getChirurgien())) {

            }
        }
    }

    public void trierListes3(List<Chirurgie> listeChirurgies) {
		Map<Chirurgien, Long> mapChirurgiens = new HashMap<>();
		Map<Salle, Long> mapSalles = new HashMap<>();
		Map<Salle, Long> mapSallesUrgentes = new HashMap<>();
		Long duree = null;

		for (Chirurgie courante : listeChirurgies) {

            // Le chirurgien est cence etre disponible cette journee
            if (this.listeChirurgiens.contains(courante.getChirurgien())) {
    			// Gestion des chirurgiens
    			duree = mapChirurgiens.get(courante.getChirurgien());
    			if (duree == null) {
    				mapChirurgiens.put(courante.getChirurgien(), courante.getDatesOperation().duree());
    			} else {
    				mapChirurgiens.put(courante.getChirurgien(), duree + courante.getDatesOperation().duree());
    			}
            }

			// Gestion des salles
            // La salle est censee etre disponible cette journee
			if (this.listeSalles.contains(courante.getSalle())) {
				// Gestion des salles classiques
				duree = mapSalles.get(courante.getSalle());
				if (duree == null) {
					mapSalles.put(courante.getSalle(), courante.getDatesOperation().duree());
				} else {
					mapSalles.put(courante.getSalle(), courante.getDatesOperation().duree() + duree);
				}

			} else if (this.listeSallesUrgence.contains(courante.getSalle())){
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

    public void trierListes2(List<Chirurgie> listeChirurgies) {
        Map<Chirurgien, Integer> mapChirurgiens = new HashMap<>();
        Map<Salle, Integer> mapSalles = new HashMap<>();
        Map<Salle, Integer> mapSallesUrgentes = new HashMap<>();
        Integer nbChrg = null;

        for (Chirurgie courante : listeChirurgies) {

            if (this.listeChirurgiens.contains(courante.getChirurgien())) {
                // Gestion des chirurgiens
                nbChrg = mapChirurgiens.get(courante.getChirurgien());
                if (nbChrg == null) {
                    mapChirurgiens.put(courante.getChirurgien(), 1);
                } else {
                    mapChirurgiens.put(courante.getChirurgien(), nbChrg + 1);
                }
            }

            // Gestion des salles
            if (this.listeSalles.contains(courante.getSalle())) {
                // Gestion des salles classiques
                nbChrg = mapSalles.get(courante.getSalle());
                if (nbChrg == null) {
                    mapSalles.put(courante.getSalle(), 1);
                } else {
                    mapSalles.put(courante.getSalle(), nbChrg + 1);
                }

            } else if (this.listeSallesUrgence.contains(courante.getSalle())) {
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


    public void trierListes(List<Chirurgie> listeChirurgies) {
        Collections.shuffle(this.listeChirurgiens);
        Collections.shuffle(this.listeSalles);
        Collections.shuffle(this.listeSallesUrgence);
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
