package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import classes.Pace;
import classes.Team;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

public class fileImport {

	private static List<Team> teams;

	public static void importFile() {
		// Creates a file dialogue
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Pace File");

		// Get team script with results from file dialog
		teams = getTeams(fileChooser.showOpenDialog(fxMain.sMRef));

		// If getting teams was not successful
		if(teams == null) return;
		if(teams.size() == 0) {
			// TODO Warning for not able to import file
			return;
		}
		importDialog();
	}

	private static void importDialog() {
		Dialog<List<Team>> dialog = new Dialog<List<Team>>();
		dialog.setTitle("Import Options");
		dialog.setHeaderText("Choose how you would like to import the teams");

		CheckBox cClearTeams = new CheckBox("Clear Teams");
		cClearTeams.setTooltip(new Tooltip("Clears all currently loaded teams"));

		CheckBox cKeepExisting = new CheckBox("Keep Updated Only");
		cKeepExisting.setTooltip(new Tooltip("Removes any teams currently listed \nthat are not updated by the import"));
		cKeepExisting.disableProperty().bind(cClearTeams.selectedProperty());

		GridPane grid = new GridPane();
		grid.add(cClearTeams, 1, 1);
		grid.add(cKeepExisting, 1, 2);
		dialog.getDialogPane().setContent(grid);

		ButtonType bAccept = new ButtonType("Import", ButtonData.OK_DONE);
		ButtonType bCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(bAccept, bCancel);

		dialog.setResultConverter(r -> {
			if(cClearTeams.isSelected()) {
				return teams;
			} else {
				// Only used if set to not keep unique
				List<Team> setTeams = new ArrayList<Team>();

				for(Team a : teams) {
					for(Team b : Pace.teams) {
						if(a.team.contentEquals(b.team)) {
							b.names = a.names;
							b.division = a.division;
							if(!cKeepExisting.isSelected()) setTeams.add(b);
						}
					}
				}

				if(!cKeepExisting.isSelected()) return setTeams;
				else return Pace.teams;
			}
		});

		Pace.teams = dialog.showAndWait().get();
		fxMain.updateTable();
	}

	private static List<Team> getTeams(File file) {
		if(file == null) return null;
		List<Team> teams = new ArrayList<Team>();
		try {
			FileInputStream stream = new FileInputStream(file);
			// Finds the workbook instance for XLSX file and gets the first spreadsheet
			XSSFWorkbook workbook = new XSSFWorkbook(stream);
			XSSFSheet sheet = workbook.getSheetAt(0);

			int posRow = 0;
			int posCol = 0;
			// Goes through each row
			Iterator<Row> rowIterator = sheet.iterator();
			while(rowIterator.hasNext()) {
				posRow++;
				Row row = rowIterator.next();

				// Goes through each column
				if(posRow > 2) {
					Team r = new Team();
					posCol = 0;
					Iterator<Cell> cellIterator = row.cellIterator();
					while(cellIterator.hasNext()) {
						posCol++;
						Cell cell = cellIterator.next();

						if(posCol == 2) {
							r.division = getString(cell).toLowerCase();
							if(r.division.length() > 1) r.division = r.division.substring(0, 1).toUpperCase() + r.division.substring(1);
						} else if(posCol == 3) {
							r.team = getString(cell);

						} else if(posCol == 4 || posCol == 5 || posCol == 6) {
							r.names.add(getString(cell));
						}

					}
					while(r.names.contains("")) r.names.remove("");
					if(r.names.size() > 0 && !r.team.contentEquals("")) {
						teams.add(r);
					}
				}
			}
			workbook.close();
		} catch(IOException e) {
			return null;
		}
		return teams;
	}

	// Returns the string data of the cell, regardless of the cell type
	private static String getString(Cell cell) {
		try {
			return cell.getStringCellValue();
		} catch(Exception e) {
			try {
				double a = cell.getNumericCellValue();
				if((int) a == a) return (int) a + "";
				else return a + "";
			} catch(Exception f) {
				try {
					return cell.getBooleanCellValue() + "";
				} catch(Exception g) {}
			}
		}
		return "";
	}
}
