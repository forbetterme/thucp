package outlier.dataclean;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import outlier.util.FilesOfFolderDelete;
import outlier.util.Param;
import view.FrameworkMain;
import view.InputData;
import view.InputDataRowType;

public class DataClean {
	public String workSpace;
	public String LDAPath;
	public DataClean(String workSpace){
		this.workSpace=workSpace;
		LDAPath=workSpace+"LDA/";
	}
	
	public static void main(String[] args) throws Exception{
		DataClean ins=new DataClean(Param.homePath+"test/");
		ins.generateCorpusForLDA();
	}
	
	public void generateCorpusForLDA() throws Exception {
		makeDir(workSpace);
		mapLogYuanLai();
//		mapLogYuanLai(Param.originPath + "yuanlai - 带引号-智杰原始.csv");
		before2now();
		mergeSameItemOfSameDay();//合并同一天的相同项目
		numGuiyihuaLog(10);//对数量进行归一化
		getAllItems4MapYuanLai();
		getInputForMalletLDAId2ItemConsiderNum();
	}
	public void makeDir(String basePath){
		 File dir = new File(basePath); 
		 if(!dir.exists()){
			 dir.mkdirs();
		 }
		 File ldaDir=new File(basePath+"LDA\\");
		 if(!ldaDir.exists())
			 ldaDir.mkdirs();
		 
		 File replayDir=new File(basePath+"replay\\");
		 if(!replayDir.exists())
			 replayDir.mkdirs();
		 
		 File kmeansDir=new File(basePath+"Kmeans\\initKPoint\\");
		 if(!kmeansDir.exists())
			 kmeansDir.mkdirs();
		 
		 File kmeansLogDir=new File(basePath+"Kmeans\\log\\");
		 if(!kmeansLogDir.exists())
			 kmeansLogDir.mkdirs();
		 
		 File kmeansClusterDir=new File(basePath+"Kmeans\\cluster\\");
		 if(!kmeansClusterDir.exists())
			 kmeansClusterDir.mkdirs();
		 File patientDir=new File(basePath+"patient\\");
		 if(!patientDir.exists())
			 patientDir.mkdirs();
	}
	public void mapLogYuanLai() throws Exception{
		Map<String,String> map=new HashMap<String,String>();
		String fileMap=Param.originPath+"yuanlai-map-1.0.csv";
		BufferedReader readerMap = new BufferedReader(new InputStreamReader(new FileInputStream(fileMap),"UTF-8"));
		String line="";
		while ((line = readerMap.readLine()) != null) {
			String[] lines=line.split(",");
			if(lines.length!=2){
				System.out.println(line);
			}
			map.put(lines[0], lines[1]);
		}
		readerMap.close();
		
		
		Map<String,String> mapAgain=new HashMap<String,String>();
		fileMap=Param.originPath+"yuanlai-map-3.0.csv";
		readerMap = new BufferedReader(new InputStreamReader(new FileInputStream(fileMap),"UTF-8"));
		while ((line = readerMap.readLine()) != null) {
			String[] lines=line.split(",");
			if(lines.length!=2){
				System.out.println(line);
			}
			mapAgain.put(lines[0], lines[1]);
		}
		readerMap.close();
		
		File file_out = new File(LDAPath+"log-map.csv");
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//
		out.write("visitId,event,eventClass,num,price,zj,time");
		out.newLine();
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		for (Map.Entry<String, InputData> entry : FrameworkMain.paramInputDataSetOfFunc.entrySet()) {
			String type = entry.getValue().getType();
			if (type.equals("log")) {// 只处理日志文件
				InputData value = entry.getValue();
				for (InputDataRowType row : value.getDataForLog()) {
					String visitId = row.getVisitId();
					String date = df.format(row.getTime());
					String event = row.getEvent();
					
					String eventClass=row.getEventClass();
					String num=row.getNum().toString();
					String price=row.getPrice().toString();
					String zj="0.0";
					String time=date;
					if(!map.containsKey(event)){
						System.out.println(event);
					}
					event=map.get(event);
					event=mapAgain.get(event);
					if(event.equals("-")) continue;
					out.write(visitId+","+event+","+eventClass+","+num+","+price+","+zj+","+time);
					out.newLine();
				}
			}
		}
		out.flush();
		out.close();
	}
	public void mapLogYuanLai(String logPath) throws Exception{
		Map<String,String> map=new HashMap<String,String>();
		String fileMap=Param.originPath+"yuanlai-map-1.0.csv";
		BufferedReader readerMap = new BufferedReader(new InputStreamReader(new FileInputStream(fileMap),"UTF-8"));
		String line="";
		while ((line = readerMap.readLine()) != null) {
			String[] lines=line.split(",");
			if(lines.length!=2){
				System.out.println(line);
			}
			map.put(lines[0], lines[1]);
		}
		readerMap.close();
		
		
		Map<String,String> mapAgain=new HashMap<String,String>();
		fileMap=Param.originPath+"yuanlai-map-3.0.csv";
		readerMap = new BufferedReader(new InputStreamReader(new FileInputStream(fileMap),"UTF-8"));
		while ((line = readerMap.readLine()) != null) {
			String[] lines=line.split(",");
			if(lines.length!=2){
				System.out.println(line);
			}
			mapAgain.put(lines[0], lines[1]);
		}
		readerMap.close();
		
		File file_out = new File(LDAPath+"log-map.csv");
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//
		out.write("visitId,event,eventClass,num,price,zj,time");
		out.newLine();
		
		String fileIn=logPath;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn),"gb2312"));
		line=reader.readLine();
		while ((line = reader.readLine()) != null) {
			String[] lines=line.split(",");
			String visitId=lines[0];
			String event=lines[1];
			event=event.substring(1,event.length()-1);
			String eventClass=lines[2];
			eventClass=eventClass.substring(1, eventClass.length()-1);
			String num=lines[3];
			String price=lines[4];
			String zj=lines[5];
			String time=lines[6];
			if(!map.containsKey(event)){
				System.out.println(event);
			}
			event=map.get(event);
			
			
			event=mapAgain.get(event);
			
			
			if(event.equals("-")) continue;
			out.write(visitId+","+event+","+eventClass+","+num+","+price+","+zj+","+time);
			out.newLine();
		}
		reader.close();
		out.flush();
		out.close();
	}
	
	public void before2now() throws Exception, FileNotFoundException{
		File file_out = new File(LDAPath+"log-map-trans.csv");
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//
		out.write("visitId,event,eventClass,num,price,zj,time");
		out.newLine();
		
		String fileIn=LDAPath+"log-map.csv";
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
			
			out.write(visitId+","+event+","+eventClass+","+num+","+price+","+zj+","+time);
			out.newLine();
		}
		reader.close();
		out.flush();
		out.close();
	}
	
	public void mergeSameItemOfSameDay() throws IOException{
		File file_out = new File(LDAPath+"log-map-trans-merge.csv");
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//
		out.write("visitId,event,eventClass,num,price,zj,time");
		out.newLine();
		
		Map<String,HashMap<String,Double>> day2itemsNum=new TreeMap<String,HashMap<String,Double>>();
		String fileIn=LDAPath+"log-map-trans.csv";
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
			
			String dayKey=visitId+"#"+time;
			Double dNum=Double.parseDouble(num);
			if(day2itemsNum.containsKey(dayKey)){
				Double temp=day2itemsNum.get(dayKey).getOrDefault(event, 0.0)+dNum;
				day2itemsNum.get(dayKey).put(event, temp);
			}else{
				HashMap<String,Double> temp=new HashMap<String,Double>();
				temp.put(event, dNum);
				day2itemsNum.put(dayKey, temp);
			}
		}
		reader.close();
		
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		for(Map.Entry<String, HashMap<String,Double>> entry:day2itemsNum.entrySet()){
			String[] strs=entry.getKey().split("#");
			String visitId=strs[0];
			String time=strs[1];
			for(Map.Entry<String, Double> it:entry.getValue().entrySet()){
				out.write(visitId+","+it.getKey()+","+"-"+","+df.format(it.getValue())+","+"-"+","+"-"+","+time);
				out.newLine();
			}
		}
		out.flush();
		out.close();
	}
	
	public void numGuiyihuaLog(Integer scale) throws Exception, FileNotFoundException{
		File file_out = new File(LDAPath+"log-map-trans-merge-guiyi.csv");
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//
		out.write("visitId,event,eventClass,num,price,zj,time");
		out.newLine();
		
		String fileIn=LDAPath+"log-map-trans-merge.csv";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn),"UTF-8"));
		String line="";
		line=reader.readLine();
		Map<String,Double> max=new HashMap<String,Double>();
		while ((line = reader.readLine()) != null) {
			String[] lines=line.split(",");
			String visitId=lines[0];
			String event=lines[1];
			String eventClass=lines[2];
			String num=lines[3];
			String price=lines[4];
			String zj=lines[5];
			String time=lines[6];
			Double dNum=Math.abs(Double.parseDouble(num));
			if(max.containsKey(event)){
				if(dNum.compareTo(max.get(event))>0){
					max.put(event, dNum);
				}
			}else{
				max.put(event, dNum);
			}
		}
		reader.close();
		
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn),"UTF-8"));
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
			Double dNum=Double.parseDouble(num);
			
			Double guiyi=dNum/max.get(event);
//			if(guiyi.compareTo(1.0)>0)
//				System.out.println(line);
			guiyi=Math.abs(guiyi)*scale;
			Integer guiyiInt=guiyi.intValue()+1;
			if(event.contains("CT")){
				guiyiInt+=Param.CTscale;
			}
//			if(event.contains("磁")){
//				guiyiInt+=Param.MRIscale;
//			}
//			if(event.contains("血肿清除")){
//				guiyiInt+=Param.XueZhongQingChuscale;
//			}
//			if(guiyiInt>11)
//				System.out.println(line);
			out.write(visitId+","+event+","+eventClass+","+guiyiInt+","+price+","+zj+","+time);
			out.newLine();
		}
		out.flush();
		out.close();
	}
	
	public void getAllItems4MapYuanLai() throws IOException{
		File file_out = new File(LDAPath+"allItems.csv");
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//
		
		String fileIn=LDAPath+"log-map-trans-merge-guiyi.csv";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn),"UTF-8"));
		String line="";
		line=reader.readLine();
		TreeSet<String> set=new TreeSet<String>();
		while ((line = reader.readLine()) != null) {
			String[] lines=line.split(",");
			String visitId=lines[0];
			String event=lines[1];
			String eventClass=lines[2];
			String num=lines[3];
			String price=lines[4];
			String zj=lines[5];
			String time=lines[6];
			set.add(event);
		}
		reader.close();
		for(String it:set){
			out.write(it+","+it);
			out.newLine();
		}
		out.flush();
		out.close();
	}
	
	public void getInputForMalletLDAId2ItemConsiderNum() throws Exception{
		File file_out = new File(LDAPath+"id2item.csv");
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//
		
		TreeSet<String> items=new TreeSet<String>();
		Map<String,String> item2id=new TreeMap<String,String>();
		Map<String,String> id2item=new TreeMap<String,String>();
		
		String fileIn=LDAPath+"log-map-trans-merge-guiyi.csv";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn),"UTF-8"));
		String line="";
		line=reader.readLine();
		while ((line = reader.readLine()) != null) {
			String[] lines=line.split(",");
			String visitId=lines[0];
			String event=lines[1];
			items.add(event);
			String eventClass=lines[2];
			String num=lines[3];
			String price=lines[4];
			String zj=lines[5];
			String time=lines[6];
		}
		Integer id=0;
		for(String it:items){
			id2item.put(id.toString(), it);
			item2id.put(it, id.toString());
			id++;
		}
		reader.close();
		for(Map.Entry<String, String> entry:id2item.entrySet()){
			out.write(entry.getKey()+"\t"+entry.getValue());
			out.newLine();
		}
		out.flush();
		out.close();
		
//		Map<String,TreeSet<String>> doc2items=new TreeMap<String,TreeSet<String>>();
		Map<String,ArrayList<String>> doc2items=new TreeMap<String,ArrayList<String>>();
		File file_out_doc2items = new File(LDAPath+"doc2items.csv");
		BufferedWriter out_doc2items = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out_doc2items), "UTF-8"));//
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn),"UTF-8"));
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
			String docKey=visitId+"#"+time;
			Integer count=Integer.parseInt(num);
			if(doc2items.containsKey(docKey)){
				for(int i=0;i<count;i++)
					doc2items.get(docKey).add(item2id.get(event));
			}else{
				ArrayList<String> temp=new ArrayList<String>();
				for(int i=0;i<count;i++)
					temp.add(item2id.get(event));
				doc2items.put(docKey, temp);
			}
		}
		reader.close();
		for(Map.Entry<String, ArrayList<String>> entry:doc2items.entrySet()){
			out_doc2items.write(entry.getKey()+",-,");
			for(String it:entry.getValue()){
				out_doc2items.write(it+" ");
			}
			out_doc2items.newLine();
		}
		out_doc2items.flush();
		out_doc2items.close();
	}
}
