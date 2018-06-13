package test;

import java.awt.Container;
import java.io.File;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import outlier.launch.AlignLaunch;

//import org.deckfour.xes.in.XParser;
//import org.deckfour.xes.in.XesXmlParser;
//import org.deckfour.xes.model.XLog;
//import org.processmining.models.heuristics.HeuristicsNet;
//import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.FlexibleHeuristicsMinerPlugin;
//import org.processmining.plugins.heuristicsnet.visualizer.HeuristicsNetAnnotatedVisualization;

import java.awt.BorderLayout;
import java.awt.Color;

public class Test extends JFrame{
	public static void main(String[] args) throws Exception{
		Test test = new Test();
		test.init();
	}

	public void init() throws Exception {
//		AlignLaunch.lauch("data/OutlierDetection/cluster/paper.csv");
//		File file = new File("D:/result.xes");
//		XParser x = new XesXmlParser();
//		boolean flag = x.canParse(file);
//		List<XLog> xlogs = x.parse(file);
//
//		HeuristicsNet hNet = FlexibleHeuristicsMinerPlugin.run(null, xlogs.get(0));
//		JPanel pan = (JPanel) HeuristicsNetAnnotatedVisualization.visualize(null, hNet);
//
//		Container cont = getContentPane();
//		cont.add(pan, BorderLayout.CENTER);
//
//		this.setBounds(0, 0, 800, 400);
//		this.setVisible(true);
		
//		double d=1.324;
//		int cost=(int)(d*100);
//		System.out.println(cost);
		String str=",,,,-,,,-,,-,,,,,-,,,-,,,,,,-,,,,-,,,-,,,,,-,,-,,,-,,,,,-,";
		System.out.println(str.split(",").length);
	}
}
