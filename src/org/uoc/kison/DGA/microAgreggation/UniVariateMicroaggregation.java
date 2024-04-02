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
package org.uoc.kison.DGA.microAgreggation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class UniVariateMicroaggregation {

	private static final Logger logger = Logger.getLogger(UniVariateMicroaggregation.class);

	/***********
	Find all partitions and their score
	d0: ordered integer sequence
	k: partitions between [k, 2k-1] items
	method: "add", "del", "all"
	print: print H(k,n) (debug only)
	 */
	public ArrayList<int[]> UnivariateMicroaggregation(int[] V, int k, String method) {
		// create H(k,n)
		WeightedGraph H = createHkn(V, k, method);

		// computing group partition
		// shortest path from 1 to n+1
		logger.debug("Computing shortest path...");
		List<Integer> shortestpath = H.getShortestPath(0, V.length);

		logger.debug(String.format("Shortest path: %s", Arrays.toString(shortestpath.toArray())));

		// apply partition to degree sequence
		return applyGroupPartition(V, shortestpath);
	}

	/****************
	 * create H(k,n) */
	private WeightedGraph createHkn(int[] d0, int k, String method) {
		// create empty digraph H
		int n = d0.length;
		WeightedGraph H = new WeightedGraph(d0.length + 1, k);

		// fake element 0
		int[] V = new int[d0.length + 1];
		V[0] = 0;
		System.arraycopy(d0, 0, V, 1, d0.length);



		logger.debug(String.format("Creating graph H(%d,%d)...", k, n));
		logger.debug("Using method: "+ method);

		/*********************
		 * create edges (i,j) */
		for (int i = 0; i < n; i++) {
			for (int j = (i + k); j < (i + (2 * k)); j++) {
				if (j < (n + 1)) {
					int L = 0;

					// C(i,j)
					int[] C = new int[j - i];
					for (int m = i + 1; m <= j; m++) {
						C[m - (i + 1)] = m;
					}

					if(method == "all") {
						// M(i,j)
						int[] M = new int[j - i];
						for (int m = i + 1; m <= j; m++) {
							M[m - (i + 1)] = V[m];
						}

						int average = M[Math.round(M.length / 2)];

						// L(i,j)
						for (int m = i + 1; m <= j; m++) {
							L += Math.abs(V[m] - average);
						}

					} else if(method == "add") {
						// M(i,j)
						int max = Integer.MIN_VALUE;
						for (int m = i + 1; m <= j; m++) {
							if (V[m] > max) max = V[m];
						}

						// L(i,j)
						for (int m = i + 1; m <= j; m++) {
							L += Math.abs(V[m] - max);
						}
					} else {
						logger.error(String.format("Method '%s' is not valid!", method));
						return null;
					}

					H.setValue(i, j, L);
				}
			}
		}

		return H;
	}

	/**************
	 * modify values
	 * V: ordered integer sequence. Example: c(1,1,2,2,2)
	 * groups: partitions. Example: c(0,2,5)
	 * @return: list(c(1,1), c(2,2,2))
	 */
	private ArrayList<int[]> applyGroupPartition(int[] V, List<Integer> groups) {
		ArrayList<int[]> partitionList = new ArrayList<>();

		for(int i=0; i<groups.size()-1; i++) {
			int a = groups.get(i)+1;
			int b = groups.get(i+1);

			int max = Integer.MIN_VALUE;
			for(int j=a; j<b; j++){
				if (V[j] > max) max = V[j];
			}

			int rep = b-a+1;
			int[] group = new int[rep];
			for(int j=0;j<rep;j++){
				group[j] = max;
			}
			partitionList.add(group);
		}

		return partitionList;
	}
}
