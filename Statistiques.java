import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.time.Duration;

public class Statistiques {
    private Set<Chirurgie> operationsSansConflit;
    private Duration dureeMoyenne;                  // Duree moyenne d'une operation
    private Duration premierQuartile;

    public Statistiques(List<Chirurgie> listeBase, List<Conflit> listeConflits) {

        // Extraction des chirurgies en conflits
        Set<Chirurgie> enConflit = new HashSet<>(); // implementer hashCode ?
        for (Conflit conflit : listeConflits) {
            enConflit.add(conflit.getPremiereChirurgie());
            enConflit.add(conflit.getSecondeChirurgie());
        }

        // Difference
        this.operationsSansConflit = new HashSet<>(listeBase);
        this.operationsSansConflit.removeAll(enConflit);

        // Remplissage des attributs
        this.dureeMoyenne = this.calculerDureeMoyenne();
        this.premierQuartile = this.calculerPremierQuartile();
    }

    private Duration calculerDureeMoyenne() {
        long sommeDurees = this.operationsSansConflit.stream()
                                .mapToLong( chrg->chrg.duree() )
                                .sum();
        return Duration.ofMinutes(sommeDurees / this.operationsSansConflit.size());
                            // Possible perte de donnees avec division par 2 long
    }

    private Duration calculerPremierQuartile() {
        return null;
    }
}
