package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import classes.Pace;
import classes.Team;
import classes.Time;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class fxTeam {
	public static Stage sTeam;

	private static Team team;

	private static TextField tTeam;
	private static TextField tDiv;
	private static TextField tStart;
	private static TextField tFinish;
	private static TextArea tRiders;
	private static TextArea tNotes;
	private static Label lError;
	private static CheckBox cExclude;

	public static void open(Team t, int column) {

		// Regular open script
		open(t);

		// sets the focused field according to the column selected
		switch (column) {
		case 0:
			tTeam.requestFocus();
			break;
		case 1:
			tDiv.requestFocus();
			break;
		case 2:
			tRiders.requestFocus();
			break;
		case 3:
			tStart.requestFocus();
			break;
		case 4:
			tFinish.requestFocus();
			break;
		default:
			break;
		}
	}

	public static void open(Team t) {
		team = t;
		if(sTeam != null) sTeam.close();
		sTeam = new Stage();
		sTeam.setWidth(500);
		sTeam.setAlwaysOnTop(true);

		// First Row
		Label lTeam = new Label("Team");
		tTeam = new TextField();

		Label lDiv = new Label("Division");
		tDiv = new TextField();

		// Second Row
		Label lStart = new Label("Start Time:");
		tStart = new TextField();
		tStart.setTooltip(new Tooltip("Start time of the rider\nHH:MM:SS (AM/PM) format"));

		Label lFinish = new Label("Finish Time:");
		tFinish = new TextField();
		tStart.setTooltip(new Tooltip("Finish time of the rider\nHH:MM:SS (AM/PM) format"));

		GridPane grid = new GridPane();
		grid.add(lTeam, 0, 0);
		grid.add(tTeam, 1, 0);
		grid.add(lDiv, 0, 1);
		grid.add(tDiv, 1, 1);
		grid.add(lStart, 2, 0);
		grid.add(tStart, 3, 0);
		grid.add(lFinish, 2, 1);
		grid.add(tFinish, 3, 1);
		grid.setHgap(10);
		grid.setVgap(10);

		// Bottom Row
		Label lRiders = new Label("Riders");
		tRiders = new TextArea();
		tRiders.setTooltip(new Tooltip("List of members of the team (full name)\nEach Rider on a separate line"));

		Label lNotes = new Label("Notes");
		tNotes = new TextArea();
		tNotes.setTooltip(new Tooltip("Separate Team Notes\nFirst line only will appear on table"));

		GridPane gridLarge = new GridPane();
		gridLarge.add(lRiders, 0, 0);
		gridLarge.add(lNotes, 1, 0);
		gridLarge.add(tRiders, 0, 1);
		gridLarge.add(tNotes, 1, 1);
		gridLarge.setHgap(10);
		gridLarge.setVgap(10);

		Button bCancel = new Button("Cancel");
		bCancel.setPrefWidth(75);
		bCancel.setOnAction(event -> {
			sTeam.close();
		});

		cExclude = new CheckBox();
		cExclude.setText("Exclude");
		cExclude.setTooltip(new Tooltip("Exclude this team from final scores."));

		lError = new Label();
		lError.setTextFill(Color.RED);

		Region rb = new Region();

		Button bSave = new Button("Save");
		bSave.setPrefWidth(75);
		bSave.setOnAction(event -> {
			if(save()) sTeam.close();
		});

		HBox hFooter = new HBox(bCancel, cExclude, lError, rb, bSave);
		HBox.setHgrow(rb, Priority.ALWAYS);
		hFooter.setSpacing(10);

		VBox hContent = new VBox(grid, gridLarge);
		hContent.setSpacing(10);
		hContent.setPadding(new Insets(10));

		BorderPane bp = new BorderPane();
		bp.setBottom(hFooter);
		bp.setCenter(hContent);

		Scene sc = new Scene(bp);
		sTeam.setScene(sc);
		sTeam.show();

		if(t == null) {
			sTeam.setTitle("Create Team");
			tTeam.requestFocus();
		} else {
			// Loads the current team details
			sTeam.setTitle("Edit Team");
			tTeam.setText(t.team);
			tDiv.setText(t.division);
			cExclude.setSelected(t.excluded);
			if(t.start != null) tStart.setText(t.start.toString());
			if(t.finish != null) tFinish.setText(t.finish.toString());
			if(t.names.size() > 0) {
				int pos = 0;
				for(String l : t.names) {
					if(pos == 0) tRiders.setText(l);
					else tRiders.setText(tRiders.getText() + "\n" + l);
					pos++;
				}
			}
			if(t.notes.size() > 0) {
				int pos = 0;
				for(String l : t.notes) {
					if(pos == 0) tNotes.setText(l);
					else tNotes.setText(tNotes.getText() + "\n" + l);
					pos++;
				}
			}
			tNotes.requestFocus();
		}
	}

	private static Team compileTeam() {
		Team r = new Team();
		r.team = tTeam.getText();
		r.division = tDiv.getText();
		r.start = new Time(tStart.getText());
		if(r.start.error != 0) r.start = null;
		r.finish = new Time(tFinish.getText());
		if(r.finish.error != 0) r.finish = null;
		String tmp = "";
		if(!tRiders.getText().contentEquals("")) {
			List<String> tmpRid = new ArrayList<String>();
			for(char c : tRiders.getText().toCharArray()) {
				if(c == '\n' || c == '\r') {
					tmpRid.add(tmp);
					tmp = "";
				} else tmp += c;
			}
			tmpRid.add(tmp);
			tmp = "";
			r.names = tmpRid;
		}
		if(!tNotes.getText().contentEquals("")) {
			List<String> tmpNot = new ArrayList<String>();
			for(char c : tNotes.getText().toCharArray()) {
				if(c == '\n' || c == '\r') {
					tmpNot.add(tmp);
					tmp = "";
				} else tmp += c;
			}
			tmpNot.add(tmp);
			r.notes = tmpNot;
		}
		r.excluded = cExclude.isSelected();

		// Remove any empty names
		while(r.names.contains("")) r.names.contains("");

		return r;
	}

	private static boolean save() {
		lError.setText("");
		if(tTeam.getText().contentEquals("")) {
			lError.setText("Must include a Team Identifier");
			return false;
		}
		// If it's "making a team"
		if(team == null) {
			Team existingTeam = paceManager.getTeam(tTeam.getText());
			if(existingTeam != null) {
				// Confirms if the user would want to override the details
				Alert alert = new Alert(AlertType.NONE);
				alert.setTitle("Team ID Taken");
				alert.setHeaderText("Would you like to overwrite this team?");
				alert.setContentText("Overwriting will permenantly delete the old values applied to the team");
				ButtonType bOverwrite = new ButtonType("Overwrite");
				ButtonType bCancel = new ButtonType("Cancel");
				alert.getButtonTypes().addAll(bOverwrite, bCancel);
				sTeam.setAlwaysOnTop(false);
				Optional<ButtonType> result = alert.showAndWait();
				sTeam.setAlwaysOnTop(true);
				if(result.get() == bOverwrite) {
					Pace.teams.set(Pace.teams.indexOf(paceManager.getTeam(tTeam.getText())), compileTeam());
				} else return false;
			} else Pace.teams.add(compileTeam());
		} else {
			Pace.teams.set(Pace.teams.indexOf(paceManager.getTeam(tTeam.getText())), compileTeam());
		}
		fxMain.updateTable();
		return true;
	}
}
