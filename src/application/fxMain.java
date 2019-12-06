package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import classes.Pace;
import classes.Team;
import classes.Time;
import classes.util;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class fxMain extends Application {

	private static TableView<Team> table;

	// Header Texts
	private static List<Text> headerTexts;

	public static Stage sMRef;

	public static void open(String[] args) {
		launch(args);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage sMain) {
		sMRef = sMain;

		sMain.setTitle("Pace Manager " + paceManager.version + " " + Pace.title);
		sMain.widthProperty().addListener(obs -> {
			resizeColumns();
		});
		sMain.maximizedProperty().addListener(obs -> {
			resizeColumns();
		});

		// Menus
		MenuBar mb = new MenuBar();
		mb.getMenus().addAll(getMenuFile(), getMenuTeams(), getMenuPace(), getMenuAbout());

		headerTexts = new ArrayList<Text>();
		HBox hbHeader = new HBox();
		hbHeader.setSpacing(30);
		int textFieldNumber = 4; // First Out, Last Out, Average Time, Estimated Last In
		for(int i = 0; i < textFieldNumber; i++) {
			Text t = new Text();
			headerTexts.add(t);
			hbHeader.getChildren().add(new Separator(Orientation.VERTICAL));
			hbHeader.getChildren().add(t);
		}
		hbHeader.getChildren().add(new Separator(Orientation.VERTICAL));

		// Table

		table = new TableView<Team>();
		table.setEditable(false);
		/*
		 * https://stackoverflow.com/a/32442449
		 */
		table.setOnMouseClicked(click -> {
			if(click.getClickCount() == 2) {
				try {
					TablePosition<?, ?> pos = table.getSelectionModel().getSelectedCells().get(0);

					int col = pos.getColumn();
					Team sel = table.getSelectionModel().getSelectedItem();
					if(col == 6) {
						openNotes(sel, click.getScreenX(), click.getScreenY());
					} else {
						fxTeam.open(sel, col);
					}
				} catch(IndexOutOfBoundsException e) {}
			}
		});
		table.setOnKeyPressed(k -> {
			if(k.getCode() == KeyCode.DELETE) {
				try {
					paceManager.deleteTeam(table.getSelectionModel().getSelectedItem());
				} catch(IndexOutOfBoundsException e) {}
			} else if(k.getCode() == KeyCode.P && k.isControlDown()) {
				fxPrint.open("All Teams");
			}
		});

		/*
		 * How the following works: - Creates the Column TableColumn cTeamName = new
		 * TableColumn("Team");
		 * - Routs the column to the specified variable (using get____(); ) of Team
		 * cTeamName.setCellValueFactory(new PropertyValueFactory<Team,String>("team"));
		 * - Any of the custom variables to set cTeamName.setReorderable(false);
		 */

		TableColumn<Team, String> cTeamName = new TableColumn<Team, String>("Team");
		cTeamName.setCellValueFactory(new PropertyValueFactory<Team, String>("team"));
		cTeamName.setReorderable(false);

		TableColumn<Team, String> cDivision = new TableColumn<Team, String>("Division");
		cDivision.setCellValueFactory(new PropertyValueFactory<Team, String>("division"));
		cDivision.setReorderable(false);

		TableColumn<Team, String> cNames = new TableColumn<Team, String>("Riders");
		cNames.setEditable(false);
		cNames.setCellFactory(column -> {
			return util.getTeamCell();
		});
		cNames.setCellValueFactory(new PropertyValueFactory<Team, String>("names"));
		cNames.setReorderable(false);

		TableColumn<Team, String> cTime = new TableColumn<Team, String>("Times");
		cTime.setReorderable(false);
		TableColumn<Team, String> cTStart = new TableColumn<Team, String>("Start");
		cTStart.setCellValueFactory(new PropertyValueFactory<Team, String>("startFXM"));
		cTStart.setReorderable(false);
		TableColumn<Team, String> cTFinish = new TableColumn<Team, String>("Finish");
		cTFinish.setCellValueFactory(new PropertyValueFactory<Team, String>("finishFXM"));
		cTFinish.setReorderable(false);
		TableColumn<Team, String> cTElapsed = new TableColumn<Team, String>("Elapsed");
		cTElapsed.setCellValueFactory(new PropertyValueFactory<Team, String>("elapsedFXM"));
		cTElapsed.setReorderable(false);

		cTime.getColumns().addAll(cTStart, cTFinish, cTElapsed);
		TableColumn<Team, String> cNotes = new TableColumn<Team, String>("Notes");
		cNotes.setCellValueFactory(new PropertyValueFactory<Team, String>("notesDisplay"));
		cNotes.setReorderable(false);

		table.getColumns().addAll(cTeamName, cDivision, cNames, cTime, cNotes);

		table.prefHeightProperty().bind(sMain.heightProperty());
		table.prefWidthProperty().bind(sMain.widthProperty());

		// Disables horizontal scroll bar
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		VBox vb = new VBox(mb, hbHeader, table);

		Scene sc = new Scene(vb, 500, 300);
		sMain.setMaximized(true);
		sMain.setScene(sc);

		updateTable();

		sMain.setOnCloseRequest(event -> {
			paceManager.closeApplication();
		});

		sMain.show();
	}

	public static void updateTable() {
		// Get first and last out
		Team firstOut = null;
		Team lastLeft = null;
		Team lastOut = null;
		double avgTime = 0;
		int count = 0;
		for(Team t : Pace.teams) {
			if(t.start != null) {
				if(firstOut == null || firstOut.start.time > t.start.time) firstOut = t;
				if(lastLeft == null || lastOut.start.time < t.start.time) lastLeft = t;
				if(lastOut == null || (t.finish == null && lastOut.start.time < t.start.time)) lastOut = t;
				if(t.finish != null) {
					avgTime += t.elapsed().time;
					count++;
				}
			}
		}
		if(count > 0) avgTime /= count;

		if(firstOut != null) headerTexts.get(0).setText("First Out: " + firstOut.team + " at " + firstOut.start.toString());
		else headerTexts.get(0).setText("First Out: -------");
		if(lastLeft != null) headerTexts.get(1).setText("Last Out: " + lastLeft.team + " at " + lastLeft.start.toString());
		else headerTexts.get(1).setText("Last Out: -------");
		if(avgTime != 0) headerTexts.get(2).setText("Average Time: " + new Time(avgTime).toString(true));
		else headerTexts.get(2).setText("Average Time: -------");
		if(lastOut != null) headerTexts.get(3).setText("Estimated Last In: " + lastOut.team + " at " + new Time(lastOut.start.time + avgTime));
		else headerTexts.get(3).setText("Estimated Last In: -------");

		table.getItems().clear();
		table.getItems().addAll(Pace.teams);
		table.sort();
		resizeColumns();
		fxScores.updateTables();
	}

	/**
	 * Automatically resizes the columns to the table
	 */
	private static void resizeColumns() {
		final double wTeam = 50;
		final double wDiv = 70;
		final double wTime = 80;

		// Min width to apply the proper formatting
		if(table.getWidth() > 420) {
			// Team
			table.getColumns().get(0).setMaxWidth(wTeam);
			table.getColumns().get(0).setMinWidth(wTeam);
			// Division
			table.getColumns().get(1).setMaxWidth(wDiv);
			table.getColumns().get(1).setMinWidth(wDiv);
			// Start Time
			table.getColumns().get(3).getColumns().get(0).setMaxWidth(wTime);
			table.getColumns().get(3).getColumns().get(0).setMinWidth(wTime);
			// End Time
			table.getColumns().get(3).getColumns().get(1).setMaxWidth(wTime);
			table.getColumns().get(3).getColumns().get(1).setMinWidth(wTime);
			// Elapsed Time
			table.getColumns().get(3).getColumns().get(2).setMaxWidth(wTime);
			table.getColumns().get(3).getColumns().get(2).setMinWidth(wTime);
		}
	}

	public static Stage mNotes;
	private static TextArea nText;
	private static Team nTeam;

	/**
	 * Method to open the sub-notes window
	 * 
	 * @param t Team to open notes
	 * @param x The x position of the mouse
	 * @param y The y position of the mouse
	 */
	private static void openNotes(Team t, double x, double y) {
		nTeam = t;
		// WIDTH and HEIGHT are the set width and height of the box
		final double WIDTH = 300;
		final double HEIGHT = 300;

		if(mNotes == null) {
			// Stage setup, only needs to happen once
			mNotes = new Stage();
			mNotes.setAlwaysOnTop(true);
			mNotes.initStyle(StageStyle.UNDECORATED);
			mNotes.setResizable(true);
			mNotes.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
				if(!isNowFocused) {
					mNotes.close();
					updateTable();
				}
			});
			mNotes.setWidth(WIDTH);
			mNotes.setHeight(HEIGHT);
		}

		// Keeps from window showing off-screen
		mNotes.setX(Math.min(sMRef.getWidth() - WIDTH, x));
		mNotes.setY(Math.min(sMRef.getHeight() - HEIGHT, y));

		nText = new TextArea();

		// Get and display the notes
		int pos = 0;
		for(String l : t.notes) {
			if(pos == 0) nText.setText(l);
			else nText.setText(nText.getText() + "\n" + l);
			pos++;
		}
		// Add a listener that saves the data (reverses the display) whenever the field
		// is edited. A bit cumbersome but it works nonetheless
		nText.textProperty().addListener((observable, oldValue, newValue) -> {
			List<String> notes = new ArrayList<String>();
			String tmp = "";
			for(char c : nText.getText().toCharArray()) {
				if(c == '\n' || c == '\r') {
					notes.add(tmp);
					tmp = "";
				} else tmp += c;
			}
			notes.add(tmp);
			if(notes.get(0).equals("")) notes.remove(0);
			nTeam.notes = notes;
		});

		nText.prefWidthProperty().bind(mNotes.maxWidthProperty());
		nText.prefHeightProperty().bind(mNotes.heightProperty());

		Button nbClose = new Button();
		nbClose.setText("Close");
		nbClose.setOnMouseClicked(click -> {
			mNotes.close();
			updateTable();
		});

		VBox vb = new VBox();
		vb.getChildren().addAll(nText, nbClose);
		Scene sc = new Scene(vb, mNotes.getWidth(), mNotes.getHeight());

		sc.addEventHandler(KeyEvent.ANY, keyEvent -> {
			if(keyEvent.getCode() == KeyCode.ESCAPE) {
				mNotes.close();
				updateTable();
			}
		});

		mNotes.setScene(sc);
		mNotes.show();
	}

	private static Menu getMenuFile() {
		Menu r = new Menu("File");

		MenuItem m1New = new MenuItem("New");
		m1New.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if(fileManager.loadedFile != null) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Create New?");
					alert.setHeaderText("Opening a new file will clear the currently loaded file");
					alert.setContentText("Make sure that you've saved the loaded one before accepting");
					Optional<ButtonType> result = alert.showAndWait();
					if(result.get() != ButtonType.OK) { return; }
				}

				// Naming
				Dialog<String> dialog = new Dialog<String>();
				dialog.setTitle("New Pace");
				dialog.setHeaderText("Please give a name");

				Text nameText = new Text("Name");
				TextField nameField = new TextField();

				HBox hb = new HBox(nameText, nameField);
				hb.setSpacing(10);
				hb.setPadding(new Insets(10));

				dialog.getDialogPane().setContent(hb);
				dialog.setResultConverter(r -> nameField.getText());

				ButtonType bComplete = new ButtonType("Create", ButtonData.OK_DONE);
				ButtonType bCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
				dialog.getDialogPane().getButtonTypes().addAll(bCancel, bComplete);

				String ret = dialog.showAndWait().get();
				if(ret.contentEquals("")) return;

				Pace.newPace(ret);
				fileManager.loadedFile = null;
				updateTable();
			}
		});

		MenuItem m1Open = new MenuItem("Open");
		m1Open.setOnAction(event -> {
			fileManager.open();
		});

		MenuItem m1Save = new MenuItem("Save");
		m1Save.setOnAction(event -> {
			fileManager.save();
		});

		MenuItem m1SaveAs = new MenuItem("Save As");
		m1SaveAs.setOnAction(event -> {
			fileManager.saveAs();
		});

		SeparatorMenuItem m1Separator1 = new SeparatorMenuItem();

		MenuItem m1Exit = new MenuItem("Exit");
		m1Exit.setOnAction(event -> {
			System.exit(0);
		});

		r.getItems().addAll(m1New, m1Open, m1Save, m1SaveAs, m1Separator1, m1Exit);
		return r;
	}

	private static Menu getMenuTeams() {
		Menu r = new Menu("Teams");
		MenuItem m2Create = new MenuItem("Create Team");
		m2Create.setOnAction(event -> {
			fxTeam.open(null);
		});

		SeparatorMenuItem m2Separator1 = new SeparatorMenuItem();
		MenuItem m2Import = new MenuItem("Import");
		m2Import.setOnAction(event -> {
			fxImport.open();
		});

		MenuItem m2ImportFile = new MenuItem("Import from File...");
		m2ImportFile.setOnAction(even -> {
			fileImport.importFile();
		});

		r.getItems().addAll(m2Create, m2Separator1, m2Import, m2ImportFile);

		return r;
	}

	private static Menu getMenuPace() {
		Menu r = new Menu("Pace");

		MenuItem m3Goals = new MenuItem("Goal Times");
		m3Goals.setOnAction(event -> {
			fxGoals.open();
		});

		MenuItem m3Scores = new MenuItem("Scores");
		m3Scores.setOnAction(event -> {
			fxScores.open();
		});

		MenuItem m3Print = new MenuItem("Print");
		m3Print.setOnAction(event -> {
			fxPrint.open();
		});

		MenuItem m3Settings = new MenuItem("Settings");
		m3Settings.setOnAction(event -> {
			fxSettings.open();
		});

		r.getItems().addAll(m3Goals, m3Scores, m3Print, m3Settings);
		return r;
	}

	private static Menu getMenuAbout() {
		Menu r = new Menu("About");

		MenuItem changeLog = new MenuItem("Change Log");
		changeLog.setOnAction(e -> fxAbout.openChangeLog());

		r.getItems().addAll(changeLog);

		return r;
	}

}
