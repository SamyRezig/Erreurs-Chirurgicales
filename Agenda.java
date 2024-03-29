import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.TreeMap;
import java.util.NavigableMap;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;

/**
  * Classe representant une structure pour organiser des chirurgies. Elles sont
  * stockees et regroupees en fontion des jours. Il est possible de faire des
  * statistiques sur un agenda.
  * @author Samy Rezig
  * @author Yves Tran
  * @see Statistiques
  * @see PlanningJournee
  * @see Ressources
  */
public class Agenda {

	private int nbIterations = 23;								// Nombre d'itaretions maximum pour resoudre les conflits
	private NavigableMap<LocalDate, PlanningJournee> planning;	// Map regroupant les chirurgies/salles/chirurgiens par jour
    private Map<LocalDate, Ressources> joursRessources;			// Ressources diponibles pour jours
	private Ressources ressourcesExistantes;					// Toutes les ressources de la base de donnees
	public Statistiques stats;									// Effectuer des statistiques sur les chirurgies et les corrections


	/**
	  * Initialiser les objets. N'est utilisable qu'a l'interieur de la classe
	  * car un agenda vide n'a pas de sens.
	  */
	private Agenda() {
		//this.listeChirurgies = new ArrayList<>();
		this.planning = new TreeMap<>();
        this.joursRessources = new HashMap<>();
	}

	/**
	  * Constructeur principal. Il lit le fichier de donnees, extrait les
	  * ressources existantes, definit les conflits, separe l'ensemble de
	  * chirurgies en plusieurs sous ensembles de chirurgie avec un jour de
	  * debut en commun, prepare des statistiques (moyenne, quartiles...)
	  */
	public Agenda(String nomFichier) {
		this();

		List<Chirurgie> listeChirurgies;
		listeChirurgies = this.remplirDepuisFichier(nomFichier);
        this.definirRessources(listeChirurgies);
		this.setPlanningParJournee(this.listeJournees(listeChirurgies), listeChirurgies);
		this.recenserTousConflits();
        this.statistiques();
	}

	/**
	  * Determiner les ressources disponibles pour chaque jour.
	  * Cette methode utilise une analyse de l'emploi du temps des chirurgiens
	  * pour completer leur emploi du temps.
	  */
	private void definirRessources(List<Chirurgie> listeChirurgies) {
        List<LocalDate> tousJours = this.listeJournees(listeChirurgies);
        Ressources dispoJour;
        List<Chirurgien> chirurgiensDispos;
        List<Salle> sallesDispos;
        List<Salle> sallesUrgenceDispos;

		// Determine les jours ou les chirurgiens devraient travailler
		for (Chirurgien medecin : this.ressourcesExistantes.getListeChirurgiens()) {
			// Determiner les jours ou le chirurgiens est disponibles.
			medecin.definirJoursTravail(listeChirurgies);
		}

		// Pour chaque jour, attribuer une ressource.
        for (LocalDate jour : tousJours) {
            chirurgiensDispos = this.extraireListeChirurgiensDispos(jour);
            sallesDispos = this.ressourcesExistantes.getListeSalles();
            sallesUrgenceDispos = this.ressourcesExistantes.getListeSallesUrgence();

            dispoJour = new Ressources(chirurgiensDispos, sallesDispos, sallesUrgenceDispos);

            this.joursRessources.put(jour, dispoJour);
        }
    }

	/**
	  * Parcours la liste de chirurgiens pour en extraire ceux disponibles
	  * selon un jour donne.
	  * @return Les chirurgiens qui sont disponibles ce jour.
	  */
    private List<Chirurgien> extraireListeChirurgiensDispos(LocalDate jour) {
        List<Chirurgien> chirurgiensDispos = new ArrayList<>();

        // Pour tout chirurgien, on l'ajoute dans la liste
     	// s'il est cense travailler ce jour-ci
		for (Chirurgien medecin : this.ressourcesExistantes.getListeChirurgiens()) {
			if (medecin.censeTravailler(jour)) {
				chirurgiensDispos.add(medecin);
			}
		}

        return chirurgiensDispos;
    }

	/**
	  * Getter pour obtenir le planning des chirurgies en fonction des jours.
	  * @return planning des chirurgies en fonction des jours.
	  */
    public NavigableMap<LocalDate, PlanningJournee> getPlanning() {
        return this.planning;
    }

	/**
	  * Remplir la liste de chirurgies avec le fichier donne. N'est utilisee que
	  * pour le constructeur principal de la classe Agenda.
	  * @throws InputMismatchException quand le fichier est incorrecte.
	  */
	private List<Chirurgie> remplirDepuisFichier(String nomFichier) {
		List<Chirurgie> listeChirurgies = new ArrayList<>();	// Liste de chirurgies recensees
		BufferedReader fluxTexte = null;
		String ligne;				// Ligne d'un fichier
		Chirurgie operation;		// Variable de stockage pour une chirrugie.

		try {
			// Ouverture du flux sur le fichier
			fluxTexte = new BufferedReader(new InputStreamReader(new FileInputStream(nomFichier)));
			System.out.println("Chargement de la base de donnees.");
			// Lecture de la premiere ligne
			fluxTexte.readLine();

			// Lecture de la 2e ligne jusqu'a la fin du fichier
			while ((ligne = fluxTexte.readLine()) != null) {
				operation = creationChirurgie(ligne.split(";"), listeChirurgies);	// Cretation d'une chirrugie a partir de la ligne du fichier
				listeChirurgies.add(operation);				// Ajouter cette nouvelle chirurgie dans la liste des chirurgies
			}
			this.definirRessourcesExistants(listeChirurgies);			// Determiner les chirurgiens et salles existants.
			System.out.println("Fin de la lecture des chirurgies.");

		} catch (IOException e) {
			// Probleme au niveau de la lecture du nomFichier
			// Le fichier n'a probablement pas ete trouve.
			throw new InputMismatchException("Pas de fichier " + nomFichier + " trouve.");
		}

		return listeChirurgies;

	}

	/**
	  * Determiner les chirrugiens et les salles existants. La methode extrait
	  * chaque ressource depuis la liste de chirurgies en attribut de la classe.
	  * Elle ne doit pas etre lancer avant le recensement des chirurgies par
	  * remplirDepuisFichier()
	  */
	private void definirRessourcesExistants(List<Chirurgie> listeChirurgies) {
		// Extraction des listes de chirurgiens et salles.
		List<Chirurgien> listeChirurgiens = this.extraireListeChirurgiens(listeChirurgies);
		List<Salle> listeSalles = this.extraireListeSalles(listeChirurgies);
		List<Salle> listeSallesUrgence = this.extraireListeSallesUrgence(listeChirurgies);

		// Creation et attribution de la nouvelle ressource
		this.ressourcesExistantes = new Ressources(listeChirurgiens, listeSalles, listeSallesUrgence);
	}

	/**
	  * Creer un nouveau fichier pour mettre les chirurgies une fois les Conflits
	  * corriges.
	  * @throws IOException Erreur de fichier.
	  */
	public void creerNouveauFichier(List<Chirurgie> listeChirurgies) throws IOException {
		String nomFichier = "ChirurgiesCorrigees.csv";
		FileWriter writer = new FileWriter(nomFichier);
		DateTimeFormatter formateurDate = DateTimeFormatter.ofPattern("dd/LL/yyyy");
		DateTimeFormatter formateurHeure = DateTimeFormatter.ofPattern("HH:mm:ss");

		StringBuilder sb = new StringBuilder();
	    sb.append("ID;DATE CHIRURGIE;HEURE_DEBUT CHIRURGIE;HEURE_FIN CHIRURGIE;SALLE;CHIRURGIEN");	// Premiere ligne
	    sb.append('\n');
	    writer.write(sb.toString());
	    sb = new StringBuilder();

	    for(Chirurgie c : listeChirurgies) {
	    	sb.append(c.getId());
	    	sb.append(';');
	    	sb.append(c.getDatesOperation().getDateDebut().toLocalDate().format(formateurDate));
	     	sb.append(';');
	     	sb.append(c.getDatesOperation().getDateDebut().toLocalTime().format(formateurHeure));
	    	sb.append(';');
	    	sb.append(c.getDatesOperation().getDateFin().toLocalTime().format(formateurHeure));
	    	sb.append(';');
	     	sb.append(c.getSalle());
	    	sb.append(';');
	    	sb.append(c.getChirurgien().getNom());
	    	sb.append('\n');
	    	writer.write(sb.toString());
	    	sb = new StringBuilder();

	    }
	    writer.flush();	// Reinitialisation du buffer
	    writer.close();
	    System.out.println("Un fichier " + nomFichier + " a ete genere.");
	}

	/**
	  * @param listeChirurgies liste contenant toutes les chirurgies qu'on veut considerees.
	  * @param infoSeparees contient dans l'ordre l'identifiant de la chirurgie,
	  * la date de debut, l'heure de debut, la date de fin, l'heure de fin,
	  * le nom de la salle et le nom du chirurgien
	  * @return La nouvelle chirurgie
	  */
	private Chirurgie creationChirurgie(String [] infoSeparees, List<Chirurgie> listeChirurgies) {
		int identifiant = Integer.parseInt(infoSeparees[0]);
		IntervalleTemps datesOperation = new IntervalleTemps(infoSeparees[1], infoSeparees[2], infoSeparees[1],
				infoSeparees[3]);
		Salle bloc = this.trouverSalle(infoSeparees[4], listeChirurgies);
		Chirurgien chirurgien = this.trouverChirurgien(infoSeparees[5], listeChirurgies);

		return new Chirurgie(identifiant, datesOperation, bloc, chirurgien);
	}

	/**
	  * Chercher une salle existante parmi la liste de chirurgie. N'est utilisee
	  * que pour la lecture du fichier.
	  * @param nomSalle Le nom de la salle
	  * @param listeChirurgies liste contenant toutes les chirurgies qu'on veut considerees.
	  * @return Un passage par valeur de la salle trouvee ou une salle creee
	  * a defaut de la trouver
	  */
	private Salle trouverSalle(String nomSalle, List<Chirurgie> listeChirurgies) {
		for (Chirurgie operation : listeChirurgies) {
			if (operation.getSalle().getNom().equals(nomSalle)) {
				return operation.getSalle();
			}
		}
		return new Salle(nomSalle);
	}

	/**
	  * Chercher un chirurgien existante parmi la liste de chirurgie. N'est utilisee
	  * que pour la lecture du fichier.
	  * @param nomChirurgien le nom du chirurgien
	  * @param listeChirurgies liste contenant toutes les chirurgies qu'on veut considerees.
	  * @return passage par valeur du chirurgien ou un completement cree.
	  */
	private Chirurgien trouverChirurgien(String nomChirurgien, List<Chirurgie> listeChirurgies) {
		for (Chirurgie operation : listeChirurgies) {
			if (operation.getChirurgien().getNom().equals(nomChirurgien)) {
				return operation.getChirurgien();
			}
		}
		return new Chirurgien(nomChirurgien);
	}

	/**
	  * Getter pour la liste de chirurgiens. Passe par les ressources existantes.
	  * @return la liste de chirurgiens.
	  */
	public List<Chirurgien> getListeChirurgiens() {
		return this.ressourcesExistantes.getListeChirurgiens();
	}

	/**
	  * Getter pour la liste de salles non urgente. Passe par les ressources existantes.
	  * @return la liste de salles non urgente.
	  */
	public List<Salle> getListeSalles() {
		return this.ressourcesExistantes.getListeSalles();
	}

	/**
	  * Getter pour la liste de salles urgente. Passe par les ressources existantes.
	  * @return la liste de salles urgente.
	  */
	public List<Salle> getListeSallesUrgence() {
		return this.ressourcesExistantes.getListeSallesUrgence();
	}

	/**
	  * Parcours les chirurgies pour extraire les chirurgiens.
	  * @param listeChirurgies liste contenant toutes les chirurgies qu'on veut considerees.
	  * @return une nouvelle liste de chirurgiens
	  */
	public List<Chirurgien> extraireListeChirurgiens(List<Chirurgie> listeChirurgies) {
		List<Chirurgien> lc = new ArrayList<>();
		for(Chirurgie c : listeChirurgies) {
			if(!lc.contains(c.getChirurgien())) {
				lc.add(c.getChirurgien());
			}
		}
		return lc;
	}

	/**
	  * Parcours les chirurgies pour extraire les salles non urgentes.
	  * @param listeChirurgies liste contenant toutes les chirurgies qu'on veut considerees.
	  * @return une nouvelle liste de salles non urgentes
	  */
	public List<Salle> extraireListeSalles(List<Chirurgie> listeChirurgies){
		List<Salle> ls = new ArrayList<>();
		for(Chirurgie c : listeChirurgies) {
			if(!ls.contains(c.getSalle()) && !c.getSalle().estUrgence()) {
				ls.add(c.getSalle());
			}
		}
		return ls;
	}

	/**
	  * Parcours les chirurgies pour extraire les salles urgentes.
	  * @param listeChirurgies liste contenant toutes les chirurgies qu'on veut considerees.
	  * @return une nouvelle liste de salles urgentes
	  */
    public List<Salle> extraireListeSallesUrgence(List<Chirurgie> listeChirurgies){
        List<Salle> lsu = new ArrayList<>();
            for(Chirurgie c :listeChirurgies) {
				if(!lsu.contains(c.getSalle()) && c.getSalle().estUrgence()) {
					lsu.add(c.getSalle());
				}
			}
        return lsu;
    }

	/**
	  * Utiliser pour creer le planning de chaque journee.
	  * @return une liste de chirurgies debutant dans la date donnee.
	  * @param l la date dont on souhaite extraire les chirurgies.
	  * @param listeChirurgies liste contenant toutes les chirurgies qu'on veut considerees.
	  */
	private List<Chirurgie> getChirurgieJournee(LocalDate l, List<Chirurgie> listeChirurgies) {
		List<Chirurgie> chirurgieJournee = new ArrayList<>();
		for (Chirurgie c : listeChirurgies) {
			if (c.getDatesOperation().getDateDebut().toLocalDate().equals(l)) {
				chirurgieJournee.add(c);
			}
		}
		return chirurgieJournee;
	}

	/**
	  * Utiliser pour creer le planning de chaque journee.
	  * @return une liste de jours ou il y a eu des chirurgies.
	  * @param listeChirurgies liste contenant toutes les chirurgies qu'on veut considerees.
	  */
	private List<LocalDate> listeJournees(List<Chirurgie> listeChirurgies) {
		List<LocalDate> ld = listeChirurgies.stream()
												.map(x -> x.getDatesOperation()
															.getDateDebut()
															.toLocalDate())
												.distinct()
												.collect(Collectors.toList());
		return ld;
	}

	/**
	  * Remplir la map de (LocalDate, Planning)
	  * @param listeChirurgies liste contenant toutes les chirurgies dont on veut
	  * extraire le sous ensemble de chirurgies.
	  * @param ld la liste de jours avec des operations.
	  */
	public void setPlanningParJournee(List<LocalDate> ld, List<Chirurgie> listeChirurgies) {
		NavigableMap<LocalDate, PlanningJournee> mapJournees = new TreeMap<>();
		PlanningJournee jour = null;

		List<Chirurgie> tmp = null; 			// Liste des chirurgies pour une journee
		List<Chirurgien> listeMedecins = null;
		List<Salle> listeSalles = null;
        List<Salle> listeSallesUrgence = null;

        Ressources ressourcesJour;

		for (LocalDate l : ld) {
            ressourcesJour = this.joursRessources.get(l);

			// Obtention des listes de chirurgiens et salles
			tmp = this.getChirurgieJournee(l, listeChirurgies);

			listeMedecins = ressourcesJour.getListeChirurgiens();			// Recuperation des chirurgiens disponibles
			listeSalles = ressourcesJour.getListeSalles(); 					// Recuperation des salles existantes !
            listeSallesUrgence = ressourcesJour.getListeSallesUrgence();	// Recuperation des salles d'urgence existantes !

			// Creer un objet PlanningJournee
			jour = new PlanningJournee(tmp, listeSalles, listeSallesUrgence, listeMedecins);

			// Mettre dans Map
			mapJournees.put(l, jour);

		}
		this.planning = mapJournees;	// Setting de l'attribut planning
	}

	/**
	  * Parcours chaque planning de chaque jour pour definir les conflits existant
	  */
	public void recenserTousConflits() {
		Statistiques.nouvelleIteration();	// Ajouter un nouveau Integer dans les listes de U/I/Ch pour les statistiques
		for (PlanningJournee contenuJour : this.planning.values()) {
			contenuJour.setConflits();
		}
	}

	/**
	  * Visualiser les conflits en ligne de commandes.
	  */
	public void montrerConflits() {
		for (PlanningJournee contenuJour : this.planning.values()) {
			contenuJour.montrerConflits();
		}
	}

	/**
	  * Resoudre tous les conflits existants dans chaque planning de chaque
	  * journee
	  */
	public void resoudreTousConflits() {
		for (PlanningJournee contenuJour : this.planning.values()) {
			contenuJour.resoudreConflits();
		}
	}

	/**
	  * Utilisable apres creation des plannings pour chaque journee.
	  * @return une liste de chirurgies extraites des planning de chaque jour.
	  * @return une liste vide si les planning sont vide. Un message d'avertissement est affiche.
	  */
	public List<Chirurgie> extraireListeChirurgies() {
		List<Chirurgie> listeChirurgies = new ArrayList<>();

		if (this.planning.size() == 0) {
			System.out.println("Pas de chirurgi trouvee. Les plannings ne sont pas remplis.");

		} else {
			for (PlanningJournee contenuJour : this.planning.values()) {
				listeChirurgies.addAll(contenuJour.getListeChirurgies());	// Pas de doublon normalement
			}
		}

		return listeChirurgies;
	}

	/**
	  * Resoudre tous les conflits recenses dans chaque planning en plusieurs iteration
	  * successives. Affiche le numero de de l'iteration avec la resolution du conflit.
	  */
	public void resolution() {
		List<Chirurgie> listeChirurgies = this.extraireListeChirurgies();
		int nbConflitsPrec = 0;
		int i = 0;

		// Les planning et la liste de conflits est deja chargee
		System.out.println("Debut de la resolution des conflits.");
		while (this.nombreConflits() > 0 && ++i < this.nbIterations) {
			nbConflitsPrec = this.nombreConflits();
			System.out.println("Nombre de conflits restant : " + nbConflitsPrec);
			System.out.println("\nIteration numero " + i + "\n");

			this.resoudreTousConflits();
			this.setPlanningParJournee(this.listeJournees(listeChirurgies), listeChirurgies);
			this.recenserTousConflits();

			Statistiques.setNombresConflitsCorriges(nbConflitsPrec - this.nombreConflits());

		}
		System.out.println("Fin de la resolution des conflits.");
		//this.nbIterations = i;	// Affecter la valeur pour l'affichage du graphique
	}

	/**
	  * Une fois les conflits corriges, comparer l'etat de l'agenda avant et apres
	  * les corrections.
	  */
	public void comparaisonStats() {
		Statistiques apresStats = new Statistiques(this);
		this.stats.comparer(apresStats);
	}

	/**
	  * Parcours chaque planning de chaque journee pour afficher les chirurgies
	  * qui semblent irrealistes.
	  */
	public void verifierChirurgies() {
		System.out.println("Chirurgies suspectes : ");
		for (PlanningJournee contenuJour : this.planning.values()) {
			contenuJour.verifierChirurgies();
		}
	}

	/**
	  * @return le nombre de conflits resultant de la somme des nombres de conflits
	  * recenses dans chaque planning.
	  */
	private int nombreConflits() {
		return this.extraireConflits().size();
	}

	/**
	  * @return une liste contenant tous les conflits recenses dans chaque planning.
	  */
	public List<Conflit> extraireConflits() {
		List<Conflit> tousConflits = new ArrayList<>();

		for (PlanningJournee contenuJour : this.planning.values()) {
			tousConflits.addAll(contenuJour.getListeConflits());
		}

		return tousConflits;
	}

	/**
	  * Instancie un objet Statistiques. Utilise uniquement par le Constructeur
	  * principal.
	  */
	public void statistiques() {
		this.stats = new Statistiques(this);
	}

	/**
	  * Visualisation de l'ensemble des chirurgies.
	  */
	public void visualiser() {
		for (PlanningJournee contenuJour : this.planning.values()) {
			contenuJour.visualiser();
		}
	}

	/**
	  * Visualisation de l'ensemble des conflits recenses dans chaque planning
	  */
	public void visualiserConflits() {
        for (PlanningJournee contenuJour : this.planning.values()) {
			contenuJour.visualiserConflits();
		}
    }

}
