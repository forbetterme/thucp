package view;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.sun.org.apache.bcel.internal.generic.NEW;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import test.Student;

public class FuncParamInputDataSetController implements Initializable {
	@FXML
    private TableView<InputData> funcParamInputTable;
	@FXML
    private TableColumn<InputData, String> key;
	@FXML
    private TableColumn<InputData, Boolean> isSelected;

	public ObservableList<InputData> getInputDataSet() {
		ObservableList<InputData> list = FXCollections.observableArrayList();
		for (Map.Entry<String, InputData> entry : FrameworkMain.inputDataSet.entrySet()) {
			list.add(entry.getValue());
		}
		return list;
	}

	public void showFunParamInputTable(ObservableList<InputData> list) {
		key.setCellValueFactory(new PropertyValueFactory<>("key"));
		isSelected.setCellFactory((col) -> {
            TableCell<InputData, Boolean> cell = new TableCell<InputData, Boolean>() {

                @Override
                public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    this.setGraphic(null);

                    if (!empty) {
                        CheckBox checkBox = new CheckBox();
                        this.setGraphic(checkBox);
                        checkBox.selectedProperty().addListener((obVal, oldVal, newVal) -> {
                            if (newVal) {
                            	InputData clickedInputData = this.getTableView().getItems().get(this.getIndex());
                            	FrameworkMain.paramInputDataSetOfFunc.put(clickedInputData.key, clickedInputData);
                            }else{
                            	InputData clickedInputData = this.getTableView().getItems().get(this.getIndex());
                            	FrameworkMain.paramInputDataSetOfFunc.remove(clickedInputData.key);
                            }

                        });
                    }
                }

            };
            return cell;
        });
		funcParamInputTable.setItems(list);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.showFunParamInputTable(getInputDataSet());
	}

}
