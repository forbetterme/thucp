package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LuPing {
	public static void main(String[] args) throws Exception{
		Map<String,ArrayList<String>> map=new HashMap<>();
		BufferedReader reader;
		reader = new BufferedReader(new FileReader("D:\\alpha\\pro\\eclipse\\thucp\\data\\OutlierDetection\\cluster\\I61dot902-kmeans-11.csv"));
		String line = reader.readLine();
		while ((line = reader.readLine()) != null) {
			String[] lines=line.split(",");
			if(map.containsKey(lines[0])){
				map.get(lines[0]).add(line);
			}else{
				ArrayList<String> temp=new ArrayList<>();
				temp.add(line);
				map.put(lines[0], temp);
			}
		}
		
		File file_out = new File("D:\\alpha\\pro\\eclipse\\thucp\\data\\OutlierDetection\\cluster\\I61dot902-kmeans-11-align.csv");
		BufferedWriter bw_out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//
		bw_out.write("case,event,time");
		bw_out.newLine();;
		int index=0;
		for(Map.Entry<String, ArrayList<String>> entry:map.entrySet()){
			if(index++>20) break;
			for(String it:entry.getValue()){
				bw_out.write(it);
				bw_out.newLine();
			}
		}
		bw_out.flush();
		bw_out.close();
	}
}
