import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Log {
    private Map<Integer, List<String>> mapEtats;

    public Log() {
        this.mapEtats = new HashMap<>();
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
