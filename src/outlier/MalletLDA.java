package outlier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;
import outlier.util.Param;

public class MalletLDA {
	public static String basePath;

	public ParallelTopicModel model;
	public Map<Integer, String> idx2word = new HashMap<Integer, String>();
	public String[][] topWords;
	public Double[][] topWordsProability;
	public double[][] d2tDist;
	public double alpha = 1.0, beta = 0.01;
	public int K, iteration = 2000, topK = 50, threadNum = 2;

	public MalletLDA(int K) {
		this.K = K;
	}

	public MalletLDA(double alpha, double beta, int K, int iteration, int topK, int threadNum,String workPath) {
		this.alpha = alpha;
		this.beta = beta;
		this.K = K;
		this.iteration = iteration;
		this.topK = topK;
		this.threadNum = threadNum;
		this.basePath=workPath;
	}

	// public MalletLDA(TCPMParameters params) {
	// this.alpha = params.alpha;
	// this.beta = params.beta;
	// this.K = params.K;
	// this.iteration = params.iteration;
	// this.topK = params.topK;
	// this.threadNum = params.threadNum;
	// }

	public void runLDA(File id2itemFile, File pditemFile) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(id2itemFile), "UTF-8"));
		String currentLine;
		while ((currentLine = br.readLine()) != null) {
			String[] strs = currentLine.split("\t");
			int id = Integer.parseInt(strs[0]);
			String term = strs[1];
			idx2word.put(id, term);
		}

		// using mallet
		// Begin by importing documents from text to feature sequences
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		// pipeList.add(new CharSequenceLowercase());
		pipeList.add(new CharSequence2TokenSequence(Pattern
				// .compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
				.compile("(\\S*)")));
		// pipeList.add( new TokenSequenceRemoveStopwords(new
		// File("stoplists/en.txt"), "UTF-8", false, false, false) );
		pipeList.add(new TokenSequence2FeatureSequence());

		InstanceList instances = new InstanceList(new SerialPipes(pipeList));

		Reader fileReader = new InputStreamReader(new FileInputStream(pditemFile), "UTF-8");
		// instances.addThruPipe(new CsvIterator(fileReader, Pattern
		// .compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"), 3, 2, 1)); // data,
		// // label, name, fields
		instances.addThruPipe(new CsvIterator(fileReader, Pattern.compile("(.*),(.*),(.*)$"), 3, 2, 1));

		// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
		// Note that the first parameter is passed as the sum over topics, while
		// the second is the parameter for a single dimension of the Dirichlet
		// prior.
		model = new ParallelTopicModel(K, alpha, beta);

		model.addInstances(instances);

		// Use two parallel samplers, which each look at one half the corpus and
		// combine
		// statistics after every iteration.
		model.setNumThreads(threadNum);

		// Run the model for 50 iterations and stop (this is for testing only,
		// for real applications, use 1000 to 2000 iterations)
		model.setNumIterations(iteration);
		model.estimate();

		Object[][] twsProability = model.getTopWordsProability(topK);
		topWordsProability = new Double[twsProability.length][twsProability[0].length];
		for (int i = 0; i < twsProability.length; i++) {
			for (int j = 0; j < twsProability[0].length; j++) {
				try {
					// Double idx = Double.parseDouble((String)
					// twsProability[i][j]);
					// topWords[i][j] = idx + "#" + idx2word.get(idx);
					topWordsProability[i][j] = (Double) twsProability[i][j];
				} catch (Exception e) { // may cause
										// java.lang.ArrayIndexOutOfBoundsException
					// TODO: handle exception
					topWordsProability[i][j] = 0.0;
				}
			}
		}

		// topK = 50;
		Object[][] tws = model.getTopWords(topK);
		topWords = new String[tws.length][tws[0].length];
		for (int i = 0; i < tws.length; i++) {
			for (int j = 0; j < tws[0].length; j++) {
				try {
					int idx = Integer.parseInt((String) tws[i][j]);
					// topWords[i][j] = idx + "#" + idx2word.get(idx);
					topWords[i][j] = idx2word.get(idx);
				} catch (Exception e) { // may cause
										// java.lang.ArrayIndexOutOfBoundsException
					// TODO: handle exception
					topWords[i][j] = "";
				}
			}
		}

		// System.out.println(model.displayTopWords(10, true));
		d2tDist = model.getDocumentTopics(true, true);
	}
	

	public static void main(String[] args) throws Exception {
		
	}
}
