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
import java.util.Scanner;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.NavigableMap;
import java.util.HashSet;
import java.util.Set;
import java.time.format.DateTimeFormatter;

public class Agenda {
	// Liste de conflits a retirer pour resoudre chaque conflits dans PlanningJournee
	private List<Chirurgie> listeChirurgies;				// Liste contenant tous les chirurgies
	private int nbIterations = 30;
	private NavigableMap<LocalDate, PlanningJournee> planning;	// Map regroupant les chirurgies/salles/chirurgiens par jour
	public Statistiques stats;
	private Ressources ressourcesExistantes;

	private Agenda() {
		this.listeChirurgies = new ArrayList<>();
		this.planning = new TreeMap<>();
	}

	public Agenda(String nomFichier) {
		this();
		this.remplirDepuisFichier(nomFichier);
		this.setPlanningParJournee(this.listeJournees());
		this.recenserTousConflits();
        this.statistiques();
	}

	private void remplirDepuisFichier(String nomFichier) {
		BufferedReader fluxTexte = null;
		String ligne;
		Chirurgie operation;

		try {
			// Ouverture du flux sur le fichier
			fluxTexte = new BufferedReader(new InputStreamReader(new FileInputStream(nomFichier)));
			System.out.println("Chargement de la base de donnees.");
			// Lecture de la premiere ligne
			fluxTexte.readLine();

			// Lecture de la 2e ligne jusqu'a la fin du fichier
			while ((ligne = fluxTexte.readLine()) != null) {
				//System.out.println(ligne);

				operation = creationChirurgie(ligne.split(";"));
				this.listeChirurgies.add(operation);

			}
			this.definirRessourcesExistants();
			System.out.println("Fin de la lecture des chirurgies.");
			//System.out.println(listeChirurgies);

		} catch (IOException e) {
			System.out.println("Pas de fichier trouve.");
		}

		// Definition des chirurgiens disponibles et des salles.
		this.setPlanningParJournee(this.listeJournees());
	}

	private void definirRessourcesExistants() {
		List<Chirurgien> listeChirurgiens = this.extraireListeChirurgiens();
		List<Salle> listeSalles = this.extraireListeSalles();
		List<Salle> listeSallesUrgence = this.extraireListeSallesUrgence();

		this.ressourcesExistantes = new Ressources(listeChirurgiens, listeSalles, listeSallesUrgence);
	}

	public void creerNouveauFichier() throws IOException {
		String nomFichier = "ChirurgiesCorrigees.csv";
		FileWriter writer = new FileWriter(nomFichier);
		DateTimeFormatter formateurDate = DateTimeFormatter.ofPattern("dd/LL/yyyy");
		DateTimeFormatter formateurHeure = DateTimeFormatter.ofPattern("HH:mm:ss");

		 StringBuilder sb = new StringBuilder();
	      sb.append("ID;DATE CHIRURGIE;HEURE_DEBUT CHIRURGIE;HEURE_FIN CHIRURGIE;SALLE;CHIRURGIEN");
	      sb.append('\n');
	      writer.write(sb.toString());
	      sb = new StringBuilder();

	    	 for(Chirurgie c : this.listeChirurgies) {
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
	    	 writer.flush();
	    	 writer.close();
	    	 System.out.println("Un fichier " + nomFichier + " a ete genere.");
	}

	public Map<LocalDate, PlanningJournee> getPlanning() {
		return this.planning;
	}

	public Chirurgie creationChirurgie(String[] infoSeparees) {
		int identifiant = Integer.parseInt(infoSeparees[0]);
		IntervalleTemps datesOperation = new IntervalleTemps(infoSeparees[1], infoSeparees[2], infoSeparees[1],
				infoSeparees[3]);
		Salle bloc = this.trouverSalle(infoSeparees[4]);
		Chirurgien chirurgien = this.trouverChirurgien(infoSeparees[5]);

		return new Chirurgie(identifiant, datesOperation, bloc, chirurgien);
	}

	private Salle trouverSalle(String nomSalle) {
		for (Chirurgie operation : this.listeChirurgies) {
			if (operation.getSalle().getNom().equals(nomSalle)) {
				return operation.getSalle();
			}
		}
		return new Salle(nomSalle);
	}

	private Chirurgien trouverChirurgien(String nomChirurgien) {
		for (Chirurgie operation : this.listeChirurgies) {
			if (operation.getChirurgien().getNom().equals(nomChirurgien)) {
				return operation.getChirurgien();
			}
		}
		return new Chirurgien(nomChirurgien);
	}

	public int sizeCsv() {
		return this.listeChirurgies.size();
	}

	public List<Chirurgien> getListeChirurgiens() {
		return this.ressourcesExistantes.getListeChirurgiens();
	}

	public List<Salle> getListeSalles() {
		return this.ressourcesExistantes.getListeSalles();
	}

	public List<Salle> getListeSallesUrgence() {
		return this.ressourcesExistantes.getListeSallesUrgence();
	}

	public List<Chirurgien> extraireListeChirurgiens() {
		List<Chirurgien> lc = new ArrayList<>();

		for(Chirurgie c : this.listeChirurgies) {

			if(!lc.contains(c.getChirurgien())) {
				lc.add(c.getChirurgien());
			}

		}
		return lc;
	}

	public List<Salle> extraireListeSalles(){
		List<Salle> ls = new ArrayList<>();
		for(Chirurgie c : this.listeChirurgies) {
			if(!ls.contains(c.getSalle()) && !c.getSalle().estUrgence()) {
				ls.add(c.getSalle());
			}
		}
		return ls;
	}

    public List<Salle> extraireListeSallesUrgence(){
        List<Salle> lsu = new ArrayList<>();
            for(Chirurgie c : this.listeChirurgies) {
				if(!lsu.contains(c.getSalle()) && c.getSalle().estUrgence()) {
					lsu.add(c.getSalle());
				}
			}
        return lsu;
    }


	public List<Chirurgie> getChirurgieJournee(LocalDate l) {
		List<Chirurgie> chirurgieJournee = new ArrayList<>();
		for (Chirurgie c : this.listeChirurgies) {
			if (c.getDatesOperation().getDateDebut().toLocalDate().equals(l)) {
				chirurgieJournee.add(c);
			}
		}

		return chirurgieJournee;
	}

	private List<LocalDate> listeJournees() {
		List<LocalDate> ld = this.listeChirurgies.stream().map(x -> x.getDatesOperation().getDateDebut().toLocalDate())
				.distinct().collect(Collectors.toList());
		return ld;
	}

	private List<Chirurgien> getChirurgienJournee(List<Chirurgie> listeChg) {
		List<Chirurgien> listeMedecins = new ArrayList<>();

		for (Chirurgie chg : listeChg) {
			if (!listeMedecins.contains(chg.getChirurgien())) {
				listeMedecins.add(chg.getChirurgien());
			}
		}

		return listeMedecins;
	}

	/*
	*######################################################## Attention : changement temporaire
	*/

	public List<Salle> getSallesJournee(LocalDate jour) {
		return this.listeChirurgies.stream()
							.filter( x -> /*x.getDatesOperation().getDateDebut().toLocalDate().equals(jour) &&*/ !x.estUrgente())
							.map( x->x.getSalle() )
							.distinct()
							.collect(Collectors.toList());
	}

    public List<Salle> getSallesUrgenceJournee(LocalDate jour) {
		return this.listeChirurgies.stream()
							.filter( x -> /*x.getDatesOperation().getDateDebut().toLocalDate().equals(jour) &&*/ x.estUrgente())
							.map( x->x.getSalle() )
							.distinct()
							.collect(Collectors.toList());
	}

	public void setPlanningParJournee(List<LocalDate> ld) {

		NavigableMap<LocalDate, PlanningJournee> mapJournees = new TreeMap<>();
		PlanningJournee jour = null;

		List<Chirurgie> tmp = new ArrayList<>(); // Liste des chirurgies pour une journee
		List<Chirurgien> listeMedecins = new ArrayList<>();
		List<Salle> listeSalles = null;
        List<Salle> listeSallesUrgence = null;

		for (LocalDate l : ld) {
			// Obtention des listes de chirurgiens et salles
			tmp = this.getChirurgieJournee(l);
			listeMedecins = this.getChirurgienJournee(tmp);
			listeSalles = this.getListeSalles();					// Recuperation des salles existantes !
            listeSallesUrgence= this.getSallesUrgenceJournee(l);
			// Creer un objet PlanningJournee
			jour = new PlanningJournee(tmp, listeSalles, listeSallesUrgence, listeMedecins);
			// Mettre dans Map
			mapJournees.put(l, jour);

		}
		this.planning = mapJournees;		// Setting de l'attribut planning
	}

	public void recenserTousConflits() {
		Statistiques.nouvelleIteration();
		// Pour jour, rescenser les conflits de ce jour
		// Ajouter tous les conflits dans la liste ListConflits
		for (PlanningJournee contenuJour : this.planning.values()) {
			contenuJour.setConflits();
		}
	}

	public void montrerConflits() {
		for (PlanningJournee contenuJour : this.planning.values()) {
			contenuJour.montrerConflits();
		}
	}

	// Resolution conflits
	public void resoudreTousConflits() {
		for (PlanningJournee contenuJour : this.planning.values()) {
			contenuJour.resoudreConflits();
		}
	}

	public void resolution() {
		int nbConflitsPrec = 0;
		int i = 0;
		System.out.println("Debut de la resolution des conflits.");
		while (this.nombreConflits() > 0 && i++ < this.nbIterations) {
			nbConflitsPrec = this.nombreConflits();
			System.out.println("Nombre de conflits : " + nbConflitsPrec);

			this.resoudreTousConflits();
			this.setPlanningParJournee(this.listeJournees());
			this.recenserTousConflits();

			Statistiques.setNombresConflitsCorriges(nbConflitsPrec - this.nombreConflits());

		}
		System.out.println("Fin de la resolution des conflits.");
	}

	public void descriptionCourante() {
		Statistiques apresStats = new Statistiques(this.listeChirurgies, this.extraireConflits(), this.planning);

		this.visualiserConflits();
		this.stats.comparer(apresStats);
	}

	public void resolutionCommentee() {
		this.resolution();
		this.descriptionCourante();
	}

	public void verifierChirurgies() {
		System.out.println("Chirurgies bizarres : ");
		for (PlanningJournee contenuJour : this.planning.values()) {
			contenuJour.verifierChirurgies();
		}
		System.out.println("Fin verification des chirurgies bizarres");
	}

	private int nombreConflits() {
		return this.extraireConflits().size();
	}

	public List<Conflit> extraireConflits() {
		List<Conflit> tousConflits = new ArrayList<>();

		for (PlanningJournee contenuJour : this.planning.values()) {
			tousConflits.addAll(contenuJour.getListeConflits());
		}

		return tousConflits;
	}

	public Map<String, List<Integer>> dataConflits() {
		Map<String, List<Integer>> data = new HashMap<>();

		data.put("Ubiquite", Statistiques.nombresUbiquite);
		data.put("Interference", Statistiques.nombresInterference);
		data.put("Chevauchement", Statistiques.nombresChevauchement);
		data.put("Total", Statistiques.nombresConflits);

		return data;
	}

	public void statistiques() {
		this.stats = new Statistiques(this.listeChirurgies, this.extraireConflits(), this.planning);
	}

	public void visualiser() {
		for (PlanningJournee contenuJour : this.planning.values()) {
			contenuJour.visualiser();
		}
	}

	public void visualiserConflits() {
        for (PlanningJournee contenuJour : this.planning.values()) {
			contenuJour.visualiserConflits();
		}
    }

	public Chirurgie derniereChirurgie() {
		return this.planning.lastEntry().getValue().derniereChirurgie();
	}

	public void afficherJoursConflit() {
		PlanningJournee contenuJour;

		for (LocalDate jour : this.planning.keySet()) {
			contenuJour = this.planning.get(jour);
				if (contenuJour.getListeConflits().size() > 0) {
					System.out.print(jour + " : " + contenuJour.getListeConflits().size() + " conflits restant, ");
					System.out.print(contenuJour.nbChirurgiensDispos() + " chirurgiens disponibles, ");
					System.out.print(contenuJour.nbChirurgies() + " chirurgies.");
					System.out.println();
				}
		}
	}

	public void afficherConflitsTotaux() {
		Map<String, List<Integer>> map = this.dataConflits();
		System.out.println("Chevauchement :\t\t\t" + map.get("Chevauchement"));
		System.out.println("Interference : \t\t\t" + map.get("Interference"));
		System.out.println("Ubiquite : \t\t\t" + map.get("Ubiquite"));
		System.out.println("Total : \t\t\t" + map.get("Total"));
		System.out.println("Nombre de conflits corriges :\t" + Statistiques.nombresConflitsCorriges);
	}



	public void afficherGraphique(String [] args) {
		Map<String, List<Integer>> map = this.dataConflits();
		Graphique g = new Graphique();

		Graphique.valeurs = map;

		g.afficher(args, this.nbIterations, 40);
	}

}
