package outlier.evaluate;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.packages.PackageManager;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.impl.PluginManagerImpl;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.PetriNetVisualization;

import outlier.util.Param;
import test.TestVisualIM;

public class ConstructPetriNet {
	public static void main(String[] args){
		ConstructPetriNet ins=new ConstructPetriNet();
		ins.getNet();
	}
	public Petrinet getNet(){
		Petrinet net = PetrinetFactory.newPetrinet("net");
		Place pStart = net.addPlace("start");
		Place p0_1 = net.addPlace("p0_1");
		Place p1_v1 = net.addPlace("p1_v1");
		Place pv1_2 = net.addPlace("pv1_2");
		Place p2_3 = net.addPlace("p2_3");
		Place p3_7 = net.addPlace("p3_7");
		Place p4_5 = net.addPlace("p4_5");
		Place p5_6 = net.addPlace("p5_6");
		Place p7_v2 = net.addPlace("p7_v2");
		Place pv2_8 = net.addPlace("pv2_8");
		Place p8_9 = net.addPlace("p8_9");
		Place p9_v3 = net.addPlace("p9_v3");
		Place pv2_10 = net.addPlace("pv2_10");
		Place p10_v3 = net.addPlace("p10_v3");
		Place pv3_v4 = net.addPlace("pv3_v4");
		Place p11_12 = net.addPlace("p11_12");
		Place pv4_13 = net.addPlace("pv4_13");
		Place p13_14 = net.addPlace("p13_14");
		Place p14_15 = net.addPlace("p14_15");
		Place p15_v7 = net.addPlace("p15_v7");
		
		Place pv7_v8 = net.addPlace("pv7_v8");
		Place pv9_19 = net.addPlace("pv9_19");
		Place pv9_20 = net.addPlace("pv9_20");
		Place p19_v10 = net.addPlace("p19_v10");
		Place p20_v10 = net.addPlace("p20_v10");
		Place pv10_16 = net.addPlace("pv10_16");
		
		Place p0_17 = net.addPlace("p0_17");
		Place p17_16 = net.addPlace("p17_16");
		Place p0_18 = net.addPlace("p0_18");
		Place p18_16 = net.addPlace("p18_16");
		Place pEnd = net.addPlace("end");
		
		int tNum=Param.evaluateEventNum;
		Transition[] ts=new Transition[tNum];
		for(Integer i=0;i<tNum;i++)
			ts[i]=net.addTransition(i.toString());
		
		Transition tv0 = net.addTransition("tv0");tv0.setInvisible(true);
		Transition tv1 = net.addTransition("tv1");tv1.setInvisible(true);
		Transition tv2 = net.addTransition("tv2");tv2.setInvisible(true);
		Transition tv3 = net.addTransition("tv3");tv3.setInvisible(true);
		Transition tv4 = net.addTransition("tv4");tv4.setInvisible(true);
		Transition tv5 = net.addTransition("tv5");tv5.setInvisible(true);
		Transition tv6 = net.addTransition("tv6");tv6.setInvisible(true);
		Transition tv7 = net.addTransition("tv7");tv7.setInvisible(true);
		Transition tv8 = net.addTransition("tv8");tv8.setInvisible(true);
		Transition tv9 = net.addTransition("tv9");tv9.setInvisible(true);
		Transition tv10 = net.addTransition("tv10");tv10.setInvisible(true);
		
		net.addArc(ts[0],p0_17);
		net.addArc(p0_17,ts[17]);
		net.addArc(ts[17],p17_16);
		net.addArc(p17_16,ts[16]);
		
		net.addArc(pStart, ts[0]);
		net.addArc(ts[0], p0_1);
		net.addArc(p0_1,ts[1]);
		net.addArc(ts[1],p1_v1);
		net.addArc(p1_v1,tv1);
		net.addArc(tv1,pv1_2);
		net.addArc(p1_v1,tv0);
		net.addArc(tv0,p0_1);
		net.addArc(pv1_2,ts[2]);
		net.addArc(ts[2],p2_3);
		net.addArc(p2_3,ts[3]);
		net.addArc(ts[3],p3_7);
		net.addArc(pv1_2,ts[4]);
		net.addArc(ts[4],p4_5);
		net.addArc(p4_5,ts[5]);
		net.addArc(ts[5],p5_6);
		net.addArc(p5_6,ts[6]);
		net.addArc(ts[6],p3_7);
		net.addArc(p3_7,ts[7]);
		net.addArc(ts[7],p7_v2);
		net.addArc(p7_v2,tv2);
		net.addArc(tv2,pv2_8);
		net.addArc(pv2_8,ts[8]);
		net.addArc(ts[8],p8_9);
		net.addArc(p8_9,ts[9]);
		net.addArc(ts[9],p9_v3);
		net.addArc(p9_v3,tv3);
		net.addArc(tv2,pv2_10);
		net.addArc(pv2_10,ts[10]);
		net.addArc(ts[10],p10_v3);
		net.addArc(p10_v3,tv3);
		net.addArc(tv3,pv3_v4);
		net.addArc(pv3_v4,ts[11]);
		net.addArc(ts[11],p11_12);
		net.addArc(p11_12,ts[12]);
		net.addArc(ts[12],pv4_13);
		net.addArc(pv4_13,ts[13]);
		net.addArc(pv3_v4,tv4);
		net.addArc(tv4,pv4_13);
		net.addArc(ts[13],p13_14);
		net.addArc(p13_14,ts[14]);
		net.addArc(ts[14],p14_15);
		net.addArc(p14_15,ts[15]);
		
		net.addArc(ts[15],p15_v7);
		net.addArc(p15_v7,tv5);
		net.addArc(tv5,p13_14);
		net.addArc(p15_v7,tv7);
		net.addArc(tv7,pv7_v8);
		net.addArc(pv7_v8,tv9);
		net.addArc(tv9,pv9_19);
		net.addArc(tv9,pv9_20);
		net.addArc(pv9_19,ts[19]);
		net.addArc(pv9_20,ts[20]);
		net.addArc(ts[19],p19_v10);
		net.addArc(ts[20],p20_v10);
		net.addArc(p19_v10,tv10);
		net.addArc(p20_v10,tv10);
		net.addArc(tv10,pv10_16);
		net.addArc(pv10_16,ts[16]);
		net.addArc(pv7_v8,tv8);
		net.addArc(tv8,pv10_16);
		
		net.addArc(ts[16],pEnd);
		
		net.addArc(ts[0],p0_18);
		net.addArc(p0_18,ts[18]);
		net.addArc(ts[18],p18_16);
		net.addArc(p18_16,ts[16]);
		net.addArc(p18_16,tv6);
		net.addArc(tv6,p0_18);
		
//		PackageManager packages = PackageManager.getInstance();
//		PluginManagerImpl.initialize(UIPluginContext.class);
//		UIContext globalContext;
//		globalContext = new UIContext();
//		globalContext.initialize();
//		PluginContext context = globalContext.getMainPluginContext();
//		
//		TestVisualIM w = new TestVisualIM();
//		PetriNetVisualization v = new PetriNetVisualization();
//		JComponent jp = v.visualize(context,net, null);
//		w.setContentPane(jp);
//		w.setVisible(true);
//		w.setTitle("fangzhen");
		
		return net;
	}
}
