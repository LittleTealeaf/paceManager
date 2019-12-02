package classes;

public class Goal {

	public String division;
	public Time time;

	public Goal() {
		division = "";
		time = null;
	}

	public Goal(String div) {
		division = div;
		time = null;
	}

	public Goal(String div, Time tm) {
		division = div;
		time = tm;
	}

	public String getDivision() {
		return division;
	}

	public String getDisplayTime() {
		if(time == null) return "";
		return time.toString(true);
	}

	@Override
	public String toString() {
		String s = division;
		if(time != null) s += ": " + time.toString(true);
		return s;
	}

	public Time getGoalTime() {
		if(time != null) return time;
		else {
			int teams = 0;
			float elapsedAvg = 0;
			for(Team t : Pace.teams) if(t.division.contentEquals(division)) {
				Time elapsed = t.elapsed();
				if(elapsed != null) {
					teams++;
					elapsedAvg += elapsed.time;
				}
			}
			elapsedAvg /= teams;
			return new Time(elapsedAvg);
		}
	}
}
