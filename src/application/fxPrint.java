package application;

import java.util.ArrayList;
import java.util.List;

import classes.Goal;
import classes.Pace;
import classes.Team;
import classes.util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Insets;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class fxPrint {

	/*
	 * Documentation http://www.java2s.com/Code/Java/2D-Graphics-GUI/
	 * PrintinJavapageformatanddocument.htm
	 * 
	 * printing tables
	 * https://docs.oracle.com/javase/tutorial/uiswing/misc/printtable.html
	 */

	public static Stage sPrint;

	private static ObservableSet<Printer> printers;
	private static ChoiceBox<Printer> setPrinter;
	private static ChoiceBox<String> setContent;
	private static PrinterJob job;
	private static PageOrientation orientation;

	private static Spinner<Integer> sCopies;

	// Check Boxes
	private static CheckBox[] cColumns;
	private static ChoiceBox<String> setValidTeams;

	// Content Selection:
	private static ChoiceBox<String> setDivision;
	private static RadioButton rtAll;
	private static RadioButton rtSelect;
	private static RadioButton rtSeparate;
	// Sort Selection
	private static ChoiceBox<String> setSortCol;

	// Default Insets
	private static final Insets DEFAULTINSETS = new Insets(10, 10, 10, 10);

	public static void open() {
		open("");
	}

	public static void open(String preset) {
		if(sPrint != null) sPrint.close();
		sPrint = new Stage();

		printers = Printer.getAllPrinters();

		sPrint.setTitle("Print");
		sPrint.setAlwaysOnTop(true);

		// PRINTING OPTIONS

		Label lPrinterSel = new Label("Printer: ");
		lPrinterSel.setTooltip(new Tooltip("Which printer do you want to print to?"));

		// Choice Box for Printers
		setPrinter = new ChoiceBox<Printer>(FXCollections.observableArrayList(printers));
		setPrinter.setValue(Printer.getDefaultPrinter());

		HBox hbSelPrinter = new HBox(lPrinterSel, setPrinter);
		hbSelPrinter.setPadding(DEFAULTINSETS);

		// Radio Buttons for Horizontal / Vertical alignment
		ToggleGroup tgOrientation = new ToggleGroup();
		RadioButton rVertical = new RadioButton("Veritcal");
		rVertical.setToggleGroup(tgOrientation);
		rVertical.armedProperty().addListener((obs) -> {
			// When Clicked
			orientation = PageOrientation.PORTRAIT;
		});
		RadioButton rHorizontal = new RadioButton("Horizontal");
		rHorizontal.setToggleGroup(tgOrientation);
		rHorizontal.armedProperty().addListener((obs) -> {
			// When Clicked
			orientation = PageOrientation.LANDSCAPE;
		});

		HBox hbOrientation = new HBox(rVertical, rHorizontal);
		hbOrientation.setSpacing(10);
		hbOrientation.setPadding(DEFAULTINSETS);

		Label lCopies = new Label("No. of Copies: ");
		lCopies.setTooltip(new Tooltip("Number of copies to print"));
		sCopies = new Spinner<Integer>();
		sCopies.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));

		HBox hbCopies = new HBox(lCopies, sCopies);
		hbCopies.setSpacing(10);
		hbCopies.setPadding(DEFAULTINSETS);

		VBox vbPrintSettings = new VBox(hbSelPrinter, hbOrientation, hbCopies);

		// CONTENT OPTIONS

		setContent = new ChoiceBox<String>(FXCollections.observableArrayList("Announcement", "Scoreboard", "Custom"));
		setContent.valueProperty().addListener((obs) -> {
			updateStage();
		});

		cColumns = new CheckBox[] {new CheckBox("Position"), new CheckBox("Team"), new CheckBox("Names"), new CheckBox("Start Time"), new CheckBox("Finish Time"), new CheckBox("Elapsed Time"), new CheckBox("Difference"), new CheckBox("Notes")};

		// Selecting a new row will update the list of columns
		for(CheckBox c : cColumns) {
			c.selectedProperty().addListener((obs) -> {
				setSortCol.getItems().setAll(getSortColumns());
				if(setSortCol.getItems().size() > 0) setSortCol.setValue(setSortCol.getItems().get(0));
			});
			c.setTooltip(new Tooltip("Include the '" + c.getText() + "' column in the table?"));
		}

		VBox vbContentOptionsColumns = new VBox(cColumns);
		vbContentOptionsColumns.setSpacing(3);
		vbContentOptionsColumns.setPadding(DEFAULTINSETS);

		// Team Selection

		// Creating the radio buttons
		ToggleGroup tgTeamSelection = new ToggleGroup();
		rtAll = new RadioButton("All Teams");
		rtSelect = new RadioButton("Select Division:");
		rtSelect.selectedProperty().addListener((obs, oldv, newv) -> {
			setDivision.setDisable(!newv.booleanValue());
		});
		rtSeparate = new RadioButton("Separate Divisions");
		// Setting them all to the toggle group
		rtAll.setToggleGroup(tgTeamSelection);
		rtSelect.setToggleGroup(tgTeamSelection);
		rtSeparate.setToggleGroup(tgTeamSelection);
		// Adding tooltips
		rtAll.setTooltip(new Tooltip("Include all recorded teams"));
		rtAll.setTooltip(new Tooltip("Include only a specific division"));
		rtSeparate.setTooltip(new Tooltip("Separate each division into a new table"));

		List<String> goals = new ArrayList<String>();
		// Add the drop-down for select division:
		if(Pace.goals.size() > 0) for(Goal g : Pace.goals) {
			goals.add(g.division);
		}
		setDivision = new ChoiceBox<String>(FXCollections.observableArrayList(goals));
		setDivision.setDisable(true);
		setDivision.setTooltip(new Tooltip("Specified Division to print"));

		HBox hbContentOptionsRTSelect = new HBox(rtSelect, setDivision);
		hbContentOptionsRTSelect.setSpacing(10);

		Text lValid = new Text("Valid Teams");

		setValidTeams = new ChoiceBox<String>(FXCollections.observableArrayList("All Teams", "Valid Only", "Arrived Only", "Departed Only", "Stale Only"));
		setValidTeams.setTooltip(new Tooltip("Which specific teams should we include?"));

		HBox hbContentValidTeams = new HBox(lValid, setValidTeams);
		hbContentValidTeams.setSpacing(10);

		Text lSort = new Text("Sort Method:");

		setSortCol = new ChoiceBox<String>();
		setSortCol.setTooltip(new Tooltip("Column in which the table should be sorted by"));

		HBox hbContentOptionsSort = new HBox(lSort, setSortCol);
		hbContentOptionsSort.setSpacing(10);

		VBox vbContentOptionsTeamSelect = new VBox(rtAll, hbContentOptionsRTSelect, rtSeparate, hbContentValidTeams, hbContentOptionsSort);
		vbContentOptionsTeamSelect.setSpacing(10);

		// rest

		HBox hbContentOptions = new HBox(vbContentOptionsColumns, vbContentOptionsTeamSelect);

		VBox vbContentOptions = new VBox(setContent, hbContentOptions);
		vbContentOptions.setSpacing(10);
		vbContentOptions.setPadding(DEFAULTINSETS);

		// BOTTOM

		Button bCancel = new Button("Cancel");
		bCancel.setOnAction(event -> {
			sPrint.close();
		});

		Region rBottom = new Region();
		HBox.setHgrow(rBottom, Priority.ALWAYS);

		Button bPrint = new Button("Print");
		bPrint.setOnAction(event -> {
			print();
		});

		HBox hbMid = new HBox(vbPrintSettings, vbContentOptions);
		HBox hbBottom = new HBox(bCancel, rBottom, bPrint);
		hbBottom.setPadding(new Insets(5, 5, 5, 5));

		VBox vb = new VBox(hbMid, hbBottom);

		Scene sc = new Scene(vb);
		sc.setOnKeyPressed(key -> {
			if(key.isControlDown() && key.getCode() == KeyCode.ENTER) print();
			if(key.getCode() == KeyCode.ESCAPE) sPrint.close();
		});
		sPrint.setScene(sc);

		// Set Defaults
		rVertical.fire();
		orientation = PageOrientation.PORTRAIT;
		setContent.setValue("");
		setValidTeams.setValue("All Teams");

		rtAll.setSelected(true);
		setValidTeams.setDisable(true);
		setSortCol.setDisable(true);

		// PRESETS
		// 'if(preset == "") {} else ' makes sure nothing happens if the preset is left
		// blank
		if(preset == "") {} else if(preset.contentEquals("All Teams")) {
			// All Teams
			setContent.setValue("Custom");
			rtAll.setSelected(true);
			setAllColumnValues(false);
			setColumnValue("Team");
			setColumnValue("Division");
			setColumnValue("Names");
			setColumnValue("Start Time");
			setColumnValue("End Time");
			setColumnValue("Elapsed Time");
			setValidTeams.setValue("All Teams");
			setSortCol.setValue("Team");
		} else if(preset.toCharArray()[0] == 'g') {
			// Specific Division
			String div = "";
			// Tries to parse the division, if errors then will just return
			try {
				div = preset.substring(1);
			} catch(Exception e) {
				return;
			}

			setValidTeams.setValue("Arrived Only");
			setContent.setValue("Custom");
			rtSelect.setSelected(true);
			setDivision.setValue(div);
			setAllColumnValues(false);
			setColumnValue("Position");
			setColumnValue("Team");
			setColumnValue("Elapsed Time");
			setColumnValue("Difference");
		}

		sPrint.show();
	}

	private static void setColumnValue(String colName) {
		setColumnValue(colName, true);
	}

	private static void setColumnValue(String colName, boolean var) {
		for(CheckBox c : cColumns) if(c.getText().contentEquals(colName)) c.setSelected(var);
	}

	private static void setAllColumnValues(boolean var) {
		for(CheckBox c : cColumns) c.setSelected(var);
	}

	/**
	 * Updates the Stage (disabled fields, etc)
	 */
	private static void updateStage() {
		boolean eCustom = !setContent.getValue().equals("Custom");
		for(CheckBox c : cColumns) {
			c.setDisable(eCustom);
		}

		rtAll.setDisable(eCustom);
		rtSelect.setDisable(eCustom);
		rtSeparate.setDisable(eCustom);

		boolean isAnnounce = setContent.getValue().equals("Announcement");
		setValidTeams.setDisable(isAnnounce);
		setSortCol.setDisable(isAnnounce);
		setSortCol.getItems().setAll(getSortColumns());
		if(setSortCol.getItems().size() > 0) setSortCol.setValue(setSortCol.getItems().get(0));
	}

	private static List<String> getSortColumns() {
		List<String> ret = new ArrayList<String>();
		switch (setContent.getValue()) {
		case "Custom":
			for(CheckBox c : cColumns) {
				if(!c.isDisabled() && c.isSelected()) ret.add(c.getText());
			}
			break;
		case "Scoreboard":
			for(String s : new String[] {"Team", "Division", "Elapsed Time"}) ret.add(s);
		}
		return ret;
	}

	/**
	 * Gets the columns for the custom print columns for table creation
	 * 
	 * @return
	 */
	private static String[] getCustomPrintColumns() {
		List<String> s = new ArrayList<String>();
		for(CheckBox c : cColumns) {
			if(c.isSelected()) switch (c.getText()) {
			case "Position":
				s.add("positionInDivision");
				break;
			case "Start Time":
				s.add("startFXM");
				break;
			case "Finish Time":
				s.add("finishFXM");
				break;
			case "Elapsed Time":
				s.add("elapsedFXM");
				break;
			default:
				s.add(c.getText().toLowerCase());
			}
		}
		String[] ret = new String[s.size()];
		s.toArray(ret);
		return ret;
	}

	/**
	 * Gets the custom sorting option for printing in the form that works for the
	 * table generation
	 * 
	 * @return
	 */
	private static String getCustomPrintSort() {
		switch (setSortCol.getValue()) {
		case "Position":
			return "positionInDivision";
		case "Start Time":
			return "startFXM";
		case "Finish Time":
			return "finishFXM";
		case "Elapsed Time":
			return "elapsedFXM";
		default:
			return setSortCol.getValue().toLowerCase();
		}
	}

	private static void print() {
		// Cancel Scripts
		if(setContent.getValue() == "") return;
		if(setSortCol.getValue() == "") return;
		if(rtSelect.isSelected() && setDivision.getValue() == "") return;
		if(setValidTeams.getValue() == "") return;

		sPrint.setAlwaysOnTop(false);

		// Gets selected printer
		Printer printer = setPrinter.getValue();

		// Sets the printer job
		job = PrinterJob.createPrinterJob(printer);

		// Create a layout using the printer's default paper
		PageLayout layout = printer.createPageLayout(printer.getDefaultPageLayout().getPaper(), orientation, Printer.MarginType.DEFAULT);

		job.getJobSettings().setPageLayout(layout);

		// Create a "printing progress" window
		sPrint.close();
		Stage sProgress = new Stage();
		Text progressText = new Text("Printing...");
		Button bCancel = new Button("Cancel");
		bCancel.setOnAction(action -> {
			job.cancelJob();
			sProgress.close();
			sPrint.close();
		});
		VBox vb = new VBox(progressText, bCancel);
		vb.setSpacing(20);
		vb.setPadding(DEFAULTINSETS);
		sProgress.setScene(new Scene(vb, 200, 75));
		sProgress.show();

		// Printing Script
		List<BorderPane> borderPanes = new ArrayList<BorderPane>();

		String header = "";
		String[] columns = null;
		List<Team> teams = null;
		switch (setContent.getValue()) {
		case "Announcement":
			if(!Pace.goals.isEmpty()) for(Goal g : Pace.goals) {
				header = g.division + "  " + g.time.toString();
				columns = new String[] {"positionInDivision", "team", "names", "elapsedFXM"};
				List<Team> tms = new ArrayList<Team>();
				for(Team t : paceManager.getTeams(g.division)) {
					if(!t.getPositionInDivision().contentEquals("")) tms.add(t);
				}
				borderPanes.addAll(getTablePages(job, header, tms, columns, "positionInDivision"));
			}
			else {
				printError("Goal List is empty");
				sProgress.close();
				return;
			}
			break;
		case "Scoreboard":
			header = "Scoreboard:";
			columns = new String[] {"team", "division", "elapsedFXM", "difference"};
			borderPanes.addAll(getTablePages(job, header, getPrintTeams(Pace.teams), columns, "team"));
			break;
		case "Custom":
			// Selected Columns
			columns = getCustomPrintColumns();
			// Get selected Teams
			if(rtAll.isSelected()) { //
				teams = Pace.teams;
			} else if(rtSelect.isSelected()) { // Specifically Selected Division
				String selDiv = setDivision.getValue();
				if(!selDiv.contentEquals("")) {
					teams = paceManager.getTeams(selDiv);
				}
			} else if(rtSeparate.isSelected()) { // Each division in its own respective list, uses custom script as
													// multiple pages are needed
				if(!Pace.goals.isEmpty()) {
					for(Goal g : Pace.goals) {
						header = g.division + "  " + g.time.toString(true);
						borderPanes.addAll(getTablePages(job, header, getPrintTeams(paceManager.getTeams(g.division)), columns, getCustomPrintSort()));
					}
					break;
				} else {
					printError("Please specify what teams to include");
					sProgress.close();
					return;
				}
			}
			borderPanes.addAll(getTablePages(job, header, getPrintTeams(teams), columns, getCustomPrintSort()));
			break;
		}

		for(int i = 0; i < sCopies.getValue(); i++) {
			for(BorderPane bp : borderPanes) {
				if(!job.printPage(bp)) {
					printError("Job could not print");
					return;
				}
			}
		}

		if(job.endJob()) {
			sProgress.close();
		} else {
			progressText.setText("Print Failed");
			bCancel.setText("Close");
		}
	}

	/**
	 * Returns pages to print
	 * 
	 * @param job        The printer job, used to get the page dimensions
	 * @param header     Text to put in the top, add \n for additional lines
	 * @param teams      The List of Teams to include
	 * @param columns    Columns in order to display. Valid Columns: team (team
	 *                   number), division (team division), names (rider names),
	 *                   startFXM (start time), finishFXM (end time), elapsedFXM
	 *                   (elapsed time), notesDisplay (notes), positionInDivision
	 *                   (team place)
	 * @param sortColumn Column, specified above, to sort by (will return null if
	 *                   set column does not)
	 * @return
	 */

	private static List<BorderPane> getTablePages(PrinterJob job, String header, List<Team> getTeams, String[] columns, String sortColumn) {
		// Checks if the sort column is in the columns
		boolean bError = true;
		for(String s : columns) if(s.contentEquals(sortColumn)) bError = false;
		if(bError) {
			System.err.append("Error: sortColumn is not a specified column in columns");
			return null;
		}

		// Sorts Tables
		List<Team> teams = util.sortTeams(getTeams, sortColumn);
		// TODO include a reverse method

		// PRESETS
		final double cellSize = 20; // TODO make cell size into a function
		final double headerSize = cellSize * 1.2;
		final double textSize = 15.9609375;

		// Gets Printable Size
		final double pWidth = job.getJobSettings().getPageLayout().getPrintableWidth();
		final double pHeight = job.getJobSettings().getPageLayout().getPrintableHeight();
		double setTableHeight = pHeight - textSize;

		// Return value
		List<BorderPane> ret = new ArrayList<BorderPane>();

		// Clear List of any nulls, then adds a null at the end
		if(teams.contains(null)) teams.remove(null);
		teams.add(null);

		// Check if columns has the names part
		boolean hasNames = false;
		for(String s : columns) if(s.toLowerCase().contentEquals("names")) hasNames = true;

		// USING
		// https://stackoverflow.com/questions/31918959/javafx-print-tableview-on-multiple-pages
		// Method: make a separate table for each page and return those

		// Get a list of all teams for each page
		List<Team> tempTm = new ArrayList<Team>();
		double tmpHeight = headerSize;

		for(Team t : teams) {
			// Get cell size
			double tSize = cellSize;
			if(t != null && hasNames) tSize = cellSize * t.names.size();

			// Test if cell size will push it to the limit
			if((tmpHeight + tSize > setTableHeight) || t == null) {
				// Hard Copy
				List<Team> ts = new ArrayList<Team>();
				for(Team a : tempTm) ts.add(a);

				// Make the table
				TableView<Team> tble = getTable(columns, sortColumn, pWidth - 5);
				tble.getItems().addAll(ts);
				tble.resize(pWidth, setTableHeight);

				// Create the Border Pane
				BorderPane bp = new BorderPane();
				bp.setPrefSize(pWidth, pHeight);
				bp.autosize();
				bp.setTop(new Text(header));
				bp.setCenter(tble);

				// Add to the list
				ret.add(bp);

				// Reset Variables
				tmpHeight = headerSize;
				tempTm = new ArrayList<Team>();
			} else {
				// Add the team
				tmpHeight += tSize;
				tempTm.add(t);
			}
		}
		return ret;
	}

	/**
	 * Creating a Table
	 * 
	 * @param teams      List of Teams
	 * @param columns    Columns as described in {@link getTablePages}
	 * @param sortColumn Column to sort, will not set sort if it's not included
	 * @return
	 */
	private static TableView<Team> getTable(String[] columns, String sortColumn, double pWidth) {
		// Preset Variables
		final double colSizeTeam = 35;
		final double colSizeDiv = 60;
		final double colSizeTime = 75;
		final double colSizePlace = 35;
		final double colSizeNames = 200;
		final double colSizeNotes = 50;
		// Variables
		double totalSize = 0;

		// Create a table
		TableView<Team> table = new TableView<Team>();

		// Cycle through each requested column
		for(String s : columns) {
			TableColumn<Team, String> col = new TableColumn<Team, String>(s);

			// Variable Settings for each column
			switch (s.toLowerCase()) {
			case "team":
				col.setText("Team");
				col.setPrefWidth(colSizeTeam);
				break;
			case "division":
				col.setText("Division");
				col.setPrefWidth(colSizeDiv);
				break;
			case "names":
				col.setText("Names");
				col.setPrefWidth(colSizeNames);
				col.setCellFactory(column -> {
					return util.getTeamCell();
				});
				break;
			case "startfxm":
				col.setText("Start");
				col.setPrefWidth(colSizeTime);
				break;
			case "finishfxm":
				col.setText("Finish");
				col.setPrefWidth(colSizeTime);
				break;
			case "elapsedfxm":
				col.setText("Elapsed");
				col.setPrefWidth(colSizeTime);
				break;
			case "positionindivision":
				col.setText("Pl.");
				col.setPrefWidth(colSizePlace);
				col.setSortType(TableColumn.SortType.DESCENDING);
				break;
			case "printablenotes":
				col.setText("Notes");
				col.setPrefWidth(colSizeNotes);
				break;
			case "difference":
				col.setText("Difference");
				col.setPrefWidth(colSizeTime);
			default:
				break;
			}

			// Adds to total size
			totalSize += col.getPrefWidth();

			// Sets the cell factory
			col.setCellValueFactory(new PropertyValueFactory<Team, String>(s));

			// Adds it to the table
			table.getColumns().add(col);

			// If it's the sort column, set it as the sort column
			if(s.contentEquals(sortColumn)) table.getSortOrder().add(col);
		}

		// If smaller than scale
		if(totalSize != pWidth) {

			// Take the difference left over
			double difference = pWidth - totalSize;

			// Split remainder up and add that amount to each column
			double colAdd = difference / table.getColumns().size();
			for(TableColumn<Team, ?> a : table.getColumns()) {
				a.setPrefWidth(a.getPrefWidth() + colAdd);
			}
		}

		// Removes the arrow
		table.getSortOrder().clear();

		return table;
	}

	private static List<Team> getPrintTeams(List<Team> teams) {
		List<Team> ret = new ArrayList<Team>();
		for(Team t : teams) {
			switch (setValidTeams.getValue()) {
			case "Valid Only":
				if(!t.excluded && t.elapsed() != null) ret.add(t);
				break;
			case "Arrived Only":
				if(t.elapsed() != null) ret.add(t);
				break;
			case "Departed Only":
				if(t.start != null && t.finish == null) ret.add(t);
				break;
			case "Stale Only":
				if(t.start == null && t.finish == null) ret.add(t);
				break;
			default:
				ret.add(t);
				break;
			}
		}
		return ret;
	}

	private static void printError(String details) {
		Alert error = new Alert(AlertType.ERROR);
		error.setTitle("Printing Error");
		error.setHeaderText("There was an error with your print request");
		error.setContentText(details);
		error.showAndWait();
	}
}
