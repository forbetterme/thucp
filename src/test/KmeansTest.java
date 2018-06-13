package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import outlier.cluster.GetLabelForCluster;
import outlier.cluster.KmeansPlusPlus;
import outlier.cluster.Point;

public class KmeansTest {

	public static void main(String[] args) throws IOException {
		Integer KKmeans = 20;
		Integer KLDA=13;
		String docToTopicFilePath = "data/OutlierDetection/topic/doc2topics/docToTopic.csv";
		String idFromCorpusForMapping = "data/OutlierDetection/corpus";
		String logFilePathForSave = "data/OutlierDetection/cluster/LogBasedOnKmeansPlusPlus-";
		String clusterToItemsFilePath = "data/OutlierDetection/cluster/clusterToItems.csv";
		String topic2itemsFilePah = "data/OutlierDetection/topic/topic2items";
		String logPath = "data/OutlierDetection/cluster/LogBasedOnKmeansPlusPlus-" + KKmeans + ".csv";
		String docToTopicPath = "data/OutlierDetection/topic/doc2topics/docToTopic.csv";
		String corpusFilePath = "data/OutlierDetection/corpus";
		Set<String> id = new HashSet<String>();
		double min = Double.MAX_VALUE;
		Map<Integer, List<Point>> result = null;
		int minIndex = -1;
		for (int j = 0; j < 100; j++) {
			int i = KKmeans;
			KmeansPlusPlus kmeansClustering = new KmeansPlusPlus(docToTopicFilePath, idFromCorpusForMapping);
			Map<Integer, List<Point>> tempResult = kmeansClustering.kcluster(i,-1);
			System.out.println(kmeansClustering.evaluate);
			if (kmeansClustering.evaluate < min) {
				min = kmeansClustering.evaluate;
				result = tempResult;
				minIndex = i;
			} else
				continue;
		}
		File file_out = new File(logFilePathForSave + minIndex + ".csv");
		// BufferedWriter bw_out = new BufferedWriter(new
		// FileWriter(file_out, false));//
		BufferedWriter bw_out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), "UTF-8"));//
		bw_out.write("\"BRBM\",\"XM\",\"RQ\"");
		bw_out.newLine();
		for (Entry<Integer, List<Point>> entry : result.entrySet()) {
			// System.out.println("===============聚簇中心为：" +
			// entry.getKey() +
			// "================");
			// System.out.println(entry.getValue().size());
			for (Point point : entry.getValue()) {
				String[] strs = point.id.split("#");
				String caseId = strs[0];
				id.add(caseId);
				String riqi = strs[1].split("\\.")[0];
				bw_out.write(caseId + "," + entry.getKey() + "," + riqi);
				bw_out.newLine();
			}
		}
		for (String it : id) {
			bw_out.write(it + "," + "s" + "," + "1900-1-1");
			bw_out.newLine();
			bw_out.write(it + "," + "e" + "," + "2018-1-1");
			bw_out.newLine();
		}
		bw_out.flush();
		bw_out.close();
		System.out.println("Kmeans++ completed!");

		// 保存各类簇的主题代表
		GetLabelForCluster ins = new GetLabelForCluster();
		ins.getClusterToItems(result, topic2itemsFilePah, clusterToItemsFilePath, 10, logPath, docToTopicPath,
				corpusFilePath, KLDA, KKmeans);// 每个topic，最多选10个代表词
		System.out.println("保存各类簇的主题代表 completed!");
	}
}
