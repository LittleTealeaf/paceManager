package application;

import java.util.ArrayList;
import java.util.List;

import classes.Goal;
import classes.Pace;
import classes.ScoreTab;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class fxScores {

	public static Stage sScores;
	private static TabPane tabPane;

	private static List<ScoreTab> scoreTabs;

	public static void open() {

		// Close any currently opened stages
		if(sScores != null) sScores.close();
		sScores = new Stage();
		sScores.setTitle("Score Leaderboard");

		// Dynamic Custom Resizing
		sScores.widthProperty().addListener(obs -> {
			resizeWindow();
		});
		sScores.heightProperty().addListener(obs -> {
			resizeWindow();
		});

		// UI Layout
		/*
		 * Menu Bar: includes Print, Reload, Etc. (or buttons) also has ones to open the
		 * goals Then tabs using javafx tabs (1 for each division)
		 */
		tabPane = new TabPane();
		updateTabs();

		Button bGoals = new Button("Goals");
		bGoals.setOnAction(event -> {
			fxGoals.open();
		});

		Region regTop1 = new Region();
		HBox.setHgrow(regTop1, Priority.ALWAYS);

		Button bPrint = new Button("Print");
		bPrint.setOnAction(event -> {
			fxPrint.open();
		});

		HBox hTop = new HBox(bGoals);

		Region regBottom1 = new Region();
		HBox.setHgrow(regBottom1, Priority.ALWAYS);

		Button bClose = new Button("Close");

		bClose.setOnAction(event -> {
			sScores.close();
		});

		HBox hBottom = new HBox(regBottom1, bClose);

		VBox vb = new VBox(hTop, tabPane, hBottom);

		Scene sc = new Scene(vb, 800, 400);
		sScores.setScene(sc);
		sScores.show();

	}

	public static void updateTabs() {
		if(sScores != null && tabPane != null) {
			tabPane.getTabs().clear();
			scoreTabs = new ArrayList<ScoreTab>();

			if(Pace.goals.size() > 0) {
				for(Goal g : Pace.goals) {
					scoreTabs.add(new ScoreTab(g));
				}
				updateTables();
				for(ScoreTab t : scoreTabs) {
					tabPane.getTabs().add(t.getTab());
				}

			}
		}
	}

	public static void updateTables() {
		if(scoreTabs != null && scoreTabs.size() > 0) for(ScoreTab t : scoreTabs) t.updateTable(sScores.getWidth());
	}

	private static void resizeWindow() {
		updateTables();
	}
}
