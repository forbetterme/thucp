package test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import outlier.lda.com.hankcs.lda.Corpus;
import outlier.lda.com.hankcs.lda.LdaGibbsSampler;
import outlier.lda.com.hankcs.lda.LdaUtil;
import outlier.topic.GetTopicBasedOnLDA;
import outlier.util.Param;
import view.FrameworkMain;
import view.InputData;
import view.InputDataRowType;

public class LDATest {
	public static void main(String[] args) throws Exception {
		Corpus corpus = new Corpus();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		Map<String,HashSet<String>> doc2itemsSet=new HashMap<String,HashSet<String>>();
		String fileIn=Param.basePath+"I61dot902-log-filter-map.csv";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn),"UTF-8"));
		String line="";
		line=reader.readLine();
		while ((line = reader.readLine()) != null) {
			String[] lines=line.split(",");
			String visitId=lines[0];
			String event=lines[1];
			String eventClass=lines[2];
			String num=lines[3];
			String price=lines[4];
			String zj=lines[5];
			String time=lines[6];
			String docKey=visitId+"#"+time;
			if(doc2itemsSet.containsKey(docKey)){
				doc2itemsSet.get(docKey).add(event);
			}else{
				HashSet<String> temp=new HashSet<String>();
				temp.add(event);
				doc2itemsSet.put(docKey, temp);
			}
		}
		reader.close();
		
		for(Map.Entry<String, HashSet<String>> entry:doc2itemsSet.entrySet()){
			ArrayList<String> temp=new ArrayList<String>();
			for(String it:entry.getValue()){
				temp.add(it);
			}
			map.put(entry.getKey(), temp);
		}

		for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
			ArrayList<String> lines = entry.getValue();
			List<String> wordList = new LinkedList<String>();
			for (String word : lines) {
				if (word.trim().length() < 2)
					continue;
				wordList.add(word);
			}
			corpus.addDocument(wordList);
		}
		if (corpus.getVocabularySize() == 0)
			corpus=null;
		Integer K=20;
        // 2. Create a LDA sampler
        LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());
        // 3. Train it
        ldaGibbsSampler.gibbs(K);
        // 4. The phi matrix is a LDA model, you can use LdaUtil to explain it.
        double[][] phi = ldaGibbsSampler.getPhi();
        Map<String, Double>[] topicMap = LdaUtil.translate(phi, corpus.getVocabulary(), 100000);
        LdaUtil.explain(topicMap);
        double[][] theta=ldaGibbsSampler.getTheta();
        LdaUtil.saveTheta(theta);
	}
}
