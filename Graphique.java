import java.util.Map;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.geometry.Point2D;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;
import java.util.List;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.embed.swing.JFXPanel;

public class Graphique extends Application {
    //final JFXPanel fxPanel = new JFXPanel();
    private double largeur = 700;
    private double hauteur = 500;
    public static Map<String, List<Integer>> valeurs;
    private LineChart graphe;

    private Group root;

    public void afficher(String [] args) {
        Application.launch(args);
    }

    private void initialiserAxes(Group root) {
        System.out.println("Initialisation des axes");
        //Defining X axis
        NumberAxis xAxis = new NumberAxis(0, 12, 1);
        xAxis.setLabel("Iteration");

        //Defining y axis
        NumberAxis yAxis = new NumberAxis(0, 400, 10);
        yAxis.setLabel("Nombre de conflits");

        this.graphe = new LineChart(xAxis, yAxis);

        root.getChildren().add(xAxis);
        root.getChildren().add(yAxis);
        root.getChildren().add(this.graphe);
    }

    private void tracer(List<Integer> nuagePoints, String nom) {
        System.out.println("Tracer une courbe");
        Series series = new XYChart.Series();
        series.setName(nom);

        for (int i = 0; i < nuagePoints.size(); i++) {
            series.getData().add(new XYChart.Data(i, nuagePoints.get(i)));
        }

        //Setting the data to Line chart
        this.graphe.getData().add(series);
    }

    public Group getGroup() {
        return this.root;
    }

    public void initGroup() {
        Group root = new Group();

        this.initialiserAxes(root);
        this.tracer(Graphique.valeurs.get("Ubiquite"), "Ubiquite");
        this.tracer(Graphique.valeurs.get("Interference"), "Interference");
        this.tracer(Graphique.valeurs.get("Chevauchement"), "Chevauchement");
        this.tracer(Graphique.valeurs.get("Total"), "Total");

        this.root = root;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Nombre de conflits en fonction du temps");
        Group root = null;
        this.initGroup();

        if (this.graphe != null) {
            root = this.getGroup();
            System.out.println("Root initialise !");
        } else {
            System.out.println("Impossible");
        }


        Scene sc = new Scene(root, this.largeur, this.hauteur);
        primaryStage.setScene(sc);
        primaryStage.show();
    }

}
