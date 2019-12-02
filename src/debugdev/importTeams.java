package debugdev;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import classes.Goal;
import classes.Pace;
import classes.Team;
import classes.Time;

public class importTeams {

	private static List<String> lines = new ArrayList<String>();

	public static List<Team> getTeams() {
		List<Team> teams = new ArrayList<Team>();
		Scanner scanner = null;

		if(lines.isEmpty()) updateDatabase();

		for(String line : lines) {
			scanner = new Scanner(line);
			Team a = new Team();
			a.team = scanner.next();
			a.division = scanner.next();
			a.names = new ArrayList<String>();
			String name = "";
			boolean skip = false;
			for(char ch : scanner.nextLine().toCharArray()) {
				if(ch == ',') {
					skip = true;
					a.names.add(name);
					name = "";
				} else if(skip) {
					skip = false;
				} else {
					name += ch;
				}
			}
			a.names.add(name);
			name = "";
			// Remove the first space of the name
			boolean swi = false;
			for(char c : a.names.get(0).toCharArray()) {
				if(swi) name += c;
				swi = true;
			}
			a.names.set(0, name);
			teams.add(a);
			scanner.close();
		}
		teams.get(0).notes.add("NOT A AWEF");
		return(teams);
	}

	public static void importGoals() {
		Time shortestTime = null;
		Time longestTime = null;
		for(Team t : Pace.teams) if(t.elapsed() != null) {
			if(shortestTime == null || shortestTime.time > t.elapsed().time) {
				shortestTime = t.elapsed();
			}
			if(longestTime == null || longestTime.time < t.elapsed().time) {
				longestTime = t.elapsed();
			}
		}
		String[] divs = {"Pleasure", "Hunt", "Western", "Junior"};
		for(String s : divs) {
			Goal g = new Goal(s);
			g.time = new Time((float) Math.random() * (longestTime.time - shortestTime.time) + shortestTime.time);
			Pace.goals.add(g);
		}
	}

	public static void randomizeTimes() {
		// Set Variables
		Time startRange = new Time("7:00:00");
		Time endRange = new Time("13:00:00");
		Time minElapsed = new Time("1:00:00");
		Time maxElapsed = new Time("2:00:00");
		for(Team a : Pace.teams) {
			if(Math.random() < 0.5) {
				a.start = new Time((float) (Math.random() * (endRange.time - startRange.time) + startRange.time));
				if(Math.random() < 0.5) {
					a.finish = new Time((float) (Math.random() * (maxElapsed.time - minElapsed.time) + a.start.time));
				}
			}
		}
	}

	private static void updateDatabase() {
		lines = new ArrayList<String>();
		lines.add("441	Pleasure Claudia Marcello, Courtney Strout");
		lines.add("461	Pleasure Paula Tilquist, Cindy Pelletier");
		lines.add("462	Western Dale Pereira, Carol Hagen");
		lines.add("495	Pleasure Lisa Peterson, Shelby McChord");
		lines.add("498	Western Trish Perrotti, Alysha Colangeb");
		lines.add("501	Western Christel Maturo, Anthony Gambardello");
		lines.add("503	Pleasure Heather Dewey, Robyn Dolby");
		lines.add("507	Hunt Rebecca Reeves Saria, Avalon Schrelker");
		lines.add("508	Pleasure John Barry, Christine Barry");
		lines.add("512	Junior Ryleigh Sough, Hailey Tinney");
		lines.add("514	Western Jennifer Uscilla, Eric Uscilla");
		lines.add("517	Hunt Shari Goldstein, John McCarron");
		lines.add("522	Pleasure Elene Moore, Stacy Moore");
		lines.add("525	Western Amy Deutermann, Thomas Vogel");
		lines.add("530	Hunt Kate Hornbecker, Alexa Duncan");
		lines.add("533	Hunt Amy Naniokas, Elizabeth Warner, Bart-Jan Hoedemaker ");
		lines.add("541	Western Lauren Troia, Cindy Horton, Cheryl Johnson");
		lines.add("543	Western Jessica Ray, Stacey Bowers, Karen O'Keefe");
		lines.add("544	Hunt Danielle Richards, Stacia Szlzepanek");
		lines.add("545	Pleasure Summer Galecki, Chelsea Mongillo");
		lines.add("546	Junior Elizabeth Morsey, Ruari Duffy Walsh, Kate Roush");
		lines.add("548	Hunt Dawn Tagliavia-Deri, Andrea Sayegh, Steve Stirbl");
		lines.add("549	Pleasure Jennifer Corti, Lindsay Fuegmann");
		lines.add("550	Pleasure Jennifer Friedly, Rebecca Disbrow, Jessica Friedly");
		lines.add("551	Pleasure Lucia Blanchard, Harper Loonsk");
		lines.add("552	Hunt Nancy Bradley, Colleen Colbert (not finished)");
		lines.add("553	Hunt Joe Secondino, Danielle Borrelli");
		lines.add("554	Hunt Maryanne Holland Ruh, Mattie Harris");
		lines.add("555	Pleasure Marylou Kennedy, Kate Bogaert, Julia Clerkin ");
		lines.add("556	Pleasure Amanda Morton, Jamie Warner");
		lines.add("557	Hunt Rosanne Hermenze, JoAnn Briggs");
		lines.add("558	Pleasure Callie Switz, Krista Reynolds");
	}
}
