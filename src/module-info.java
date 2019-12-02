module paceManager {
	exports application;
	exports classes;

	opens classes to javafx.base;

	requires javafx.base;
	requires transitive javafx.graphics;
	requires transitive javafx.controls;
	requires poi;
	requires poi.ooxml;
	requires java.desktop;
	requires java.base;
	requires com.google.gson;
}
