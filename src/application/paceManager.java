package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import classes.Pace;
import classes.Team;
import debugdev.importTeams;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class paceManager {

	public static final String version = "0.1.1";

	private static final boolean development = false;

	public static void main(String[] args) {

		Pace.newPace();

		// Debug Only
		if(development) {
			Pace.teams = importTeams.getTeams();
			importTeams.randomizeTimes();
			importTeams.importGoals();
		}

		fxMain.open(args);
	}

	/**
	 * Gets team by it's name
	 * 
	 * @param tmName Team Identifier
	 * @return Requested Team
	 */
	public static Team getTeam(String tmName) {
		for(Team t : Pace.teams) {
			String a = t.team.toLowerCase().replace(" ", "").replace("\n", "").replace("\r", "");
			String b = tmName.toLowerCase().replace(" ", "").replace("\n", "").replace("\r", "");
			if(a.contentEquals(b)) return t;
		}
		return null;
	}

	/**
	 * Deletes specified team, alerts user if confirmation is enabled
	 * 
	 * @param t Team to delete
	 */
	public static void deleteTeam(Team t) {
		if(Pace.settings.alertOnDeleteTeam) {
			Alert conf = new Alert(AlertType.CONFIRMATION);
			conf.setTitle("Delete " + t.team + "?");
			conf.setHeaderText("Do you really want to delete " + t.team + "?");
			Optional<ButtonType> result = conf.showAndWait();
			if(result.get() != ButtonType.OK) return;
		}
		Pace.teams.remove(t);
		fxMain.updateTable();
	}

	/**
	 * Gets a list of teams in a division
	 * 
	 * @param division Division to access teams from
	 * @return List of teams in the division
	 */
	public static List<Team> getTeams(String division) {
		List<Team> ret = new ArrayList<Team>();
		for(Team t : Pace.teams) {
			if(t.division.contentEquals(division)) {
				ret.add(t);
			}
		}
		return ret;
	}

	public static void closeApplication() {
		Stage[] stages = new Stage[] {fxMain.mNotes, fxPrint.sPrint, fxScores.sScores, fxSettings.sSettings, fxTeam.sTeam, fxGoals.sGoals, fxImport.sImport};
		for(Stage s : stages) if(s != null && s.isShowing()) s.close();
	}
}
