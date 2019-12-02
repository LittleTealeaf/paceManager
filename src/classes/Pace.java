package classes;

import java.util.ArrayList;
import java.util.List;

import application.fxMain;
import application.fxScores;

public class Pace {

	public static String title;
	public static String version;
	public static List<Team> teams;
	public static List<Goal> goals;
	public static Settings settings; // TODO move settings to user-based

	public Pace() {}

	/**
	 * Applies object-values to the pace main values
	 */
	public static void loadPace() {
		fxMain.updateTable();
		fxScores.updateTabs();
	}

	public static void newPace() {
		newPace("");
	}

	public static void newPace(String setTitle) {
		title = setTitle;
		version = application.paceManager.version;
		teams = new ArrayList<Team>();
		goals = new ArrayList<Goal>();
		settings = new Settings();
	}
}