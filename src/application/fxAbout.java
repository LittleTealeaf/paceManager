package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class fxAbout {

	public static Stage sChangeLog;

	public static void openChangeLog() {
		if(sChangeLog != null && sChangeLog.isShowing()) sChangeLog.close();
		sChangeLog = new Stage();
		sChangeLog.setTitle("Change Log");

		TextArea changeLog = new TextArea(getChangeLog());
		changeLog.setEditable(false);

		sChangeLog.setScene(new Scene(changeLog));
		sChangeLog.show();

	}

	public static String getChangeLog() {
		String r = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("Changelog.txt")));
			String line = "";
			while((line = br.readLine()) != null) {
				if(!r.contentEquals("")) r += "\n";
				r += line;
			}

		} catch(Exception e) {}
		return r;
	}
}
