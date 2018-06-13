package outlier.cluster;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lmu.ifi.dbs.elki.algorithm.Algorithm;
import de.lmu.ifi.dbs.elki.algorithm.clustering.DBSCAN;
import de.lmu.ifi.dbs.elki.algorithm.clustering.kmeans.AbstractKMeans;
import de.lmu.ifi.dbs.elki.algorithm.clustering.kmeans.KMeans;
import de.lmu.ifi.dbs.elki.algorithm.clustering.kmeans.KMeansLloyd;
import de.lmu.ifi.dbs.elki.algorithm.clustering.kmeans.KMediansLloyd;
import de.lmu.ifi.dbs.elki.algorithm.clustering.kmeans.KMedoidsPAM;
import de.lmu.ifi.dbs.elki.algorithm.outlier.lof.LOF;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.LabelList;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.model.ClusterModel;
import de.lmu.ifi.dbs.elki.data.model.KMeansModel;
import de.lmu.ifi.dbs.elki.data.model.MedoidModel;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDRange;
import de.lmu.ifi.dbs.elki.database.relation.DoubleRelation;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.datasource.FileBasedDatabaseConnection;
import de.lmu.ifi.dbs.elki.result.Result;
import de.lmu.ifi.dbs.elki.result.ResultUtil;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;

public class TestELKIDef {
	public static void main(String[] args) throws Exception, Exception {
		TestELKIDef ins = new TestELKIDef();
//		String logPath="D:\\data4code\\ncx-2\\elki\\input4elki.csv";
//		ins.ELIKKMedoidsPAM(17,logPath);
		ins.ELKILOF(10, "D:\\data4code\\yibaoju_2.0\\gxy\\jiaoYiLiuShui2VectorDrug-LOF.csv");
	}
	public Map<Integer, List<Point>> ELIKKMeansLloyd(Integer clusterK,String logPath){
		ListParameterization params = new ListParameterization();
		params.addParameter(FileBasedDatabaseConnection.Parameterizer.INPUT_ID,logPath
				);
		// Add other parameters for the database here!

		// Instantiate the database:
		Database db = ClassGenericsUtil.parameterizeOrAbort(StaticArrayDatabase.class, params);
		// Don't forget this, it will load the actual data (otherwise you get
		// null values below)
		db.initialize();

		Relation<NumberVector> vectors = db.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);
		Relation<LabelList> labels = db.getRelation(TypeUtil.LABELLIST);

		ListParameterization paramsAlg = new ListParameterization();
		paramsAlg.addParameter(KMeansLloyd.K_ID, clusterK);
//		paramsAlg.addParameter(KMedoidsPAM<V>.Parameterizer<V>, clusterK);

		Algorithm alg = ClassGenericsUtil.parameterizeOrAbort(KMedoidsPAM.class, paramsAlg);
		Clustering<KMeansModel> c = (Clustering<KMeansModel>) alg.run(db); // will
																			// choose
																			// the
																			// relation
																			// automatically!

		Map<Integer, List<Point>> result = new HashMap<Integer, List<Point>>();
		DBIDRange ids = (DBIDRange) vectors.getDBIDs();
		Integer i = 0;
		for (Cluster<KMeansModel> clu : c.getAllClusters()) {
			List<Point> points=new ArrayList<Point>();
			System.out.println("____________________________");
			// K-means will name all clusters "Cluster" in lack of noise
			// support:
			System.out.println("#" + i + ": " + clu.getNameAutomatic());
			System.out.println("Size: " + clu.size());
			System.out.println("Center: " + clu.getModel().getPrototype().toString());
			// Iterate over objects:
			System.out.print("Objects: ");
			for (DBIDIter it = clu.getIDs().iter(); it.valid(); it.advance()) {
				// To get the vector use:
				NumberVector v = vectors.get(it);
				// Offset within our DBID range: "line number"
				final int offset = ids.getOffset(it);
				// System.out.print(" " + labels.get(it));
				System.out.print(" " + offset);
				// Do NOT rely on using "internalGetIndex()" directly!
				String id=labels.get(it).toString();
				ArrayList<Double> value=new ArrayList<Double>();
				for(int j=0;j<v.getDimensionality();j++){
					value.add(v.doubleValue(j));
				}
				Point point=new Point(id,value);
				points.add(point);
			}
			result.put(i, points);
			System.out.println();
			++i;
		}
		// LOF<NumberVector> lof =
		// ClassGenericsUtil.parameterizeOrAbort(LOF.class, params);
		// OutlierResult outliers = alg.run(rel); // Manually chosen relation -
		// not general!
		return result;
	}
	
	public Map<Integer, List<Point>> ELIKKMedoidsPAM(Integer clusterK,String logPath){
		ListParameterization params = new ListParameterization();
		params.addParameter(FileBasedDatabaseConnection.Parameterizer.INPUT_ID,logPath
				);
		// Add other parameters for the database here!

		// Instantiate the database:
		Database db = ClassGenericsUtil.parameterizeOrAbort(StaticArrayDatabase.class, params);
		// Don't forget this, it will load the actual data (otherwise you get
		// null values below)
		db.initialize();

		Relation<NumberVector> vectors = db.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);
		Relation<LabelList> labels = db.getRelation(TypeUtil.LABELLIST);

		ListParameterization paramsAlg = new ListParameterization();
		paramsAlg.addParameter(KMeansLloyd.K_ID, clusterK);
//		paramsAlg.addParameter(KMedoidsPAM<V>.Parameterizer<V>, clusterK);

		Algorithm alg = ClassGenericsUtil.parameterizeOrAbort(KMedoidsPAM.class, paramsAlg);
		Clustering<MedoidModel> c = (Clustering<MedoidModel>) alg.run(db); // will
																			// choose
																			// the
																			// relation
																			// automatically!

		Map<Integer, List<Point>> result = new HashMap<Integer, List<Point>>();
		DBIDRange ids = (DBIDRange) vectors.getDBIDs();
		Integer i = 0;
		for (Cluster<MedoidModel> clu : c.getAllClusters()) {
			List<Point> points=new ArrayList<Point>();
			System.out.println("____________________________");
			// K-means will name all clusters "Cluster" in lack of noise
			// support:
			System.out.println("#" + i + ": " + clu.getNameAutomatic());
			System.out.println("Size: " + clu.size());
			System.out.println("Center: " + clu.getModel().getPrototype().toString());
			// Iterate over objects:
			System.out.print("Objects: ");
			for (DBIDIter it = clu.getIDs().iter(); it.valid(); it.advance()) {
				// To get the vector use:
				NumberVector v = vectors.get(it);
				// Offset within our DBID range: "line number"
				final int offset = ids.getOffset(it);
				// System.out.print(" " + labels.get(it));
				System.out.print(" " + offset);
				// Do NOT rely on using "internalGetIndex()" directly!
				String id=labels.get(it).toString();
				ArrayList<Double> value=new ArrayList<Double>();
				for(int j=0;j<v.getDimensionality();j++){
					value.add(v.doubleValue(j));
				}
				Point point=new Point(id,value);
				points.add(point);
			}
			result.put(i, points);
			System.out.println();
			++i;
		}
		// LOF<NumberVector> lof =
		// ClassGenericsUtil.parameterizeOrAbort(LOF.class, params);
		// OutlierResult outliers = alg.run(rel); // Manually chosen relation -
		// not general!
		return result;
	}
	
	public void ELKILOF(Integer K,String logPath) throws Exception, FileNotFoundException{
		ListParameterization params = new ListParameterization();
		params.addParameter(FileBasedDatabaseConnection.Parameterizer.INPUT_ID,logPath
				);
		// Add other parameters for the database here!

		// Instantiate the database:
		Database db = ClassGenericsUtil.parameterizeOrAbort(StaticArrayDatabase.class, params);
		// Don't forget this, it will load the actual data (otherwise you get
		// null values below)
		db.initialize();

		Relation<NumberVector> vectors = db.getRelation(TypeUtil.DOUBLE_VECTOR_FIELD);
		Relation<LabelList> labels = db.getRelation(TypeUtil.LABELLIST);

		ListParameterization paramsAlg = new ListParameterization();
		paramsAlg.addParameter(LOF.Parameterizer.K_ID, K);
		Algorithm alg = ClassGenericsUtil.parameterizeOrAbort(LOF.class, paramsAlg);
		Result result = alg.run(db); // will choose the relation automatically!
		List<OutlierResult> outlierResult=ResultUtil.getOutlierResults(result);
		DBIDRange ids = (DBIDRange) vectors.getDBIDs();
		for(OutlierResult each:outlierResult){
			DoubleRelation dr=each.getScores();
			for (DBIDIter it = dr.getDBIDs().iter(); it.valid(); it.advance()) {
				String id=labels.get(it).toString();
				final int offset = ids.getOffset(it);
				System.out.println(id+","+dr.doubleValue(it)+","+offset);
			}
		}
	}
	
	public Map<Integer, List<Point>> ELIKDBSCAN(Integer topicK,Double epsilon,String logPath){
		ListParameterization params = new ListParameterization();
		params.addParameter(FileBasedDatabaseConnection.Parameterizer.INPUT_ID,logPath
				);
		// Add other parameters for the database here!

		// Instantiate the database:
		Database db = ClassGenericsUtil.parameterizeOrAbort(StaticArrayDatabase.class, params);
		// Don't forget this, it will load the actual data (otherwise you get
		// null values below)
		db.initialize();

		Relation<NumberVector> vectors = db.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);
		Relation<LabelList> labels = db.getRelation(TypeUtil.LABELLIST);

		ListParameterization paramsAlg = new ListParameterization();
		paramsAlg.addParameter(DBSCAN.Parameterizer.EPSILON_ID, epsilon);//0.065
		paramsAlg.addParameter(DBSCAN.Parameterizer.MINPTS_ID, topicK*2);
//		paramsAlg.addParameter(KMeansLloyd.K_ID, clusterK);
//		paramsAlg.addParameter(KMedoidsPAM<V>.Parameterizer<V>, clusterK);

		Algorithm alg = ClassGenericsUtil.parameterizeOrAbort(DBSCAN.class, paramsAlg);
		Clustering<ClusterModel> c = (Clustering<ClusterModel>) alg.run(db); // will
																			// choose
																			// the
																			// relation
																			// automatically!

		Map<Integer, List<Point>> result = new HashMap<Integer, List<Point>>();
		DBIDRange ids = (DBIDRange) vectors.getDBIDs();
		Integer i = 0;
		int count=0;
		for (Cluster<ClusterModel> clu : c.getAllClusters()) {
			List<Point> points=new ArrayList<Point>();
			System.out.println("____________________________");
			// K-means will name all clusters "Cluster" in lack of noise
			// support:
			System.out.println("#" + i + ": " + clu.getNameAutomatic());
			System.out.println("Size: " + clu.size());
			count+=clu.size();
			// Iterate over objects:
			System.out.print("Objects: ");
			for (DBIDIter it = clu.getIDs().iter(); it.valid(); it.advance()) {
				// To get the vector use:
				NumberVector v = vectors.get(it);
				// Offset within our DBID range: "line number"
				final int offset = ids.getOffset(it);
				// System.out.print(" " + labels.get(it));
				System.out.print(" " + offset);
				// Do NOT rely on using "internalGetIndex()" directly!
				String id=labels.get(it).toString();
				ArrayList<Double> value=new ArrayList<Double>();
				for(int j=0;j<v.getDimensionality();j++){
					value.add(v.doubleValue(j));
				}
				Point point=new Point(id,value);
				points.add(point);
			}
			result.put(i, points);
			System.out.println();
			++i;
		}
		System.out.println("count:"+count);
		// LOF<NumberVector> lof =
		// ClassGenericsUtil.parameterizeOrAbort(LOF.class, params);
		// OutlierResult outliers = alg.run(rel); // Manually chosen relation -
		// not general!
		return result;
	}
}
