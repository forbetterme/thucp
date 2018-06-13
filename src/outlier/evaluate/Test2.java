package outlier.evaluate;

import outlier.util.Param;

public class Test2 {//寻找改进成本之前与之后，对齐方式发生变化的id
	public static void main(String[] args) throws Exception{
		SimulationEvaluate simu=new SimulationEvaluate();
		simu.findDiff(Param.alignPathStdCost, Param.alignPath);
	}
}
