package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import test.TableController;

public class FrameworkMain extends Application {
	public static Integer indexInputData = 0;
	public static Map<String, InputData> inputDataSet = new HashMap<String, InputData>();
	public static Map<String, InputData> paramInputDataSetOfFunc = new HashMap<String, InputData>();

	@Override
	public void start(Stage primaryStage) {
		try {
			VBox vbox = new VBox();
//			vbox.setPrefSize(1000, 800);
//			vbox.setAlignment(Pos.CENTER);
			vbox.setSpacing(5);
			vbox.setPadding(new Insets(5, 0, 5, 0));
			TabPane tabPane = new TabPane();
			Tab input = new Tab("    input    ");
			input.closableProperty().set(false);

			final MenuBar inputMenuBar = new MenuBar();
			MenuItem menuFile = new MenuItem("从文件导入");
			MenuItem menuDataBase = new MenuItem("从数据库导入");
			Menu importMenu = new Menu("导入");
			importMenu.getItems().addAll(menuFile, menuDataBase);
			inputMenuBar.getMenus().add(importMenu);
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(this.getClass().getResource("InputDataSetTable.fxml"));
			// 为表格设置控制器
			loader.setController(new InputTableController());
			AnchorPane tableViewPane = loader.load();
			tableViewPane.setPadding(new Insets(5, 300, 5, 300));
//			tableViewPane.setPrefSize(1000, 1000);
//			tableViewPane.autosize();
			TabPane inputTabPane = new TabPane();
			Tab tableViewTab = new Tab("    data set    ");
			tableViewTab.setContent(tableViewPane);
			tableViewTab.setClosable(false);
			inputTabPane.getTabs().add(tableViewTab);

			VBox inputVbox = new VBox();
//			inputVbox.setAlignment(Pos.CENTER);
			inputVbox.setSpacing(5);
			inputVbox.setPadding(new Insets(5, 0, 5, 0));
			inputVbox.getChildren().add(inputMenuBar);
			inputVbox.getChildren().add(inputTabPane);
			input.setContent(inputVbox);
			InputFromFileUI inputFromFileUI = new InputFromFileUI(inputVbox, menuFile, primaryStage,loader,inputTabPane);
			InputFromDataBaseUI inputFromDatabase = new InputFromDataBaseUI(inputVbox, menuDataBase, primaryStage, loader, inputTabPane);
			Tab func = new Tab("    func    ");
			func.closableProperty().set(false);
			Menu menu1 = new Menu("1.临床路径模型挖掘");
			MenuItem menu11 = new MenuItem("LDA+PM");
			MenuItem menu12 = new MenuItem("SS-LDA+PM");
			MenuItem menu13 = new MenuItem("SSS-LDA+PM+SC");
			menu1.getItems().addAll(menu11, menu12, menu13);
			Menu menu2 = new Menu("2.异常诊疗过程发现");
			MenuItem menuwei1 = new MenuItem("基于临床数据挖掘的医疗过程异常发现方法研究与应用");
			menu2.getItems().add(menuwei1);
			Menu menu3 = new Menu("3.本地化临床路径模型设计");
			MenuItem menu31 = new MenuItem("CPMRM");
			menu3.getItems().addAll(menu31);
			Menu menu4 = new Menu("4.合规性度量");
			Menu menu5 = new Menu("5.后续路径推荐");
			Menu menu6 = new Menu("6.诊疗过程比较");
			Menu menu7 = new Menu("7.临床路径模型分析");
			final MenuBar funcMenuBar = new MenuBar();
			funcMenuBar.getMenus().addAll(menu1, menu2, menu3, menu4, menu5, menu6, menu7);
			TabPane funcTabPane = new TabPane();
			VBox funVbox = new VBox();
			funVbox.setSpacing(5);
			funVbox.setPadding(new Insets(5, 0, 5, 0));
			func.setContent(funVbox);

			funcTabPane.setPrefSize(1000, 1000);
			funcTabPane.autosize();
			funVbox.getChildren().add(funcMenuBar);
			funVbox.getChildren().add(funcTabPane);
			FuncOutlieDetectionUI funcOutlieDetectionUI = new FuncOutlieDetectionUI(primaryStage,menuwei1,funcTabPane);
			CPMRMUI cpmrmUI = new CPMRMUI(primaryStage,  menu31, tabPane);

			Tab output = new Tab("    output    ");
			output.closableProperty().set(false);

			tabPane.getTabs().addAll(input, func);
			final Scene scene = new Scene(vbox);
			vbox.getChildren().addAll(tabPane);
			primaryStage.setMaximized(true);
			primaryStage.setTitle("THUCP");
			primaryStage.setScene(scene);
			primaryStage.show();

			// final Label outputLabel = new Label();
			// final MenuBar menuBar = new MenuBar();
			//
			// // Options->Submenu 1 submenu
			// MenuItem menu11 = new MenuItem("LDA+PM");
			// MenuItem menu12 = new MenuItem("SS-LDA+PM");
			// MenuItem menu13 = new MenuItem("SSS-LDA+PM+SC");
			//
			// MenuItem menuwei1 = new MenuItem("LDA+CLUSTER+PM+REPLAY");
			//
			// // Options menu
			// Menu menu1 = new Menu("1.临床路径模型挖掘");
			// menu1.getItems().addAll(menu11, menu12, menu13);
			//
			// Menu menu2 = new Menu("2.异常诊疗过程发现");
			// menu2.getItems().add(menuwei1);
			// Menu menu3 = new Menu("3.本地化临床路径模型设计");
			// Menu menu4 = new Menu("4.合规性度量");
			// Menu menu5 = new Menu("5.后续路径推荐");
			// Menu menu6 = new Menu("6.诊疗过程比较");
			// Menu menu7 = new Menu("7.临床路径模型分析");
			//
			// menuBar.getMenus().addAll(menu1, menu2, menu3, menu4, menu5,
			// menu6, menu7);
			//
			// ToolBar toolbar = new ToolBar();
			//
			// Label stateLabel = new Label("");
			//
			// vbox.getChildren().addAll(menuBar);
			// vbox.getChildren().addAll(toolbar);
			// vbox.getChildren().addAll(tabPane);
			// vbox.getChildren().addAll(stateLabel);
			//
			// VBox.setVgrow(tabPane, Priority.ALWAYS);
			//
			// final Scene scene = new Scene(vbox);
			//
			// TCPMUI tcpmUI = new TCPMUI(primaryStage, scene, menu11, toolbar,
			// tabPane, stateLabel);
			// OutlierDetectionUI outlierDetectionUI = new
			// OutlierDetectionUI(primaryStage, scene, menuwei1, toolbar,
			// tabPane, stateLabel);
			//
			// primaryStage.setScene(scene);
			// primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
