/*
 * Copyright 2015 Jordi Casas-Roma, Alexandre Dotor Casals
 * 
 * This file is part of DGA. 
 * 
 * DGA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DGA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DGA.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.uoc.kison.DGA.anonymization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.uoc.kison.DGA.ILP.ILP;
import org.uoc.kison.DGA.microAgreggation.UniVariateMicroaggregation;
import org.uoc.kison.DGA.utils.Functions;
import org.uoc.kison.DGA.utils.printUtils;

public class IndependentAnonymization {
	
	public static final String PARAM = "I";
    public static final String NAME = "Independent";
    private static final Logger logger = Logger.getLogger(IndependentAnonymization.class);
	
    public static ArrayList<int[]> independentDegSeqAnonymization(int[] din, int[] dout, int k) {
    	UniVariateMicroaggregation uvma = new UniVariateMicroaggregation();
    	// in-degree sequence
    	// get optimal partition
    	logger.info("*** Computing in-degree sequence...");
    	int[] din_permut = orderPermutation(din);

    	// copy & inline sort
    	int[] odin = new int[din.length];
    	for (int i=0;i<odin.length;i++) odin[i] = din[i];
    	Arrays.sort(odin);
    	
    	ArrayList<int[]> odink = uvma.UnivariateMicroaggregation(odin, k, "add");
    	printUtils.printDegreeSequencesSummary(odin, Functions.unlist(odink));

    	// out-degree sequence
    	// get optimal partition
    	logger.info("*** Computing out-degree sequence...");
    	int[] dout_permut = orderPermutation(dout);
    	//sort
    	int[] odout = new int[dout.length];
    	for (int i=0;i<odout.length;i++) odout[i] = dout[i];
    	Arrays.sort(odout);    	
    	ArrayList<int[]> odoutk = uvma.UnivariateMicroaggregation(odout, k, "add");
    	printUtils.printDegreeSequencesSummary(odout, Functions.unlist(odoutk));
    	
    	//////////////////////////////
    	//////// ILP solver		
    	if(Functions.unlistSum(odink) != Functions.unlistSum(odoutk)) {
    		logger.info("*** Length of the anonymous degree sequence are different... computing ILP!");

    		ArrayList<ArrayList<int[]>> res = ILP.ilp(odink, odoutk);
    		odink = res.get(0);
    		odoutk = res.get(1);
    	} else {
    		logger.info("*** Length of the anonymous degree sequence is equal! (don't need to compute ILP)");
    		logger.info("Length = "+Functions.unlistSum(odink));
    	}
    	
    	
    	int[] odinkUnlisted = Functions.unlist(odink);
    	int[] odoutkUnlisted = Functions.unlist(odoutk);

    	// re-order
    	int[] dink = new int[odinkUnlisted.length];
    	int[] secondOrder = orderPermutation(din_permut);
    	for (int i=0; i<dink.length; i++) dink[i] = odinkUnlisted[secondOrder[i]];
    	
    	int[] doutk = new int[odoutkUnlisted.length];
    	secondOrder = orderPermutation(dout_permut);
    	for (int i=0; i<doutk.length; i++) doutk[i] = odoutkUnlisted[secondOrder[i]];

    	// Print results
    	printUtils.printResults(din, dink, dout, doutk);

    	int sumDink = Functions.sum(dink);
		int sumDoutk = Functions.sum(doutk);
    	if(sumDink == sumDoutk) {
    		ArrayList<int[]> result = new ArrayList<int[]>(2);
    		logger.info("Anonymous degree sequences are correct!");
    		result.add(dink);
    		result.add(doutk);
    		return result;
    	} else {
    		logger.error(String.format("sum(dink)=%s <> sum(doutk)=%s", sumDink, sumDoutk));
    		logger.error("Error on anonymous degree sequences!");
    		return null;
    	}
    }
    
    private static int[] orderPermutation(final int[] din){
    	List<Integer> indices = new ArrayList<Integer>(din.length);
    	for (int i = 0; i < din.length; i++) {
    	  indices.add(i);
    	}
    	Comparator<Integer> comparator = new Comparator<Integer>() {
    	  public int compare(Integer i, Integer j) {
    	    return Integer.compare(din[i], din[j]);
    	  }
    	};
    	Collections.sort(indices, comparator);
    	
    	int[] result = new int[indices.size()];
    	for (int i=0;i<result.length;i++) result[i] = indices.get(i);
    	
    	return result;
    }
    

}
