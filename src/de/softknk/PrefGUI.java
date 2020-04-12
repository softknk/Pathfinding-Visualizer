package de.softknk;

import de.softknk.algorithms.Algorithms;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.LinkedList;
import java.util.List;

import static de.softknk.Main.*;

public class PrefGUI {

    public static Algorithms selected;

    private List<CheckBox> algorithms;
    private Text[] keyInstructions;
    private VBox box;

    public PrefGUI() {
        algorithms = new LinkedList<>();

        box = new VBox();
        box.setPrefSize(190 - 30, GRID_SIZE - 30);
        box.setTranslateX(GRID_SIZE + 15);
        box.setTranslateY(15);
        box.setSpacing(25);
        box.setAlignment(Pos.BASELINE_LEFT);

        Text choose = new Text("Choose algorithm ...");
        choose.getStyleClass().add("choose");
        choose.setX(GRID_SIZE + choose.getWrappingWidth() / 2);
        choose.setY(40);

        CheckBox dijkstra = new CheckBox("Dijkstra");
        dijkstra.getStyleClass().add("check-box");
        dijkstra.setOnAction(event -> select(0));

        CheckBox astar = new CheckBox("A Star");
        astar.getStyleClass().add("check-box");
        astar.setOnAction(event -> select(1));

        CheckBox greedy = new CheckBox("Greedy Search");
        greedy.getStyleClass().add("check-box");
        greedy.setOnAction(event -> select(2));

        algorithms.add(dijkstra);
        algorithms.add(astar);
        algorithms.add(greedy);

        keyInstructions = new Text[8];

        keyInstructions[0] = new Text(getKeyInstructionFormat("start visualization", 'S'));
        keyInstructions[1] = new Text(getKeyInstructionFormat("reset grid", 'R'));
        keyInstructions[2] = new Text(getKeyInstructionFormat("create random obstacles", 'C'));
        keyInstructions[3] = new Text(getKeyInstructionFormat("pause/continue algorithm", 'P'));
        keyInstructions[4] = new Text(getKeyInstructionFormat("select the start node", 'F'));
        keyInstructions[5] = new Text(getKeyInstructionFormat("select the target node", 'E'));
        keyInstructions[6] = new Text(getKeyInstructionFormat("show/hide circle border", 'D'));
        keyInstructions[7] = new Text(getKeyInstructionFormat("enter/exit creative mode", 'Z'));


        for (int i = 0; i < keyInstructions.length; i++) {
            keyInstructions[i].getStyleClass().add("keyInstruction");
        }

        box.getChildren().add(choose);
        box.getChildren().addAll(algorithms);
        Line line = new Line();
        line.setEndX(box.getPrefWidth());
        box.getChildren().add(line);
        box.getChildren().addAll(keyInstructions);
        Line _line = new Line();
        _line.setEndX(line.getEndX());
        box.getChildren().add(_line);
        pane.getChildren().add(box);
    }

    private void select(int index) {
        switch (index) {
            case 0: selected = Algorithms.DIJKSTRA; break;
            case 1: selected = Algorithms.ASTAR; break;
            case 2: selected = Algorithms.GREEDY; break;
        }

        algorithms.forEach(box -> box.setSelected(false));
        algorithms.get(index).setSelected(true);
    }

    private String getKeyInstructionFormat(String description, char key) {
        return key + " " + "\u2192" + "  " + description;
    }

    public VBox getContainer() {
        return box;
    }
}
