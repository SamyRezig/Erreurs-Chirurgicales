import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Agenda {
	private List<Chirurgie> listChirurgies;
	private List<Conflit> listConflits;
	private Map<LocalDate, List<Chirurgie>> edt; 
        
	
	private Agenda() {
		this.listChirurgies = new ArrayList<>();
		this.listConflits = new ArrayList<>();
		this.edt = new HashMap<>();
	}
	
	public Agenda(String nomFichier) {
		this();
		this.remplirDepuisFichier(nomFichier);
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
				this.listChirurgies.add(operation);
				
			}
			System.out.println("Fin");
			System.out.println(listChirurgies);
			
			
		} catch (IOException e) {
			System.out.println("Pas de fichier trouve.");
		}
		
		this.edt = getListeChirurgieJournee();
	}
	
	public Map<LocalDate, List<Chirurgie>> getEdt() {
		return this.edt;
	}
	
	public Chirurgie creationChirurgie(String [] infoSeparees) {
		int identifiant = Integer.parseInt(infoSeparees[0]);
		IntervalleTemps datesOperation = new IntervalleTemps(infoSeparees[1], infoSeparees[2], infoSeparees[1], infoSeparees[3]);
		Salle bloc = this.trouverSalle(infoSeparees[4]);
		Chirurgien chirurgien = this.trouverChirurgien(infoSeparees[5]);
		
		return new Chirurgie(identifiant, datesOperation, bloc, chirurgien);
	}
	
	private Salle trouverSalle(String nomSalle) {
		for (Chirurgie operation : this.listChirurgies) {
			if (operation.getSalle().getNom().equals(nomSalle)) {
				return operation.getSalle();
			}
		}
		return new Salle(nomSalle);
	}
	
	private Chirurgien trouverChirurgien(String nomChirurgien) {
		for (Chirurgie operation : this.listChirurgies) {
			if (operation.getChirurgien().getNom().equals(nomChirurgien)) {
				return operation.getChirurgien();
			}
		}
		return new Chirurgien(nomChirurgien);
	}
	
	public int sizeCsv() {
		return this.listChirurgies.size();
	}
	
	
	public List<Chirurgie> getChirurgieJournee(LocalDate l){
        List<Chirurgie> chirurgieJournee = new ArrayList<>();
        for(Chirurgie c : this.listChirurgies){
                if(c.getDatesOperation().getDateDebut().toLocalDate().equals(l)){
                    chirurgieJournee.add(c);
                }
            }
            
            return chirurgieJournee;
        }
        
    public Map<LocalDate,List<Chirurgie>> getListeChirurgieJournee(){
    	Map<LocalDate,List<Chirurgie>> mapJournee = new HashMap<>();
    	List<LocalDate> ld = this.listChirurgies.stream()
                .map( x -> x.getDatesOperation().getDateDebut().toLocalDate())
                .distinct()
                .collect(Collectors.toList());
    	List<Chirurgie> tmp = new ArrayList<>();
    	for(LocalDate l : ld) {
    		tmp=getChirurgieJournee(l);
    		System.out.println(tmp.size());
    		mapJournee.put(l, tmp);

    	}
    			
		return mapJournee;
    }
    
    public void rescencerTousConflits() {
    	// Parcours chaque liste de chirurgie pour rescenser les conflits (pour chaque journee)
    	for (List<Chirurgie> chgJournee : this.edt.values()) {
    		this.rescencerConflitsDepuis(chgJournee);
    	}
    }
    
    private void rescencerConflitsDepuis(List<Chirurgie> listeChg) {
    	Conflit nouveauConflit;		// Sauvegarde le nouveau conflit, est null s'il y en a pas
    	
    	for (int i = 0; i < listeChg.size(); i++) {
    		for (int j = i + 1; j < listeChg.size(); j++) {
    			// Creer un nouveau conflit s'il y a lieu ou retourne null, reinitialisation de nouveauConflit
    			nouveauConflit = listeChg.get(i).enConflit(listeChg.get(j));
    			
    			if (nouveauConflit != null) {
    				System.out.println(nouveauConflit);
    				this.listConflits.add(nouveauConflit);
    			}
    			
    		}
    	}
    	System.out.println(listConflits);
    }
	
	
	
	
	/**
	 * Ubiquite : (17,18) , (21,22)
	 * Chevauchement : (9,10)
	 * Interference : (9,10,11) , (13,14)
	 * 
	 */
}