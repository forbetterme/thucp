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

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.DirectoryChooserBuilder;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.CPMRM.ImportCPUtil;
import model.CPMRM.StandardCPStage;
import test.TableController;

public class InputFromFileUI {
	VBox inputVbox;
	MenuItem menuFile;
	Stage stage;
	FXMLLoader loader;
	TabPane inputTabPane;
	String dataType;

	public InputFromFileUI(VBox inputVbox, MenuItem menuFile, Stage stage, FXMLLoader loader,TabPane inputTabPane) {
		this.inputVbox = inputVbox;
		this.menuFile = menuFile;
		this.stage = stage;
		this.loader = loader;
		this.inputTabPane = inputTabPane;
		setActions();
	}

	public void setActions() {
		SetMenuFileAction();
	}

	public void SetMenuFileAction() {
		menuFile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				TextInputDialog dialog = new TextInputDialog("");
				dialog.setTitle("选择文件类型");
				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);
				grid.setPadding(new Insets(20, 150, 10, 10));

				final ToggleGroup group = new ToggleGroup();
				RadioButton rb1 = new RadioButton("国家标准临床路径");
				rb1.setToggleGroup(group);
				rb1.setUserData("国家标准临床路径");
				RadioButton rb2 = new RadioButton("日志文件");
				rb2.setToggleGroup(group);
				rb2.setUserData("日志文件");
				rb2.setSelected(true);
				dataType = "日志文件";
				group.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
				    public void changed(ObservableValue<? extends Toggle> ov,Toggle old_toggle, Toggle new_toggle) {
				            if(group.getSelectedToggle() != null) {
				            	dataType = group.getSelectedToggle().getUserData().toString();
				            }
				        }
				});

				grid.add(rb1, 0, 0);
				grid.add(rb2, 1, 0);

				dialog.getDialogPane().setContent(grid);
//				Platform.runLater(() -> KofKmeans.requestFocus());
				dialog.showAndWait();
				if (dataType.equals("国家标准临床路径")) {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("选择国家标准临床路径");
					File file = fileChooser.showOpenDialog(stage);
					// System.out.println(file.getAbsolutePath());

						InputData fileData = new InputData();
						FrameworkMain.indexInputData++;
						String key = FrameworkMain.indexInputData.toString() + "-" + file.getAbsolutePath();
						fileData.setKey(key);
						ImportCPUtil imcp = new ImportCPUtil();
						StandardCPStage[] CPS;
						try {
							CPS = imcp.readJsonInput(file.getAbsolutePath());
							fileData.setDataForCP(CPS);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						fileData.setType("stdClinicalPathway");
						FrameworkMain.inputDataSet.put(key, fileData);
						InputTableController temp = loader.getController();
						temp.setInputTabPane(inputTabPane);
						temp.showInputTable(temp.getInputDataSet());

				} else if(dataType.equals("日志文件")) {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("选择文件");
					File file = fileChooser.showOpenDialog(stage);
					// System.out.println(file.getAbsolutePath());
					try {
						if(file == null) return;
						InputData fileData = new InputData();
						FrameworkMain.indexInputData++;
						String key = FrameworkMain.indexInputData.toString() + "-" + file.getAbsolutePath();
						fileData.setKey(key);
						BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
						String line = br.readLine();
						String[] lines = line.split(",");
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						Map<String, Integer> columName2index = new HashMap<String, Integer>();
						for (int i = 0; i < lines.length; i++){
							columName2index.put(lines[i], i);
						}
//						for(Map.Entry<String, Integer> entry:columName2index.entrySet()){
//							System.out.println(entry.getKey()+":"+entry.getValue());
//						}
						while ((line = br.readLine()) != null) {
							lines = line.split(",");
							InputDataRowType tempRow = new InputDataRowType();
							if (columName2index.containsKey("visitId")){
//								System.out.println(lines[columName2index.get("visitId")]);
								tempRow.setVisitId(lines[columName2index.get("visitId")]);
							}

							if (columName2index.containsKey("event"))
								tempRow.setEvent(lines[columName2index.get("event")]);

							if (columName2index.containsKey("eventClass"))
								tempRow.setEventClass(lines[columName2index.get("eventClass")]);

							if (columName2index.containsKey("num"))
								tempRow.setNum(Double.parseDouble(lines[columName2index.get("num")]));

							if (columName2index.containsKey("price"))
								tempRow.setPrice(Double.parseDouble(lines[columName2index.get("price")]));

							if (columName2index.containsKey("time"))
								tempRow.setTime(df.parse(lines[columName2index.get("time")]));

							if (columName2index.containsKey("diagnoseId"))
								tempRow.setDiagnoseId(lines[columName2index.get("diagnoseId")]);

							if (columName2index.containsKey("hospitalId"))
								tempRow.setHospitalId(lines[columName2index.get("hospitalId")]);

							if (columName2index.containsKey("departmentId"))
								tempRow.setDepartmentId(lines[columName2index.get("departmentId")]);

							if (columName2index.containsKey("doctorId"))
								tempRow.setDoctorId(lines[columName2index.get("doctorId")]);

							fileData.dataForLog.add(tempRow);
						}
						fileData.setType("log");
						FrameworkMain.inputDataSet.put(key, fileData);
						InputTableController temp = loader.getController();
						temp.setInputTabPane(inputTabPane);
						temp.showInputTable(temp.getInputDataSet());

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		});
	}
}
