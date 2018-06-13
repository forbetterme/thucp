package outlier.cost;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import outlier.util.Param;


public class ComputeCostBasedOnFreq {
	Comparator<Event> com = new Comparator<Event>() {

		public int compare(Event o1, Event o2) {
			// TODO Auto-generated method stub
			return o1.date.compareTo(o2.date);
		}
	};
	HashMap<String, HashSet<String>> pre = new HashMap<String, HashSet<String>>();
	HashMap<String, HashSet<String>> post = new HashMap<String, HashSet<String>>();
	Map<String, ArrayList<Event>> patientId2events = new HashMap<String, ArrayList<Event>>();
	Set<String> allEventLabels = new HashSet<String>();
	Map<String,Integer> cacheForFreq=new HashMap<String,Integer>();
	Map<String,Integer> cacheForFreqInsert=new HashMap<String,Integer>();

	public static void main(String[] args) throws Exception {
		ComputeCostBasedOnFreq ins = new ComputeCostBasedOnFreq();
		 ins.initPreMapping(Param.prePath);
		// for(Map.Entry<String,HashSet<String>> it:ins.pre.entrySet()){
		// for(String s:it.getValue()){
		// System.out.print(s+" ");
		// }
		// System.out.println();
		// }
		ins.getEventSeqForEachPatient(Param.logPath);
		ins.removeOneLoop();
//		ins.splitLog();
//		for (Map.Entry<String, ArrayList<Event>> entry : ins.patientId2events.entrySet()) {
//			ArrayList<Event> value=entry.getValue();
//			for(Event s:value){
//				System.out.print(s.label+" ");
//			}
//			System.out.println();
//		}
//		Set<String> set1=new HashSet<String>();
//		set1.add("0");
//		set1.add("2");
//		set1.add("6");
//		set1.add("7");
//		set1.add("13");
//		String tar="e";
//		System.out.println(ins.countFreqTargetEventAfterPre(set1, tar));
		Set<String> set1=new HashSet<String>();
		set1.add("6");
//		System.out.println(ins.costOfTargetEventAfterPre(set1, "9"));
//		System.out.println(ins.costOfTargetEventAfterPre("8","0"));
//		System.out.println(ins.costOfTargetEventAfterPre("9","0"));
//		System.out.println(ins.costOfTargetEventAfterPre("4","0"));
//		System.out.println(ins.countFreqCuNeverOccurAfterTarget("8","0"));
//		System.out.println(ins.countFreqCuNeverOccurAfterTarget("9","0"));
//		System.out.println(ins.countFreqCuNeverOccurAfterTarget("4","0"));
		System.out.println(ins.countFreqTargetEventAfterPre(set1, "0"));
		System.out.println(ins.countFreqCuNeverOccurAfterTarget("9","10"));
	}

	public void initPreMapping(String preMappingFilePath) throws IOException {
		File f = new File(preMappingFilePath);
		BufferedReader read = new BufferedReader(new FileReader(f));
		String line = "";
		while ((line = read.readLine()) != null) {
			String[] strs = line.split(",");
			HashSet<String> setTemp = new HashSet<String>();
			for (int i = 1; i < strs.length; i++)
				setTemp.add(strs[i]);
			pre.put(strs[0], setTemp);
		}
		read.close();
		for (Map.Entry<String, HashSet<String>> it : pre.entrySet()) {
			HashSet<String> value=it.getValue();
			for(String s:value){
				if(post.containsKey(s)){
					post.get(s).add(it.getKey());
				}else{
					HashSet<String> temp=new HashSet<String>();
					temp.add(it.getKey());
					post.put(s, temp);
				}
			}
		}
		HashSet<String> temp=new HashSet<String>();
		temp.add("-1");
		post.put("e", temp);
	}

	public void getEventSeqForEachPatient(String inPath) throws Exception {
		File f = new File(inPath);
		BufferedReader read = new BufferedReader(new FileReader(f));
		String line = read.readLine();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		while ((line = read.readLine()) != null) {
			String[] strs = line.split(",");
			String caseId = strs[0];
			allEventLabels.add(strs[1]);
			Event e = new Event(caseId, strs[1], df.parse(strs[2]));
			if (patientId2events.get(caseId) == null) {
				ArrayList<Event> temp = new ArrayList<Event>();
				temp.add(e);
				patientId2events.put(caseId, temp);
			} else {
				patientId2events.get(caseId).add(e);
			}
		}
		read.close();
		for (Map.Entry<String, ArrayList<Event>> entry : patientId2events.entrySet()) {
			Collections.sort(entry.getValue(), com);
			// for(Event it:entry.getValue()){
			// System.out.print(it.getLabel());
			// }
			// System.out.println();
			if(entry.getKey().equals("752B4032D9EC0A485B272CCA1462DAE2")){
				File outFile=new File("D:/data4code/cluster/LogBasedOnKmeansPlusPlus-14single.csv");
				BufferedWriter out=new BufferedWriter(new FileWriter(outFile));
				out.write("BRBM"+","+"XM"+","+"RQ");
				out.newLine();
				for(int i=0;i<entry.getValue().size();i++){
					Event e=entry.getValue().get(i);
					out.write(e.patientId+","+e.label+","+df.format(e.date));
					out.newLine();
				}
				out.flush();
				out.close();
			}
		}
	}
	
	public void getEventSeqForEachPatientAfterAlign(String inPath) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		File f = new File(inPath);
		BufferedReader read = new BufferedReader(new FileReader(f));
		String line ;
		while ((line = read.readLine()) != null) {
			String[] strs = line.split(",");
			String caseId = strs[0];
			ArrayList<Event> temp = new ArrayList<Event>();
			for(int i=1;i<strs.length;i++){
				if(strs[i].contains("LMGOOD")||strs[i].contains("MREAL")){
					allEventLabels.add(strs[i].split("#")[0]);
					Event e = new Event(caseId, strs[i].split("#")[0],  df.parse("1900/01/01"));
					temp.add(e);
				}
			}
			if(temp.size()==0)
				System.out.println(caseId);
			patientId2events.put(caseId, temp);
		}
		read.close();
	}
	
	public void splitLog() throws IOException{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		File outFile=new File("D:/data4code/cluster/LogBasedOnKmeansPlusPlus-0-99.csv");
		BufferedWriter out=new BufferedWriter(new FileWriter(outFile));
		out.write("BRBM"+","+"XM"+","+"RQ");
		out.newLine();
		File outFile2=new File("D:/data4code/cluster/LogBasedOnKmeansPlusPlus-100-199.csv");
		BufferedWriter out2=new BufferedWriter(new FileWriter(outFile2));
		out2.write("BRBM"+","+"XM"+","+"RQ");
		out2.newLine();
		File outFile3=new File("D:/data4code/cluster/LogBasedOnKmeansPlusPlus-200-240.csv");
		BufferedWriter out3=new BufferedWriter(new FileWriter(outFile3));
		out3.write("BRBM"+","+"XM"+","+"RQ");
		out3.newLine();
		int count=0;
		for (Map.Entry<String, ArrayList<Event>> entry : patientId2events.entrySet()) {
			int x=count/100;
			if(x==0){
				for(int i=0;i<entry.getValue().size();i++){
					Event e=entry.getValue().get(i);
					out.write(e.patientId+","+e.label+","+df.format(e.date));
					out.newLine();
				}
			}else if(x==1){
				for(int i=0;i<entry.getValue().size();i++){
					Event e=entry.getValue().get(i);
					out2.write(e.patientId+","+e.label+","+df.format(e.date));
					out2.newLine();
				}
			}else{
				if(count>210) continue;
				for(int i=0;i<entry.getValue().size();i++){
					Event e=entry.getValue().get(i);
					out3.write(e.patientId+","+e.label+","+df.format(e.date));
					out3.newLine();
				}
			}
			count++;
			
		}

		out.flush();
		out.close();

		out2.flush();
		out2.close();

		out3.flush();
		out3.close();
	}
	public void removeOneLoop(){
		for (Map.Entry<String, ArrayList<Event>> entry : patientId2events.entrySet()) {
			ArrayList<Event> value=entry.getValue();
			ArrayList<Event> newEventSeqAfterRemove=new ArrayList<Event>();
			newEventSeqAfterRemove.add(value.get(0));
			String qian=value.get(0).label;
			for(int i=1;i<value.size();i++){
				if(value.get(i).label.equals(qian)) continue;
				else{
					newEventSeqAfterRemove.add(value.get(i));
					qian=value.get(i).label;
				}
			}
			patientId2events.put(entry.getKey(), newEventSeqAfterRemove);
		}
	}
	int getLastIndexOfTargetPre(Set<String> targetPre,String cuEvent,String caseId){
		Set<String> cuPre=new HashSet<String>();
		ArrayList<Event> seq=patientId2events.get(caseId);
		Set<String> allPre=pre.get(cuEvent);
//		if(allPre==null)
//			System.out.println(cuEvent);
		int re=-1;
		for(int i=0;i<seq.size();i++){
			Event eventTemp=seq.get(i);
//			if(allPre==null){
//				System.out.println(cuEvent);
//			}
//			System.out.println(allPre.toString()+"#"+eventTemp.label);
			if(allPre.contains(eventTemp.label)){
				cuPre.add(eventTemp.label);
				re=i;
			}
		}
		
		if(cuPre.size()==targetPre.size()){
			boolean flag=true;
			for(String s:cuPre){
				if(!targetPre.contains(s)){
					flag=false;
					break;
				}
			}
			if(flag){
				return re;
			}
		}
		return -1;
	}
	
	public Comparator<Event> getCom() {
		return com;
	}

	public HashMap<String, HashSet<String>> getPre() {
		return pre;
	}

	public Map<String, ArrayList<Event>> getPatientId2events() {
		return patientId2events;
	}

	public Set<String> getAllEventLabels() {
		return allEventLabels;
	}

	public Map<String, Integer> getCacheForFreq() {
		return cacheForFreq;
	}

//	public int countFreqTargetEventAfterPre(Set<String> targetPre,String cuEvent){
//		String key=getKeyOfTargetPreAndCuEvent(targetPre, cuEvent);
//		if(cacheForFreq.containsKey(key))
//			return cacheForFreq.get(key);
//		int count=0;
//		for (Map.Entry<String, ArrayList<Event>> entry : patientId2events.entrySet()) {
//			int index=getLastIndexOfTargetPre(targetPre,cuEvent,entry.getKey());
//			ArrayList<Event> seq=entry.getValue();
//			if(index>0){
//				for(int i=index+1;i<seq.size();i++){
//					if(seq.get(i).label.equals(cuEvent)){
//						count++;
//						break;
//					}
//				}
//			}
//		}
//		cacheForFreq.put(key, count);
//		return count;
//	}
	
	public int countFreqTargetEventAfterPre(Set<String> targetPre,String cuEvent){
		String key=getKeyOfTargetPreAndCuEvent(targetPre, cuEvent);
		if(cacheForFreq.containsKey(key))
			return cacheForFreq.get(key);
		Set<String> cuStaticPre=pre.get(cuEvent); 
		Set<String> cuPre=new HashSet<String>();
		int count=0;
		for (Map.Entry<String, ArrayList<Event>> entry : patientId2events.entrySet()) {
			ArrayList<Event> seq=entry.getValue();
			int index=0;
			for(index=seq.size()-1;index>=0;index--){
				if(seq.get(index).label.equals(cuEvent))
					break;
			}
			if(index>0){
				for(int i=index-1;i>=0;i--){
					if(cuStaticPre.contains(seq.get(i).label)){
						cuPre.add(seq.get(i).label);
					}
				}
			}
			int flag=0;
			if(cuPre.size()==targetPre.size()){
				flag=1;
				for(String it:cuPre){
					if(!targetPre.contains(it)){
						flag=0;
						break;
					}
				}
			}
			if(flag==1)
				count++;
		}
		cacheForFreq.put(key, count);
		return count;
	}
	public int countFreqCuNeverOccurAfterTarget(String tar,String cu){
		if(cacheForFreqInsert.containsKey(tar+"#"+cu))
			return cacheForFreqInsert.get(tar+"#"+cu);
		int count=0;
		for (Map.Entry<String, ArrayList<Event>> entry : patientId2events.entrySet()) {
			ArrayList<Event> seq=entry.getValue();
			Set<String> temp=new HashSet<String>();
			int i=0;
			for(i=0;i<seq.size();i++){
				if(seq.get(i).getLabel().equals(tar)) break;
			}
			for(i=i+1;i<seq.size();i++)
				temp.add(seq.get(i).getLabel());
			if(temp.size()!=0&&!temp.contains(cu))
				count++;
		}
		if(count==0) count=1;
		cacheForFreqInsert.put(tar+"#"+cu, count);
		return count;
	}
//	public int countFreqCuNeverOccurAfterTarget(String tar,String cu){
//		if(cacheForFreqInsert.containsKey(tar+"#"+cu))
//			return cacheForFreqInsert.get(tar+"#"+cu);
//		int count=0;
//		for (Map.Entry<String, ArrayList<Event>> entry : patientId2events.entrySet()) {
//			ArrayList<Event> seq=entry.getValue();
//			Set<String> temp=new HashSet<String>();
//			int i=0;
//			for(i=0;i<seq.size();i++){
//				if(seq.get(i).getLabel().equals(tar)) break;
//			}
//			for(i=i+1;i<seq.size();i++)
//				temp.add(seq.get(i).getLabel());
//			if(!temp.contains(cu))
//				count++;
//		}
//		if(count==0) count=1;
//		cacheForFreqInsert.put(tar+"#"+cu, count);
//		return count;
//	}
	public double costOfTargetEventAfterPre(Set<String> targetPre,String cuEvent){
		int count=countFreqTargetEventAfterPre(targetPre, cuEvent);
		if(count==0) count=1;
		double p=(count*1.0)/patientId2events.size();
//		double cost=1+1-p;
//		double cost=1+1*Math.log(1/p)/Math.log(Param.traceNum);
		double cost=1+1*Math.log(1/p);
//		System.out.println(cost);
//		if(count>10)
//		System.out.println(count);
		if(Param.ifAdvance==0)
			return 1;
		else
			return cost;
	}
	public double costOfTargetEventAfterPre(String tar,String cu){
		int count=countFreqCuNeverOccurAfterTarget(tar, cu);
		double p=(count*1.0)/patientId2events.size();
//		double cost=1+1-p;
//		double cost=1+1*Math.log(1/p)/Math.log(Param.traceNum);
		double cost=1+1*Math.log(1/p);
//		System.out.println(cost);
//		if(count>2)
//			System.out.println(count);
		if(Param.ifAdvance==0)
			return 1;
		else
			return cost;
	}
	public String getKeyOfTargetPreAndCuEvent(Set<String> targetPre,String cuEvent){
		ArrayList<String> arrayForSort=new ArrayList<String>();
		for(String s:targetPre){
			arrayForSort.add(s);
		}
		String key=cuEvent;
		Collections.sort(arrayForSort);
		for(String s:arrayForSort){
			key+=("#"+s);
		}
		return key;
	}
}
