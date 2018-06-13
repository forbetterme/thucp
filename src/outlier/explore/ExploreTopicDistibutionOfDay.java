package outlier.explore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import outlier.util.Comparator4Sort;
import outlier.util.Param;
import outlier.util.String2DoubleNode;

public class ExploreTopicDistibutionOfDay {
	public static void main(String[] args) throws Exception {
		ExploreTopicDistibutionOfDay ins = new ExploreTopicDistibutionOfDay();
		Integer kTpoic = Param.topicK;
		Double clusterTheta = Param.ExploreTopicDistibutionOfDayClusterTheta;

		ins.sortTopicDistibutionByvalue(kTpoic, Param.num);
		ins.clusterByMainTopic(kTpoic, clusterTheta);
//		ins.clusterByFirstTopic(kTpoic);
	}

	public void sortTopicDistibutionByvalue(Integer kTopic, Integer num) throws Exception {
		File file_out = new File(Param.basePath + "LDA\\" + "doc2topics-" + kTopic + "-sort.csv");
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//

		String fileIn = Param.basePath + "LDA\\" + "doc2topics-" + kTopic + "-" + num + ".csv";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn), "UTF-8"));

		String line = "";
		while ((line = reader.readLine()) != null) {
			String[] lines = line.split(",");
			String visitId = lines[0];
			ArrayList<String2DoubleNode> temp = new ArrayList<String2DoubleNode>();
			for (int i = 1; i < lines.length; i++) {
				String[] strs = lines[i].split("=");
				String2DoubleNode node = new String2DoubleNode();
				node.key = strs[0];
				node.value = Double.parseDouble(strs[1]);
				temp.add(node);
			}
			Collections.sort(temp, Comparator4Sort.comString2Double);
			out.write(visitId);
			for (String2DoubleNode it : temp) {
				out.write("," + it.key + "=" + it.value);
			}
			out.newLine();
		}
		reader.close();
		out.flush();
		out.close();
	}

	public void clusterByMainTopic(Integer kTpoic, Double theta) throws Exception {
		Double meanProability = 1.0 / kTpoic;
		meanProability = 0.2;
		System.out.println(meanProability);

		File file_out = new File(Param.basePath + "LDA\\" + "doc2topics-" + kTpoic + "-log.csv");
		BufferedWriter bw_out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//

		File file_out_setZero = new File(Param.basePath + "LDA\\" + "doc2topics-" + kTpoic + "-setZero-log.csv");
		BufferedWriter bw_out_setZero = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file_out_setZero), "UTF-8"));//

		String fileIn = Param.basePath + "LDA\\" + "doc2topics-" + kTpoic + "-sort.csv";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn), "UTF-8"));

		TreeMap<String, ArrayList<String>> cluster = new TreeMap<String, ArrayList<String>>();

		String line = "";
		while ((line = reader.readLine()) != null) {
			String[] lines = line.split(",");
			String visitId = lines[0];
			ArrayList<String2DoubleNode> temp = new ArrayList<String2DoubleNode>();
			for (int i = 1; i < lines.length; i++) {
				String[] strs = lines[i].split("=");
				String2DoubleNode node = new String2DoubleNode();
				node.key = strs[0];
				node.value = Double.parseDouble(strs[1]);
				temp.add(node);
			}
			Double sum = 0.0;
			int index = 0;
			TreeSet<Integer> mainTopi = new TreeSet<Integer>();
			while (sum.compareTo(theta) <= 0) {
				if (temp.get(index).value.compareTo(meanProability) <= 0)
					break;
				sum += temp.get(index).value;
				mainTopi.add(Integer.parseInt(temp.get(index).key));
				index++;
			}

			bw_out_setZero.write(visitId);
			for (int i = 0; i < temp.size(); i++) {
				Integer topicTemp = Integer.parseInt(temp.get(i).key);
				Double vTemp = temp.get(i).value;
				if (mainTopi.contains(topicTemp)) {
					bw_out_setZero.write("," + topicTemp + "=" + vTemp);
				} else {
					bw_out_setZero.write("," + topicTemp + "=" + "0.0");
				}
			}
			bw_out_setZero.newLine();

			String key = "";
			int cnt = 0;
			for (Integer it : mainTopi) {
				// if(cnt>1) break;
				key += it.toString() + ",";
				cnt++;
			}
			if (cluster.containsKey(key)) {
				cluster.get(key).add(visitId);
			} else {
				ArrayList<String> list = new ArrayList<String>();
				list.add(visitId);
				cluster.put(key, list);
			}
		}
		reader.close();
		bw_out_setZero.flush();
		bw_out_setZero.close();

		int count = 0;
		for (Map.Entry<String, ArrayList<String>> entry : cluster.entrySet()) {
			System.out.println(entry.getKey() + "=" + entry.getValue().size());
			if (entry.getValue().size() <= 5)
				count++;
		}
		System.out.println(":" + cluster.size());
		System.out.println(":" + count);

		//
		bw_out.write("case,event,time");
		bw_out.newLine();
		for (Map.Entry<String, ArrayList<String>> entry : cluster.entrySet()) {
			String key = entry.getKey();
			key = key.replaceAll(",", "&");
			// if(entry.getValue().size()<=100) continue;
			for (String it : entry.getValue()) {
				String[] keys = it.split("#");
				String visitId = keys[0];
				String time = keys[1];
				bw_out.write(visitId + "," + key + "," + time);
				bw_out.newLine();
			}
		}
		bw_out.flush();
		bw_out.close();
	}

	public void clusterByFirstTopic(Integer kTpoic) throws Exception {
		File file_out = new File(Param.basePath + "LDA\\" + "doc2topics-" + kTpoic + "-first-log.csv");
		BufferedWriter bw_out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//

		String fileIn = Param.basePath + "LDA\\" + "doc2topics-" + kTpoic + "-sort.csv";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn), "UTF-8"));

		TreeMap<String, ArrayList<String>> cluster = new TreeMap<String, ArrayList<String>>();

		String line = "";
		while ((line = reader.readLine()) != null) {
			String[] lines = line.split(",");
			String visitId = lines[0];
			ArrayList<String2DoubleNode> temp = new ArrayList<String2DoubleNode>();
			for (int i = 1; i < lines.length; i++) {
				String[] strs = lines[i].split("=");
				String2DoubleNode node = new String2DoubleNode();
				node.key = strs[0];
				node.value = Double.parseDouble(strs[1]);
				temp.add(node);
			}
			TreeSet<Integer> mainTopi = new TreeSet<Integer>();
			mainTopi.add(Integer.parseInt(temp.get(0).key));
			String key = "";
			for (Integer it : mainTopi) {
				key += it.toString();
			}
			if (cluster.containsKey(key)) {
				cluster.get(key).add(visitId);
			} else {
				ArrayList<String> list = new ArrayList<String>();
				list.add(visitId);
				cluster.put(key, list);
			}
		}
		reader.close();

		int count = 0;
		for (Map.Entry<String, ArrayList<String>> entry : cluster.entrySet()) {
			System.out.println(entry.getKey() + "=" + entry.getValue().size());
			if (entry.getValue().size() <= 100)
				count++;
		}
		System.out.println(":" + cluster.size());
		System.out.println(":" + count);

		//
		bw_out.write("case,event,time");
		bw_out.newLine();
		Set<String> visits = new HashSet<String>();
		for (Map.Entry<String, ArrayList<String>> entry : cluster.entrySet()) {
			String key = entry.getKey();
			for (String it : entry.getValue()) {
				String[] keys = it.split("#");
				String visitId = keys[0];
				visits.add(visitId);
				String time = keys[1];
				bw_out.write(visitId + "," + key + "," + time);
				bw_out.newLine();
			}
		}
		for (String visitId : visits) {
			bw_out.write(visitId + "," + "s" + "," + "1900-1-1");
			bw_out.newLine();
			bw_out.write(visitId + "," + "e" + "," + "2018-1-1");
			bw_out.newLine();
		}
		bw_out.flush();
		bw_out.close();
	}
}
