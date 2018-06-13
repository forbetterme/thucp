package view;

import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class InputDataStatics {
	Comparator<InputDataRowType> com = new Comparator<InputDataRowType>() {

		@Override
		public int compare(InputDataRowType o1, InputDataRowType o2) {
			// TODO Auto-generated method stub
			return o1.time.compareTo(o2.time);
		}
	};

	public Text computeStatics(String inputDataKey) {
		InputData data = FrameworkMain.inputDataSet.get(inputDataKey);
		ArrayList<InputDataRowType> rows = data.getDataForLog();
		HashSet<String> visits = new HashSet<String>();
		HashSet<String> items = new HashSet<String>();
		for (InputDataRowType it : rows) {
			visits.add(it.getVisitId());
			items.add(it.getEvent());
		}
		Integer size = data.getDataForLog().size();
		Text re = new Text();
		re.setText("\n" + "病人数量: " + visits.size() + "\n\n" + "诊疗项目数量:" + items.size()+"\n\n\n");
		return re;
	}

	public LineChart<Number, Number> getStayDistribution(String inputDataKey) {
		Map<String, ArrayList<InputDataRowType>> case2events = new HashMap<String, ArrayList<InputDataRowType>>();
		InputData data = FrameworkMain.inputDataSet.get(inputDataKey);
		TreeMap<Long, Integer> day2casesSize = new TreeMap<Long, Integer>();
		ArrayList<InputDataRowType> rows = data.getDataForLog();
		for (InputDataRowType it : rows) {
			String key = it.getVisitId();
			if (case2events.containsKey(key)) {
				case2events.get(key).add(it);
			} else {
				ArrayList<InputDataRowType> array = new ArrayList<InputDataRowType>();
				array.add(it);
				case2events.put(key, array);
			}
		}
		for (Map.Entry<String, ArrayList<InputDataRowType>> entry : case2events.entrySet()) {
			Collections.sort(entry.getValue(), com);
		}
		for (Map.Entry<String, ArrayList<InputDataRowType>> entry : case2events.entrySet()) {
			ArrayList<InputDataRowType> value = entry.getValue();
			Date s = value.get(0).time;
			Date e = value.get(value.size() - 1).time;
			Long key = getDatePoor(e, s);
			if (day2casesSize.containsKey(key)) {
				day2casesSize.put(key, day2casesSize.get(key) + 1);
			} else {
				day2casesSize.put(key, 1);
			}
		}
		
		final NumberAxis xAxis = new NumberAxis();
	    final NumberAxis yAxis = new NumberAxis();
	    xAxis.setLabel("住院天数");
	    yAxis.setLabel("病人数量");
	    final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(
	        xAxis, yAxis);
	    XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
	    series.setName("         住院天数分布图");
		
		DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
		for (Map.Entry<Long, Integer> entry : day2casesSize.entrySet()) {
			series.getData().add(new XYChart.Data<Number, Number>(entry.getKey(), entry.getValue()));
//			mDataset.addValue(entry.getValue(), "住院天数", entry.getKey());
		}
		lineChart.getData().add(series);
		return lineChart;
		
	}

	public static Long getDatePoor(Date endDate, Date nowDate) {

		long nd = 1000 * 24 * 60 * 60;
		long nh = 1000 * 60 * 60;
		long nm = 1000 * 60;
		// long ns = 1000;
		// 获得两个时间的毫秒时间差异
		long diff = endDate.getTime() - nowDate.getTime();
		// 计算差多少天
		long day = diff / nd;
		// 计算差多少小时
		long hour = diff % nd / nh;
		// 计算差多少分钟
		long min = diff % nd % nh / nm;
		// 计算差多少秒//输出结果
		// long sec = diff % nd % nh % nm / ns;
		return (day * 24 + hour) / 24;
	}
	
	public TableView<ObservableList<String>> getDetails(String inputDataKey) {
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		Map<String, ArrayList<InputDataRowType>> case2events = new HashMap<String, ArrayList<InputDataRowType>>();
		InputData data = FrameworkMain.inputDataSet.get(inputDataKey);
		TreeMap<Long, Integer> day2casesSize = new TreeMap<Long, Integer>();
		ArrayList<InputDataRowType> rows = data.getDataForLog();
		for (InputDataRowType it : rows) {
			String key = it.getVisitId();
			if (case2events.containsKey(key)) {
				case2events.get(key).add(it);
			} else {
				ArrayList<InputDataRowType> array = new ArrayList<InputDataRowType>();
				array.add(it);
				case2events.put(key, array);
			}
		}
		for (Map.Entry<String, ArrayList<InputDataRowType>> entry : case2events.entrySet()) {
			Collections.sort(entry.getValue(), com);
		}
		
		final String[] tableHead = new String[6];
		tableHead[0]="就诊号码";
		tableHead[1]="诊疗项目";
		tableHead[2]="项目类别";
		tableHead[3]="数量";
		tableHead[4]="单价";
		tableHead[5]="日期";
		ArrayList<String[]> rowsVis=new ArrayList<>();
		for (Map.Entry<String, ArrayList<InputDataRowType>> entry : case2events.entrySet()) {
			String[] strs=new String[6];
			for(InputDataRowType it:entry.getValue()){
				strs[0]=it.getVisitId();
				strs[1]=it.getEvent();
				strs[2]=it.getEventClass();
				strs[3]=it.getNum().toString();
				strs[4]=it.getPrice().toString();
				strs[5]=df.format(it.getTime()).toString();
			}
			rowsVis.add(strs);
			
		}
		
		ObservableList<ObservableList<String>> topic2ItemsData = FXCollections.observableArrayList();
		for (String[] row : rowsVis) {
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
		return topic2ItemsTV;
	}

	public static void main(String[] args) {
		InputDataStatics ins = new InputDataStatics();
	}
}
