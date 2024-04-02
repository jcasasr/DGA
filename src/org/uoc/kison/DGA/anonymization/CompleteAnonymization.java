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
import java.util.LinkedHashSet;

import org.apache.log4j.Logger;
import org.uoc.kison.DGA.utils.Functions;

public class CompleteAnonymization {
	
	public static final String PARAM = "C";
    public static final String NAME = "Complete";
    private static final Logger logger = Logger.getLogger(CompleteAnonymization.class);
    
	public static ArrayList<int[]> completeDegSeqAnonymization(int[] din, int[] dout, int k) {
		// create data structure
		int[][] cd = new int[2][din.length];
		for(int i=0; i<din.length;i++){
			cd[0][i] = din[i];
			cd[1][i] = dout[i];
		}		  
		  // compute multivariate microaggregation
		 /* int[][] m2 = microaggregation(as.data.frame(cd), aggr=k, method="mdav");
		  
		  // get results as array
		  int[][] cdk = new int[2][din.length];
		  cdk[0] = Arrays.copyOf(m2[0], m2[0].length);
		  cdk[1] = Arrays.copyOf(m2[1], m2[1].length);
		  
		  // create the mask
		  int[] mask = createMask(cdk);
		  
		  // number of groups
		  LinkedHashSet<Integer> uniqueMask = new LinkedHashSet<>(mask.length);
		  for(int i=0; i<mask.length; i++) uniqueMask.add(mask[i]);
		  
		  int numGroups = uniqueMask.size();
		  logger.debug(String.format("There are %s groups", numGroups));
		  
		  for(Integer v : uniqueMask) {
		    // assign max values (edge addition)
		    cdk[mask==v,1] = max(cd[mask==v,1]);
		    cdk[mask==v,2] = max(cd[mask==v,2]);
		  }*/
		  
		  // set order
		  /* dinout_permut = order(mask);
		  // in-degree sequence
		  din = cdk[,1];
		  odin = din[dinout_permut];
		  // out-degree sequence
		  dout = cdk[,2];
		  odout = dout[dinout_permut];
		  
		  //////////////////////////////
		  //////// ILP solver
		  odin_list = list(NULL);
		  odout_list = list(NULL);
		  length(odin_list) = numGroups;
		  length(odout_list) = numGroups;
		  i = 1;
		  for(v in unique(mask)) {
		    odin_list[i] = list(din[mask==v]);
		    odout_list[i] = list(dout[mask==v]);
		    i = i+1;
		  }
		  
		  res = ILP(odin_list, odout_list);
		  odink = unlist(res[1]);
		  odoutk = unlist(res[2]);
		  
		  // re-order
		  dink = odink[order(dinout_permut)];
		  doutk = odoutk[order(dinout_permut)];
		  
		  // Print results
		  printResults(din, dink, dout, doutk)
		  
		  if(sum(dink) == sum(doutk)) {
		    loginfo("Anonymous degree sequences are correct!");
		    return(list(dink, doutk));
		  } else {
		    logerror("sum(dink)=%s <> sum(doutk)=%s", sum(dink), sum(doutk));
		    stop("Error on anonymous degree sequences!");
		  }
		}*/
		return null;
	}

	private static int[] createMask(int[][] cdk) {
		int[][] uni = Functions.uniqueMultiArray(cdk);
		int[] mask = new int[cdk[0].length];
		for(int i=0; i<uni[0].length; i++) {
			int a1 = uni[0][i];
			int a2 = uni[1][i];
			for(int j=0;j<cdk[0].length; j++){
				if (cdk[0][j] == a1 && cdk[1][j] == a2){
					mask[j] = i;
				}
			}
		}
		
		return mask;
	}

}
