package test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.jbpt.petri.Flow;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.io.PNMLSerializer;
import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.packages.PackageManager;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.impl.PluginManagerImpl;
import org.processmining.log.csv.CSVFileReferenceUnivocityImpl;
import org.processmining.log.csv.ICSVReader;
import org.processmining.log.csv.config.CSVConfig;
import org.processmining.log.csvimport.CSVConversion;
import org.processmining.log.csvimport.CSVConversion.ConversionResult;
import org.processmining.log.csvimport.CSVConversion.NoOpProgressListenerImpl;
import org.processmining.log.csvimport.CSVConversion.ProgressListener;
import org.processmining.log.csvimport.config.CSVConversionConfig;
import org.processmining.log.csvimport.config.CSVConversionConfig.CSVEmptyCellHandlingMode;
import org.processmining.log.csvimport.config.CSVConversionConfig.CSVErrorHandlingMode;
import org.processmining.log.csvimport.config.CSVConversionConfig.CSVMapping;
import org.processmining.log.csvimport.config.CSVConversionConfig.Datatype;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMi;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;
import org.processmining.plugins.astar.petrinet.AbstractPetrinetReplayer;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithILP;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithoutILP;
import org.processmining.plugins.astar.petrinet.impl.AbstractPILPDelegate;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.visualization.PNLogReplayResultVisPanel;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

import com.google.common.collect.ImmutableList;

import nl.tue.astar.AStarException;
import outlier.evaluate.SimulationEvaluate;
import outlier.evaluate.ConstructPetriNet;
import outlier.util.Param;

public class AlignmentTest {

	public static int iteration = 0;

	//	static {
	//		try {
	//			System.loadLibrary("lpsolve55");
	//			System.loadLibrary("lpsolve55j");
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//	}

	public static void main(String[] args) throws Exception {
		if(Param.ifSimulate==0)
			test(args);//普通对齐
		else
			testSimulation(args);//仿真部分对齐
	}

	public static void test(String[] args) throws Exception {
		PackageManager packages = PackageManager.getInstance();
		//		 Then the plugin manager, as it listens to the package manager
		PluginManagerImpl.initialize(UIPluginContext.class);
		//		
		UIContext globalContext;
		globalContext = new UIContext();
		globalContext.initialize();
		//		final UITopiaController controller = new UITopiaController(globalContext);
		//		globalContext.setController(controller);
		//		globalContext.setFrame(controller.getFrame());
		//		controller.getFrame().setIconImage(ImageLoader.load("prom_icon_32x32.png"));
		//		controller.getFrame().setVisible(true);
		////		controller.getMainView().showWorkspaceView();
		////		controller.getMainView().getWorkspaceView().showFavorites();
		////		globalContext.startup();

//		File file = new File("D:/data4code/bestlog.csv");
		File file = new File(Param.logPath);
//		File file = new File(Param.rawLogPath);
		CSVFileReferenceUnivocityImpl csvFile = new CSVFileReferenceUnivocityImpl(file.toPath());
		CSVConfig config = new CSVConfig(csvFile);
		try (ICSVReader reader = csvFile.createReader(config)) {
			CSVConversion conversion = new CSVConversion();
			CSVConversionConfig conversionConfig = new CSVConversionConfig(csvFile, config);
			conversionConfig.autoDetect();
			conversionConfig.setCaseColumns(ImmutableList.of("case"));
			conversionConfig.setEventNameColumns(ImmutableList.of("event"));
			conversionConfig.setCompletionTimeColumn("time");
			conversionConfig.setEmptyCellHandlingMode(CSVEmptyCellHandlingMode.SPARSE);
			conversionConfig.setErrorHandlingMode(CSVErrorHandlingMode.ABORT_ON_ERROR);
			Map<String, CSVMapping> conversionMap = conversionConfig.getConversionMap();
			CSVMapping mapping = conversionMap.get("time");
			mapping.setDataType(Datatype.TIME);
			mapping.setPattern("yyyy-MM-dd");

			final ProgressListener progressListener = new NoOpProgressListenerImpl();
			ConversionResult<XLog> result = conversion.doConvertCSVToXES(progressListener, csvFile, config,
					conversionConfig);
			XLog log = result.getResult();
			System.out.println(log.size());
			PluginContext context = globalContext.getMainPluginContext();
			IMPetriNet ins = new IMPetriNet();
			Object[] re = ins.minePetriNet(context, log, new MiningParametersIMi());

			AbstractPILPDelegate.setDebugMode(null);

//			ComputeCostBasedOnFreq insComputeCostBasedOnFreq = new ComputeCostBasedOnFreq();
//			insComputeCostBasedOnFreq.initPreMapping("D:/data4code/replay/pre.csv");
//			insComputeCostBasedOnFreq.getEventSeqForEachPatient("D:/data4code/cluster/LogBasedOnKmeansPlusPlus-14.csv");
//			insComputeCostBasedOnFreq.removeOneLoop();

			PetrinetGraph net = (PetrinetGraph) re[0];
			Marking initialMarking = (Marking) re[1];System.out.println(initialMarking.toString());
			Marking[] finalMarkings = { (Marking) re[2] }; // only one marking is used so far
			System.out.println(finalMarkings.length+","+finalMarkings[0].toString());
			Map<Transition, Integer> costMOS = null; // movements on system
			Map<XEventClass, Integer> costMOT = null; // movements on trace
			TransEvClassMapping mappingTransEvClassMapping = null;
			//			XParserRegistry temp=XParserRegistry.instance();
			//			temp.setCurrentDefault(new XesXmlParser());
			//			log = temp.currentDefault().parse(new File("D:/data4code/mm.xes")).get(0);
			//			log = XParserRegistry.instance().currentDefault().parse(new File("d:/temp/BPI2013all90.xes.gz")).get(0);
			//			log = XParserRegistry.instance().currentDefault().parse(new File("d:/temp/BPI 730858110.xes.gz")).get(0);
			//			log = XFactoryRegistry.instance().currentDefault().openLog();
			costMOS = constructMOSCostFunction(net);
			XEventClass dummyEvClass = new XEventClass("DUMMY", 99999);
			XEventClassifier eventClassifier = XLogInfoImpl.NAME_CLASSIFIER;
			costMOT = constructMOTCostFunction(net, log, eventClassifier, dummyEvClass);
			mappingTransEvClassMapping = constructMapping(net, log, dummyEvClass, eventClassifier);
			costMOS = constructMOSCostFunction(net);
			int cost1 = AlignmentTest.computeCost(costMOS, costMOT, initialMarking, finalMarkings, context, net, log,
					mappingTransEvClassMapping, false);
			System.out.println(cost1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testSimulation(String[] args) throws Exception {
		PackageManager packages = PackageManager.getInstance();
		PluginManagerImpl.initialize(UIPluginContext.class);
		UIContext globalContext;
		globalContext = new UIContext();
		globalContext.initialize();

//		File file = new File("D:/data4code/bestlog.csv");
		Param.logPath=Param.outlierLogPath;
//		Param.logPath=Param.rawLogPath;//测试生成的原始序列是否能够100%匹配过程模型
		File file = new File(Param.logPath);
		CSVFileReferenceUnivocityImpl csvFile = new CSVFileReferenceUnivocityImpl(file.toPath());
		CSVConfig config = new CSVConfig(csvFile);
		try (ICSVReader reader = csvFile.createReader(config)) {
			CSVConversion conversion = new CSVConversion();
			CSVConversionConfig conversionConfig = new CSVConversionConfig(csvFile, config);
			conversionConfig.autoDetect();
			conversionConfig.setCaseColumns(ImmutableList.of("case"));
			conversionConfig.setEventNameColumns(ImmutableList.of("event"));
			conversionConfig.setCompletionTimeColumn("time");
			conversionConfig.setEmptyCellHandlingMode(CSVEmptyCellHandlingMode.SPARSE);
			conversionConfig.setErrorHandlingMode(CSVErrorHandlingMode.ABORT_ON_ERROR);
			Map<String, CSVMapping> conversionMap = conversionConfig.getConversionMap();
			CSVMapping mapping = conversionMap.get("time");
			mapping.setDataType(Datatype.TIME);
			mapping.setPattern("yyyy-MM-dd");

			final ProgressListener progressListener = new NoOpProgressListenerImpl();
			ConversionResult<XLog> result = conversion.doConvertCSVToXES(progressListener, csvFile, config,
					conversionConfig);
			XLog log = result.getResult();
			System.out.println(log.size());
			PluginContext context = globalContext.getMainPluginContext();
			IMPetriNet ins = new IMPetriNet();
//			Object[] re = ins.minePetriNet(context, log, new MiningParametersIMi());

			AbstractPILPDelegate.setDebugMode(null);

//			ComputeCostBasedOnFreq insComputeCostBasedOnFreq = new ComputeCostBasedOnFreq();
//			insComputeCostBasedOnFreq.initPreMapping("D:/data4code/replay/pre.csv");
//			insComputeCostBasedOnFreq.getEventSeqForEachPatient("D:/data4code/cluster/LogBasedOnKmeansPlusPlus-14.csv");
//			insComputeCostBasedOnFreq.removeOneLoop();

			ConstructPetriNet visualPNet=new ConstructPetriNet();
			PetrinetGraph net = (PetrinetGraph) visualPNet.getNet();
			
//			Set<PetrinetNode> nodes=net.getNodes();
//			for(PetrinetNode it:nodes){
//				System.out.println(it.getLabel());
//			}
//			Collection<Transition> Transitions=net.getTransitions();
//			for(Transition it:Transitions){
//				System.out.println(it.getLabel());
//			}
			
			Collection<Place> initialPlace=new ArrayList<Place>();
			Collection<Place> places=net.getPlaces();
			for(Place it:places){
				if(it.getLabel().equals("start")){
					initialPlace.add(it);
				}
			}
			Marking initialMarking = (Marking) new Marking(initialPlace);
			
			Collection<Place> finalPlace=new ArrayList<Place>();
			places=net.getPlaces();
			for(Place it:places){
				if(it.getLabel().equals("end")){
					finalPlace.add(it);
				}
			}
			Marking[] finalMarkings = { (Marking) new Marking(finalPlace) }; // only one marking is used so far
			
			Map<Transition, Integer> costMOS = null; // movements on system
			Map<XEventClass, Integer> costMOT = null; // movements on trace
			TransEvClassMapping mappingTransEvClassMapping = null;
			//			XParserRegistry temp=XParserRegistry.instance();
			//			temp.setCurrentDefault(new XesXmlParser());
			//			log = temp.currentDefault().parse(new File("D:/data4code/mm.xes")).get(0);
			//			log = XParserRegistry.instance().currentDefault().parse(new File("d:/temp/BPI2013all90.xes.gz")).get(0);
			//			log = XParserRegistry.instance().currentDefault().parse(new File("d:/temp/BPI 730858110.xes.gz")).get(0);
			//			log = XFactoryRegistry.instance().currentDefault().openLog();
			costMOS = constructMOSCostFunction(net);
			XEventClass dummyEvClass = new XEventClass("DUMMY", 99999);
			XEventClassifier eventClassifier = XLogInfoImpl.NAME_CLASSIFIER;
			costMOT = constructMOTCostFunction(net, log, eventClassifier, dummyEvClass);
			mappingTransEvClassMapping = constructMapping(net, log, dummyEvClass, eventClassifier);
			costMOS = constructMOSCostFunction(net);
			int cost1 = AlignmentTest.computeCost(costMOS, costMOT, initialMarking, finalMarkings, context, net, log,
					mappingTransEvClassMapping, false);
			
			SimulationEvaluate simu=new SimulationEvaluate();
			simu.computeRecallAndAccuracy(Param.stdAlignPath, Param.alignPath);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int computeCost(Map<Transition, Integer> costMOS, Map<XEventClass, Integer> costMOT,
			Marking initialMarking, Marking[] finalMarkings, PluginContext context, PetrinetGraph net, XLog log,
			TransEvClassMapping mapping, boolean useILP) {
		AbstractPetrinetReplayer<?, ?> replayEngine;
		if (useILP) {
			replayEngine = new PetrinetReplayerWithILP();
		} else {
			replayEngine = new PetrinetReplayerWithoutILP();
		}

		IPNReplayParameter parameters = new CostBasedCompleteParam(costMOT, costMOS);
		parameters.setInitialMarking(initialMarking);
		parameters.setFinalMarkings(finalMarkings[0]);
		parameters.setGUIMode(false);
		parameters.setCreateConn(false);
		parameters.setNumThreads(8);

		int cost = 0;
		try {
			PNRepResult result = replayEngine.replayLog(context, net, log, mapping, parameters);
			PNLogReplayResultVisPanel pan = new PNLogReplayResultVisPanel(log, result, context.getProgress());
			TestVisualAlign w = new TestVisualAlign();
			w.setContentPane(pan);
			w.setVisible(true);

			for (SyncReplayResult res : result) {
				cost += ((int) res.getInfo().get(PNRepResult.RAWFITNESSCOST).doubleValue())
						* res.getTraceIndex().size();
			}
		} catch (AStarException e) {
			e.printStackTrace();
		}

		return cost;
	}

	private static PetrinetGraph constructNet(String netFile) {
		PNMLSerializer PNML = new PNMLSerializer();
		NetSystem sys = PNML.parse(netFile);

		//System.err.println(sys.getMarkedPlaces());

		//		int pi, ti;
		//		pi = ti = 1;
		//		for (org.jbpt.petri.Place p : sys.getPlaces())
		//			p.setName("p" + pi++);
		//		for (org.jbpt.petri.Transition t : sys.getTransitions())
		//				t.setName("t" + ti++);

		PetrinetGraph net = PetrinetFactory.newPetrinet(netFile);

		// places
		Map<org.jbpt.petri.Place, Place> p2p = new HashMap<org.jbpt.petri.Place, Place>();
		for (org.jbpt.petri.Place p : sys.getPlaces()) {
			Place pp = net.addPlace(p.toString());
			p2p.put(p, pp);
		}

		// transitions
		int l = 0;
		Map<org.jbpt.petri.Transition, Transition> t2t = new HashMap<org.jbpt.petri.Transition, Transition>();
		for (org.jbpt.petri.Transition t : sys.getTransitions()) {
			Transition tt = net.addTransition(t.getLabel());
			tt.setInvisible(t.isSilent());
			t2t.put(t, tt);
		}

		// flow
		for (Flow f : sys.getFlow()) {
			if (f.getSource() instanceof org.jbpt.petri.Place) {
				net.addArc(p2p.get(f.getSource()), t2t.get(f.getTarget()));
			} else {
				net.addArc(t2t.get(f.getSource()), p2p.get(f.getTarget()));
			}
		}

		// add unique start node
		if (sys.getSourceNodes().isEmpty()) {
			Place i = net.addPlace("START_P");
			Transition t = net.addTransition("");
			t.setInvisible(true);
			net.addArc(i, t);

			for (org.jbpt.petri.Place p : sys.getMarkedPlaces()) {
				net.addArc(t, p2p.get(p));
			}

		}

		return net;
	}

	private static Marking[] getFinalMarkings(PetrinetGraph net) {
		Marking finalMarking = new Marking();

		for (Place p : net.getPlaces()) {
			if (net.getOutEdges(p).isEmpty())
				finalMarking.add(p);
		}

		Marking[] finalMarkings = new Marking[1];
		finalMarkings[0] = finalMarking;

		return finalMarkings;
	}

	private static Marking getInitialMarking(PetrinetGraph net) {
		Marking initMarking = new Marking();

		for (Place p : net.getPlaces()) {
			if (net.getInEdges(p).isEmpty())
				initMarking.add(p);
		}

		return initMarking;
	}

	private static Map<Transition, Integer> constructMOSCostFunction(PetrinetGraph net) {
		Map<Transition, Integer> costMOS = new HashMap<Transition, Integer>();

		for (Transition t : net.getTransitions())
			if (t.isInvisible() || t.getLabel().equals(""))
				costMOS.put(t, 0);
			else
				costMOS.put(t, 1);

		return costMOS;
	}

	private static Map<XEventClass, Integer> constructMOTCostFunction(PetrinetGraph net, XLog log,
			XEventClassifier eventClassifier, XEventClass dummyEvClass) {
		Map<XEventClass, Integer> costMOT = new HashMap<XEventClass, Integer>();
		XLogInfo summary = XLogInfoFactory.createLogInfo(log, eventClassifier);

		for (XEventClass evClass : summary.getEventClasses().getClasses()) {
			costMOT.put(evClass, 1);
		}

		//		costMOT.put(dummyEvClass, 1);

		return costMOT;
	}

	private static TransEvClassMapping constructMapping(PetrinetGraph net, XLog log, XEventClass dummyEvClass,
			XEventClassifier eventClassifier) {
		TransEvClassMapping mapping = new TransEvClassMapping(eventClassifier, dummyEvClass);

		XLogInfo summary = XLogInfoFactory.createLogInfo(log, eventClassifier);

		for (Transition t : net.getTransitions()) {
			boolean mapped = false;

			for (XEventClass evClass : summary.getEventClasses().getClasses()) {
				String id = evClass.getId();

				if (t.getLabel().equals(id)) {
					mapping.put(t, evClass);
					mapped = true;
					break;
				}
			}

			//			if (!mapped && !t.isInvisible()) {
			//				mapping.put(t, dummyEvClass);
			//			}

		}

		return mapping;
	}
}
