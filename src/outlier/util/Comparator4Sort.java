package outlier.util;

import java.util.Comparator;

public class Comparator4Sort {
	public static Comparator<String2DoubleNode> comString2Double=new Comparator<String2DoubleNode>() {

		@Override
		public int compare(String2DoubleNode o1, String2DoubleNode o2) {
			// TODO Auto-generated method stub
			return o2.value.compareTo(o1.value);
		}
	};
	public static Comparator<Double> comDoubleDesc=new Comparator<Double>() {

		@Override
		public int compare(Double o1, Double o2) {
			// TODO Auto-generated method stub
			return o2.compareTo(o1);
		}
	};
}
