package classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.VBox;

public class util {
	/**
	 * Turns int into readable value for place
	 * 
	 * @param Position (1st is 1, 2nd is 2, 3rd is 3)
	 * @return 1st, 2nd, 3rd, etc (as a string)
	 */
	public static String readablePlace(int position) {
		if(position == 1) return "1st";
		else if(position == 2) return "2nd";
		else if(position == 3) return "3rd";
		else if(position >= 4 && position <= 20) return position + "th";
		else return position + "";
	}

	public static TableCell<Team, String> getTeamCell() {
		return new TableCell<Team, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if(item == null || empty) {
					setGraphic(null);
				} else {
					VBox vbox = new VBox();
					List<String> textList = Arrays.asList(item.split(", "));
					for(int i = 0; i < textList.size(); i++) {
						Label lbl = new Label(textList.get(i));
						vbox.getChildren().add(lbl);
					}
					setGraphic(vbox);
				}
			}
		};
	}

	/**
	 * Sorts teams by a certain method
	 * 
	 * @param teams   List of Teams to sort
	 * @param sortCol Valid Columns: team (team number), division (team division),
	 *                startFXM (start time), finishFXM (end time), elapsedFXM
	 *                (elapsed time) positionInDivision (team place)
	 * @return
	 */
	public static List<Team> sortTeams(List<Team> teams, String sortCol) {
		return sortTeams(teams, sortCol, false);
	}

	public static List<Team> sortTeamsReverse(List<Team> teams, String sortCol) {
		return sortTeams(teams, sortCol, true);
	}

	private static List<Team> sortTeams(List<Team> listTeams, String sortCol, boolean reverse) {
		List<Team> teams = new ArrayList<Team>();
		for(Team t : listTeams) teams.add(t);
		Collections.sort(teams, (a, b) -> {
			int r = 1;
			if(reverse) r = -1;

			switch (sortCol) {
			case "team":
				r *= a.team.compareTo(b.team);
				break;
			case "division":
				r *= a.division.compareTo(b.division);
				break;
			case "startFXM":
				if(a.start == null) {
					r *= 1;
					break;
				}
				if(b.start == null) {
					r *= -1;
					break;
				}
				r *= Float.compare(a.start.time, b.start.time);
				break;
			case "finishFXM":
				if(a.finish == null) {
					r *= 1;
					break;
				}
				if(b.finish == null) {
					r *= -1;
					break;
				}
				r *= Float.compare(a.finish.time, b.finish.time);
				break;
			case "elapsedFXM":
				if(a.elapsed() == null) {
					r *= 1;
					break;
				}
				if(b.elapsed() == null) {
					r *= -1;
					break;
				}
				r *= Float.compare(a.elapsed().time, b.elapsed().time);
				break;
			case "positionInDivision":
				r *= -1 * a.getPositionInDivision().compareTo(b.getPositionInDivision());
				break;
			default:
				break;
			}
			return r;
		});
		for(Team t : teams) System.out.println(t);
		return teams;
	}
}
