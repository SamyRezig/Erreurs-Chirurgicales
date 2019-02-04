import java.util.List;
import java.util.OptionalLong;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class Statistiques {
    private Set<Chirurgie> operationsSansConflit;
    private long dureeMoyenne;                  // Duree moyenne d'une operation
    private long premierQuartile;
    private long mediane;
    private long dernierQuartile;

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
        this.mediane = this.calculerMediane();
        this.dernierQuartile = this.calculerDernierQuartile();
    }

    private long calculerDureeMoyenne() {
        long sommeDurees = this.operationsSansConflit.stream()
                                .mapToLong( chrg->chrg.duree() )
                                .sum();
        return sommeDurees / this.operationsSansConflit.size();
                            // Possible perte de donnees avec division par 2 long
    }

    private long calculerPremierQuartile() {
    	OptionalLong ol = this.operationsSansConflit.stream()
    							.mapToLong( chrg->chrg.duree() )
    							.sorted()
    							.skip(this.operationsSansConflit.size() / 4)
    							.findFirst();
    	long premierQuartile = ol.getAsLong();
    							
        return premierQuartile;
    }
    
    private long calculerMediane() {
    	OptionalLong ol = this.operationsSansConflit.stream()
    							.mapToLong( chrg->chrg.duree() )
    							.sorted()
    							.skip(this.operationsSansConflit.size() / 2)
    							.findFirst();
    	long mediane = ol.getAsLong();
    							
        return mediane;
    }
    
    private long calculerDernierQuartile() {
    	OptionalLong ol = this.operationsSansConflit.stream()
    							.mapToLong( chrg->chrg.duree() )
    							.sorted()
    							.skip(this.operationsSansConflit.size() * 3 / 4)
    							.findFirst();
    	long dernierQuartile = ol.getAsLong();
    							
        return dernierQuartile;
    }
    
    public long getDureeMoyenne() {
    	return this.dureeMoyenne;
    }
    
    public long getPremierQuartile() {
    	return this.premierQuartile;
    }
    
    public long getMediane() {
    	return this.mediane;
    }
    
    public long getDernierQuartile() {
    	return this.dernierQuartile;
    }
    
    public void afficheTout() {
    	this.operationsSansConflit.stream()
		.mapToLong( chrg->chrg.duree() )
		.sorted()
		.forEach(System.out::println);
    }
}
