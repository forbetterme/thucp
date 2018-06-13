package test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class FindAdvancedCase {
	public static void main(String[] args) throws IOException{
		FindAdvancedCase ins =new FindAdvancedCase();
		HashMap<String,String> map1=ins.getCaseId2Fitness("D:\\alpha\\pro\\eclipse\\thucp\\data\\OutlierDetection\\replay\\alignment-info - 副本.csv");
		HashMap<String,String> map2=ins.getCaseId2Fitness("D:\\alpha\\pro\\eclipse\\thucp\\data\\OutlierDetection\\replay\\alignment-info.csv");
		for(Map.Entry<String, String> entry:map1.entrySet()){
			if(!entry.getValue().equals(map2.get(entry.getKey()))){
				System.out.println(entry.getKey()+","+entry.getValue());
			}
		}
	}
	public HashMap<String,String> getCaseId2Fitness(String path) throws IOException{
		HashMap<String,String> re=new HashMap<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));
		String line="";
		while ((line = reader.readLine()) != null) {
			String[] lines=line.split(",");
			Double fit1=Double.parseDouble(lines[5].substring(20));
			Double fit2=Double.parseDouble(lines[8].substring(18));
			String str=fit1.toString()+"#"+fit2.toString();
			String caseId=lines[0];
			re.put(caseId, str);
			
		}
		reader.close();
		return re;
	}
}
