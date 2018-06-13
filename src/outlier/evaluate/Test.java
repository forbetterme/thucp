package outlier.evaluate;

import outlier.util.Param;
import test.AlignmentTest;

public class Test {
	public static void main(String[] args) throws Exception{
		SimulationEvaluate simu=new SimulationEvaluate();
		simu.generate(simu);
		
		AlignmentTest testAlign=new AlignmentTest();
		Param.ifAdvance=0;
		testAlign.testSimulation(args);
		Param.ifAdvance=1;
		testAlign.testSimulation(args);
	}
}
