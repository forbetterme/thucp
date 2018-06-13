/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2015/1/29 19:07</create-date>
 *
 * <copyright file="LdaUtil.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package com.hankcs.lda;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import org.netlib.util.doubleW;

/**
 * @author hankcs
 */
public class LdaUtil
{
    /**
     * To translate a LDA matrix to readable result
     * @param phi the LDA model
     * @param vocabulary
     * @param limit limit of max words in a topic
     * @return a map array
     */
    public static Map<String, Double>[] translate(double[][] phi, Vocabulary vocabulary, int limit)
    {
        limit = Math.min(limit, phi[0].length);
        Map<String, Double>[] result = new Map[phi.length];
        for (int k = 0; k < phi.length; k++)
        {
            Map<Double, String> rankMap = new TreeMap<Double, String>(Collections.reverseOrder());
            for (int i = 0; i < phi[k].length; i++)
            {
                rankMap.put(phi[k][i], vocabulary.getWord(i));
            }
            Iterator<Map.Entry<Double, String>> iterator = rankMap.entrySet().iterator();
            result[k] = new LinkedHashMap<String, Double>();
            for (int i = 0; i < limit; ++i)
            {
            	if (iterator.hasNext()) {
            		Map.Entry<Double, String> entry = iterator.next();
            		result[k].put(entry.getValue(), entry.getKey());
				}
            }
        }
        return result;
    }

    public static Map<String, Double> translate(double[] tp, double[][] phi, Vocabulary vocabulary, int limit)
    {
        Map<String, Double>[] topicMapArray = translate(phi, vocabulary, limit);
        double p = -1.0;
        int t = -1;
        for (int k = 0; k < tp.length; k++)
        {
            if (tp[k] > p)
            {
                p = tp[k];
                t = k;
            }
        }
        return topicMapArray[t];
    }

    /**
     * To print the result in a well formatted form
     * @param result
     * @param i2cHashMap
     */
    public static void explain(Map<String, Double>[] result, HashMap<Integer, String> i2cHashMap,StringBuffer sr)
    {
        int i = 0;
        for (Map<String, Double> topicMap : result)
        {
        	sr.append("□ topic "+(i+1)+" \n");
            System.out.printf("topic %d :\n", i);
            ++i;
            explain(topicMap,i2cHashMap,sr);
            System.out.println();
        }
    }

    public static void explain(Map<String, Double> topicMap, HashMap<Integer, String> i2cHashMap,StringBuffer sr)
    {
        for (Map.Entry<String, Double> entry : topicMap.entrySet())
        {
        	double p = entry.getValue();
      /*  	BigDecimal P = new BigDecimal(p);
        	p = P.setScale(3,BigDecimal.ROUND_HALF_UP).doubleValue();*/
            sr.append(" "+i2cHashMap.get(Integer.valueOf(entry.getKey()))+" : "+String.format("%.3f ", entry.getValue())+"  ");
            System.out.println(i2cHashMap.get(Integer.valueOf(entry.getKey()))+":"+String.format("%.3f", entry.getValue()));
           // System.out.println(i2cHashMap.get(Integer.valueOf(entry.getKey()))+":"+p);

        }
        sr.append("\n");
    }

    public static void explain(Map<String, Double>[] result)
    {
        int i = 0;
        for (Map<String, Double> topicMap : result)
        {
            System.out.printf("topic %d :\n", i++);
            explain(topicMap);
            System.out.println();
        }
    }

    public static void explain(Map<String, Double> topicMap)
    {
        for (Map.Entry<String, Double> entry : topicMap.entrySet())
        {

            System.out.println(entry);
        }
    }

}
