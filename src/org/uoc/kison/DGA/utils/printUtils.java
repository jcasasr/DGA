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
package org.uoc.kison.DGA.utils;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.uoc.kison.objects.SimpleIntGraph;

public class printUtils {
	
	private static final Logger logger = Logger.getLogger(printUtils.class);
	
	public static void printInfo(SimpleIntGraph g, int k) {
		UtilsGraph utilsGraph = new UtilsGraph();
		
		logger.info("***********");
		logger.info("*** DGA ***");
		logger.info("***********");
		logger.info(String.format(
				"Properties: n=%s, m=%s, indep-k=%s, complet-k=%s", g.getNumNodes(), g.getNumEdges(), 
					Arrays.toString(utilsGraph.getIndependentKAnonymityValueFromDigraph(g)),
					utilsGraph.getCompleteKAnonymityFromDigraph(g)));
		logger.info("K value: " + k);
	}
	
	public static void printResults(int[] din, int[] dink, int[] dout, int[] doutk) {
    	/********************
    	  PRINT AND VERIFY    */	
    	logger.info("*********************************");
    	logger.info(String.format("In-degree original sequence     : %s [%d arcs]", Arrays.toString(din), Functions.sum(din)));
    	logger.info(String.format("In-degree k-anonymous sequence  : %s [%d arcs]", Arrays.toString(dink), Functions.sum(dink)));
    	logger.info(String.format("Out-degree original sequence    : %s [%d arcs]", Arrays.toString(dout), Functions.sum(dout)));
    	logger.info(String.format("Out-degree k-anonymous sequence : %s [%d arcs]", Arrays.toString(doutk), Functions.sum(doutk)));
    }

	public static void printDegreeSequencesSummary(int[] d0, int[] dk) {
		UtilsGraph utilsGraph = new UtilsGraph();
		
		// Print results
		logger.info(String.format("D original   [%s arcs]", Functions.sum(d0)));
		logger.info(String.format("D Anonymized [%s arcs]", Functions.sum(dk)));
		
		// they can have different size
		int max;
		if (d0.length >= dk.length) max = d0.length;
		else max = dk.length;
		
		int d0Value;
		int dkValue;
		int difs = 0;
		for (int i=0; i<max; i++){
			if (i >= dk.length) dkValue = 0;
			else dkValue = dk[i];
			if (i >= d0.length) d0Value = 0;
			else d0Value = d0[i];
			
			difs += Math.abs(d0Value - dkValue);
		}
		
		logger.info(String.format("Difs=%d [%.2f %%]", difs, (double)difs/Functions.sum(d0)*100));
		logger.info("k value=" + utilsGraph.getKAnonymityValueFromDegreeSequence(dk));
	}
}
