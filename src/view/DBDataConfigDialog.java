package view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.netlib.util.intW;

import ca.pfv.spmf.algorithms.classifiers.decisiontree.id3.Node;
import common.BaseUnit;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.CPMRM.DBUtil;

public class DBDataConfigDialog {

	static List<String> prop = new ArrayList<String>();
	static{
		prop.add("病人编码");
		prop.add("医嘱名称");
		prop.add("医嘱类型");
		prop.add("总量");
		prop.add("总价");
		prop.add("日期");
		prop.add("疾病编码");
		prop.add("医院编码");
		prop.add("部门编码");
		prop.add("医生编码");
	}

	static Map<String, String> prop_NameMap =  new HashMap<String,String>();
	static{
		prop_NameMap.put("病人编码", "visitId");
		prop_NameMap.put("医嘱名称", "event");
		prop_NameMap.put("医嘱类型", "eventClass");
		prop_NameMap.put("总量", "num");
		prop_NameMap.put("总价", "price");
		prop_NameMap.put("日期", "time");
		prop_NameMap.put("疾病编码", "diagnoseId");
		prop_NameMap.put("医院编码", "hospitalId");
		prop_NameMap.put("部门编码", "departmentId");
		prop_NameMap.put("医生编码", "doctorId");
	}
	 int curRows = 1;
	 String outputFileName = "";
	 InputData inputData;

	public String getOutPutFileName(){
		return this.outputFileName;
	}
	public void display(Connection connection,String title , String message, String databaseType, String userName){
	    Stage window = new Stage();
	    window.setTitle(title);
	    //modality要使用Modality.APPLICATION_MODEL
	    window.initModality(Modality.APPLICATION_MODAL);
	    window.setMinWidth(500);
	    window.setMinHeight(500);

        VBox verticalBox = new VBox();
	    GridPane grid = new GridPane();

	    grid.setPadding(new Insets(10, 10, 10, 10));
	    grid.setGridLinesVisible(true);

	    Text category = new Text("\t标准输入数据属性\t");
	    category.setFont(Font.font("黑体", FontWeight.BOLD, 15));
	    grid.add(category, 0, 0);


	    Text chartTitle = new Text("\t数据库表及对应列选择\t");
	    chartTitle.setFont(Font.font("黑体", FontWeight.BOLD, 15));
	    grid.add(chartTitle, 1, 0);
	    BorderPane bp = new BorderPane();
	    //查询数据库表和列
	    try {
	      DatabaseMetaData dbmd;
		  dbmd = connection.getMetaData();
		  System.out.println("2");
		  String schema = "%";
		  if (databaseType.equals("Oracle")) {
			  schema = userName.toUpperCase();
		  }
		  ResultSet rs = dbmd.getTables(connection.getCatalog(), schema, null, new String[] { "TABLE"});// 2,null - "%"
		  System.out.println("3");
		  List<String> tableNameList = new ArrayList<String>();
		  tableNameList.add("-");
		  while (rs.next()) {
		     tableNameList.add(rs.getString("TABLE_NAME"));
		   }
		  ObservableList<String>  tablesList = FXCollections.observableArrayList(tableNameList);
		  System.out.println("4");
		  ResultSet rs1 = null;
		  HashMap<String,ObservableList<String>> chooseColumn = new HashMap<String,ObservableList<String>>();
		  ArrayList<String> columnNameList = new ArrayList<String>();
		  columnNameList.add(" ");
		  chooseColumn.put("-", FXCollections.observableArrayList(columnNameList));
		  for (String str : tableNameList) {
			    rs1 = dbmd.getColumns(null, schema, str,null);
			    columnNameList = new ArrayList<String>();
				while (rs1.next()) {
				   columnNameList.add(rs1.getString("COLUMN_NAME"));
				}
				chooseColumn.put(str, FXCollections.observableArrayList(columnNameList));
	       }
		  System.out.println("5");
		  ArrayList<CorrespondChoose> correspondChoosesList = new ArrayList<>();
		  for (int i = 0; i < prop.size(); i++) {
		    	Text row = new Text("\t"+prop.get(i)+"\t");
		    	// category.setFont(Font.font("黑体", FontWeight.NORMAL, 13));
		 	    grid.add(row, 0, i+1);

		 	    CorrespondChoose choose = new CorrespondChoose(tablesList,chooseColumn);
		 	    correspondChoosesList.add(choose);
		 	    grid.add(choose.hBox, 1, i+1);

			}
		   verticalBox.getChildren().add(grid);

		    //添加条件选择区域
		   GridPane whereGrid = new GridPane();
		   whereGrid.setPadding(new Insets(10, 10, 10, 10));
//		   whereGrid.setGridLinesVisible(true);

		   Text title1 = new Text("\t选择连接条件\t");
		   title1.setFont(Font.font("黑体", FontWeight.BOLD, 15));
		   whereGrid.add(title1, 2, 0);

		   List<String> newTableNameList = new ArrayList<String>();
		   for(String str:tableNameList) {
			   if(str.equals("-")) continue;
			   newTableNameList.add(str);
		   }
		   ObservableList<String>  newTablesList = FXCollections.observableArrayList(newTableNameList);
		   ArrayList<ConditionChoose> conditionChoosesList = new ArrayList<ConditionChoose>();
		   Button insertConditionButton = new Button("添加条件");
		   insertConditionButton.setOnAction(new EventHandler<ActionEvent>() {
	    		public void handle(ActionEvent event) {
	    			ConditionChoose condChoose = new ConditionChoose(newTablesList, chooseColumn);
	    			whereGrid.add(condChoose.hBox,0,curRows,3,1);
	    			conditionChoosesList.add(condChoose);
	    			Button deleteConditionButton = new Button("删除");
	    			deleteConditionButton.setOnAction(new EventHandler<ActionEvent>() {
	    				public void handle(ActionEvent event) {
	    					int a = whereGrid.getChildren().indexOf(deleteConditionButton);
	    					int b = whereGrid.getChildren().indexOf(condChoose.hBox);
	    					whereGrid.getChildren().remove(a);
	    					whereGrid.getChildren().remove(b);
	    					conditionChoosesList.remove((b-2)/2);
	    					//curRows--;
	    				}
	    			});
	    			whereGrid.add(deleteConditionButton,3,curRows);
	    			curRows++;
	    		}
	    	});

		   whereGrid.add(insertConditionButton, 0, 0);

		   GridPane centerGrid = new GridPane();
		   centerGrid.setVgap(10);

		   centerGrid.add(grid, 0, 0);
		   centerGrid.add(whereGrid, 0, 1);

		    bp.setPadding(new Insets(10, 20, 10, 20));
	        bp.setCenter(centerGrid);

	        GridPane bottomGrid = new GridPane();
	        bottomGrid.setHgap(10);

	        Label inputFileLabel = new Label("导入数据名称");
	        TextField inputFileField = new TextField();
	        bottomGrid.add(inputFileLabel,0,0);
	        bottomGrid.add(inputFileField,1,0);

	        Button okButton = new Button("导入数据");
	        bottomGrid.add(okButton,3,0);
	        okButton.setOnAction(new EventHandler<ActionEvent>() {
	    		public void handle(ActionEvent event) {

	    			if (inputFileField.getText().isEmpty()) {
	    				Alert alert = new Alert(AlertType.ERROR);
                		alert.setTitle("错误");
                		alert.setContentText("请输入数据名称");
                		alert.showAndWait();
					}
	    			else {

	    			HashSet<String> selectTableSet = new HashSet<String>();
	    			StringBuffer selectString = new StringBuffer();
	    			int selectCount = 0;
	    			String tName = new String();
	    			String cName = new String();
	    			for (int i = 0; i < correspondChoosesList.size(); i++) {
	    				tName = correspondChoosesList.get(i).tableCombo.getValue();
	    				cName = correspondChoosesList.get(i).columnCombo.getValue();
	    				if(tName.equals("-")) continue;
	    				else {
		    				selectTableSet.add(tName);
		    				if(selectCount == 0)
		    					selectString.append(tName+"."+cName);
		    				else
		    					selectString.append(","+tName+"."+cName);
		    				selectCount++;
	    				}
					}
	    			StringBuffer fromString = new StringBuffer();
	    			int j = 0;
	    			for(Iterator it=selectTableSet.iterator();it.hasNext();++j)
	    			{

	    			  if(j == 0)
	    			      fromString.append(it.next());
	    			  else
	    				  fromString.append(","+it.next());
	    			}

                    //判断若》两个表，需有连接条件
                	if(conditionChoosesList.size()==0 && selectTableSet.size()>=2)
                	{
                		Alert alert = new Alert(AlertType.WARNING);
                		alert.setTitle("警告");
                		alert.setContentText("请填写连接条件，否则导入数据可能出现错误！");

                		alert.showAndWait();
                        return;
                	}

                    StringBuffer whereString = new StringBuffer();
                    j = 0;
                    for (int i = 0; i < conditionChoosesList.size(); i++) {
                      if(j == 0)
                    	  whereString.append(conditionChoosesList.get(i).getCondition());
   	    			  else
   	    				whereString.append(" and "+conditionChoosesList.get(i).getCondition());
                      ++j;
					}

                   //拼出SQL
	    			String sql;
	    			if (whereString.length() > 0) {
	    				sql = "select "+selectString.toString()+" from "+fromString.toString()+" where "+whereString.toString();    //要执行的SQL
					}
	    			else {
	    				sql = "select "+selectString.toString()+" from "+fromString.toString();    //要执行的SQL
					}
	    			System.out.println(sql);
	    			Statement stmt;
//	    			for (String p:prop) {
//	                    System.out.print(p);
//					}
					try {
						/*
						 * 结果写入文件
						 *
						 */
//						String fileAddress = "./data/tempInput/";
//						String filename;
//						if (inputFileField.getText().isEmpty()) {
//							File folder = new File("./data/tempInput");
//							File[] list = folder.listFiles();
//							filename = fileAddress+"file"+list.length+".txt";
//						} else {
//							filename = fileAddress+inputFileField.getText()+".txt";
//						}
//
//                      File outFile = new File(filename);
//                      outputFileName = filename;
//                		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
//						stmt = connection.createStatement();
//	                    ResultSet rs = stmt.executeQuery(sql);//创建数据对象
//	                    ArrayList<Integer> selectedColNumber = new ArrayList<Integer>();//记录选择了第几列属性
//	                    StringBuffer s = new StringBuffer();
//	                    for (int i = 0; i < correspondChoosesList.size(); i++) {
//	                    	if (!correspondChoosesList.get(i).tableCombo.getValue().equals("-")) {
//	                    		selectedColNumber.add(i);
//	                    		if(selectedColNumber.size() == 1){
//	                    			s.append(prop_NameMap.get(prop.get(i)));
//	                    		}
//	                    		else {
//	                    			s.append(",");
//	                    			s.append(prop_NameMap.get(prop.get(i)));
//								}
//	                    	}
//	                    }
//	                    writer.write(s.toString());
//                		writer.newLine();
//	                    while (rs.next()){//1-prop.sieze()
////	                        System.out.print(rs.getString(1) + "\t");
////	                        System.out.print(rs.getString(2) + "\t");
////	                        System.out.print(rs.getString(3) + "\t");
////	                        System.out.print(rs.getString(4) + "\t");
////	                        System.out.println();
//
//	                        StringBuffer seq = new StringBuffer(rs.getString(1));
//	                        for(j = 2; j <= selectedColNumber.size(); j++) {
//	                				seq.append(",");
//	                				seq.append(rs.getString(j));
//	                		}
//	                		writer.write(seq.toString());
//	                		writer.newLine();
//	                    }
//	                    writer.close();
						inputData = new InputData();
						FrameworkMain.indexInputData++;
						String inputName = "temp";
						if (!inputFileField.getText().isEmpty()) {
							inputName = inputFileField.getText();
						}
						inputData.setKey(inputName);
						stmt = connection.createStatement();
	                    ResultSet rs = stmt.executeQuery(sql);//创建数据对象
	                    ArrayList<Integer> selectedColNumber = new ArrayList<Integer>();//记录选择了第几列属性
	                    Map<String, Integer> columName2index = new HashMap<String, Integer>();
	                    StringBuffer s = new StringBuffer();
	                    int index = 1;
	                    for (int i = 0; i < correspondChoosesList.size(); i++) {
	                    	if (!correspondChoosesList.get(i).tableCombo.getValue().equals("-")) {
	                    		selectedColNumber.add(i);
	                    		columName2index.put(prop_NameMap.get(prop.get(i)), index++);
	                    	}
	                    }
	                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	                    while (rs.next()){//1-prop.sieze()
	                    	InputDataRowType tempRow = new InputDataRowType();
                        	if (columName2index.containsKey("visitId")){
    							System.out.println(rs.getString(columName2index.get("visitId")));
    							tempRow.setVisitId(rs.getString(columName2index.get("visitId")));
    						}

    						if (columName2index.containsKey("event"))
    							tempRow.setEvent(rs.getString(columName2index.get("event")));

    						if (columName2index.containsKey("eventClass"))
    							tempRow.setEventClass(rs.getString(columName2index.get("eventClass")));

    						if (columName2index.containsKey("num"))
    							tempRow.setNum(Double.parseDouble(rs.getString(columName2index.get("num"))));

    						if (columName2index.containsKey("price"))
    							tempRow.setPrice(Double.parseDouble(rs.getString(columName2index.get("price"))));

    						if (columName2index.containsKey("time"))
    							tempRow.setTime(df.parse(rs.getString(columName2index.get("time"))));

    						if (columName2index.containsKey("diagnoseId"))
    							tempRow.setDiagnoseId(rs.getString(columName2index.get("diagnoseId")));

    						if (columName2index.containsKey("hospitalId"))
    							tempRow.setHospitalId(rs.getString(columName2index.get("hospitalId")));

    						if (columName2index.containsKey("departmentId"))
    							tempRow.setDepartmentId(rs.getString(columName2index.get("departmentId")));

    						if (columName2index.containsKey("doctorId"))
    							tempRow.setDoctorId(rs.getString(columName2index.get("doctorId")));
    						inputData.dataForLog.add(tempRow);
	                    }
	                    inputData.type="log";
						FrameworkMain.inputDataSet.put(inputName, inputData);

						DBUtil.closeConnection(rs, stmt, connection);
//						DBUtil.closeResultSet(rs);
//						DBUtil.closeStatement(stmt);

	                    window.close();
					}catch (SQLException e) {
						// TODO Auto-generated catch block

						Alert alert = new Alert(AlertType.ERROR);
                		alert.setTitle("错误");
                		alert.setContentText("请对所有选择框进行选择");
                		alert.showAndWait();

						e.printStackTrace();
					}
					catch (ParseException e) {
						// TODO Auto-generated catch block

						Alert alert = new Alert(AlertType.ERROR);
                		alert.setTitle("错误");
                		alert.setContentText("输入的时间格式有误");
                		alert.showAndWait();

						e.printStackTrace();
					}
//					catch (IOException e) {
//						// TODO Auto-generated catch block
//
//						Alert alert = new Alert(AlertType.ERROR);
//                		alert.setTitle("错误");
//                		alert.setContentText("生成文件出错");
//                		alert.showAndWait();
//
//						e.printStackTrace();
//					}

	    			}
	    		}
	    	});

	        bp.setBottom(bottomGrid);

	    }
	    catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    Scene scene = new Scene(bp);
	    window.setScene(scene);
	    //使用showAndWait()先处理这个窗口，而如果不处理，main中的那个窗口不能响应
	    window.showAndWait();
//	    DBUtil.closeConnection(connection);
	}

}
class CorrespondChoose {

	 HBox hBox = new HBox();
	 ComboBox<String> tableCombo;
	 ComboBox<String> columnCombo;

	 public CorrespondChoose( ObservableList<String> tables, HashMap<String,ObservableList<String>> columns)
	 {

		 tableCombo = new ComboBox<String>(tables);
		 columnCombo = new  ComboBox<String>();
		 tableCombo.setOnAction(ev -> {
	         columnCombo.setItems(columns.get(tableCombo.getValue()));
	        });
		 if (tables.contains("-")) {
			 tableCombo.setValue("-");
		 }

		 hBox.getChildren().addAll(tableCombo,columnCombo);
	 }
}

class ConditionChoose {

	 HBox hBox = new HBox();
	 CorrespondChoose left;
	 CorrespondChoose right;

	 public ConditionChoose( ObservableList<String> tables, HashMap<String,ObservableList<String>> columns)
	 {
		 left = new CorrespondChoose(tables,columns);
		right = new CorrespondChoose(tables,columns);
	    Text condition = new Text("\t=\t");
		hBox.getChildren().addAll(left.hBox,condition,right.hBox);

	 }
	 public String getCondition(){
		return left.tableCombo.getValue()+"."+left.columnCombo.getValue()+"="+ right.tableCombo.getValue()+"."+right.columnCombo.getValue();
	 }
}

