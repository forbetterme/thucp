package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.processmining.log.csvimport.exception.CSVConversionException;

import java.util.Map.Entry;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.DirectoryChooserBuilder;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import outlier.MalletLDA;
import outlier.cluster.GetLabelForCluster;
import outlier.cluster.KmeansPlusPlus;
import outlier.cluster.Point;
import outlier.cluster.SelectK;
import outlier.cluster.TestELKIDef;
import outlier.dataclean.DataClean;
import outlier.launch.AlignLaunch;
import outlier.launch.IMLaunch;
import outlier.topic.GetTopicBasedOnLDA;
import outlier.util.FilesOfFolderDelete;
import outlier.util.Param;
import test.TestVisualAlign;

public class FuncOutlieDetectionUI {
	Stage stage;
	MenuItem menuAlign;
	MenuItem menuDataClean;
	MenuItem menuLDA;
	MenuItem menuKMeans;
	TabPane funcTabPane;

	String workSpace;

	ArrayList<String[]> topicId2ItemsArray;
	ArrayList<String[]> doc2Topic;

	int KLDA = 13;
	int NumLDA = 13;
	int KKmeans;
	int KmeansRoundTimes;

	int KLDAStart;
	int KLDAEnd;
	int round;
	int iterationsLDA;

	int KKmeansStart;
	int KKmeansEnd;
	int KmeansKOfLDA;
	int KmeansNumOfLDA;

	public FuncOutlieDetectionUI(Stage stage, MenuItem menuDataClean, MenuItem menuLDA, MenuItem menuKMeans,
			MenuItem menuAlign, TabPane funcTabPane) {
		this.stage = stage;
		this.menuAlign = menuAlign;
		this.menuDataClean = menuDataClean;
		this.menuLDA = menuLDA;
		this.funcTabPane = funcTabPane;
		this.menuKMeans = menuKMeans;
		setActions();
	}

	public FuncOutlieDetectionUI() {

	}

	public void setActions() {
		SetMenuOutlierDetectionAction();
	}

	public void SetMenuOutlierDetectionAction() {
		menuAlign.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					setParams();
					// generateCorpusForLDA("data/OutlierDetection/corpus/");
//					runLDA("data/OutlierDetection/corpus/");
//					runKmeansLuPing();
					// runKmeansPlusPlus();
					// runAlign("data/OutlierDetection/cluster/paper_log.csv");
					// runAlign("data/OutlierDetection/cluster/paper_log -
					// 副本.csv");
					runAlign(workSpace+"Kmeans/log/kmeans-"+KKmeans+".csv");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		menuDataClean.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					setParamsDataClean();
					new FuncParamInputDataSetDialog().setParamInputDataSet();
					DataClean dc = new DataClean(workSpace);
					dc.generateCorpusForLDA();
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.titleProperty().set("信息");
					alert.headerTextProperty().set("预处理完成！");
					alert.showAndWait();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		menuLDA.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					setParamsLDA();
					runMalletLDA();
					disPlayKChart(workSpace + "LDA\\evaluate.csv","K值","困惑度","困惑度随K值的变化曲线");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		menuKMeans.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					setParamsKmeans();
					runKmeans();
					disPlayKChart(workSpace + "Kmeans\\cluster\\evaluate.csv","K值","误差平方和","误差平方和随K值的变化曲线");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void main(String[] args) throws Exception {
		FuncOutlieDetectionUI ins = new FuncOutlieDetectionUI();
		ins.KLDA = 13;
		ins.runLDA("data/OutlierDetection/corpus/");
	}

	public void setParamsDataClean() {
		DirectoryChooser fileChooser = new DirectoryChooser();
		fileChooser.setTitle("选择工作空间");
		fileChooser.setInitialDirectory(new File(Param.homePath));
		File file = fileChooser.showDialog(stage);
		workSpace = file.getAbsolutePath() + "/";
		System.out.println(workSpace);
	}

	public void setParamsLDA() {
		DirectoryChooser fileChooser = new DirectoryChooser();
		fileChooser.setTitle("选择工作空间");
		fileChooser.setInitialDirectory(new File(Param.homePath));
		File file = fileChooser.showDialog(stage);
		workSpace = file.getAbsolutePath() + "/";
		System.out.println(workSpace);

		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("设置参数");
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField startKofLDA = new TextField("3");
		// KTF.setPromptText("K");
		startKofLDA.setPrefColumnCount(5);
		grid.add(new Label("LDA的K值起点: "), 0, 0);
		grid.add(startKofLDA, 1, 0);

		TextField endKofLDA = new TextField("50");
		endKofLDA.setPrefColumnCount(5);
		grid.add(new Label("LDA的K值终点: "), 0, 1);
		grid.add(endKofLDA, 1, 1);
		
		TextField iterationsTimesParam = new TextField("2000");
		iterationsTimesParam.setPrefColumnCount(5);
		grid.add(new Label("迭代次数: "), 0, 2);
		grid.add(iterationsTimesParam, 1, 2);
		
		TextField roundTimesParam = new TextField("2");
		roundTimesParam.setPrefColumnCount(5);
		grid.add(new Label("重复次数: "), 0, 3);
		grid.add(roundTimesParam, 1, 3);

		dialog.getDialogPane().setContent(grid);
		Platform.runLater(() -> startKofLDA.requestFocus());
		dialog.showAndWait().ifPresent(response -> {
			KLDAStart = Integer.parseInt(startKofLDA.getText());
			KLDAEnd = Integer.parseInt(endKofLDA.getText());
			iterationsLDA=Integer.parseInt(iterationsTimesParam.getText());
			round = Integer.parseInt(roundTimesParam.getText());
		});
	}

	public void setParamsKmeans() {
		DirectoryChooser fileChooser = new DirectoryChooser();
		fileChooser.setTitle("选择工作空间");
		fileChooser.setInitialDirectory(new File(Param.homePath));
		File file = fileChooser.showDialog(stage);
		workSpace = file.getAbsolutePath() + "/";
		System.out.println(workSpace);

		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("设置参数");
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField KofLDA = new TextField("16");
		KofLDA.setPrefColumnCount(5);
		grid.add(new Label("LDA的K值: "), 0, 0);
		grid.add(KofLDA, 1, 0);

		TextField num = new TextField("16");
		num.setPrefColumnCount(5);
		grid.add(new Label("序号: "), 0, 1);
		grid.add(num, 1, 1);

		TextField startKofKmeans = new TextField("11");
		startKofKmeans.setPrefColumnCount(5);
		grid.add(new Label("K-means的K值起点: "), 0, 2);
		grid.add(startKofKmeans, 1, 2);

		TextField endKofKmeans = new TextField("11");
		endKofKmeans.setPrefColumnCount(5);
		grid.add(new Label("K-means的K值终点: "), 0, 3);
		grid.add(endKofKmeans, 1, 3);
		
		TextField KmeansRoundTimesParam = new TextField("300");
		KmeansRoundTimesParam.setPrefColumnCount(5);
		grid.add(new Label("Kmeans最大迭代次数: "), 0, 4);
		grid.add(KmeansRoundTimesParam, 1, 4);

		dialog.getDialogPane().setContent(grid);
		Platform.runLater(() -> KofLDA.requestFocus());
		dialog.showAndWait().ifPresent(response -> {
			KKmeansStart = Integer.parseInt(startKofKmeans.getText());
			KKmeansEnd = Integer.parseInt(endKofKmeans.getText());
			KmeansKOfLDA = Integer.parseInt(KofLDA.getText());
			KmeansNumOfLDA = Integer.parseInt(num.getText());
			KmeansRoundTimes = Integer.parseInt(KmeansRoundTimesParam.getText());
		});
	}

	public void getInitKPoint4Kmeans(Integer K) throws Exception{
		File file_out = new File(workSpace+"Kmeans\\initKPoint\\"+"init-"+K+"-docId.csv");
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//
		
		String fileIn=workSpace+"LDA/"+"log-map-trans-merge-guiyi.csv";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn),"UTF-8"));
		
		Map<String,TreeMap<String,TreeSet<String>>> p2days=new HashMap<String,TreeMap<String,TreeSet<String>>>();
		TreeMap<Integer,ArrayList<String>> itemSize2dayKey=new TreeMap<Integer,ArrayList<String>>();
		String line="";
		line=reader.readLine();
		while ((line = reader.readLine()) != null) {
			String[] lines=line.split(",");
			String visitId=lines[0];
			String event=lines[1];
			String eventClass=lines[2];
			String num=lines[3];
			String price=lines[4];
			String zj=lines[5];
			String time=lines[6];
			if(p2days.containsKey(visitId)){
				TreeMap<String,TreeSet<String>> cuDay2items=p2days.get(visitId);
				if(cuDay2items.containsKey(time)){
					cuDay2items.get(time).add(event);
				}else{
					TreeSet<String> temp=new TreeSet<String>();
					temp.add(event);
					cuDay2items.put(time, temp);
				}
			}else{
				TreeMap<String,TreeSet<String>> cuDay2items =new TreeMap<String,TreeSet<String>>();
				TreeSet<String> temp=new TreeSet<String>();
				temp.add(event);
				cuDay2items.put(time, temp);
				p2days.put(visitId, cuDay2items);
			}
		}
		reader.close();
		for(Map.Entry<String, TreeMap<String,TreeSet<String>>> entry:p2days.entrySet()){
			String visitId=entry.getKey();
			TreeMap<String,TreeSet<String>> day2items=entry.getValue();
			for(Map.Entry<String, TreeSet<String>> it:day2items.entrySet()){
				String docKey=visitId+"#"+it.getKey();
				Integer size=it.getValue().size();
				if(itemSize2dayKey.containsKey(size)){
					itemSize2dayKey.get(size).add(docKey);
				}else{
					ArrayList<String> temp=new ArrayList<String>();
					temp.add(docKey);
					itemSize2dayKey.put(size, temp);
				}
			}
		}
		Set<String> KTarDocKeys=new HashSet<String>();
		ArrayList<ArrayList<String>> list=new ArrayList<ArrayList<String>>();
		for(Map.Entry<Integer, ArrayList<String>> entry:itemSize2dayKey.entrySet()){
			list.add(entry.getValue());
		}
		int gap=itemSize2dayKey.size()/(K+1);
		if(gap==0){
			while(KTarDocKeys.size()!=K){
				Random random = new Random();
				int rand=random.nextInt(list.size());
				int rand2=random.nextInt(list.get(rand).size());
				KTarDocKeys.add(list.get(rand).get(rand2));
			}
		}else{
			Integer index=gap/2;
			int count=0;
			while(count<K){
				count++;
				KTarDocKeys.add(list.get(index).get(0));
				index+=gap;
			}
		}
		for(String it:KTarDocKeys){
			out.write(it);
			out.newLine();
		}
		out.flush();
		out.close();
	}
	public void runKmeans() throws Exception {
		Param.topicK=KmeansKOfLDA;
		basePath=workSpace;
		for (Integer ii = KmeansNumOfLDA; ii <= KmeansNumOfLDA; ii++) {
			Param.num = ii;
			Double clusterMainTopicTheta = Param.clusterMainTopicTheta;
			Integer topicK = Param.topicK;
			Integer num = Param.num;
			Double epsilon = Param.epsilon;
			String cluster2topicPath = basePath + "Kmeans\\cluster\\cluster2topics.csv";
			String topic2itemsPath = basePath + "LDA\\topic2items-" + topicK + "-" + num + ".csv";
			SelectK ins = new SelectK();

			File file_out_eval = new File(basePath + "Kmeans\\cluster\\evaluate.csv");
			BufferedWriter outEval = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file_out_eval), "UTF-8"));
			// BufferedWriter outEval = new BufferedWriter(new
			// OutputStreamWriter(new FileOutputStream(file_out_eval,true),
			// "UTF-8"));

			// String docToTopicFilePath =
			// basePath+"LDA\\doc2topics-"+topicK+"-elki.csv";
			String docToTopicFilePath = basePath + "LDA\\doc2topics-" + topicK + "-" + Param.num + ".csv";
			// String docToTopicFilePath =
			// basePath+"LDA\\doc2topics-"+topicK+"-setZero-log.csv";
			for (Integer i = KKmeansStart; i <= KKmeansEnd; i++) {
				getInitKPoint4Kmeans(i);
				
				KmeansPlusPlus kmeansClustering = new KmeansPlusPlus(docToTopicFilePath);
				Integer maxIteration = KmeansRoundTimes;
				TestELKIDef elki = new TestELKIDef();
				// Map<Integer, List<Point>> result =
				// elki.ELIKDBSCAN(topicK,epsilon,docToTopicFilePath);
				// Map<Integer, List<Point>> result =
				// elki.ELIKKMedoidsPAM(i,docToTopicFilePath);
				Map<Integer, List<Point>> result = kmeansClustering.kcluster(i, maxIteration,
						basePath + "Kmeans\\initKPoint\\" + "init-" + i + "-docId.csv");
				// Map<Integer, List<Point>> result =
				// kmeansClustering.kcluster(i,maxIteration);
				// System.out.println(i+","+ins.computeSilhouetteCoefficient(result));
				// System.out.println();
				String logPath = basePath + "Kmeans\\log\\kmeans-" + i + ".csv";

				Map<Integer, List<Point>> resultTree = new TreeMap<Integer, List<Point>>();
				for (Entry<Integer, List<Point>> entry : result.entrySet()) {
					resultTree.put(entry.getKey(), entry.getValue());
				}
				result = resultTree;

				File file_out = new File(logPath);
				BufferedWriter bw_out = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//
				bw_out.write("case,event,time");
				bw_out.newLine();
				Set<String> id = new HashSet<String>();

				ins.getPatient2Day2items(result);
				Map<Integer, ArrayList<String>> topic2items = ins.getTopic2items(topic2itemsPath);
				// getLabelForCluster(result, clusterMainTopicTheta,
				// cluster2topicPath,topic2items);
				ins.getLabelForClusterHorizontal(result, clusterMainTopicTheta, cluster2topicPath, topic2items);

				for (Entry<Integer, List<Point>> entry : result.entrySet()) {
					System.out.println("===============聚簇中心为：" + entry.getKey() + "================");
					System.out.println(entry.getValue().size());
					for (Point point : entry.getValue()) {
						String[] strs = point.id.split("#");
						String caseId = strs[0];
						id.add(caseId);
						String riqi = strs[1].split("\\.")[0];
						bw_out.write(caseId + "," + entry.getKey() + "," + riqi);
						bw_out.newLine();

					}
				}
				for (String it : id) {
					bw_out.write(it + "," + "s" + "," + "1900-1-1");
					bw_out.newLine();
					bw_out.write(it + "," + "e" + "," + "2018-1-1");
					bw_out.newLine();
				}
				bw_out.flush();
				bw_out.close();

				outEval.write(i + "," + kmeansClustering.evaluate);
				outEval.newLine();

				// System.out.println(ins.computeSilhouetteCoefficient(result));
				
				dispalyCluster2Topic2Items(workSpace+"Kmeans/cluster/cluster2topics.csv",topicK,num,i);

				System.out.println("kmeans completed!");
				JComponent jc=ins.runIMAndReturn(logPath, i, ii,Param.topicK);
				
				String titleIM=topicK+"-"+num+"-"+i.toString()+":过程模型";
				Tab IMPan = null;
				for (int j = 0; j < funcTabPane.getTabs().size(); j++) {
					String title = funcTabPane.getTabs().get(j).getText();
					if (title.equals(titleIM)) {
						IMPan = funcTabPane.getTabs().get(j);
						final SwingNode swingNodeIM = new SwingNode();
						swingNodeIM.setContent(jc);
						IMPan.setContent(swingNodeIM);
					}
				}
				if (IMPan == null) {
					IMPan = new Tab(titleIM);
					final SwingNode swingNodeIM = new SwingNode();
					swingNodeIM.setContent(jc);
					IMPan.setContent(swingNodeIM);
					funcTabPane.getTabs().add(IMPan);
					funcTabPane.getSelectionModel().select(IMPan);
				}
				
			}
			outEval.flush();
			outEval.close();
		}
	}
	
	public void dispalyCluster2Topic2Items(String path,Integer topicK,Integer num,Integer KKmeans) throws Exception{
		ArrayList<String[]> rows=new ArrayList<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "gb2312"));
		String line = reader.readLine();
		String[] lines = line.split(",");
		String[] tableHead = new String[lines.length];
		for(int i=0;i<lines.length;i++){
			tableHead[i]="";
		}
		rows.add(lines);
		while ((line = reader.readLine()) != null) {
			lines = line.split(",");
			if(lines.length>1)
				rows.add(lines);
		}
		reader.close();
		for (String[] row : rows) {
			for(int j=0;j<row.length;j++){
				if(row[j].equals("")) row[j]="-";
			}
			System.out.println(";"+row.length);
		}
		ObservableList<ObservableList<String>> cluster2ItemsData = FXCollections.observableArrayList();
		for (String[] row : rows) {
			cluster2ItemsData.add(FXCollections.observableArrayList(row));
		}
		TableView<ObservableList<String>> topic2ItemsTV = new TableView<>();
		topic2ItemsTV.setStyle("-fx-alignment: CENTER-LEFT;");
		topic2ItemsTV.setItems(cluster2ItemsData);
		for (int i = 0; i < tableHead.length; i++) {
			System.out.println(i);
			final int curCol = i;
			final TableColumn<ObservableList<String>, String> column = new TableColumn<>(tableHead[curCol]);
			column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(curCol)));
			topic2ItemsTV.getColumns().add(column);
		}

		String titleIM=topicK+"-"+num+"-"+KKmeans.toString();
		Tab cluster2topic = null;
		for (int i = 0; i < funcTabPane.getTabs().size(); i++) {
			String title = funcTabPane.getTabs().get(i).getText();
			if (title.equals(titleIM+":类簇-诊疗主题")) {
				cluster2topic = funcTabPane.getTabs().get(i);
				cluster2topic.setContent(topic2ItemsTV);
				funcTabPane.getSelectionModel().select(cluster2topic);
			}
		}
		if (cluster2topic == null) {
			cluster2topic = new Tab(titleIM+":类簇-诊疗主题");
			cluster2topic.setContent(topic2ItemsTV);
			funcTabPane.getTabs().add(cluster2topic);
			funcTabPane.getSelectionModel().select(cluster2topic);
		}
	}

	public void setParams() {
		DirectoryChooser fileChooser = new DirectoryChooser();
		fileChooser.setTitle("选择工作空间");
		fileChooser.setInitialDirectory(new File(Param.homePath));
		File file = fileChooser.showDialog(stage);
		workSpace = file.getAbsolutePath() + "/";
		Param.workspace=workSpace;
		System.out.println(workSpace);
		
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("设置参数");
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

//		TextField KofLDA = new TextField("13");
//		KofLDA.setPrefColumnCount(5);
//		grid.add(new Label("K of LDA: "), 0, 0);
//		grid.add(KofLDA, 1, 0);
//		
//		TextField num = new TextField("16");
//		num.setPrefColumnCount(5);
//		grid.add(new Label("序号: "), 0, 1);
//		grid.add(num, 1, 1);

		TextField KofKmeans = new TextField("14");
		KofKmeans.setPrefColumnCount(5);
		grid.add(new Label("K of Kmeans: "), 0, 0);
		grid.add(KofKmeans, 1, 0);
		
		

		dialog.getDialogPane().setContent(grid);
		Platform.runLater(() -> KofKmeans.requestFocus());
		dialog.showAndWait().ifPresent(response -> {
//			KLDA = Integer.parseInt(KofLDA.getText());
//			NumLDA=Integer.parseInt(num.getText());
			KKmeans = Integer.parseInt(KofKmeans.getText());
		});
	}

	public static Map<String, String> getDrug2type() throws Exception, FileNotFoundException {
		Map<String, String> drug2type = new HashMap<String, String>();
		String fileIn = Param.originPath + "drug2type.csv";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn), "UTF-8"));
		String line = "";
		while ((line = reader.readLine()) != null) {
			String[] lines = line.split(",");
			drug2type.put(lines[0], lines[1]);
		}
		reader.close();
		return drug2type;
	}

	public void runMalletLDA() throws Exception {
		Map<String, String> drug2type = getDrug2type();

		Double thetaMainwords = 0.8;
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00000");
		File file_out_KK = new File(workSpace + "LDA\\evaluate.csv");
		BufferedWriter out_KK = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out_KK), "gb2312"));//
		for (Integer KK = KLDAStart; KK <= KLDAEnd; KK++) {
			int itr = round;
			// if(KK<itS||KK>itE)
			// itr=1;
			double[] res = new double[itr];
			for (Integer jj = 0; jj < itr; jj++) {
				Integer iterations = iterationsLDA;
//				if (KK >= 30)
//					iterations = 2000;
				MalletLDA mLda = new MalletLDA(1, 0.01, KK, iterations, Param.topK, 20, workSpace);
				File id2item = new File(workSpace + "LDA/id2item.csv");
				File doc2items = new File(workSpace + "LDA/doc2items.csv");
				mLda.runLDA(id2item, doc2items);
				String[][] topWords = mLda.topWords;
				double[][] d2tDist = mLda.d2tDist;
				Double[][] topWordsProability = mLda.topWordsProability;
				File file_out = new File(workSpace + "LDA\\topic2items-" + KK + "-" + jj + ".csv");
				BufferedWriter out = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(file_out), "gb2312"));//
				// for(int i=0;i<topWords.length;i++){
				// out.write(i+",");
				// for(int j=0;j<topWords[0].length;j++){
				// out.write(topWords[i][j]+",");
				// }
				// out.newLine();
				// }

				ArrayList<Double> topic2Sum = new ArrayList<Double>();
				for (int i = 0; i < topWords.length; i++) {
					topic2Sum.add(0.0);
				}
				for (int j = 0; j < topWords[0].length; j++) {
					for (int i = 0; i < topWords.length; i++) {
						if (topic2Sum.get(i).compareTo(thetaMainwords) < 0) {
							String strTemp = topWords[i][j];
							if (drug2type.containsKey(strTemp))
								strTemp = drug2type.get(strTemp) + "-" + strTemp;
							out.write(strTemp + "=" + df.format(topWordsProability[i][j]) + ",");
						} else {
							out.write("-" + ",");
						}
						topic2Sum.set(i, topic2Sum.get(i) + topWordsProability[i][j]);
					}
					out.newLine();
				}
				out.flush();
				out.close();
				double re = Double.parseDouble(
						mLda.model.formatter.format(mLda.model.modelLogLikelihood() / mLda.model.totalTokens));
				re = Math.exp(-re);
				res[jj] = re;

				File file_out_doc2topics = new File(workSpace + "LDA\\doc2topics-" + KK + "-" + jj + ".csv");
				BufferedWriter out_doc2topics = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(file_out_doc2topics), "UTF-8"));//
				ArrayList<String> docLabels = mLda.model.getDocLabels();
				for (Integer i = 0; i < docLabels.size(); i++) {
					out_doc2topics.write(docLabels.get(i));
					for (Integer j = 0; j < d2tDist[0].length; j++) {
						out_doc2topics.write("," + j.toString() + "=" + d2tDist[i][j]);
					}
					out_doc2topics.newLine();
				}
				out_doc2topics.flush();
				out_doc2topics.close();
			}
			double reSum = 0.0;
			for (int jj = 0; jj < itr; jj++) {
				reSum += res[jj];
			}
			out_KK.write(KK + "," + reSum / itr);
			out_KK.newLine();
			out_KK.flush();
		}
		out_KK.close();
	}


	public void disPlayKChart(String path,String xLabel,String yLabel,String chartTitle) throws Exception {
		final NumberAxis xAxis = new NumberAxis();
	    final NumberAxis yAxis = new NumberAxis();
	    xAxis.setLabel(xLabel);
	    yAxis.setLabel(yLabel);
	    final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(
	        xAxis, yAxis);

//	    lineChart.setTitle(chartTitle);
	    XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
	    series.setName("        "+chartTitle);
	    
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
		String line = "";
		while ((line = reader.readLine()) != null) {
			String[] lines = line.split(",");
			Integer K = Integer.parseInt(lines[0]);
			Double V = Double.parseDouble(lines[1]);
			series.getData().add(new XYChart.Data<Number, Number>(K, V));
		}
		reader.close();
		lineChart.getData().add(series);

		
		Tab LDAKChartPan = null;
		for (int i = 0; i < funcTabPane.getTabs().size(); i++) {
			String title = funcTabPane.getTabs().get(i).getText();
			if (title.equals(chartTitle)) {
				LDAKChartPan = funcTabPane.getTabs().get(i);
				LDAKChartPan.setContent(lineChart);
				funcTabPane.getSelectionModel().select(LDAKChartPan);
			}
		}

		if (LDAKChartPan == null) {
			LDAKChartPan = new Tab(chartTitle);
			LDAKChartPan.setContent(lineChart);
			funcTabPane.getTabs().add(LDAKChartPan);
			funcTabPane.getSelectionModel().select(LDAKChartPan);
		}
	}

	public void runAlign(String logPath) throws Exception {
		AlignLaunch.lauch(logPath);

		Tab IMPan = null;
		for (int i = 0; i < funcTabPane.getTabs().size(); i++) {
			String title = funcTabPane.getTabs().get(i).getText();
			if (title.equals("诊疗过程模型")) {
				IMPan = funcTabPane.getTabs().get(i);
				final SwingNode swingNodeIM = new SwingNode();
				JComponent jc = AlignLaunch.resultIM;
				swingNodeIM.setContent(jc);
				IMPan.setContent(swingNodeIM);
			}
		}
		if (IMPan == null) {
			IMPan = new Tab("诊疗过程模型");
			final SwingNode swingNodeIM = new SwingNode();
			JComponent jc = AlignLaunch.resultIM;
			swingNodeIM.setContent(jc);
			IMPan.setContent(swingNodeIM);
			funcTabPane.getTabs().add(IMPan);
			funcTabPane.getSelectionModel().select(IMPan);
		}
		

//		Tab alignPan = null;//需要手动刷新
//		for (int i = 0; i < funcTabPane.getTabs().size(); i++) {
//			String title = funcTabPane.getTabs().get(i).getText();
//			if (title.equals("异常结果")) {
//				alignPan = funcTabPane.getTabs().get(i);
//				SwingNode swingNode = new SwingNode();
//				swingNode.setContent(AlignLaunch.resultAlignment);
//				alignPan.setContent(swingNode);
//				funcTabPane.getSelectionModel().select(alignPan);
//			}
//		}
//
//		if (alignPan == null) {
//			alignPan = new Tab("异常结果");
//			SwingNode swingNode = new SwingNode();
//			swingNode.setContent(AlignLaunch.resultAlignment);
//			alignPan.setContent(swingNode);
//			funcTabPane.getTabs().add(alignPan);
//			funcTabPane.getSelectionModel().select(alignPan);
//		}
		
		Tab alignPan = null;
		for (int i = 0; i < funcTabPane.getTabs().size(); i++) {
			String title = funcTabPane.getTabs().get(i).getText();
			if (title.equals("异常结果")) {
				alignPan = funcTabPane.getTabs().get(i);
				SwingNode swingNode = new SwingNode();
				swingNode.setContent(AlignLaunch.resultAlignment);
				
				StackPane pane = new StackPane();//加了一层StackPane，而不是直接把swingnode设置到Tab
		        pane.getChildren().add(swingNode);
		        
				alignPan.setContent(pane);
				funcTabPane.getSelectionModel().select(alignPan);
			}
		}

		if (alignPan == null) {
			alignPan = new Tab("异常结果");
			SwingNode swingNode = new SwingNode();
			swingNode.setContent(AlignLaunch.resultAlignment);
			
			
			StackPane pane = new StackPane();
	        pane.getChildren().add(swingNode);
	        
	        alignPan.setContent(pane);
			funcTabPane.getTabs().add(alignPan);
			funcTabPane.getSelectionModel().select(alignPan);
		}

	}
	
	private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                swingNode.setContent(AlignLaunch.resultAlignment);
            }
        });
    }

	public static String basePath = Param.basePath;

//	public void runKmeansLuPing() throws IOException {
//
//		String docToTopicFilePath = "data/OutlierDetection/topic/doc2topics/docToTopic.csv";
//		String idFromCorpusForMapping = "data/OutlierDetection/corpus";
//		String logFilePathForSave = "data/OutlierDetection/cluster/LogBasedOnKmeansPlusPlus-";
//		String clusterToItemsFilePath = "data/OutlierDetection/cluster/clusterToItems.csv";
//		String topic2itemsFilePah = "data/OutlierDetection/topic/topic2items";
//		String logPath = "data/OutlierDetection/cluster/LogBasedOnKmeansPlusPlus-" + KKmeans + ".csv";
//		String docToTopicPath = "data/OutlierDetection/topic/doc2topics/docToTopic.csv";
//		String corpusFilePath = "data/OutlierDetection/corpus";
//		try {
//			Set<String> id = new HashSet<String>();
//			double min = Double.MAX_VALUE;
//			Map<Integer, List<Point>> result = null;
//			int minIndex = -1;
//			for (int j = 0; j < 1; j++) {
//				int i = KKmeans;
//				KmeansPlusPlus kmeansClustering = new KmeansPlusPlus(docToTopicFilePath);
//				Integer maxIteration = 300;
//				Map<Integer, List<Point>> tempResult = kmeansClustering.kcluster(i, maxIteration,
//						basePath + "Kmeans\\initKPoint\\" + "init-" + i + "-docId.csv");
//				System.out.println(kmeansClustering.evaluate);
//				if (kmeansClustering.evaluate < min) {
//					min = kmeansClustering.evaluate;
//					result = tempResult;
//					minIndex = i;
//				} else
//					continue;
//			}
//			File file_out = new File(logFilePathForSave + minIndex + ".csv");
//			// BufferedWriter bw_out = new BufferedWriter(new
//			// FileWriter(file_out, false));//
//			BufferedWriter bw_out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//
//			bw_out.write("\"BRBM\",\"XM\",\"RQ\"");
//			bw_out.newLine();
//			for (Entry<Integer, List<Point>> entry : result.entrySet()) {
//				// System.out.println("===============聚簇中心为：" +
//				// entry.getKey() +
//				// "================");
//				// System.out.println(entry.getValue().size());
//				for (Point point : entry.getValue()) {
//					String[] strs = point.id.split("#");
//					String caseId = strs[0];
//					id.add(caseId);
//					String riqi = strs[1].split("\\.")[0];
//					bw_out.write(caseId + "," + entry.getKey() + "," + riqi);
//					bw_out.newLine();
//				}
//			}
//			for (String it : id) {
//				bw_out.write(it + "," + "s" + "," + "1900-1-1");
//				bw_out.newLine();
//				bw_out.write(it + "," + "e" + "," + "2018-1-1");
//				bw_out.newLine();
//			}
//			bw_out.flush();
//			bw_out.close();
//			System.out.println("Kmeans++ completed!");
//
//			// 保存各类簇的主题代表
//			GetLabelForCluster ins = new GetLabelForCluster();
//			ins.getClusterToItems(result, topic2itemsFilePah, clusterToItemsFilePath, 30, logPath, docToTopicPath,
//					corpusFilePath, KLDA, KKmeans);// 每个topic，最多选10个代表词
//			System.out.println("保存各类簇的主题代表 completed!");
//			Text t = new Text();
//			readFromClusterToItems(clusterToItemsFilePath, t);
//
//			Tab tabCluster2items = null;
//			for (int i = 0; i < funcTabPane.getTabs().size(); i++) {
//				String title = funcTabPane.getTabs().get(i).getText();
//				if (title.equals("类簇-诊疗主题")) {
//					tabCluster2items = funcTabPane.getTabs().get(i);
//					ScrollPane sp = new ScrollPane();
//					sp.setContent(t);
//					tabCluster2items.setContent(sp);
//					funcTabPane.getSelectionModel().select(tabCluster2items);
//				}
//			}
//
//			if (tabCluster2items == null) {
//				tabCluster2items = new Tab("类簇-诊疗主题");
//				ScrollPane sp = new ScrollPane();
//				sp.setContent(t);
//				tabCluster2items.setContent(sp);
//				funcTabPane.getTabs().add(tabCluster2items);
//				funcTabPane.getSelectionModel().select(tabCluster2items);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

	public void runKmeansPlusPlus() {
		String docToTopicFilePath = "data/OutlierDetection/topic/doc2topics/docToTopic.csv";
		String idFromCorpusForMapping = "data/OutlierDetection/corpus";
		String logFilePathForSave = "data/OutlierDetection/cluster/LogBasedOnKmeansPlusPlus-";
		String clusterToItemsFilePath = "data/OutlierDetection/cluster/clusterToItems.csv";
		String topic2itemsFilePah = "data/OutlierDetection/topic/topic2items";
		String logPath = "data/OutlierDetection/cluster/LogBasedOnKmeansPlusPlus-" + KKmeans + ".csv";
		String docToTopicPath = "data/OutlierDetection/topic/doc2topics/docToTopic.csv";
		String corpusFilePath = "data/OutlierDetection/corpus";
		try {
			Set<String> id = new HashSet<String>();
			double min = Double.MAX_VALUE;
			Map<Integer, List<Point>> result = null;
			int minIndex = -1;
			for (int j = 0; j < KmeansRoundTimes; j++) {
				int i = KKmeans;
				KmeansPlusPlus kmeansClustering = new KmeansPlusPlus(docToTopicFilePath, idFromCorpusForMapping);
				Map<Integer, List<Point>> tempResult = kmeansClustering.kcluster(i);
				System.out.println(kmeansClustering.evaluate);
				if (kmeansClustering.evaluate < min) {
					min = kmeansClustering.evaluate;
					result = tempResult;
					minIndex = i;
				} else
					continue;
			}
			File file_out = new File(logFilePathForSave + minIndex + ".csv");
			BufferedWriter bw_out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//
			bw_out.write("\"BRBM\",\"XM\",\"RQ\"");
			bw_out.newLine();
			for (Entry<Integer, List<Point>> entry : result.entrySet()) {
				for (Point point : entry.getValue()) {
					String[] strs = point.id.split("#");
					String caseId = strs[0];
					id.add(caseId);
					String riqi = strs[1].split("\\.")[0];
					bw_out.write(caseId + "," + entry.getKey() + "," + riqi);
					bw_out.newLine();
				}
			}
			for (String it : id) {
				bw_out.write(it + "," + "s" + "," + "1900-1-1");
				bw_out.newLine();
				bw_out.write(it + "," + "e" + "," + "2018-1-1");
				bw_out.newLine();
			}
			bw_out.flush();
			bw_out.close();
			System.out.println("Kmeans++ completed!");

			// 保存各类簇的主题代表
			GetLabelForCluster ins = new GetLabelForCluster();
			ins.getClusterToItems(result, topic2itemsFilePah, clusterToItemsFilePath, 10, logPath, docToTopicPath,
					corpusFilePath, KLDA, KKmeans);// 每个topic，最多选10个代表词
			System.out.println("保存各类簇的主题代表 completed!");
			Text t = new Text();
			readFromClusterToItems(clusterToItemsFilePath, t);

			Tab tabCluster2items = null;
			for (int i = 0; i < funcTabPane.getTabs().size(); i++) {
				String title = funcTabPane.getTabs().get(i).getText();
				if (title.equals("cluster2items")) {
					tabCluster2items = funcTabPane.getTabs().get(i);
					ScrollPane sp = new ScrollPane();
					sp.setContent(t);
					tabCluster2items.setContent(sp);
					funcTabPane.getSelectionModel().select(tabCluster2items);
				}
			}

			if (tabCluster2items == null) {
				tabCluster2items = new Tab("cluster2items");
				ScrollPane sp = new ScrollPane();
				sp.setContent(t);
				tabCluster2items.setContent(sp);
				funcTabPane.getTabs().add(tabCluster2items);
				funcTabPane.getSelectionModel().select(tabCluster2items);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void readFromClusterToItems(String clusterToItemsFilePath, Text t) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(clusterToItemsFilePath)));
			String str = "";
			String line = "";
			while ((line = br.readLine()) != null) {
				str += line + "\n";
			}
			t.setText(str);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void generateCorpusForLDA(String path) throws IOException {
		FilesOfFolderDelete.delFolder(path);
		// String path="data/OutlierDetection/corpus/";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		for (Map.Entry<String, InputData> entry : FrameworkMain.paramInputDataSetOfFunc.entrySet()) {
			String type = entry.getValue().type;
			if (type.equals("log")) {// 只处理日志文件
				InputData value = entry.getValue();
				for (InputDataRowType row : value.dataForLog) {
					String visitId = row.getVisitId();
					String date = df.format(row.getTime());
					String event = row.getEvent();
					if (map.containsKey(visitId + "#" + date)) {
						map.get(visitId + "#" + date).add(event);
					} else {
						ArrayList<String> temp = new ArrayList<String>();
						temp.add(event);
						map.put(visitId + "#" + date, temp);
					}
				}
			}
		}
		for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
			String key = entry.getKey();
			ArrayList<String> value = entry.getValue();
			File file_out = new File(path + key + ".txt");
			BufferedWriter bw_out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//
			for (int i = 0; i < value.size() - 1; i++) {
				bw_out.write(value.get(i) + " ");
			}
			bw_out.write(value.get(value.size() - 1));
			bw_out.newLine();
			bw_out.flush();
			bw_out.close();
		}
	}

	public void runLDA(String corpusPath) throws Exception {
		GetTopicBasedOnLDA ins = new GetTopicBasedOnLDA();

		System.out.println("LDA completed!");

		topicId2ItemsArray = new ArrayList<String[]>();
		loadTopic2Items(topicId2ItemsArray, "data/OutlierDetection/topic/topic2items/");
		int k = topicId2ItemsArray.get(0).length;
		final String[] tableHead = new String[k];
		for (Integer i = 0; i < k; i++) {
			tableHead[i] = "诊疗主题-" + i;
		}
		ObservableList<ObservableList<String>> topic2ItemsData = FXCollections.observableArrayList();
		for (String[] row : topicId2ItemsArray) {
			topic2ItemsData.add(FXCollections.observableArrayList(row));
		}
		TableView<ObservableList<String>> topic2ItemsTV = new TableView<>();
		topic2ItemsTV.setItems(topic2ItemsData);
		for (int i = 0; i < tableHead.length; i++) {
			final int curCol = i;
			final TableColumn<ObservableList<String>, String> column = new TableColumn<>(tableHead[curCol]);
			column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(curCol)));
			topic2ItemsTV.getColumns().add(column);
		}
		Tab tab1 = null;
		for (int i = 0; i < funcTabPane.getTabs().size(); i++) {
			String title = funcTabPane.getTabs().get(i).getText();
			if (title.equals("诊疗主题-诊疗项目")) {
				tab1 = funcTabPane.getTabs().get(i);
				tab1.setContent(topic2ItemsTV);
				funcTabPane.getSelectionModel().select(tab1);
			}
		}

		if (tab1 == null) {
			tab1 = new Tab("诊疗主题-诊疗项目");
			tab1.setContent(topic2ItemsTV);
			funcTabPane.getTabs().add(tab1);
			funcTabPane.getSelectionModel().select(tab1);
		}

		ArrayList<String[]> doc2Topic = new ArrayList<String[]>();
		String doc2TopicFilePath = "data/OutlierDetection/topic/doc2topics/docToTopic.csv";
		String idFromCorpusForMapping = "data/OutlierDetection/corpus";
		loadDoc2Topic(doc2Topic, doc2TopicFilePath, idFromCorpusForMapping);
		final String[] doc2TopicsTableHead = new String[k + 1];
		doc2TopicsTableHead[0] = "病人编码#日期";
		for (int i = 1; i <= k; i++) {
			doc2TopicsTableHead[i] = "诊疗主题-" + (i - 1);
		}
		ObservableList<ObservableList<String>> doc2TopicsData = FXCollections.observableArrayList();
		for (String[] row : doc2Topic) {
			doc2TopicsData.add(FXCollections.observableArrayList(row));
		}
		TableView<ObservableList<String>> doc2TopicsDataTV = new TableView<>();
		doc2TopicsDataTV.setItems(doc2TopicsData);
		for (int i = 0; i < doc2TopicsTableHead.length; i++) {
			final int curCol = i;
			final TableColumn<ObservableList<String>, String> column = new TableColumn<>(doc2TopicsTableHead[curCol]);
			column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(curCol)));
			doc2TopicsDataTV.getColumns().add(column);
		}
		Tab tab2 = null;
		for (int i = 0; i < funcTabPane.getTabs().size(); i++) {
			String title = funcTabPane.getTabs().get(i).getText();
			if (title.equals("天-诊疗主题")) {
				tab2 = funcTabPane.getTabs().get(i);
				tab2.setContent(doc2TopicsDataTV);
				funcTabPane.getSelectionModel().select(tab1);
			}
		}

		if (tab2 == null) {
			tab2 = new Tab("天-诊疗主题");
			tab2.setContent(doc2TopicsDataTV);
			funcTabPane.getTabs().add(tab2);
			funcTabPane.getSelectionModel().select(tab1);
		}

	}

	public void loadTopic2Items(ArrayList<String[]> topicId2ItemsArray, String topicFilePath) {
		Map<String, ArrayList<String>> topic2Items = new HashMap<String, ArrayList<String>>();
		BufferedReader br;
		int k = 0;
		int max = Integer.MIN_VALUE;
		try {
			File folder = new File(topicFilePath);
			for (File file : folder.listFiles()) {
				k++;
				String topicNum = file.getName().split("\\.")[0];
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String currentLine;
				ArrayList<String> eachTopic2Items = new ArrayList<String>();
				while ((currentLine = br.readLine()) != null) {
					eachTopic2Items.add(currentLine);
				}
				topic2Items.put(topicNum, eachTopic2Items);
				if (eachTopic2Items.size() > max)
					max = eachTopic2Items.size();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int itemSize = max;
		for (int i = 0; i < itemSize; i++) {
			String[] dataTemp = new String[k];
			for (Integer j = 0; j < k; j++) {
				if (i < topic2Items.get(j.toString()).size())
					dataTemp[j] = topic2Items.get(j.toString()).get(i);
				else
					dataTemp[j] = "-";
			}
			topicId2ItemsArray.add(dataTemp);
		}
	}

	public void loadDoc2Topic(ArrayList<String[]> doc2Topic, String doc2TopicFilePath, String idFromCorpusForMapping) {
		DecimalFormat df = new java.text.DecimalFormat("#.000");
		ArrayList<String> id = new ArrayList<String>();
		ArrayList<ArrayList<Double>> data = new ArrayList<ArrayList<Double>>();
		try {
			BufferedReader reader;
			reader = new BufferedReader(new FileReader(doc2TopicFilePath));
			String line = null;
			while ((line = reader.readLine()) != null) {
				Map<String, Double> temp = new HashMap<String, Double>();
				String[] strs = line.split(",");
				id.add(strs[0]);
				for (int i = 1; i < strs.length; i++) {
					temp.put(strs[i].split("=")[0], Double.valueOf(df.format(Double.valueOf(strs[i].split("=")[1]))));
				}
				ArrayList<Double> arrayTemp = new ArrayList<Double>();
				for (Integer k = 0; k < temp.size(); k++)
					arrayTemp.add(temp.get(k.toString()));
				data.add(arrayTemp);
			}
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int k = data.get(0).size();
		for (int i = 0; i < id.size(); i++) {
			String[] rowTemp = new String[k + 1];
			rowTemp[0] = id.get(i);
			for (int j = 1; j <= k; j++) {
				rowTemp[j] = data.get(i).get(j - 1).toString();
			}
			doc2Topic.add(rowTemp);
		}
	}
}
