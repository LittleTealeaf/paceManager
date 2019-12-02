package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import classes.Goal;
import classes.Pace;
import classes.Team;
import classes.Time;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class fxGoals {

	public static Stage sGoals;
	private static TableView<Goal> table;

	@SuppressWarnings("unchecked") // one unsolvable warning
	public static void open() {
		if(sGoals != null) sGoals.close();
		sGoals = new Stage();
		sGoals.setTitle("Goal Times");
		sGoals.widthProperty().addListener(obs -> {
			resizeWindow();
		});
		sGoals.heightProperty().addListener(obs -> {
			resizeWindow();
		});
		sGoals.setOnCloseRequest(event -> {
			if(sgEdit != null && sgEdit.isShowing()) sgEdit.close();
		});

		// Creating the Table
		table = new TableView<Goal>();
		table.setEditable(false);
		table.prefWidthProperty().bind(sGoals.widthProperty());

		TableColumn<Goal, String> tDiv = new TableColumn<Goal, String>("Division");
		tDiv.setCellValueFactory(new PropertyValueFactory<Goal, String>("division"));
		tDiv.setReorderable(false);
		TableColumn<Goal, String> tGoal = new TableColumn<Goal, String>("Optimal Time");
		tGoal.setCellValueFactory(new PropertyValueFactory<Goal, String>("displayTime"));
		tGoal.setReorderable(false);
		table.getColumns().addAll(tDiv, tGoal);
		table.setOnMouseClicked(click -> {
			if(click.getClickCount() == 2) {
				try {
					editDiv(table.getSelectionModel().getSelectedItem());
				} catch(IndexOutOfBoundsException e) {}
			}
		});
		table.setOnKeyPressed(k -> {
			if(k.getCode() == KeyCode.DELETE) {
				try {
					deleteDiv(table.getSelectionModel().getSelectedItem());
				} catch(IndexOutOfBoundsException e) {}
			}
		});

		// Disable the horizontal scrollbar
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// Adding buttons to the bottom of the window
		HBox hBottom = new HBox();
		hBottom.setSpacing(10);
		Button bCreate = new Button("New");
		bCreate.setOnAction(event -> {
			editDiv(null);
		});
		Button bImport = new Button("Import");
		bImport.setOnAction(event -> {
			importDivs();
		});
		Button bClose = new Button("Close");
		bClose.setOnAction(event -> {
			sGoals.close();
		});
		Region botRegion = new Region();
		HBox.setHgrow(botRegion, Priority.ALWAYS);
		hBottom.getChildren().addAll(bCreate, bImport, botRegion, bClose);
		hBottom.setPadding(new Insets(5, 10, 5, 10));

		VBox vb = new VBox();
		vb.getChildren().addAll(table, hBottom);
		Scene sc = new Scene(vb, 500, 300);
		sGoals.setScene(sc);
		resizeWindow();
		sGoals.show();
		updateTable();
	}

	private static Stage sgEdit;
	private static TextField teDiv;
	private static TextField teTime;
	private static Label leError;

	private static void editDiv(Goal g) {
		if(sgEdit != null) sgEdit.close();
		sgEdit = new Stage();
		sgEdit.setAlwaysOnTop(true);
		if(g == null) sgEdit.setTitle("Create Goal");
		else sgEdit.setTitle("Edit Goal");

		teDiv = new TextField();
		teDiv.setOnKeyPressed(keyEvent -> {
			if(keyEvent.getCode() == KeyCode.ENTER) {
				leError.setText("");
				if(teDiv.getText() == "") {
					leError.setText("Please include a division name");
				} else teTime.requestFocus();
			}
		});
		Label leDiv = new Label("Division");

		teTime = new TextField();
		teTime.setOnKeyPressed(keyEvent -> {
			if(keyEvent.getCode() == KeyCode.ENTER) {
				eSave(g);
			}
		});
		Label leTime = new Label("Optimal Time");

		leError = new Label("");
		leError.setTextFill(Color.RED);

		HBox v1h1 = new HBox(teDiv, leDiv);
		v1h1.setSpacing(10);
		HBox v1h2 = new HBox(teTime, leTime);
		v1h2.setSpacing(10);

		VBox vb1 = new VBox(v1h1, v1h2, leError);
		vb1.setSpacing(10);

		Button bSave = new Button("Save");
		bSave.setOnMouseClicked(click -> {
			eSave(g);
		});
		Button bCancel = new Button("Cancel");
		bCancel.setOnMouseClicked(click -> {
			sgEdit.close();
		});

		VBox vb2 = new VBox(bSave, new Label(""), bCancel);
		vb2.setSpacing(10);

		HBox hb = new HBox(vb1, vb2);
		hb.setSpacing(20);
		hb.setPadding(new Insets(15, 12, 15, 12));

		Scene sc = new Scene(hb);

		sgEdit.setScene(sc);
		sgEdit.show();
		if(g != null) {
			teDiv.setText(g.division);
			if(g.time != null) teTime.setText(g.time.toString());
			teTime.requestFocus();
		} else teDiv.requestFocus();
	}

	private static void eSave(Goal g) {
		// Clears Error Message
		leError.setText("");
		// Checks for any basic error
		if(teDiv.getText() == "") {
			leError.setText("Please include a division name");
		} else {
			Time t = new Time(teTime.getText());
			if(t.error == 1) {
				leError.setText("Could not Parse Time");
			} else {
				if(Pace.goals.size() > 0 && g != null) for(Goal a : Pace.goals) if(a == g) {
					a.division = teDiv.getText();
					a.time = t;
					updateTable();
					sgEdit.close();
					return;
				}
				Goal v = new Goal(teDiv.getText());
				if(t.error == 0) v.time = t;
				for(Goal a : Pace.goals) if(a.division.toLowerCase().equals(v.division.toLowerCase())) {
					// Confirmation message if
					Alert conf = new Alert(AlertType.CONFIRMATION);
					conf.setTitle("Divison Exists");
					conf.setHeaderText("Would you like to update the existing division?");
					sgEdit.setAlwaysOnTop(false);
					Optional<ButtonType> result = conf.showAndWait();
					sgEdit.setAlwaysOnTop(true);
					if(result.get() == ButtonType.OK) {
						eSave(a);
					} else return;
				}
				Pace.goals.add(v);
				updateTable();
				sgEdit.close();
			}
		}
	}

	private static void importDivs() {
		// Popup
		Alert prompt = new Alert(AlertType.NONE);
		prompt.setTitle("Import");
		prompt.setHeaderText("Do you wish to remove unused goals?");
		ButtonType bYes = new ButtonType("Yes");
		ButtonType bNo = new ButtonType("No");
		ButtonType bCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		prompt.getButtonTypes().addAll(bYes, bNo, bCancel);

		Optional<ButtonType> result = prompt.showAndWait();
		boolean remove = false;
		if(result.get() == bYes) remove = true;
		// else if(result.get() == bNo) remove = false;
		else if(result.get() == bCancel) return;

		List<String> strDivs = new ArrayList<String>();
		for(Team t : Pace.teams) if(!strDivs.contains(t.division)) strDivs.add(t.division);
		List<Goal> goals = new ArrayList<Goal>();
		if(Pace.goals != null) for(Goal g : Pace.goals) if(strDivs.remove(g.division)) goals.add(g);
		for(String s : strDivs) goals.add(new Goal(s));
		// Adds back remaining if set
		if(!remove) for(Goal g : Pace.goals) if(!goals.contains(g)) goals.add(g);
		Pace.goals = goals;
		updateTable();
	}

	public static void deleteDiv(Goal g) {
		if(Pace.settings.alertOnDeleteGoal) {
			Alert conf = new Alert(AlertType.CONFIRMATION);
			conf.setTitle("Delete " + g.division + "?");
			conf.setHeaderText("Do you really want to delete " + g.division + "?");
			Optional<ButtonType> result = conf.showAndWait();
			if(result.get() != ButtonType.OK) return;
		}
		Pace.goals.remove(g);
		updateTable();
	}

	private static void resizeWindow() {

		table.getColumns().get(0).setPrefWidth(0.5 * table.getWidth());
		table.getColumns().get(1).setPrefWidth(0.5 * table.getWidth() - 5);
	}

	private static void updateTable() {
		if(Pace.goals != null) {
			table.getItems().clear();
			table.getItems().addAll(Pace.goals);
			table.sort();
		}
		// Updates fxScores
		fxScores.updateTabs();
	}
}
