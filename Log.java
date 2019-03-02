import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;

/**
  * Classe pour stocker les differents etats des chirurgies avant et apres chaque
  * modification de celle-ci. Elle retrace l'historique de chaque chirurgie.
  * @author Samy Rezig
  * @author Yves Tran
  * @see Correcteur
  * @see Chirurgie
  */
public class Log {

    private NavigableMap<Integer, List<String>> mapEtats;   // Historique des etats differents etats des chirurgies.


    /**
	  * Constructeur principal. Instancie la map.
	  */
    public Log() {
        this.mapEtats = new TreeMap<>();
    }

    /**
      * Afficher l'historique des etats de chaque chirurgies.
      */
    public void afficher() {
    	this.mapEtats.entrySet().stream()
    							.forEach( x->this.afficherEtats(x.getKey() ));
    }

    /**
      * Afficher l'historique des etes d'une chirurgie donnee. Affiche un message
      * d'erreur si la chirurgie n'a pas ete trouvee.
      * @param id l'identifiant de la chirurgie.
      */
    public void afficherEtats(int id) {
        List<String> etats = mapEtats.get(id);

        if (etats == null) {
            System.out.println("L'identifiant " + id + " ne correspond pas a une chirurgie qui a ete modifiee.");

        } else {
            System.out.println(etats);
        }
    }

    /**
      * Ajouter un nouvelle etat d'une chirurgie a l'historique
      * @param operation la chirurgie dans un nouvel etat. L'etat n'est pas gardee
      * s'il est deja recense.
      */
    public void ajouter(Chirurgie operation) {
        List<String> etats = this.mapEtats.get(operation.getId());
        String nouvelleEtat = operation.toString();

        if (etats == null) {
            // La chirurgie est nouvelle / n'a pas ete modifiee auparavant
            this.integrer(operation.getId(), nouvelleEtat);

        } else if (!etats.contains(nouvelleEtat)) {
            // La chirurgie existait dans la Map mais pas dans cet etat
            etats.add(nouvelleEtat);
            this.mapEtats.put(operation.getId(), etats);
        }
    }

    /**
      * Integrer une nouvelle chirurgie a la base.
      * @param id identifiant de la nouvelle chirurgie.
      * @param nouvelleEtat l'etat initial de la chirurgie
      */
    private void integrer(int id, String nouvelleEtat) {
        // Creation de la liste d'etats
        List<String> nouvelleListe = new ArrayList<>();
        nouvelleListe.add(nouvelleEtat);

        // Ajout de la liste a la map
        this.mapEtats.put(id, nouvelleListe);
    }

    @Override
    public String toString() {
        return mapEtats.toString();
    }

}
