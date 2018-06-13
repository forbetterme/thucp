package view;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import test.Student;

public class InputTableController implements Initializable {
	TabPane inputTabPane;
	@FXML
	private TableView<InputData> inputTable;
	@FXML
	private TableColumn<InputData, String> key, opDelete, opLook;

	public ObservableList<InputData> getInputDataSet() {
		ObservableList<InputData> list = FXCollections.observableArrayList();
		for (Map.Entry<String, InputData> entry : FrameworkMain.inputDataSet.entrySet()) {
			list.add(entry.getValue());
		}
		return list;
	}

	public void showInputTable(ObservableList<InputData> list) {
		key.setCellValueFactory(new PropertyValueFactory<>("key"));
		opDelete.setCellFactory((col) -> {
			TableCell<InputData, String> cell = new TableCell<InputData, String>() {

				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);
					this.setGraphic(null);

					if (!empty) {
						// ImageView delICON = new
						// ImageView(getClass().getResource("delete.png").toString());
						Button delBtn = new Button("删除");
						this.setGraphic(delBtn);
						delBtn.setOnMouseClicked((me) -> {
							InputData clickedInputData = this.getTableView().getItems().get(this.getIndex());
							this.getTableView().getItems().remove(this.getIndex());
							FrameworkMain.inputDataSet.remove(clickedInputData.key);
							// System.out.println("删除 " +
							// clickedStu.getFirstName() +
							// clickedStu.getLastName() + " 的记录");
						});
					}
				}

			};
			return cell;
		});
		opLook.setCellFactory((col) -> {
			TableCell<InputData, String> cell = new TableCell<InputData, String>() {

				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);
					this.setGraphic(null);

					if (!empty) {
						// ImageView delICON = new
						// ImageView(getClass().getResource("delete.png").toString());
						Button delBtn = new Button("查看");
						this.setGraphic(delBtn);
						delBtn.setOnMouseClicked((me) -> {
							InputData clickedInputData = this.getTableView().getItems().get(this.getIndex());
							// this.getTableView().getItems().remove(this.getIndex());
							// NewFramwork.inputDataSet.remove(clickedInputData.key);
							// System.out.println("删除 " +
							// clickedStu.getFirstName() +
							// clickedStu.getLastName() + " 的记录");
							InputDataStatics ins = new InputDataStatics();
							LineChart<Number, Number> sn=ins.getStayDistribution(clickedInputData.getKey());
							Text t = ins.computeStatics(clickedInputData.getKey());
							ScrollPane spText = new ScrollPane();
							spText.setContent(t);
							
							VBox vb = new VBox();
//							grid.setHgap(10);
//							grid.setVgap(10);
//							grid.setPadding(new Insets(20, 150, 10, 10));
//							grid.setPrefSize(900, 600);
							vb.getChildren().add(spText);
							vb.getChildren().add(sn);
							
							
							
							Tab inputsSatics = null;
							for (int i = 0; i < inputTabPane.getTabs().size(); i++) {
								String title = inputTabPane.getTabs().get(i).getText();
								if (title.equals("统计结果-"+clickedInputData.getKey())) {
									inputsSatics = inputTabPane.getTabs().get(i);
									inputsSatics.setContent(vb);
									inputTabPane.getSelectionModel().select(inputsSatics);
								}
							}
							if (inputsSatics == null) {
								inputsSatics = new Tab("统计结果-"+clickedInputData.getKey());
								inputsSatics.setContent(vb);
								inputTabPane.getTabs().add(inputsSatics);
								inputTabPane.getSelectionModel().select(inputsSatics);
							}
							
							TableView<ObservableList<String>> tv=ins.getDetails(clickedInputData.getKey());
							Tab inputsDetais = null;
							for (int i = 0; i < inputTabPane.getTabs().size(); i++) {
								String title = inputTabPane.getTabs().get(i).getText();
								if (title.equals("数据详情-"+clickedInputData.getKey())) {
									inputsDetais = inputTabPane.getTabs().get(i);
									inputsDetais.setContent(tv);
									inputTabPane.getSelectionModel().select(inputsDetais);
								}
							}
							if (inputsDetais == null) {
								inputsDetais = new Tab("数据详情-"+clickedInputData.getKey());
								inputsDetais.setContent(tv);
								inputTabPane.getTabs().add(inputsDetais);
								inputTabPane.getSelectionModel().select(inputsDetais);
							}
							
							
						});
					}
				}

			};
			return cell;
		});
		inputTable.setItems(list);
	}

	public TabPane getInputTabPane() {
		return inputTabPane;
	}

	public void setInputTabPane(TabPane inputTabPane) {
		this.inputTabPane = inputTabPane;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.showInputTable(getInputDataSet());
	}
}
