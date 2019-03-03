import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

/**
  *Structure stockant les chirurgies et les ressources disponibles pour une
  * certaine date.
  * @author Samy Rezig
  * @author Yves Tran
  * @see Agenda
  * @see Conflit
  * @see Chirurgie
  */
public class PlanningJournee {

	private List<Chirurgie> listeChirurgies;	// La liste des chirurgies commencant dans la journee.
	private Ressources disponibilites;			// Les ressources disponibles pour cette journee.
	private List<Conflit> listeConflits;		// Les conflits de la journee.


	/**
	  * Constructeur principal.
	  * @param lc la liste des chirurgies.
	  * @param ls liste des salles normales.
	  * @param lsu la liste des salles d'urgence.
	  * @param lc liste des chirurgiens.
	  */
	public PlanningJournee(List<Chirurgie> lc,List<Salle> ls, List<Salle> lsu,  List<Chirurgien> lch) {
		this.listeChirurgies = lc;
		Collections.sort(this.listeChirurgies);		// Trie les chirurgies par date de debut du plus tot au plus tard
		this.disponibilites = new Ressources(lch, ls, lsu);
		this.listeConflits = new ArrayList<>();
	}

	/**
	  * Getter pour la liste des chirurgies.
	  * @return la liste des chirurgies de la journee.
	  */
	public List<Chirurgie> getListeChirurgies(){
		return this.listeChirurgies;
	}

	/**
	  * Getter pour la liste des conflits.
	  * @return la liste des conflits recenses dans la journee.
	  */
	public List<Conflit> getListeConflits() {
		return this.listeConflits;
	}

	/**
	  * Mise a jour de la liste des conflits.
	  */
	public void setConflits() {
		// Vider la liste de conflit actuelle
		this.listeConflits.clear();

		Conflit nouveauConflit; // Sauvegarde le nouveau conflit, est null s'il y en a pas

		// Determiner les conflits par double boucles sur le liste de chirurgies de la journee.
		for (int i = 0; i < this.listeChirurgies.size(); i++) {
			for (int j = i + 1; j < this.listeChirurgies.size(); j++) {
				// Creer un nouveau conflit s'il y a lieu ou retourne null, reinitialisation de
				// nouveauConflit
				nouveauConflit = this.listeChirurgies.get(i).enConflit(this.listeChirurgies.get(j));

				if (nouveauConflit != null) {
					// Un conflit a ete trouve.
					this.listeConflits.add(nouveauConflit);
					Statistiques.recenser(nouveauConflit);
				}
			}
		}
	}

	/**
	  * Affichage des conflits en ligne de commandes.
	  */
	public void montrerConflits() {
		this.listeConflits.stream()
							.forEach(System.out::println);
	}

	/**
	  * @return la liste de chirurgies qui intersectent la chirurgies donnee.
	  * Eventuellement une liste vide.
	  * @param base chirurgie de reference.
	  */
	private List<Chirurgie> chirurgiesIntersectent(Chirurgie base) {
		List<Chirurgie> intersectent = new ArrayList<>();

		// Parcours de chaque chirurgie de la journee.
		for (Chirurgie operation : this.listeChirurgies) {
			if (!base.equals(operation) && base.getDatesOperation().intersect(operation.getDatesOperation())) {
				intersectent.add(operation);
			}
		}

		return intersectent;
	}

	/**
	  * Determiner des chirurgiens qui sont utilisables si on retire les chirurgiens
	  * occupes dans la liste de chirurgies donnee
	  * @param chirurgies la liste de chirurgies avec les chirurgiens occupes.
	  * @return la liste de chirurgies qui reste.
	  */
	private List<Chirurgien> chirurgiensUtilisables(List<Chirurgie> chirurgies) {
		List<Chirurgien> chirurgiensUtilisables;	// Chirurgien disponibles dans la journee.
		// Chirurgiens occupes par une chirurgie d'apres la liste de chirurgie donnee.
		List<Chirurgien> chirurgiensUtilises = chirurgies.stream()
													.map( x->x.getChirurgien() )
													.collect(Collectors.toList());

		chirurgiensUtilisables = new ArrayList<>(this.disponibilites.getListeChirurgiens());
		chirurgiensUtilisables.removeAll(chirurgiensUtilises);

		return chirurgiensUtilisables;
	}

	/**
	  * Determiner les salless qui sont utilisables si on retire les salles
	  * occupes dans la liste de chirurgies donnee
	  * @param salles la liste de salles avec les salles occupes.
	  * @param chirurgies les chirurgies avec les salles occupees.
	  * @return la liste de salles qui reste.
	  */
	private List<Salle> sallesUtilisables(List<Chirurgie> chirurgies, List<Salle> salles) {
		List<Salle> sallesUtilisables;
		List<Salle> sallesUtilises = chirurgies.stream()
												.map( x->x.getSalle() )
												.collect(Collectors.toList());

		sallesUtilisables = new ArrayList<>(salles);
		sallesUtilisables.removeAll(sallesUtilises);

		return sallesUtilisables;
	}

	/**
	  * Tri de toutes les ressources.
	  */
	private void trierRessources() {
		// Ordonner les listes de salles classiques, d'urgence et des chirurgiens
		this.disponibilites.trierListesParDuree(this.listeChirurgies);
	}

	/**
	  * Resolution des conflits de la journee.
	  */
	public void resoudreConflits() {
		List<Chirurgie> intersectent;
		List<Chirurgien> chirurgiensUtilisables;
		List<Salle> sallesUtilisables;

    	for(Conflit conflitCourant : this.listeConflits) {
			conflitCourant.reordonner();	// Classer les 2 chirurgies en conflit en fonction de leur date de debut.
			//if (false)	this.trierRessources();			// Mise a jour des ressources en les ordonnant.

			// Definir les ressources utilisabales :
			// Retirer les ressources qui provoqueraient un future conflit
			// La salle/chirrugien a modifier ne fait plus partie des listes
			intersectent = this.chirurgiesIntersectent(conflitCourant.getSecondeChirurgie());
			chirurgiensUtilisables = this.chirurgiensUtilisables(intersectent);

			// Choix des salles
        	if (conflitCourant.getPremiereChirurgie().estUrgente()) {
				sallesUtilisables = this.sallesUtilisables(intersectent, this.disponibilites.getListeSallesUrgence());
			} else {
				sallesUtilisables = this.sallesUtilisables(intersectent, this.disponibilites.getListeSalles());
        	}
			// Correction du conflit
			conflitCourant.resoudreConflit(chirurgiensUtilisables, sallesUtilisables);
        }
	}

	/**
	  * Afficher les conflits de la journee en ligne de commandes.
	  */
	public void visualiserConflits() {
		for (Conflit conflitCourant : this.listeConflits) {
			conflitCourant.visualiser();
		}
	}

	/**
	  * Afficher toutes les chirurgies de la journee en ligne de commande.
	  */
	public void visualiser() {
		for (Chirurgie chrg : this.listeChirurgies) {
			System.out.print(chrg + " : ");
			chrg.visualisation();	// Visualisation de la chirurgie.
		}
	}

	/**
	  * Afficher les chirurgies de la journee etranges (trop longues ou trop courtes)
	  */
	public void verifierChirurgies() {
		// Parcours de tous les chirurgies.
		for (Chirurgie chrg : this.listeChirurgies) {
			if (chrg.incoherente()) {	// La chirurgie est-elle bizarre ?
				System.out.println(chrg);
				chrg.visualisation();
			}
		}
	}

	/**
	  * @return la liste des chirurgies en conflit.
	  */
	private Set<Chirurgie> chirurgiesEnConflit() {
		Set<Chirurgie> enConflit = new HashSet<>();
		List<Conflit> tousConflits  = this.listeConflits;

		for (Conflit c : tousConflits) {
			// Ajout des 2 chirurgies en conflit
			enConflit.add(c.getPremiereChirurgie());
			enConflit.add(c.getSecondeChirurgie());
		}

		return enConflit;
	}

	/**
	  * Duree totale du travail de la journee.
	  * @return l'horaire de fin de la derniere chirurgie moins l'horaire de debut
	  * de la premiere chirurgie de la journee.
	  */
	public long dureeTotale() {
		Chirurgie premiere = this.listeChirurgies.get(0);
		Chirurgie derniere = this.listeChirurgies.get( this.listeChirurgies.size() - 1 );

		return Duration.between(premiere.getDatesOperation().getDateDebut(), derniere.getDatesOperation().getDateFin()).toMinutes();
	}

	/**
	  * @return true si le chirurgien travaille ce jour-ci et false sinon.
	  * @param medecin le chirurgien en question
	  */
	public boolean travaille(Chirurgien medecin) {
		// Parcours de toutes les chirurgies pour le savoir.
		for (Chirurgie operation : this.listeChirurgies) {
			if (operation.getChirurgien().equals(medecin)) {
				return true;
			}
		}
		return false;
	}

	/**
	  * @return trie si la salle est occupee dans la journee.
	  * @param bloc la salle en suestion
	  */
	public boolean occupe(Salle bloc) {
		for (Chirurgie operation : this.listeChirurgies) {
			if (operation.getSalle().equals(bloc)) {
				return true;
			}
		}
		return false;
	}

	/**
	  * @return nombre de chirurgies dans la journee.
	  */
	public int nbChirurgies() {
		return this.listeChirurgies.size();
	}

	/**
	  * @return nombre de chirurgiens disponibles dans la journee.
	  */
	public int nbChirurgiensDispos() {
		return this.disponibilites.getListeChirurgiens().size();
	}

	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();

		strb.append(listeChirurgies.toString());
		strb.append(this.disponibilites.toString());

		return strb.toString();
	}

}
