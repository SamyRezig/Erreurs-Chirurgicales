import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.DayOfWeek;
import java.util.stream.Collectors;

import java.util.Scanner;
import java.time.LocalDate;

/**
  * Classe representant un chirurgien.
  * @author Samy Rezig
  * @author Yves Tran
  * @see Chirurgie
  * @see Ressources
  */
public class Chirurgien {

	private String nom;									// Nom du chirurgien.
	private Map<DayOfWeek, Long> frequencesTravail;		// Nombre de fois qu'un chirurgien travaille pour chaque jour de la semaine.
	private List<LocalDate> joursTravail;				// Liste des jours ou le chirurgien est disponible.


	/**
	  * Constructeur principal.
	  * @param nom nom du chirurgien.
	  */
	public Chirurgien(String nom) {
		this.nom = nom;
		this.frequencesTravail = new HashMap<>();
		this.joursTravail = new ArrayList<>();
	}

	/**
	  * Getter pour le nom du chirurgien.
	  * @return le nom du chirurgien.
	  */
	public String getNom() {
		return this.nom;
	}

	/**
	  * Remplir la map des frequences de travail par jour de la semaine.
	  * Cela se fait en fonctino de la liste de chirurgies donnee.
	  * @param listeChirurgies la liste de chirurgies de reference pour analyse.
	  */
	public void definirFrequencesTravail(List<Chirurgie> listeChirurgies) {
		Long frequence;

		for (DayOfWeek jour : DayOfWeek.values()) {
			// Compte le nombre de fois que le chirurgien a travaille ce jour-ci
			frequence = listeChirurgies.stream()
										.filter( x -> x.getDatesOperation().getDateDebut().getDayOfWeek().equals(jour)	// Retenir les chirurgies correspondant a un jour de la semaine
														&& x.getChirurgien().equals(this))								// Retenir les jours ou le chirurgie courant a opere.
										.count();		// Compter le nombre de chirurgies restante.
			this.frequencesTravail.put(jour, frequence);
		}
	}

	/**
	  * Determiner les jours ou le chirurgien doit travailler. L'analyse se fait
	  * par semaine.
	  * @param listeChirurgies la liste de chirurgies de reference et a parcourir.
	  */
	public void definirJoursTravail(List<Chirurgie> listeChirurgies) {

		// Definition les frequences de travail
		this.definirFrequencesTravail(listeChirurgies);

		// On extrait les chirurgies operees par ce chirurgien
		List<Chirurgie> chirurgiesConcernees = listeChirurgies.stream()
													.filter( x->x.getChirurgien().equals(this))
													.collect(Collectors.toList());
		List<Chirurgie> chirurgiesSemaine = new ArrayList<>();
		LocalDate jour;						// Variable de stockage pour un jour de lannee.
		IntervalleTemps semaine = null;		// Semaine courante.
		IntervalleTemps semainePrec = null;	// Semaine precedente.

		for (Chirurgie operation : chirurgiesConcernees) {
			jour = operation.getDatesOperation().getDateDebut().toLocalDate();

			// Definition des dates extremes d'une semaine
			semainePrec = semaine;
			semaine = IntervalleTemps.enSemaine(jour);

			// Si on a change de semaine
			if (semainePrec != null && !semaine.equals(semainePrec)) {
				this.analyserSemaineTravail(chirurgiesSemaine, semainePrec);
				chirurgiesSemaine.clear();			// Vider la liste pour la prochaine semaine
			}

			// On ajoute la chirurgie quoi qu'il arrive
			chirurgiesSemaine.add(operation);
		}
		// Prise en compte de la toute derniere semaine de travail
		this.analyserSemaineTravail(chirurgiesSemaine, semaine);
	}

	/**
	  * Analyser et determiner quels jours de la semaine doit etre ajouter au chirurgien.
	  * Il doit travailler 5 jours dans la semaine. Les jours sont completes en
	  * fonction de leur frequence de travail par jour de la semaine.
	  */
	private void analyserSemaineTravail(List<Chirurgie> chirurgiesSemaineChirurgien, IntervalleTemps semaine) {
		LocalDate max = null;
		// Liste contenant les jours ou le chirurgien a travaille dans la semaine courante
		List<LocalDate> joursTravail = chirurgiesSemaineChirurgien.stream()
												.map( x->x.getDatesOperation().getDateDebut().toLocalDate() )
												.distinct()
												.collect( Collectors.toList() );
		// Liste contenant des jours ou le chirurgie n'a pas travaille dans la semaine courante
		List<LocalDate> joursLibres = semaine.listeLocalDateEntre();
		joursLibres.removeAll(joursTravail);	// Garder les jours ou le chirurgien n'a pas travaille

		// Si le chirurgien a travaille moins de 5 jours
		// mais strictement plus que 1 jour, il faut lui rajouter des jours !
		while (1 < joursTravail.size() && joursTravail.size() < 5 && !joursLibres.isEmpty()) {
			// Completer par le jour de la semaine le plus frequent les jours ou il ne travaille pas
			// Recherche du jour ou il travaille le plus
			max = null;
			for (LocalDate jour : joursLibres) {
				if (max == null || this.frequencesTravail.get(max.getDayOfWeek()) < this.frequencesTravail.get(jour.getDayOfWeek())) {
					max = jour;	// Un maximum a ete trouve.
				}
			}
			if (this.frequencesTravail.get(max.getDayOfWeek()) != 0) {
				joursLibres.remove(max);// Retirer ce jour : le chirurgien peut travailler ce jour-ci
				joursTravail.add(max);	// Ajouter ce jour parmi les jours de travail
			} else {
				joursLibres.remove(max); // Retirer ce jour : le chirurgien n'est pas libre et ne pourra jamais travailler ce jour-ci
			}
		}
		this.joursTravail.addAll(joursTravail);	// Ajouter les nouveaux jours a l'attribut
	}

	/**
	  * Determine si le chirurgien est cense travailler durant le jour donnee de l'annee.
	  * @return true si le chirurigen est disponible ce jour-ci et false sinon.
	  */
	public boolean censeTravailler(LocalDate jour) {
		return this.joursTravail.contains(jour);
	}

	@Override
	/**
	  * Hash sur le nom du chirurgien.
	  */
	public int hashCode() {
		return this.nom.hashCode();
	}

	@Override
	/**
	  * Egalite sur le nom du chirurgien.
	  */
	public boolean equals(Object o) {
		if (this.getClass() == o.getClass()) {
			// Egalite par le nom du chirurgien
			return this.nom.equals( ( (Chirurgien) o).nom );
		}
		return false;
	}

	@Override
	public String toString() {
		return this.nom;
	}

}
