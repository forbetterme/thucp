package outlier.evaluate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import org.jbpt.petri.PetriNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

import outlier.util.Param;

public class SimulationEvaluate {
	int loopNum = 3;

	int n1 = 800 * 8 / 10;
	int n2 = 200 * 8 / 10;
	int n11 = 640 * 8 / 10;
	int n12 = 160 * 8 / 10;
	int n21 = 160 * 8 / 10;
	int n22 = 40 * 8 / 10;

	int n3 = 800 * 2 / 10;
	int n4 = 200 * 2 / 10;
	int n31 = 640 * 2 / 10;
	int n32 = 160 * 2 / 10;
	int n41 = 160 * 2 / 10;
	int n42 = 40 * 2 / 10;

	int eventNum = n11 * 14 + n12 * 16 + n21 * 15 + n22 * 17 + (n31 * 12 + n32 * 14 + n41 * 13 + n42 * 15);
	int eventTypeNum = Param.evaluateEventNum;

	int outlierInsertCount;
	int outlierSkipCount;

//	double[][] disDouble;
//	int nodeSize = Param.evaluateEventNum;

	public static void main(String[] args) throws Exception {
		SimulationEvaluate ins = new SimulationEvaluate();

		ins.generate(ins);
		// ins.evaluate(ins);
	}

	public void generate(SimulationEvaluate ins) throws Exception {
		ins.generateLogConsiderFreq(Param.rawTracePath);
		ins.changeTrace2LogFormat(Param.rawTracePath, Param.rawLogPath);
		ins.generateOutlier(Param.rawTracePath, Param.stdAlignPath, Param.outlierTracePath);
		// ins.generateOutlierBasedDistance(Param.rawTracePath,
		// Param.stdAlignPath, Param.outlierTracePath);
		ins.changeTrace2LogFormat(Param.outlierTracePath, Param.outlierLogPath);
	}

	public void evaluate(SimulationEvaluate ins) throws Exception {
		ins.computeRecallAndAccuracy(Param.stdAlignPath, Param.alignPath);
		// ins.findDiff(Param.alignPathStdCost, Param.alignPath);
	}

	public void generateLogConsiderFreq(String rawTracePath) throws Exception {
		File file_out = new File(rawTracePath);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "utf-8"));//

		LinkedList<String> trace;
		Integer id = 0;

		for (int k = 0; k < n11; k++) {
			trace = new LinkedList<>();
			trace.add("0");
			getLoopTimesAndAppendSeq(trace, "1", loopNum);
			trace.add("2");
			trace.add("3");
			trace.add("7");
			getParallelSeqAndAppend(trace);
			trace.add("13");
			getLoopTimesAndAppendSeq(trace, "14,15", loopNum);
			getParallelSeqAndAppend19_20(trace);
			trace.add("16");
			insertParallelSeq(trace, "17");
			insertParallelLoop(trace, "18", loopNum);
			System.out.println(trace);

			out.write(id.toString());
			for (String it : trace) {
				out.write("," + it);
			}
			out.newLine();
			id++;
		}

		for (int k = 0; k < n12; k++) {
			trace = new LinkedList<>();
			trace.add("0");
			getLoopTimesAndAppendSeq(trace, "1", loopNum);
			trace.add("2");
			trace.add("3");
			trace.add("7");
			getParallelSeqAndAppend(trace);
			trace.add("11");
			trace.add("12");
			trace.add("13");
			getLoopTimesAndAppendSeq(trace, "14,15", loopNum);
			getParallelSeqAndAppend19_20(trace);
			trace.add("16");
			insertParallelSeq(trace, "17");
			insertParallelLoop(trace, "18", loopNum);
			System.out.println(trace);

			out.write(id.toString());
			for (String it : trace) {
				out.write("," + it);
			}
			out.newLine();
			id++;
		}

		for (int k = 0; k < n21; k++) {
			trace = new LinkedList<>();
			trace.add("0");
			getLoopTimesAndAppendSeq(trace, "1", loopNum);
			trace.add("4");
			trace.add("5");
			trace.add("6");
			trace.add("7");
			getParallelSeqAndAppend(trace);
			trace.add("13");
			getLoopTimesAndAppendSeq(trace, "14,15", loopNum);
			getParallelSeqAndAppend19_20(trace);
			trace.add("16");
			insertParallelSeq(trace, "17");
			insertParallelLoop(trace, "18", loopNum);
			System.out.println(trace);

			out.write(id.toString());
			for (String it : trace) {
				out.write("," + it);
			}
			out.newLine();
			id++;
		}

		for (int k = 0; k < n22; k++) {
			trace = new LinkedList<>();
			trace.add("0");
			getLoopTimesAndAppendSeq(trace, "1", loopNum);
			trace.add("4");
			trace.add("5");
			trace.add("6");
			trace.add("7");
			getParallelSeqAndAppend(trace);
			trace.add("11");
			trace.add("12");
			trace.add("13");
			getLoopTimesAndAppendSeq(trace, "14,15", loopNum);
			getParallelSeqAndAppend19_20(trace);
			trace.add("16");
			insertParallelSeq(trace, "17");
			insertParallelLoop(trace, "18", loopNum);
			System.out.println(trace);

			out.write(id.toString());
			for (String it : trace) {
				out.write("," + it);
			}
			out.newLine();
			id++;
		}

		/////

		for (int k = 0; k < n31; k++) {
			trace = new LinkedList<>();
			trace.add("0");
			getLoopTimesAndAppendSeq(trace, "1", loopNum);
			trace.add("2");
			trace.add("3");
			trace.add("7");
			getParallelSeqAndAppend(trace);
			trace.add("13");
			getLoopTimesAndAppendSeq(trace, "14,15", loopNum);
			// getParallelSeqAndAppend19_20(trace);
			trace.add("16");
			insertParallelSeq(trace, "17");
			insertParallelLoop(trace, "18", loopNum);
			System.out.println(trace);

			out.write(id.toString());
			for (String it : trace) {
				out.write("," + it);
			}
			out.newLine();
			id++;
		}

		for (int k = 0; k < n32; k++) {
			trace = new LinkedList<>();
			trace.add("0");
			getLoopTimesAndAppendSeq(trace, "1", loopNum);
			trace.add("2");
			trace.add("3");
			trace.add("7");
			getParallelSeqAndAppend(trace);
			trace.add("11");
			trace.add("12");
			trace.add("13");
			getLoopTimesAndAppendSeq(trace, "14,15", loopNum);
			// getParallelSeqAndAppend19_20(trace);
			trace.add("16");
			insertParallelSeq(trace, "17");
			insertParallelLoop(trace, "18", loopNum);
			System.out.println(trace);

			out.write(id.toString());
			for (String it : trace) {
				out.write("," + it);
			}
			out.newLine();
			id++;
		}

		for (int k = 0; k < n41; k++) {
			trace = new LinkedList<>();
			trace.add("0");
			getLoopTimesAndAppendSeq(trace, "1", loopNum);
			trace.add("4");
			trace.add("5");
			trace.add("6");
			trace.add("7");
			getParallelSeqAndAppend(trace);
			trace.add("13");
			getLoopTimesAndAppendSeq(trace, "14,15", loopNum);
			// getParallelSeqAndAppend19_20(trace);
			trace.add("16");
			insertParallelSeq(trace, "17");
			insertParallelLoop(trace, "18", loopNum);
			System.out.println(trace);

			out.write(id.toString());
			for (String it : trace) {
				out.write("," + it);
			}
			out.newLine();
			id++;
		}

		for (int k = 0; k < n42; k++) {
			trace = new LinkedList<>();
			trace.add("0");
			getLoopTimesAndAppendSeq(trace, "1", loopNum);
			trace.add("4");
			trace.add("5");
			trace.add("6");
			trace.add("7");
			getParallelSeqAndAppend(trace);
			trace.add("11");
			trace.add("12");
			trace.add("13");
			getLoopTimesAndAppendSeq(trace, "14,15", loopNum);
			// getParallelSeqAndAppend19_20(trace);
			trace.add("16");
			insertParallelSeq(trace, "17");
			insertParallelLoop(trace, "18", loopNum);
			System.out.println(trace);

			out.write(id.toString());
			for (String it : trace) {
				out.write("," + it);
			}
			out.newLine();
			id++;
		}

		out.flush();
		out.close();
	}

	public void getLoopTimesAndAppendSeq(LinkedList<String> trace, String str, int loopNum) {
		String[] strs = str.split(",");
		Random rand = new Random();
		int loopTime = rand.nextInt(loopNum) + 1;
		for (int i = 0; i < loopTime; i++) {
			for (int j = 0; j < strs.length; j++) {
				trace.add(strs[j]);
			}
		}
	}

	public void getParallelSeqAndAppend(LinkedList<String> trace) {
		ArrayList<String> allParallelSeq = new ArrayList<>();
		allParallelSeq.add("10,8,9");
		allParallelSeq.add("8,10,9");
		allParallelSeq.add("8,9,10");
		Random rand = new Random();
		int index = rand.nextInt(allParallelSeq.size());
		String[] strs = allParallelSeq.get(index).split(",");
		for (int j = 0; j < strs.length; j++) {
			trace.add(strs[j]);
		}
	}

	public void getParallelSeqAndAppend19_20(LinkedList<String> trace) {
		ArrayList<String> allParallelSeq = new ArrayList<>();
		allParallelSeq.add("19,20");
		allParallelSeq.add("20,19");
		Random rand = new Random();
		int index = rand.nextInt(allParallelSeq.size());
		String[] strs = allParallelSeq.get(index).split(",");
		for (int j = 0; j < strs.length; j++) {
			trace.add(strs[j]);
		}
	}

	public void insertParallelSeq(LinkedList<String> trace, String str) {
		int len = trace.size();
		Random rand = new Random();
		String[] strs = str.split(",");
		for (int j = 0; j < strs.length; j++) {
			int index = rand.nextInt(len);
			if (index == 0)
				index = 1;
			trace.add(index, strs[j]);
		}
	}

	public void insertParallelLoop(LinkedList<String> trace, String str, int loopNum) {//对于多重循环可能会出错，一个循环插入到另一个循环内部
		int len = trace.size();
		Random rand = new Random();
		String[] strs = str.split(",");
		int loopTime = rand.nextInt(loopNum) + 1;
		for (int i = 0; i < loopTime; i++) {
			for (int j = 0; j < strs.length; j++) {
				int index = rand.nextInt(len);
				if (index == 0)
					index = 1;
				trace.add(index, strs[j]);
			}
		}
	}

	public void changeTrace2LogFormat(String fileIn, String fileOut) throws Exception {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Date start = sf.parse("2018-03-01");

		File file_out = new File(fileOut);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "utf-8"));//
		out.write("case,event,time");
		out.newLine();

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn), "UTF-8"));
		String line = "";
		while ((line = reader.readLine()) != null) {
			String[] lines = line.split(",");
			String id = lines[0];
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(start);
			for (int i = 1; i < lines.length; i++) {
				out.write(id + "," + lines[i] + "," + gc.get(Calendar.YEAR) + "-" + (int) (gc.get(Calendar.MONTH) + 1)
						+ "-" + gc.get(Calendar.DATE));
				gc.add(5, 1);
				out.newLine();
			}
		}
		reader.close();
		out.flush();
		out.close();
	}

	public void generateOutlier(String fileIn, String fileOutStdAlign, String fileOutLogWithOutlier)
			throws IOException {
		Random rand = new Random();
		int randBound = 10;
		Map<Integer, Integer> index2num = new HashMap<>();
		Double bound = eventNum * 0.1;
		int intBound = bound.intValue();
		outlierInsertCount = intBound;
		for (int i = 0; i < intBound; i++) {
			Integer index = rand.nextInt(eventNum);
			index2num.put(index, index2num.getOrDefault(index, 0) + 1);
		}

		BufferedWriter out = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(new File(fileOutStdAlign)), "utf-8"));//

		BufferedWriter out_trace = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(new File(fileOutLogWithOutlier)), "utf-8"));//

		int count = 0;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn), "UTF-8"));
		String line = "";
		while ((line = reader.readLine()) != null) {
			String[] lines = line.split(",");
			String id = lines[0];
			out.write(id);
			out_trace.write(id);
			int randNum = -1;
			for (int i = 1; i < lines.length; i++) {
				randNum = rand.nextInt(randBound);
				if (randNum == 0) {
					outlierSkipCount++;
					out.write("," + lines[i] + "#" + "MREAL");
				} else {
					out.write("," + lines[i] + "#" + "LMGOOD");
					out_trace.write("," + lines[i]);
				}
				if (index2num.containsKey(count)) {
					for (int j = 0; j < index2num.get(count); j++) {
						int indexInsert = rand.nextInt(eventTypeNum);
						out.write("," + indexInsert + "#" + "L");
						out_trace.write("," + indexInsert);
					}
				}
				count++;
			}
			out.newLine();
			out_trace.newLine();
		}
		reader.close();
		out.flush();
		out.close();
		out_trace.flush();
		out_trace.close();
		System.out.println(outlierInsertCount + "," + outlierSkipCount + "," + (outlierInsertCount + outlierSkipCount));
	}

//	public void generateOutlierBasedDistance(String fileIn, String fileOutStdAlign, String fileOutLogWithOutlier)//暂时无用，用于根据距离来生成插入异常
//			throws IOException {
//		getProbabilityBasedOnDistance();
//
//		Random rand = new Random();
//		int randBound = 10;
//		Map<Integer, Integer> index2num = new HashMap<>();
//		Double bound = eventNum * 0.1;
//		int intBound = bound.intValue();
//		outlierInsertCount = intBound;
//		for (int i = 0; i < intBound; i++) {
//			Integer index = rand.nextInt(eventNum);
//			index2num.put(index, index2num.getOrDefault(index, 0) + 1);
//		}
//
//		BufferedWriter out = new BufferedWriter(
//				new OutputStreamWriter(new FileOutputStream(new File(fileOutStdAlign)), "utf-8"));//
//
//		BufferedWriter out_trace = new BufferedWriter(
//				new OutputStreamWriter(new FileOutputStream(new File(fileOutLogWithOutlier)), "utf-8"));//
//
//		int count = 0;
//		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn), "UTF-8"));
//		String line = "";
//		while ((line = reader.readLine()) != null) {
//			String[] lines = line.split(",");
//			String id = lines[0];
//			out.write(id);
//			out_trace.write(id);
//			int randNum = -1;
//			for (int i = 1; i < lines.length; i++) {
//				randNum = rand.nextInt(randBound);
//				if (randNum == 1) {
//					outlierSkipCount++;
//					out.write("," + lines[i] + "#" + "MREAL");
//				} else if (randNum > 1) {
//					out.write("," + lines[i] + "#" + "LMGOOD");
//					out_trace.write("," + lines[i]);
//				}
//				if (index2num.containsKey(count)) {
//					for (int j = 0; j < index2num.get(count); j++) {
//						int indexInsert = getInsertAfter(Integer.parseInt(lines[i]));
//						out.write("," + indexInsert + "#" + "L");
//						out_trace.write("," + indexInsert);
//					}
//				}
//				count++;
//			}
//			out.newLine();
//			out_trace.newLine();
//		}
//		reader.close();
//		out.flush();
//		out.close();
//		out_trace.flush();
//		out_trace.close();
//		System.out.println(outlierInsertCount + "," + outlierSkipCount + "," + (outlierInsertCount + outlierSkipCount));
//	}

//	public void computeRate(String stdAlignPath, String alignPath) throws IOException {//暂时无用，用于计算完全识别准确的案例数量
//
//		Map<String, String> stdAlignMap = new HashMap<String, String>();
//		Map<String, String> alignMap = new HashMap<String, String>();
//
//		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(stdAlignPath), "UTF-8"));
//		BufferedReader reader_align = new BufferedReader(
//				new InputStreamReader(new FileInputStream(alignPath), "UTF-8"));
//		String line = "";
//		while ((line = reader.readLine()) != null) {
//			String[] lines = line.split(",");
//			String id = lines[0];
//			String value = "";
//			for (int i = 1; i < lines.length; i++) {
//				String[] labelAndType = lines[i].split("#");
//				if (!labelAndType[0].contains("tv")) {
//					value += lines[i] + ",";
//				}
//			}
//			stdAlignMap.put(id, value);
//
//			line = reader_align.readLine();
//			lines = line.split(",");
//			id = lines[0];
//			value = "";
//			for (int i = 1; i < lines.length; i++) {
//				String[] labelAndType = lines[i].split("#");
//				if (!labelAndType[0].contains("tv")) {
//					value += lines[i] + ",";
//				}
//			}
//			alignMap.put(id, value);
//		}
//		reader.close();
//		reader_align.close();
//
//		int count = 0;
//		for (Map.Entry<String, String> entry : stdAlignMap.entrySet()) {
//			if (entry.getValue().equals(alignMap.get(entry.getKey())))
//				count++;
//		}
//		System.out.println(count + "," + count / 1000.0);
//	}

	public void computeRecallAndAccuracy(String stdAlignPath, String alignPath) throws IOException {// event
		// 粒度

		Map<String, ArrayList<String>> stdAlignMap = new HashMap<String, ArrayList<String>>();
		Map<String, ArrayList<String>> alignMap = new HashMap<String, ArrayList<String>>();

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(stdAlignPath), "UTF-8"));
		BufferedReader reader_align = new BufferedReader(
				new InputStreamReader(new FileInputStream(alignPath), "UTF-8"));
		String line = "";
		while ((line = reader.readLine()) != null) {
			// System.out.println("-"+line);
			String[] lines = line.split(",");
			String id = lines[0];
			ArrayList<String> value = new ArrayList<String>();
			for (int i = 1; i < lines.length; i++) {
				String[] strs=lines[i].split("#");
				String t=strs[0];
				String type=strs[1];
				if (!t.contains("tv") && !type.equals("LMGOOD")) {
					if(type.equals("MREAL"))
						value.add(lines[i]);
					else if(type.equals("L")){
						int flag=0;
						for(int j=i-1;j>=1;j--){
							String[] strsIn=lines[j].split("#");
							String tIn=strsIn[0];
							String typeIn=strsIn[1];
							if(tIn.contains("tv")) continue;
							if(typeIn.equals("LMGOOD")||typeIn.equals("MREAL")){
								flag=1;
								value.add(tIn+"#"+lines[i]);
								break;
							}
						}
						if(flag==0){
							value.add("-1"+"#"+lines[i]);
						}
					}
				}
			}
			stdAlignMap.put(id, value);
		}
		reader.close();

		while ((line = reader_align.readLine()) != null) {
			String[] lines = line.split(",");
			String id = lines[0];
			ArrayList<String> value = new ArrayList<String>();
			for (int i = 1; i < lines.length; i++) {
				String[] strs=lines[i].split("#");
				String t=strs[0];
				String type=strs[1];
				if (!t.contains("tv") && !type.equals("LMGOOD")) {
					if(type.equals("MREAL"))
						value.add(lines[i]);
					else if(type.equals("L")){
						int flag=0;
						for(int j=i-1;j>=1;j--){
							String[] strsIn=lines[j].split("#");
							String tIn=strsIn[0];
							String typeIn=strsIn[1];
							if(tIn.contains("tv")) continue;
							if(typeIn.equals("LMGOOD")||typeIn.equals("MREAL")){//typeIn
								flag=1;
								value.add(tIn+"#"+lines[i]);
								break;
							}
						}
						if(flag==0){
							value.add("-1"+"#"+lines[i]);
						}
					}
				}
			}
			alignMap.put(id, value);
		}
		reader_align.close();

		if (stdAlignMap.size() != alignMap.size()) {
			System.out.println("false" + "," + stdAlignMap.size() + "," + alignMap.size());
		} else {
			System.out.println("true");
		}
		getFitCount(stdAlignMap, alignMap);
		getFitCount(alignMap, stdAlignMap);
	}

	public int getFitCount(Map<String, ArrayList<String>> stdAlignMap, Map<String, ArrayList<String>> alignMap) {
		int count = 0;
		int allCount = 0;
		for (Map.Entry<String, ArrayList<String>> entry : stdAlignMap.entrySet()) {
			ArrayList<String> stdList = entry.getValue();
			ArrayList<String> list = alignMap.get(entry.getKey());
			boolean[] flag = new boolean[list.size()];
			for (String it : stdList) {
				allCount++;
				for (int i = 0; i < flag.length; i++) {
					if (!flag[i] && list.get(i).equals(it)) {
						flag[i] = true;
						count++;
						break;//
					}
				}
			}
		}
		System.out.println(count + "," + allCount + "," + count * 1.0 / allCount);
		return count;
	}

	public void findDiff(String stdAlignPath, String alignPath) throws Exception, FileNotFoundException {
		Map<String, ArrayList<String>> stdAlignMap = new HashMap<String, ArrayList<String>>();
		Map<String, ArrayList<String>> alignMap = new HashMap<String, ArrayList<String>>();

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(stdAlignPath), "UTF-8"));
		BufferedReader reader_align = new BufferedReader(
				new InputStreamReader(new FileInputStream(alignPath), "UTF-8"));
		String line = "";
		while ((line = reader.readLine()) != null) {
			String[] lines = line.split(",");
			String id = lines[0];
			ArrayList<String> value = new ArrayList<String>();
			for (int i = 1; i < lines.length; i++) {
				if (!lines[i].contains("tv") && !lines[i].contains("LMGOOD")) {
					value.add(lines[i]);
				}
			}
			stdAlignMap.put(id, value);

			line = reader_align.readLine();
			lines = line.split(",");
			id = lines[0];
			value = new ArrayList<String>();
			for (int i = 1; i < lines.length; i++) {
				if (!lines[i].contains("tv") && !lines[i].contains("LMGOOD")) {
					value.add(lines[i]);
				}
			}
			alignMap.put(id, value);
		}
		reader.close();
		reader_align.close();

		for (Map.Entry<String, ArrayList<String>> entry : stdAlignMap.entrySet()) {
			ArrayList<String> stdList = entry.getValue();
			ArrayList<String> list = alignMap.get(entry.getKey());
			boolean[] flag = new boolean[list.size()];
			for (String it : stdList) {
				for (int i = 0; i < flag.length; i++) {
					if (!flag[i] && list.get(i).equals(it)) {
						flag[i] = true;
					}
				}
			}
			int adFlag = 0;
			for (int i = 0; i < flag.length; i++) {
				if (!flag[i]) {
					adFlag = 1;
				}
			}
			if (adFlag == 1) {
				System.out.println(entry.getKey());
			}
		}
	}

//	public void getProbabilityBasedOnDistance() {
//		String[] paths = new String[6];
//		paths[0] = "0,1,2,3,7,8,9,10,13,14,15,16";
//		paths[1] = "0,1,2,3,7,8,9,10,11,12,13,14,15,16";
//		paths[2] = "0,1,4,5,6,7,8,9,10,13,14,15,16";
//		paths[3] = "0,1,4,5,6,7,8,9,10,11,12,13,14,15,16";
//
//		paths[4] = "0,1,2,3,7,8,10,9,11,12,13,14,15,16";
//		paths[5] = "0,1,4,5,6,7,8,10,9,11,12,13,14,15,16";
//		int[][] dis = new int[nodeSize][nodeSize];
//		disDouble = new double[nodeSize][nodeSize];
//		for (int i = 0; i < paths.length; i++) {
//			String[] strs = paths[i].split(",");
//			for (int j = 0; j < strs.length; j++) {
//				Integer cu = Integer.parseInt(strs[j]);
//				for (int k = 0; k < strs.length; k++) {
//					if (k == j)
//						continue;
//					Integer other = Integer.parseInt(strs[k]);
//					if (dis[cu][other] == 0 || k - j < dis[cu][other]) {
//						dis[cu][other] = k - j;
//					}
//				}
//			}
//		}
//		for (int i = 0; i < nodeSize; i++) {
//			for (int j = 0; j < nodeSize; j++) {
//				if (dis[i][j] < 0)
//					dis[i][j] = 0;
//			}
//		}
//		for (int i = 0; i < nodeSize; i++) {
//			for (int j = 0; j < nodeSize; j++) {
//				System.out.print(dis[i][j] + " ");
//			}
//			System.out.println();
//		}
//		for (int i = 0; i < nodeSize; i++) {
//			int max = Integer.MIN_VALUE;
//			for (int j = 0; j < nodeSize; j++) {
//				if (dis[i][j] > max)
//					max = dis[i][j];
//			}
//			if (max == 0) {
//				for (int j = 0; j < nodeSize; j++) {
//					disDouble[i][j] = 0.0;
//				}
//			} else {
//				for (int j = 0; j < nodeSize; j++) {
//					if (dis[i][j] != 0)
//						disDouble[i][j] = Math.pow(-1 / Math.log((1 - dis[i][j] * 1.0 / max)), 2);
//					else
//						disDouble[i][j] = 0.0;
//				}
//			}
//		}
//		for (int i = 0; i < nodeSize; i++) {
//			double sum = 0;
//			for (int j = 0; j < nodeSize; j++) {
//				sum += disDouble[i][j];
//			}
//			if (sum != 0.0) {
//				for (int j = 0; j < nodeSize; j++) {
//					disDouble[i][j] /= sum;
//				}
//			}
//		}
//		System.out.println();
//		for (int i = 0; i < nodeSize; i++) {
//			for (int j = 0; j < nodeSize; j++) {
//				System.out.print(disDouble[i][j] + " ");
//			}
//			System.out.println();
//		}
//	}
//
//	public int getInsertAfter(int x) {
//		Random rand = new Random();
//		double randDouble = rand.nextDouble();
//		double sum = 0.0;
//		for (int j = 0; j < nodeSize; j++) {
//			if (disDouble[x][j] == 0.0)
//				continue;
//			sum += disDouble[x][j];
//			if (randDouble <= sum) {
//				return j;
//			}
//		}
//		return nodeSize - 1;
//	}
}
