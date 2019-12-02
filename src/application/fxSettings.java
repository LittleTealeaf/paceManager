package application;

import classes.Pace;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class fxSettings {

	public static Stage sSettings;

	public static void open() {
		if(sSettings != null) sSettings.close();
		sSettings = new Stage();
		sSettings.setTitle("Settings");

		CheckBox cDelTeam = new CheckBox("Warn on Delete Teams");
		CheckBox cDelGoal = new CheckBox("Warn on Delete Goals");

		cDelTeam.setSelected(Pace.settings.alertOnDeleteTeam);
		cDelGoal.setSelected(Pace.settings.alertOnDeleteGoal);

		Button bSave = new Button("Apply");
		bSave.setOnAction(event -> {
			Pace.settings.alertOnDeleteGoal = cDelGoal.isSelected();
			Pace.settings.alertOnDeleteTeam = cDelTeam.isSelected();
			sSettings.close();
		});

		Button bCancel = new Button("Cancel");
		bCancel.setOnAction(event -> {
			sSettings.close();
		});

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setHgap(10);
		grid.setVgap(10);

		grid.add(cDelTeam, 0, 0);
		grid.add(cDelGoal, 0, 1);
		grid.add(bSave, 0, 2);
		grid.add(bCancel, 2, 2);

		sSettings.setScene(new Scene(grid));
		sSettings.show();
	}

}
