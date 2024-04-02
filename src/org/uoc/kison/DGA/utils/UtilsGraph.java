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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;
import org.uoc.kison.objects.SimpleIntGraph;

public class UtilsGraph {

	private static final Logger logger = Logger.getLogger(UtilsGraph.class);

	public int[] inDegree(SimpleIntGraph graph) {
		int numNodes = graph.getNumNodes();
		int[] degrees = new int[numNodes];

		for (int i = 0; i < numNodes; i++) {
			HashSet<Integer> edges = graph.getEdges(i);
			for (int edge : edges){
				degrees[edge]++;
			}
		}

		return degrees;
	}

	public int[] outDegree(SimpleIntGraph graph) {
		int numNodes = graph.getNumNodes();
		int[] degrees = new int[numNodes];

		for (int i = 0; i < numNodes; i++) {
			degrees[i] = graph.getEdges(i).size();
		}

		return degrees;
	}

	public boolean isDirected(SimpleIntGraph graph){
		int numNodes = graph.getNumNodes();
		for (int i = 0; i < numNodes; i++) {
			HashSet<Integer> destinations = graph.getEdges(i);
			for (Integer dest : destinations) {
				if(!graph.getEdges(dest).contains(i)) return true;
			}
		}

		return false;
	}


	public int getKAnonymityValueFromDegreeSequence(int[] d) {
		return (getKAnonymityValueFromHistogram(getDegreeHistogramFromDegreeSequence(d)));
	}

	public int[] getDegreeHistogramFromDegreeSequence(int[] d) {
		int max = d[0];

		// find the max degree of all nodes
		for (int i = 1; i < d.length; i++) {
			if (d[i] > max) {
				max = d[i];
			}
		}
		// create an empty sequence with max+1 values [0..max]
				int[] h = new int[max + 1];
				for (int i = 0; i < d.length; i++) {
					h[d[i]]++;
				}

				return h;
	}

	public int getKAnonymityValueFromHistogram(int[] h) {
		int min = Integer.MAX_VALUE;
		int i;

		for (i = 0; i < h.length; i++) {
			if (h[i] > 0 && h[i] < min) {
				min = h[i];
			}
		}

		return min;
	}


	public int[] getIndependentKAnonymityValueFromDigraph(SimpleIntGraph g) {
		int kin = getKAnonymityValueFromDegreeSequence(inDegree(g));
		int kout = getKAnonymityValueFromDegreeSequence(outDegree(g));

		logger.info(String.format("G is Independent (%s,%s)-degree anonymous", kin, kout));

		return(new int[]{kin, kout});
	}

	public int getCompleteKAnonymityFromDigraph(SimpleIntGraph g) {
		int[] din = inDegree(g);
		int[] dout = outDegree(g);
		boolean debug = false;

		HashMap<String, Integer> map = new HashMap<>();
		for(int i=0; i<din.length; i++) {
			String label = din[i]+"-"+dout[i];
			if(!map.containsKey(label)) {
				map.put(label, 1);
			} else {
				map.put(label, map.get(label) + 1);
			}
		}

		// DEBUG
		if(debug) {
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				logger.debug("- "+entry.getKey()+" : "+ entry.getValue());
			}
		}

		//get the minimum value
		int minVal = Integer.MAX_VALUE;
		for (Integer value : map.values()){
			if (value < minVal) minVal = value;
		}

		logger.info(String.format("G is Complete %s-degree anonymous", minVal));

		return(minVal);
	}
	
	public SimpleIntGraph copyGraph(SimpleIntGraph g) {
        SimpleIntGraph clone = new SimpleIntGraph(g.getNumNodes());

        for (int source = 0; source < g.getNumNodes(); source++) {
            for (int target : g.getEdges(source)){
                clone.addEdge(source, target);
            }
        }

        return clone;
    }
	
	public int[] getEdge(SimpleIntGraph g, int edgeNum){
		int[] result = new int[2];
		int numNodes = g.getNumNodes();
		int count = 0;
		for(int i=0; i<numNodes; i++){
			int adjSize = g.getEdges(i).size();
			if (count + adjSize > edgeNum){
				int pos = edgeNum - count;
                count = 0;
                for (int target : g.getEdges(i)){
                    if (count == pos){
                        result[0] = i;
                        result[1] = target;
                        break;
                    }
                }
				return result;
			}else{
				count += adjSize;
			}
		}
		return null; // should never happend
	}
}
