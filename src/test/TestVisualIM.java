package test;

import java.io.File;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.deckfour.xes.model.XLog;
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
import org.processmining.log.csvimport.exception.CSVConversionException;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMi;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;
import org.processmining.plugins.petrinet.PetriNetVisualization;

import com.google.common.collect.ImmutableList;

import outlier.util.Param;

public class TestVisualIM extends JFrame {
	public TestVisualIM() {
		super();
		this.setSize(1800, 1000);
	}

	public static void main(String[] args) throws CSVConversionException {
		String logPath=Param.rawLogPath;
		
		TestVisualIM w = new TestVisualIM();
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
		File file = new File(logPath);
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
			
			MiningParameters param=new MiningParametersIMi();
			param.setNoiseThreshold((float) 0.2);
			
			Object[] re = ins.minePetriNet(context, log, param);
			PetriNetVisualization v = new PetriNetVisualization();
			JComponent jp = v.visualize(context, (Petrinet) re[0], (Marking) (re[1]));
			w.setContentPane(jp);
			w.setVisible(true);
			w.setTitle(logPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
