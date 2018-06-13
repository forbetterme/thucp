package outlier.explore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import outlier.cluster.Point;
import outlier.util.Comparator4Sort;
import outlier.util.Param;

public class ExploreDBSCANParam {
	public static void main(String[] args) throws Exception, Exception{
		ExploreDBSCANParam ins = new ExploreDBSCANParam();
		Integer topicK=Param.topicK;
		Integer num=Param.num;
		String doc2topicsPath=Param.basePath+"LDA\\doc2topics-"+topicK+"-"+num+".csv";
//		String doc2topicsPath = Param.basePath+"LDA\\doc2topics-"+topicK+"-elki-second.csv";
		
		String kNearstDisPath=Param.basePath+"DBSCAN\\cluster\\estimateEpsilon.csv";
		Integer minPts = topicK*2;
//		ins.estimateEpsilonELKIFormat(doc2topicsPath, minPts, kNearstDisPath);
		ins.estimateEpsilon(doc2topicsPath, minPts, kNearstDisPath);
	}
	public void estimateEpsilon(String doc2topicsPath,Integer minPts,String kNearstDisPath) throws Exception, FileNotFoundException{
		File file_out = new File(kNearstDisPath);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//
		
		List<Point> dataset = new ArrayList<Point>();
		ArrayList<Point> points = new ArrayList<Point>();
		ArrayList<String> id = new ArrayList<String>();
		Map<String, ArrayList<Double>> map = new HashMap<String, ArrayList<Double>>();
		try {
			BufferedReader reader;
			reader = new BufferedReader(new FileReader(doc2topicsPath));
			String line = null;
			int j = 0;
			while ((line = reader.readLine()) != null) {
				Map<String, Double> temp = new HashMap<String, Double>();
				String[] strs = line.split(",");
				String label=strs[0];
				id.add(label);
				for (int i = 1; i < strs.length; i++) {
					temp.put(strs[i].split("=")[0], Double.valueOf(strs[i].split("=")[1]));
				}
				ArrayList<Double> arrayTemp = new ArrayList<Double>();
				for (Integer k = 0; k < temp.size(); k++)
					arrayTemp.add(temp.get(k.toString()));
				// System.out.println(arrayTemp.toString());
				map.put(label, arrayTemp);
				j++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Integer i = 0; i < id.size(); i++) {
			Point temp = new Point(id.get(i), map.get(id.get(i)));
			points.add(temp);
		}
		dataset = points;
		PriorityQueue<Double> kNearstDisQue=new PriorityQueue<Double>(Comparator4Sort.comDoubleDesc);
		for(int i=0;i<dataset.size();i++){
			PriorityQueue<Double> disQueue=new PriorityQueue<Double>();
			for(int j=0;j<dataset.size();j++){
				if(j==i) continue;
				disQueue.add(distance(dataset.get(i), dataset.get(j)));
			}
			int count=0;
			while(count<minPts-1){
				disQueue.poll();
				count++;
			}
			kNearstDisQue.add(disQueue.poll());
		}
		Integer index=0;
		while(!kNearstDisQue.isEmpty()){
			out.write(index+","+kNearstDisQue.poll().toString());
			out.newLine();
			index+=1;
		}
		out.flush();
		out.close();
	}
	
	public void estimateEpsilonELKIFormat(String doc2topicsPath,Integer minPts,String kNearstDisPath) throws Exception, FileNotFoundException{
		File file_out = new File(kNearstDisPath);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//
		
		List<Point> dataset = new ArrayList<Point>();
		ArrayList<Point> points = new ArrayList<Point>();
		ArrayList<String> id = new ArrayList<String>();
		Map<String, ArrayList<Double>> map = new HashMap<String, ArrayList<Double>>();
		try {
			BufferedReader reader;
			reader = new BufferedReader(new FileReader(doc2topicsPath));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] strs = line.split(" ");
				String docKey=strs[strs.length-1];
				ArrayList<Double> v=new ArrayList<Double>();
				for(int j=0;j<strs.length-1;j++)
					v.add(Double.parseDouble(strs[j]));
				Point pTemp=new Point(docKey,v);
				points.add(pTemp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		dataset = points;
		PriorityQueue<Double> kNearstDisQue=new PriorityQueue<Double>(Comparator4Sort.comDoubleDesc);
		for(int i=0;i<dataset.size();i++){
			PriorityQueue<Double> disQueue=new PriorityQueue<Double>();
			for(int j=0;j<dataset.size();j++){
				if(j==i) continue;
				disQueue.add(distance(dataset.get(i), dataset.get(j)));
			}
			int count=0;
			while(count<minPts-1){
				disQueue.poll();
				count++;
			}
			kNearstDisQue.add(disQueue.poll());
		}
		Integer index=0;
		while(!kNearstDisQue.isEmpty()){
			out.write(index+","+kNearstDisQue.poll().toString());
			out.newLine();
			index+=1;
		}
		out.flush();
		out.close();
	}
	
	private double distance(Point point1, Point point2) {
		ArrayList<Double> x1 = point1.value;
		ArrayList<Double> x2 = point2.value;
		double distance = 0;
		for (int i = 0; i < x1.size(); i++) {
			distance += Math.pow(x1.get(i) - x2.get(i), 2);
		}
		distance = Math.sqrt(distance);
		return distance;
	}
}
