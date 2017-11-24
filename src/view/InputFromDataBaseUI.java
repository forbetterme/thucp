package view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.DirectoryChooserBuilder;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import test.TableController;

public class InputFromDataBaseUI {
		VBox inputVbox;
		MenuItem menuDataBase;
		Stage stage;
		FXMLLoader loader;
		TabPane inputTabPane;

		public InputFromDataBaseUI(VBox inputVbox, MenuItem menuDataBase, Stage stage, FXMLLoader loader,TabPane inputTabPane) {
			this.inputVbox = inputVbox;
			this.menuDataBase = menuDataBase;
			this.stage = stage;
			this.loader = loader;
			this.inputTabPane = inputTabPane;
			setActions();
		}

		public void setActions() {
			SetMenuFileAction();
		}

		public void SetMenuFileAction() {
			menuDataBase.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					String filename;
					DBConnectDialog dbcd = new DBConnectDialog();
					dbcd.display("数据库导入数据", "message");
					// System.out.println(file.getAbsolutePath());
					InputTableController temp = loader.getController();
					temp.setInputTabPane(inputTabPane);
					temp.showInputTable(temp.getInputDataSet());
				}
			});
		}


}
