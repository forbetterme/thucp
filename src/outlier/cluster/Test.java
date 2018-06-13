package outlier.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Test {
	public static void main(String[] args){
		String id1="1";
		ArrayList<Double> v1=new ArrayList<Double>();
		v1.add(1.0);
		v1.add(2.0);
		Point p1=new Point(id1,v1);
		
		String id2="2";
		ArrayList<Double> v2=new ArrayList<Double>();
		v2.add(1.0);
		v2.add(2.0);
		Point p2=new Point(id2,v2);
		
		HashSet<Point> s=new HashSet<Point>();
		s.add(p1);
		Point p3=p2;
		s.add(p3);
		System.out.println(s.contains(p3));
	}
}
