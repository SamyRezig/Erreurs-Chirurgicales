import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Agenda {
	// Liste de conflits a retirer pour resoudre chaque conflits dans PlanningJournee
	private List<Chirurgie> listeChirurgies;				// Liste contenant tous les chirurgies
	//private List<Conflit> listConflits;					// Liste contenant tous les conflits
	private Map<LocalDate, PlanningJournee> planning;	// Map regroupant les chirurgies/salles/chirurgiens par jour
	public Statistiques stats;

	private Agenda() {
		this.listeChirurgies = new ArrayList<>();
		//this.listConflits = new ArrayList<>();
		this.planning = new HashMap<>();
	}

	public Agenda(String nomFichier) {
		this();
		this.remplirDepuisFichier(nomFichier);
		this.setPlanningParJournee(this.listeJournees());
		this.rescencerTousConflits();
	}

	private void remplirDepuisFichier(String nomFichier) {
		BufferedReader fluxTexte = null;
		String ligne;
		Chirurgie operation;

		try {
			// Ouverture du flux sur le fichier
			fluxTexte = new BufferedReader(new InputStreamReader(new FileInputStream(nomFichier)));
			// Lecture de la premiere ligne
			fluxTexte.readLine();

			// Lecture de la 2e ligne jusqu'a la fin du fichier
			while ((ligne = fluxTexte.readLine()) != null) {
				System.out.println(ligne);

				operation = creationChirurgie(ligne.split(";"));
				this.listeChirurgies.add(operation);

			}
			System.out.println("Fin");
			System.out.println(listeChirurgies);

		} catch (IOException e) {
			System.out.println("Pas de fichier trouve.");
		}

		// Definition des chirurgiens disponibles et des salles.
		this.setPlanningParJournee(this.listeJournees());
	}

	/*public List<Conflit> getListConflits() {
		return this.listConflits;
	}*/

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

	public List<Salle> getListeSalles(){
		List<Salle> ls = new ArrayList<>();
		for(Chirurgie c : this.listeChirurgies) {
			if(!ls.contains(c.getSalle())) {
				ls.add(c.getSalle());
			}
		}
		return ls;
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

	public List<Salle> getSallesJournee(LocalDate jour) {
		return this.listeChirurgies.stream()
							.filter( x -> x.getDatesOperation().getDateDebut().toLocalDate().equals(jour) )
							.map( x->x.getSalle() )
							.distinct()
							.collect(Collectors.toList());
	}

	public void setPlanningParJournee(List<LocalDate> ld) {
		//Map<LocalDate, List<Chirurgie>> mapJournee = new HashMap<>();
		//Map<LocalDate, List<Chirurgien>> mapMedecins = new HashMap<>();

		Map<LocalDate, PlanningJournee> mapJournees = new HashMap<>();
		PlanningJournee jour = null;

		List<Chirurgie> tmp = new ArrayList<>(); // Liste des chirurgies pour une journee
		List<Chirurgien> listeMedecins = new ArrayList<>();
		List<Salle> listeSalles = null;

		for (LocalDate l : ld) {
			// Obtention des listes de chirurgiens et salles
			tmp = this.getChirurgieJournee(l);
			listeMedecins = this.getChirurgienJournee(tmp);
			listeSalles = this.getSallesJournee(l);

			// Creer un objet PlanningJournee
			jour = new PlanningJournee(tmp, listeSalles, listeMedecins);
			// Mettre dans Map
			mapJournees.put(l, jour);

		}
		this.planning = mapJournees;		// Setting de l'attribut planning
	}

	public void rescencerTousConflits() {
		List<Conflit> conflitsDuJour;

		// Pour jour, rescenser les conflits de ce jour
		// Ajouter tous les conflits dans la liste ListConflits
		for (PlanningJournee contenuJour : this.planning.values()) {
			contenuJour.setConflits();
			conflitsDuJour = contenuJour.getListeConflits();
			//this.listConflits.addAll(conflitsDuJour);
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
	
	public List<Conflit> extraireConflits() {
		List<Conflit> tousConflits = new ArrayList<>();
		
		for (PlanningJournee contenuJour : this.planning.values()) {
			tousConflits.addAll(contenuJour.getListeConflits());
		}
		
		return tousConflits;
	}
	
	public void statistiques() {
		this.stats = new Statistiques(this.listeChirurgies, this.extraireConflits());
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


}