import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class Log {
    private NavigableMap<Integer, List<String>> mapEtats;

    public Log() {
        this.mapEtats = new TreeMap<>();
    }

    public void afficher() {
    	this.mapEtats.entrySet().stream()
    							.forEach( x->this.afficherEtats(x.getKey() ));
    }

    public void afficherEtats(int id) {
        List<String> etats = mapEtats.get(id);

        if (etats == null) {
            System.out.println("L'identifiant " + id + " ne correspond pas a une chirurgie qui a ete modifiee.");

        } else {
            System.out.println(etats);
        }
    }

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

    private void integrer(int id, String nouvelleEtat) {
        List<String> nouvelleListe = new ArrayList<>();
        nouvelleListe.add(nouvelleEtat);

        this.mapEtats.put(id, nouvelleListe);
    }

    @Override
    public String toString() {
        return mapEtats.toString();
    }
}
