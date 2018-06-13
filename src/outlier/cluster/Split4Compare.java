package outlier.cluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMi;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;
import org.processmining.plugins.petrinet.PetriNetVisualization;

import com.google.common.collect.ImmutableList;

import outlier.util.Param;
import test.TestVisualIM;

public class Split4Compare {
	public static void main(String[] args) throws Exception, Exception{
		Split4Compare ins = new Split4Compare();
//		ins.exploreSpecificPatientColum();
//		ins.splitLog();
		ArrayList<String> allLogPaths = ins.splitLogByYear();//注意1900和2018这样的开始和结束节点
		for(String path:allLogPaths){
			ins.runIM(path);
		}
	}

	public void exploreSpecificPatientColum() throws Exception, FileNotFoundException{
		File file_out = new File(Param.basePath+"patient\\qianPatient.csv");
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "gb2312"));//
		
		Map<String,TreeMap<String,TreeSet<String>>> p2days=new HashMap<String,TreeMap<String,TreeSet<String>>>();
		String fileIn=Param.basePath+"I61dot902-log-filter-map-lowfq-merge-guiyi.csv";
//		String fileIn=Param.basePath+"I61dot902-log-filter-map.csv";
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
			event=event+"="+price;
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
		TreeSet<String> timeSet=new TreeSet<String>();
		for(Map.Entry<String, TreeMap<String,TreeSet<String>>> entry:p2days.entrySet()){
			String visitId=entry.getKey();
			TreeMap<String,TreeSet<String>> day2items=entry.getValue();
			String time="";
			for(Map.Entry<String, TreeSet<String>> it:day2items.entrySet()){
				time=it.getKey();
				break;
			}
			timeSet.add(time+"#"+visitId);
		}
		reader.close();
		
		System.out.println(p2days.size());
		String splitTime="2012-01-01";
		for(String it:timeSet){
			System.out.println(it);
			String[] strs=it.split("#");
			String visitId=strs[1];
			String time=strs[0];
			if(time.compareTo(splitTime)<0){
				out.write(visitId);
				out.newLine();
			}
		}
		
		out.flush();
		out.close();
	}
	public void splitLog() throws Exception{
		String qianPatientPath=Param.basePath+"patient\\qianPatient.csv";
		Set<String> houPatient=getHouPatient(qianPatientPath);
		
		File file_out_hou = new File(Param.basePath+"Kmeans\\log\\houLog.csv");
		BufferedWriter outHou = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out_hou), "UTF-8"));//
		outHou.write("case,event,time");
		outHou.newLine();
		
		File file_out_qian = new File(Param.basePath+"Kmeans\\log\\qianLog.csv");
		BufferedWriter outQian = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out_qian), "UTF-8"));//
		outQian.write("case,event,time");
		outQian.newLine();
		
		String logPath=Param.basePath+"Kmeans\\log\\I61dot902-kmeans-"+"11"+".csv";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(logPath),"UTF-8"));
		String line=reader.readLine();
		while ((line = reader.readLine()) != null) {
			String[] lines=line.split(",");
			String visitId=lines[0];
			if(houPatient.contains(visitId)){
				outQian.write(line);
				outQian.newLine();
			}else{
				outHou.write(line);
				outHou.newLine();
			}
		}
		reader.close();
		
		outHou.flush();
		outHou.close();
		outQian.flush();
		outQian.close();
	}
	public Set<String> getHouPatient(String qianPatientPath) throws Exception{
		Set<String> re=new HashSet<String>();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(qianPatientPath),"UTF-8"));
		String line="";
		while ((line = reader.readLine()) != null) {
			re.add(line);
		}
		reader.close();
		
		return re;
	}
	
	public ArrayList<String> splitLogByYear() throws Exception{
		ArrayList<String> allLogPaths=new ArrayList<String>();
		TreeMap<String,ArrayList<String>> year2lines=new TreeMap<String,ArrayList<String>>();
		
		String logPath=Param.basePath+"Kmeans\\log\\I61dot902-kmeans-"+"11"+".csv";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(logPath),"UTF-8"));
		String line=reader.readLine();
		while ((line = reader.readLine()) != null) {
			String[] lines=line.split(",");
			String time=lines[2];
			String year=time.split("-")[0];
			if(year2lines.containsKey(year))
				year2lines.get(year).add(line);
			else{
				ArrayList<String> temp=new ArrayList<String>();
				temp.add(line);
				year2lines.put(year, temp);
			}
		}
		reader.close();
		
		for(Map.Entry<String, ArrayList<String>> entry:year2lines.entrySet()){
			if(entry.getKey().equals("1900")||entry.getKey().equals("2018"))
				continue;
			Set<String> patientSet4StartAndEnd=new HashSet<String>();
			for(String it:entry.getValue()){
				patientSet4StartAndEnd.add(it.split(",")[0]);
			}
			File file_out = new File(Param.basePath+"Kmeans\\log\\"+entry.getKey()+"-Log.csv");
			allLogPaths.add(Param.basePath+"Kmeans\\log\\"+entry.getKey()+"-Log.csv");
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//
			out.write("case,event,time");
			out.newLine();
			for(String it:entry.getValue()){
				out.write(it);
				out.newLine();
			}
			for(String it:patientSet4StartAndEnd){
				out.write(it+","+"s"+","+"1900-01-01");
				out.newLine();
				out.write(it+","+"e"+","+"2018-01-01");
				out.newLine();
			}
			out.flush();
			out.close();
		}
		return allLogPaths;
	}
	public void runIM(String logPath) throws CSVConversionException{
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
			w.setTitle(logPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
