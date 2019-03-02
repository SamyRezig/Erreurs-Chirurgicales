import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Comparator;

/**
  * CLasse regroupant les ressources des chirurgies. Elle comporte une liste de
  * chirurgiens, une liste de salles non urgentes et une liste de salles d'urgence.
  * @author Samy Rezig
  * @author Yves Tran
  * @see Salle
  * @see Chirurgien
  * @see Agenda
  */
public class Ressources {

    private List<Chirurgien> listeChirurgiens;      // Liste contenant les chirurgiens.
    private List<Salle> listeSalles;                // Liste contenant les salles non urgentes.
    private List<Salle> listeSallesUrgence;         // Liste contenant les salles d'urgence.


    /**
      * Constructeur princiapl.
      * @param chirurgiens la liste contenant les chirurgiens.
      * @param salles la liste contenant les salles non urgentes.
      * @param sallesUrgence la liste contenant les salles d'urgente.
      */
    public Ressources(List<Chirurgien> chirurgiens, List<Salle> salles, List<Salle> sallesUrgence) {
        this.listeChirurgiens = chirurgiens;
        this.listeSalles = salles;
        this.listeSallesUrgence = sallesUrgence;
    }

    /**
      * Getter pour la liste des chirurgiens.
      * @return la liste des chirurgiens.
      */
    public List<Chirurgien> getListeChirurgiens() {
        return this.listeChirurgiens;
    }


    /**
      * Getter pour les salles non urgentes.
      * @return la liste des salles non urgentes.
      */
    public List<Salle> getListeSalles() {
        return this.listeSalles;
    }

    /**
      * Getter pour les salles d'urgence.
      * @return la liste des salles d'urgence.
      */
    public List<Salle> getListeSallesUrgence() {
        return this.listeSallesUrgence;
    }

    /**
      * Trier les listes de salles et de chirurgiens en fonction de leur duree d'operation.
      * Le tri se fait par ordre croissant de duree. La salle / le chirurgien ayant
      * une duree de travail minimale sera en tete de liste.
      * Le tri s'effectue en fonction de la liste de chirurgies donnee.
      * @param listeChirurgies la liste des chirurgies de reference pour le tri.
      */
    public void trierListesParDuree(List<Chirurgie> listeChirurgies) {
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
                    // Nouveau chirurgien : pas encore recense dans la map.
    				mapChirurgiens.put(courante.getChirurgien(), courante.getDatesOperation().duree());
    			} else {
                    // Chirurgien deja recense dans la map. On lui ajoute une duree supplementaire.
    				mapChirurgiens.put(courante.getChirurgien(), duree + courante.getDatesOperation().duree());
    			}
            }

			// Gestion des salles
            // La salle est censee etre disponible dans les ressources de cette journee
			if (this.listeSalles.contains(courante.getSalle())) {
				// Gestion des salles classiques
				duree = mapSalles.get(courante.getSalle());
				if (duree == null) {
                    // Nouvelle salle : pas encore recensee dans la map. On l'ajoute.
					mapSalles.put(courante.getSalle(), courante.getDatesOperation().duree());
				} else {
                    // Salle daja recensee. On lui ajoute une duree supplementaire.
					mapSalles.put(courante.getSalle(), courante.getDatesOperation().duree() + duree);
				}

			} else if (this.listeSallesUrgence.contains(courante.getSalle())){
				// Gestion des salles urgentes
				duree = mapSallesUrgentes.get(courante.getSalle());
				if (duree == null) {
                    // Nouvelle salle : pas encore recensee dans la map. On l'ajoute.
					mapSallesUrgentes.put(courante.getSalle(), courante.getDatesOperation().duree());
				} else {
                    // Salle daja recensee. On lui ajoute une duree supplementaire.
					mapSallesUrgentes.put(courante.getSalle(), courante.getDatesOperation().duree() + duree);
				}
			}
		}
		// Conversion des chirurgiens en une liste
		this.listeChirurgiens = mapChirurgiens.entrySet().stream()
									.sorted(Map.Entry.comparingByValue())  // Tri croissant
									.map( x->x.getKey() )
									.collect(Collectors.toList());
		// Conversion des salles classiques en une liste
		this.listeSalles = mapSalles.entrySet().stream()
									.sorted(Map.Entry.comparingByValue())  // Tri croissant
									.map(x->x.getKey())
									.collect(Collectors.toList());
		// Conversion des salles d'urgence en une liste
		this.listeSallesUrgence = mapSallesUrgentes.entrySet().stream()
										.sorted(Map.Entry.comparingByValue()) // Tri croissant
										.map(x->x.getKey())
										.collect(Collectors.toList());
	}

    /**
      * Trier les listes de salles et de chirurgiens en fonction du nombre d'operations.
      * Le tri se fait par ordre croissant du nombre d'operations. La salle / le chirurgien ayant
      * un nombre de chirurgies minimale sera en tete de liste.
      * Le tri s'effectue en fonction de la liste de chirurgies donnee.
      * @param listeChirurgies la liste des chirurgies de reference pour le tri.
      */
    public void trierListesParNombre(List<Chirurgie> listeChirurgies) {
        Map<Chirurgien, Integer> mapChirurgiens = new HashMap<>();
        Map<Salle, Integer> mapSalles = new HashMap<>();
        Map<Salle, Integer> mapSallesUrgentes = new HashMap<>();
        Integer nbChrg = null;

        for (Chirurgie courante : listeChirurgies) {
            // Gestoin des chirurgiens
            if (this.listeChirurgiens.contains(courante.getChirurgien())) {
                // Gestion des chirurgiens
                nbChrg = mapChirurgiens.get(courante.getChirurgien());
                if (nbChrg == null) {
                    // Nouveau chirurgien : pas encore dans la map. On l'ajoute.
                    mapChirurgiens.put(courante.getChirurgien(), 1);
                } else {
                    // Chirurgien deja recense. On lui ajoute une chirurgie supplementaire.
                    mapChirurgiens.put(courante.getChirurgien(), nbChrg + 1);
                }
            }

            // Gestion des salles
            if (this.listeSalles.contains(courante.getSalle())) {
                // Gestion des salles classiques
                nbChrg = mapSalles.get(courante.getSalle());
                if (nbChrg == null) {
                    // Nouvelle salle : pas encore recensee. On l'ajoute dans la map.
                    mapSalles.put(courante.getSalle(), 1);
                } else {
                    // Salle deja resencee. On lui ajoute une chirurgie supplementaire.
                    mapSalles.put(courante.getSalle(), nbChrg + 1);
                }

            } else if (this.listeSallesUrgence.contains(courante.getSalle())) {
                // Gestion des salles urgentes
                nbChrg = mapSallesUrgentes.get(courante.getSalle());
                if (nbChrg == null) {
                    // Nouvelle salle : pas encore recensee. On l'ajoute dans la map.
                    mapSallesUrgentes.put(courante.getSalle(), 1);
                } else {
                    // Salle deja resencee. On lui ajoute une chirurgie supplementaire.
                    mapSallesUrgentes.put(courante.getSalle(), nbChrg + 1);
                }
            }
        }
        // Conversion des chirurgiens en une liste
        this.listeChirurgiens = mapChirurgiens.entrySet().stream()
                                    .sorted(Map.Entry.comparingByValue())   // Tri croissant
                                    .map( x->x.getKey() )
                                    .collect(Collectors.toList());
        // Conversion des salles classiques en une liste
        this.listeSalles = mapSalles.entrySet().stream()
                                    .sorted(Map.Entry.comparingByValue())   // Tri croissant
                                    .map(x->x.getKey())
                                    .collect(Collectors.toList());
        // Conversion des salles d'urgence en une liste
        this.listeSallesUrgence = mapSallesUrgentes.entrySet().stream()
                                        .sorted(Map.Entry.comparingByValue())   // Tir croissant
                                        .map(x->x.getKey())
                                        .collect(Collectors.toList());
    }

    /**
      * Melanger l'ordre des composantes de chaque liste.
      */
    public void trierListesAleatoire() {
        Collections.shuffle(this.listeChirurgiens);     // Melange des chirurgiens
        Collections.shuffle(this.listeSalles);          // Melange des salles non urgentes.
        Collections.shuffle(this.listeSallesUrgence);      // Melange des salles d'urgence.
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
