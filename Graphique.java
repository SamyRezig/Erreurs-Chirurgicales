import java.util.Map;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Graphique extends Application {
    private double hauteur;
    private double largeur;
    private Map<String, Integer> valeurs;

    public Graphique(double hauteur, double largeur, Map<String, Integer> valuers) {
        this.hauteur = hauteur;
        this.largeur = largeur;
        this.valeurs = valeurs;
    }

    public static void afficher(String [] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Nombre de conflits en fonction du temps");
        Group root = new Group();

        /*List<String> list = Word.generateStringList(100);
        Evolution life = new Evolution(4, list);
        life.evolve( x -> (x < 0.1) );


        Cloud cr = life.bestCandidate();
        root = cr.generateGroup();*/

        Scene sc = new Scene(root, 600, 600);
        primaryStage.setScene(sc);
        primaryStage.show();
    }

    /*
    public Group generateGroup() {
        Group root = new Group();
        for (Text t : this.cloud) {
            root.getChildren().add(t);
        }
        return root;
    }
    */
}
