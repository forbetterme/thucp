package outlier.util;

public class Param {
	public static String homePath="data/OutlierDetection/";
	public static String workspace;
	public static String originPath=homePath+"origin/";
	public static int CTscale=2;
	
	public static String yljgTar="yuanlai-map3.0-ct+2-new"; 
//	public static String yljgTar="yuanlai-map3.0-ct+2-20180227"; 
	public static Integer topicK=16;
	public static Integer num=-1;
	
	public static Double clusterMainTopicTheta=0.8;
	public static Double epsilon=0.15;
	public static Double ExploreTopicDistibutionOfDayClusterTheta=0.8;
	
	public static int topK=30;
	public static String basePath="D:\\data4code\\mainProcess\\"+yljgTar+"-all\\";
//	public static String mapPath="D:\\data4code\\mainProcess\\map\\";
	
	public static String logPath="D:/data4code/I61dot902-kmeans-11.csv";//重要
	public static String alignRePath="D:\\alpha\\pro\\eclipse\\thucp\\data\\OutlierDetection\\replay\\alignment - 副本.csv";
	
	//成本改进效果评估部分参数；
	public static String rawTracePath="data/OutlierDetection/evaluate/rawTrace.csv";
	public static String rawLogPath="data/OutlierDetection/evaluate/rawLog.csv";
	public static String stdAlignPath="data/OutlierDetection/evaluate/stdAlign.csv";
	public static String alignPath="data/OutlierDetection/replay/alignment.csv";
	public static String alignPathStdCost="data/OutlierDetection/replay/alignment - 副本.csv";
	public static String outlierTracePath="data/OutlierDetection/evaluate/outlierTrace.csv";
	public static String outlierLogPath="data/OutlierDetection/evaluate/outlierLog.csv";
	public static int evaluateEventNum=21;
	
	//成本改进相关
	public static int ifSimulate=0;//0代表不是仿真的
	public static int ifAdvance=1;//0代表不改进成本
	public static int traceNum=240;
	public static String prePath="data/OutlierDetection/replay/pre - 101.csv";
//	public static String prePath="data/OutlierDetection/replay/pre.csv";
}
