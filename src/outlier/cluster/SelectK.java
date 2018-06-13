package outlier.cluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.swing.JComponent;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.packages.PackageManager;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.impl.PluginManagerImpl;
import org.processmining.log.csv.CSVFileReferenceUnivocityImpl;
import org.processmining.log.csv.ICSVReader;
import org.processmining.log.csv.config.CSVConfig;
import org.processmining.log.csvimport.CSVConversion;
import org.processmining.log.csvimport.CSVConversion.ConversionResult;
import org.processmining.log.csvimport.CSVConversion.NoOpProgressListenerImpl;
import org.processmining.log.csvimport.CSVConversion.ProgressListener;
import org.processmining.log.csvimport.config.CSVConversionConfig;
import org.processmining.log.csvimport.config.CSVConversionConfig.CSVEmptyCellHandlingMode;
import org.processmining.log.csvimport.config.CSVConversionConfig.CSVErrorHandlingMode;
import org.processmining.log.csvimport.config.CSVConversionConfig.CSVMapping;
import org.processmining.log.csvimport.config.CSVConversionConfig.Datatype;
import org.processmining.log.csvimport.exception.CSVConversionException;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMi;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;
import org.processmining.plugins.petrinet.PetriNetVisualization;

import com.google.common.collect.ImmutableList;

import outlier.util.Comparator4Sort;
import outlier.util.Param;
import outlier.util.String2DoubleNode;
import test.TestVisualIM;

public class SelectK {
	public static String basePath=Param.basePath;
	public static void main(String[] args) throws Exception{
		for(Integer ii=16;ii<=16;ii++){
			Param.num=ii;
		Double clusterMainTopicTheta=Param.clusterMainTopicTheta;
		Integer topicK=Param.topicK;
		Integer num=Param.num;
		Double epsilon=Param.epsilon;
		String cluster2topicPath=basePath+"Kmeans\\cluster\\cluster2topics.csv";
		String topic2itemsPath=basePath+"LDA\\topic2items-" + topicK + "-"+num+".csv";
		SelectK ins = new SelectK();
		
		File file_out_eval = new File(basePath+"Kmeans\\cluster\\evaluate.csv");
		BufferedWriter outEval = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out_eval), "UTF-8"));
//		BufferedWriter outEval = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out_eval,true), "UTF-8"));
		
//		String docToTopicFilePath = basePath+"LDA\\doc2topics-"+topicK+"-elki.csv";
		String docToTopicFilePath = basePath+"LDA\\doc2topics-"+topicK+"-"+Param.num+".csv";
//		String docToTopicFilePath = basePath+"LDA\\doc2topics-"+topicK+"-setZero-log.csv";
		for(Integer i=11;i<=11;i++){
			KmeansPlusPlus kmeansClustering = new KmeansPlusPlus(docToTopicFilePath);
			Integer maxIteration=300;
			TestELKIDef  elki = new TestELKIDef();
//			Map<Integer, List<Point>> result = elki.ELIKDBSCAN(topicK,epsilon,docToTopicFilePath);
//			Map<Integer, List<Point>> result = elki.ELIKKMedoidsPAM(i,docToTopicFilePath);
			Map<Integer, List<Point>> result = kmeansClustering.kcluster(i,maxIteration,basePath+"Kmeans\\initKPoint\\"+"init-"+i+"-docId.csv");
//			Map<Integer, List<Point>> result = kmeansClustering.kcluster(i,maxIteration);
//			System.out.println(i+","+ins.computeSilhouetteCoefficient(result));
//			System.out.println();
			String logPath=basePath+"Kmeans\\log\\I61dot902-kmeans-"+i+".csv";
			
			Map<Integer, List<Point>> resultTree = new TreeMap<Integer,List<Point>>();
			for (Entry<Integer, List<Point>> entry : result.entrySet()) {
				resultTree.put(entry.getKey(), entry.getValue());
			}
			result=resultTree;
			
			File file_out = new File(logPath);
			BufferedWriter bw_out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//
			bw_out.write("case,event,time");
			bw_out.newLine();
			Set<String> id = new HashSet<String>();
			
			getPatient2Day2items(result);
			Map<Integer,ArrayList<String>> topic2items = getTopic2items(topic2itemsPath);
//			getLabelForCluster(result, clusterMainTopicTheta, cluster2topicPath,topic2items);
			getLabelForClusterHorizontal(result, clusterMainTopicTheta, cluster2topicPath,topic2items);
			
			for (Entry<Integer, List<Point>> entry : result.entrySet()) {
				 System.out.println("===============聚簇中心为：" +
				 entry.getKey() +
				 "================");
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
			
			outEval.write(i+","+kmeansClustering.evaluate);
			outEval.newLine();
			
//			System.out.println(ins.computeSilhouetteCoefficient(result));
			
			System.out.println("kmeans completed!");
			ins.runIM(logPath,i,ii,Param.topicK);
		}
		outEval.flush();
		outEval.close();
		
		
		}
	}
	
	public static Map<Integer,ArrayList<String>> getTopic2items(String path) throws Exception, FileNotFoundException{
		Map<Integer,ArrayList<String>> topic2items=new HashMap<Integer,ArrayList<String>>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),"gb2312"));
		String line="";
		while ((line = reader.readLine()) != null) {
			String[] lines=line.split(",");
			for(Integer i=0;i<lines.length;i++){
				if(topic2items.containsKey(i)){
					if(!lines[i].equals("-"))
						topic2items.get(i).add(lines[i]);
				}else{
					ArrayList<String> temp=new ArrayList<String>();
					if(!lines[i].equals("-"))
						temp.add(lines[i]);
					topic2items.put(i, temp);
				}
			}
		}
		reader.close();
		return topic2items;
	}
	public static void getLabelForCluster(Map<Integer, List<Point>> result ,Double clusterMainTopicTheta,String cluster2topicPath,Map<Integer,ArrayList<String>> topic2items) throws Exception, FileNotFoundException{
		File file_out = new File(cluster2topicPath);
		BufferedWriter bw_out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "gb2312"));//
		
		TreeMap<Integer, List<Point>> clusters=new TreeMap<Integer,List<Point>>();
		Integer clusterK=result.size();
		Integer topicK=result.get(0).get(0).value.size();
		Double meanProability = 1.0/topicK;
		for (Entry<Integer, List<Point>> entry : result.entrySet()) {
			clusters.put(entry.getKey(), entry.getValue());
		}
		for (Entry<Integer, List<Point>> entry : clusters.entrySet()) {
			ArrayList<Double> sumTemp=new ArrayList<Double>();
			for(int i=0;i<topicK;i++)
				sumTemp.add(0.0);
			for(Point p:entry.getValue()){
				for(int j=0;j<p.value.size();j++){
					sumTemp.set(j, sumTemp.get(j)+p.value.get(j));
				}
			}
			ArrayList<String2DoubleNode> listForSort=new ArrayList<String2DoubleNode>();
			for(Integer i=0;i<topicK;i++){
				sumTemp.set(i, sumTemp.get(i)/entry.getValue().size());
				String2DoubleNode node=new String2DoubleNode();
				node.key=i.toString();
				node.value=sumTemp.get(i);
				listForSort.add(node);
			}
			Collections.sort(listForSort,Comparator4Sort.comString2Double);
			
			Double sum = 0.0;
			int index = 0;
			TreeSet<Integer> mainTopi = new TreeSet<Integer>();
			while (sum.compareTo(clusterMainTopicTheta) <= 0) {
				if(listForSort.get(index).value.compareTo(meanProability)<=0) break;
				sum += listForSort.get(index).value;
				mainTopi.add(Integer.parseInt(listForSort.get(index).key));
				index++;
			}
			bw_out.write("Cluster-"+entry.getKey()+":");
			bw_out.newLine();
			ArrayList<ArrayList<String>> topicsTemp=new ArrayList<ArrayList<String>>();
			for (Integer it : mainTopi) {
				bw_out.write(it+",");
				topicsTemp.add(topic2items.get(it));
			}
			bw_out.newLine();
			index=0;
			boolean flag=true;
			while(flag){
				flag=false;
				for(int i=0;i<topicsTemp.size();i++){
					if(index<topicsTemp.get(i).size()){
						bw_out.write(topicsTemp.get(i).get(index)+",");
						flag=true;
					}else{
						bw_out.write("-,");
					}
				}
				bw_out.newLine();
				index++;
			}
			bw_out.newLine();
		}
		bw_out.flush();
		bw_out.close();
	}
	
	public static void getLabelForClusterHorizontal(Map<Integer, List<Point>> result ,Double clusterMainTopicTheta,String cluster2topicPath,Map<Integer,ArrayList<String>> topic2items) throws Exception, FileNotFoundException{
		File file_out = new File(cluster2topicPath);
		BufferedWriter bw_out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "gb2312"));//
		
		ArrayList<ArrayList<String>> cluster2topic2items=new ArrayList<ArrayList<String>>();
		
		TreeMap<Integer, List<Point>> clusters=new TreeMap<Integer,List<Point>>();
		Integer clusterK=result.size();
		Integer topicK=result.get(0).get(0).value.size();
		Double meanProability = 1.0/topicK;
		for (Entry<Integer, List<Point>> entry : result.entrySet()) {
			clusters.put(entry.getKey(), entry.getValue());
		}
		for (Entry<Integer, List<Point>> entry : clusters.entrySet()) {
			ArrayList<Double> sumTemp=new ArrayList<Double>();
			for(int i=0;i<topicK;i++)
				sumTemp.add(0.0);
			for(Point p:entry.getValue()){
				for(int j=0;j<p.value.size();j++){
					sumTemp.set(j, sumTemp.get(j)+p.value.get(j));
				}
			}
			ArrayList<String2DoubleNode> listForSort=new ArrayList<String2DoubleNode>();
			for(Integer i=0;i<topicK;i++){
				sumTemp.set(i, sumTemp.get(i)/entry.getValue().size());
				String2DoubleNode node=new String2DoubleNode();
				node.key=i.toString();
				node.value=sumTemp.get(i);
				listForSort.add(node);
			}
			Collections.sort(listForSort,Comparator4Sort.comString2Double);
			
			Double sum = 0.0;
			int index = 0;
			ArrayList<Integer> mainTopi = new ArrayList<Integer>();
			while (sum.compareTo(clusterMainTopicTheta) <= 0) {
				if(listForSort.get(index).value.compareTo(meanProability)<=0) break;
				sum += listForSort.get(index).value;
				mainTopi.add(Integer.parseInt(listForSort.get(index).key));
				index++;
			}
			
			ArrayList<ArrayList<String>> topicsTemp=new ArrayList<ArrayList<String>>();
			for (Integer it : mainTopi) {
				ArrayList<String> temp=new ArrayList<String>();
				temp.add("cluster-"+entry.getKey().toString());
				temp.add("");
				temp.add("topic-"+it.toString());
				for(String2DoubleNode node:listForSort){
					if(node.key.equals(it.toString()))
						temp.add(node.value.toString());
				}
				temp.addAll(topic2items.get(it));
				topicsTemp.add(temp);
			}
			cluster2topic2items.addAll(topicsTemp);
			ArrayList<String> splitList=new ArrayList<String>();
			splitList.add("-");
			cluster2topic2items.add(splitList);
			
		}
		int index=0;
		boolean flag=true;
		while(flag){
			flag=false;
			for(int i=0;i<cluster2topic2items.size();i++){
				if(index<cluster2topic2items.get(i).size()){
					bw_out.write(cluster2topic2items.get(i).get(index)+",");
					flag=true;
				}else{
					bw_out.write("-,");
				}
			}
			bw_out.newLine();
			index++;
		}
		bw_out.newLine();
		bw_out.flush();
		bw_out.close();
	}
	
 	public double computeSilhouetteCoefficient(Map<Integer,List<Point>> clusters){
		Map<Point,Integer> point2id=new HashMap<Point,Integer>();
		ArrayList<Point> points=new ArrayList<Point>();
		Integer id=0;
		for(Map.Entry<Integer, List<Point>> entry:clusters.entrySet()){
			List<Point> list=entry.getValue();
			for(Point p:list){
				point2id.put(p, id);
				points.add(p);
				id++;
			}
		}
		Map<String,Double> dis=new HashMap<String,Double>();
		for(Integer i=0;i<points.size();i++){
			Point iPoint=points.get(i);
			for(Integer j=i;j<points.size();j++){
				Point jPoint=points.get(j);
				String key1=i.toString()+"#"+j.toString();
				String key2=j.toString()+"#"+i.toString();
				double distanceTemp=distance(iPoint, jPoint);
				dis.put(key1, distanceTemp);
				dis.put(key2, distanceTemp);
			}
		}
		System.out.println("dis finished");
		Map<String,Double> A=new HashMap<String,Double>();
		for(Map.Entry<Integer, List<Point>> entry:clusters.entrySet()){
			List<Point> list=entry.getValue();
			for(Point p1:list){
				String keyP1=point2id.get(p1).toString();
				Double disSum=0.0;
				for(Point p2:list){
					String keyP2=point2id.get(p2).toString();
					disSum+=dis.get(keyP1+"#"+keyP2);
				}
				disSum/=list.size()-1;
				A.put(keyP1, disSum);
			}
		}
		System.out.println("A finished");
		Map<String,Double> B=new HashMap<String,Double>();
		for(Map.Entry<Integer, List<Point>> entry:clusters.entrySet()){
			Integer key=entry.getKey();
			List<Point> v=entry.getValue();
			for(Point p:v){
				String keyP=point2id.get(p).toString();
				Double min=Double.MAX_VALUE;
				for(Map.Entry<Integer, List<Point>> it:clusters.entrySet()){
					List<Point> list=it.getValue();
					Integer keyIt=it.getKey();
					if(key.equals(keyIt)) continue;
					Double sum=0.0;
					for(Point p3:list){
						sum+=dis.get(keyP+"#"+point2id.get(p3));
					}
					sum/=list.size();
					if(min>sum)
						min=sum;
				}
				B.put(keyP, min);
			}
		}
		System.out.println("B finished");
		Double mean=0.0;
		for(Point p:points){
			String key=point2id.get(p).toString();
			Double a=A.get(key);
			Double b=B.get(key);
			mean+=(b-a)/Math.max(a, b);
		}
		mean/=points.size();
		System.out.println("finished");
		System.out.println(":"+mean);
		return mean;
	}
	public  double distance(Point point1, Point point2) {
		ArrayList<Double> x1 = point1.value;
		ArrayList<Double> x2 = point2.value;
		double distance = 0;
		for (int i = 0; i < x1.size(); i++) {
			distance += Math.pow(x1.get(i) - x2.get(i), 2);
		}
		distance = Math.sqrt(distance);
		return distance;
	}
	public void runIM(String logPath,Integer clusterK,Integer num,Integer topicK) throws CSVConversionException{
		TestVisualIM w = new TestVisualIM();
		PackageManager packages = PackageManager.getInstance();
		//		 Then the plugin manager, as it listens to the package manager
		PluginManagerImpl.initialize(UIPluginContext.class);
		//		
		UIContext globalContext;
		globalContext = new UIContext();
		globalContext.initialize();
		//		final UITopiaController controller = new UITopiaController(globalContext);
		//		globalContext.setController(controller);
		//		globalContext.setFrame(controller.getFrame());
		//		controller.getFrame().setIconImage(ImageLoader.load("prom_icon_32x32.png"));
		//		controller.getFrame().setVisible(true);
		////		controller.getMainView().showWorkspaceView();
		////		controller.getMainView().getWorkspaceView().showFavorites();
		////		globalContext.startup();

//		File file = new File("D:/data4code/bestlog.csv");
		File file = new File(logPath);
		CSVFileReferenceUnivocityImpl csvFile = new CSVFileReferenceUnivocityImpl(file.toPath());
		CSVConfig config = new CSVConfig(csvFile);
		try (ICSVReader reader = csvFile.createReader(config)) {
			CSVConversion conversion = new CSVConversion();
			CSVConversionConfig conversionConfig = new CSVConversionConfig(csvFile, config);
			conversionConfig.autoDetect();
			conversionConfig.setCaseColumns(ImmutableList.of("case"));
			conversionConfig.setEventNameColumns(ImmutableList.of("event"));
			conversionConfig.setCompletionTimeColumn("time");
			conversionConfig.setEmptyCellHandlingMode(CSVEmptyCellHandlingMode.SPARSE);
			conversionConfig.setErrorHandlingMode(CSVErrorHandlingMode.ABORT_ON_ERROR);
			Map<String, CSVMapping> conversionMap = conversionConfig.getConversionMap();
			CSVMapping mapping = conversionMap.get("time");
			mapping.setDataType(Datatype.TIME);
			mapping.setPattern("yyyy-MM-dd");

			final ProgressListener progressListener = new NoOpProgressListenerImpl();
			ConversionResult<XLog> result = conversion.doConvertCSVToXES(progressListener, csvFile, config,
					conversionConfig);
			XLog log = result.getResult();
			System.out.println(log.size());
			PluginContext context = globalContext.getMainPluginContext();
			IMPetriNet ins = new IMPetriNet();
			
			MiningParameters param=new MiningParametersIMi();
			param.setNoiseThreshold((float) 0.2);
			
			Object[] re = ins.minePetriNet(context, log, param);
			PetriNetVisualization v = new PetriNetVisualization();
			JComponent jp = v.visualize(context, (Petrinet) re[0], (Marking) (re[1]));
			w.setContentPane(jp);
			w.setVisible(true);
			w.setTitle(topicK+"-"+num+"-"+clusterK.toString());
			
//			TreeSet<String> labels=new TreeSet<>();
//			for(Integer i=0;i<clusterK;i++){
//				labels.add(i.toString());
//			}
//			labels.add("s");
//			labels.add("e");
//			Petrinet p=(Petrinet)re[0];
//			Set<PetrinetNode>  nodes=p.getNodes();
//			for(PetrinetNode it:nodes){
//				if(labels.contains(it.getLabel())){
//					TreeSet<String> set=getPre(p,it,labels);
//					set.remove(it.getLabel());
//					System.out.println(it.getLabel()+","+set);
//				}
////				System.out.println(it.getLabel());
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public JComponent runIMAndReturn(String logPath,Integer clusterK,Integer num,Integer topicK) throws CSVConversionException, Exception{
		PackageManager packages = PackageManager.getInstance();
		//		 Then the plugin manager, as it listens to the package manager
		PluginManagerImpl.initialize(UIPluginContext.class);
		//		
		UIContext globalContext;
		globalContext = new UIContext();
		globalContext.initialize();
		//		final UITopiaController controller = new UITopiaController(globalContext);
		//		globalContext.setController(controller);
		//		globalContext.setFrame(controller.getFrame());
		//		controller.getFrame().setIconImage(ImageLoader.load("prom_icon_32x32.png"));
		//		controller.getFrame().setVisible(true);
		////		controller.getMainView().showWorkspaceView();
		////		controller.getMainView().getWorkspaceView().showFavorites();
		////		globalContext.startup();

//		File file = new File("D:/data4code/bestlog.csv");
		File file = new File(logPath);
		CSVFileReferenceUnivocityImpl csvFile = new CSVFileReferenceUnivocityImpl(file.toPath());
		CSVConfig config = new CSVConfig(csvFile);
		ICSVReader reader = csvFile.createReader(config);
			CSVConversion conversion = new CSVConversion();
			CSVConversionConfig conversionConfig = new CSVConversionConfig(csvFile, config);
			conversionConfig.autoDetect();
			conversionConfig.setCaseColumns(ImmutableList.of("case"));
			conversionConfig.setEventNameColumns(ImmutableList.of("event"));
			conversionConfig.setCompletionTimeColumn("time");
			conversionConfig.setEmptyCellHandlingMode(CSVEmptyCellHandlingMode.SPARSE);
			conversionConfig.setErrorHandlingMode(CSVErrorHandlingMode.ABORT_ON_ERROR);
			Map<String, CSVMapping> conversionMap = conversionConfig.getConversionMap();
			CSVMapping mapping = conversionMap.get("time");
			mapping.setDataType(Datatype.TIME);
			mapping.setPattern("yyyy-MM-dd");

			final ProgressListener progressListener = new NoOpProgressListenerImpl();
			ConversionResult<XLog> result = conversion.doConvertCSVToXES(progressListener, csvFile, config,
					conversionConfig);
			XLog log = result.getResult();
			System.out.println(log.size());
			PluginContext context = globalContext.getMainPluginContext();
			IMPetriNet ins = new IMPetriNet();
			
			MiningParameters param=new MiningParametersIMi();
			param.setNoiseThreshold((float) 0.2);
			
			Object[] re = ins.minePetriNet(context, log, param);
			PetriNetVisualization v = new PetriNetVisualization();
			JComponent jp = v.visualize(context, (Petrinet) re[0], (Marking) (re[1]));
			
		
		return jp;
	}
	
	public TreeSet<String> getPre(Petrinet p,PetrinetNode node,TreeSet<String> labels){
		TreeSet<String> re=new TreeSet<>();
		Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> c=p.getInEdges(node);
		for(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e:c){
			if(labels.contains(e.getSource().getLabel())){
				re.add(e.getSource().getLabel());
			}else{
				re.addAll(getPre(p,e.getSource(),labels));
			}
		}
		if(re.size()==0){
			re.add("-1");
		}
		return re;
	}
	public static void getPatient2Day2items(Map<Integer, List<Point>> result) throws Exception, FileNotFoundException{
		File file_out = new File(basePath+"Kmeans\\cluster\\cluster2dayitems.csv");
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "gb2312"));//
		
		Map<String,TreeMap<String,TreeSet<String>>> p2days=new HashMap<String,TreeMap<String,TreeSet<String>>>();
		String fileIn=basePath+"I61dot902-log-filter-map-lowfq-merge-guiyi.csv";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn),"UTF-8"));
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
		
		for (Entry<Integer, List<Point>> entry : result.entrySet()) {
			out.write("Cluster-"+entry.getKey());
			out.newLine();
			List<Point> v=entry.getValue();
			ArrayList<ArrayList<String>> list=new ArrayList<ArrayList<String>>();
			for(int i=0;i<v.size();i++){
				ArrayList<String> temp=new ArrayList<String>();
				String docKey=v.get(i).id;
				String[] strs=docKey.split("#");
				String visitId=strs[0];
				String time=strs[1];
				TreeSet<String> cuDay=p2days.get(visitId).get(time);
				for(String it:cuDay)
					temp.add(it);
				list.add(temp);
			}
			boolean flag=true;
			Integer lineNum=0;
			while(flag){
				flag=false;
				for(int j=0;j<list.size();j++){
					if(lineNum<list.get(j).size()){
						flag=true;
						out.write(list.get(j).get(lineNum)+",");
					}else{
						out.write(" "+",");
					}
				}
				out.newLine();
				lineNum++;
			}
		}
		out.flush();
		out.close();
	}
	
}
