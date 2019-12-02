package application;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import classes.Pace;
import classes.Settings;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class fileManager {

	public static File loadedFile;

	private static Gson staticGson;

	/**
	 * Saves the currently loaded file, if there is no file or the file does not
	 * exist it will use the save-as script
	 */
	public static void save() {
		if(loadedFile == null || !loadedFile.exists()) {
			saveAs();
			return;
		} else {
			fileSave(loadedFile);
		}
	}

	/**
	 * Opens a "save-as" dialog
	 */
	public static void saveAs() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save File");
		fileChooser.setInitialFileName(Pace.title);
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Pace Files", "*.pace"));
		fileSave(fileChooser.showSaveDialog(fxMain.sMRef));
	}

	/**
	 * Opens an "open file" dialog to pass to the openFile method
	 */
	public static void open() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Pace File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Pace Files", "*.pace"));
		fileOpen(fileChooser.showOpenDialog(fxMain.sMRef));
	}

	// http://tutorials.jenkov.com/java-json/gson.html
	public static void fileSave(File saveFile) {
		staticGson = new GsonBuilder().excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT).setPrettyPrinting().create();
		try {
			// Create a writer
			FileWriter writer = new FileWriter(saveFile);

			// Write the JSON file, using the GSON library
			writer.write(staticGson.toJson(new Pace()));
			writer.close();
			Pace.title = saveFile.getName();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void fileOpen(File openFile) {
		// Cancel if file doesn't exist
		if(openFile == null || !openFile.exists()) return;
		try {

			staticGson.fromJson(Files.newBufferedReader(openFile.toPath()), Pace.class);
			Pace.loadPace();

			// TODO add differentiation for different versions
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
