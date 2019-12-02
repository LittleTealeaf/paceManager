package classes;

import application.fxTeam;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class ScoreTab {
	public TableView<Team> table;
	public Label label;
	public Goal goal;

	@SuppressWarnings("unchecked")
	public ScoreTab(Goal g) {
		goal = g;

		table = new TableView<Team>();
		table.setEditable(false);

		table.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				if(click.getClickCount() == 2) {
					try {
						fxTeam.open(table.getSelectionModel().getSelectedItem(), table.getSelectionModel().getSelectedCells().get(0).getColumn());
					} catch(IndexOutOfBoundsException e) {}
				}
			}
		});

		table.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if(e.getCode() == KeyCode.P && e.isControlDown()) {
					application.fxPrint.open("g" + g.division);
				}
			}
		});

		TableColumn<Team, String> cPlace = new TableColumn<Team, String>("Place");
		cPlace.setEditable(false);
		cPlace.setSortable(true);
		cPlace.setReorderable(false);
		cPlace.setCellValueFactory(new PropertyValueFactory<Team, String>("difference"));

		TableColumn<Team, String> cTeamName = new TableColumn<Team, String>("Team");
		cTeamName.setEditable(false);
		cTeamName.setSortable(false);
		cTeamName.setReorderable(false);
		cTeamName.setCellValueFactory(new PropertyValueFactory<Team, String>("team"));

		TableColumn<Team, String> cNames = new TableColumn<Team, String>("Riders");
		cNames.setEditable(false);
		cNames.setSortable(false);
		cNames.setReorderable(false);
		cNames.setCellValueFactory(new PropertyValueFactory<Team, String>("names"));

		TableColumn<Team, String> cElapsed = new TableColumn<Team, String>("Elapsed Time");
		cElapsed.setEditable(false);
		cElapsed.setSortable(true);
		cElapsed.setReorderable(false);
		cElapsed.setCellValueFactory(new PropertyValueFactory<Team, String>("elapsedFXM"));

		TableColumn<Team, String> cDifference = new TableColumn<Team, String>("Difference");
		cDifference.setEditable(false);
		cDifference.setSortable(true);
		cDifference.setReorderable(false);
		cDifference.setCellValueFactory(new PropertyValueFactory<Team, String>("difference"));

		TableColumn<Team, String> cNotes = new TableColumn<Team, String>("Notes");
		cNotes.setEditable(false);
		cNotes.setSortable(false);
		cNotes.setReorderable(false);
		cNotes.setCellValueFactory(new PropertyValueFactory<Team, String>("notesDisplay"));

		table.getColumns().addAll(cTeamName, cNames, cElapsed, cDifference, cNotes);
		table.getSortOrder().add(cDifference);

		// Disables Horizontal Scrollbar
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		label = new Label("Division Optimum Time: " + goal.getGoalTime().toString(true));
	}

	public Tab getTab() {
		Tab tab = new Tab(goal.division);
		tab.setClosable(false);
		tab.setContent(new VBox(label, table));
		return tab;
	}

	public void updateTable() {
		updateTable(0);
	}

	public void updateTable(double width) {
		label = new Label("Division Optimum Time: " + goal.getGoalTime().toString(true));
		table.getItems().clear();
		for(Team t : Pace.teams) {
			// If team isn't excluded, is the same division, and has an elapsed time, add it
			// to the list
			if(!t.excluded && t.division.contentEquals(goal.division) && t.elapsed() != null) table.getItems().add(t);
		}
		table.autosize();
		table.sort();
		resizeTable(width);
	}

	public void resizeTable(double width) {
		if(width == 0) return;
		final double wTeam = 50;
		final double wTime = 80;
		table.getColumns().get(0).setMinWidth(wTeam);
		table.getColumns().get(0).setMaxWidth(wTeam);

		table.getColumns().get(2).setMinWidth(wTime);
		table.getColumns().get(2).setMaxWidth(wTime);

		table.getColumns().get(3).setMinWidth(wTime);
		table.getColumns().get(3).setMaxWidth(wTime);
	}
}
