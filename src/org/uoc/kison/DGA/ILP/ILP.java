/*
 * Copyright 2013 Jordi Casas-Roma, Alexandre Dotor Casals
 * 
 * This file is part of UMGA. 
 * 
 * UMGA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UMGA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UMGA.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.uoc.kison.DGA.ILP;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.uoc.kison.DGA.utils.Functions;

import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryGLPK;
import net.sf.javailp.SolverFactoryLpSolve;

public class ILP {

	private static final Logger logger = Logger.getLogger(ILP.class);

	public static ArrayList<ArrayList<int[]>> ilp(ArrayList<int[]> oindk, ArrayList<int[]> ooutdk){
		boolean debug = true;
		logger.info("*** Starting ILP process...");

		// plot original values
		if(debug) {
			logger.debug(String.format("In-degree k-anonymous sequence  [%d arcs]: %s", Functions.unlistSum(oindk), Functions.getListedString(oindk)));
			logger.debug(String.format("Out-degree k-anonymous sequence [%d arcs]: %s", Functions.unlistSum(ooutdk), Functions.getListedString(ooutdk)));
		}

		// define local vars
		ArrayList<Integer> inDegree = new ArrayList<>();
		ArrayList<Integer> outDegree = new ArrayList<>();
		
		// process IN-DEGREE
		for(int[] ind : oindk) inDegree.add(ind.length);

		// process OUT-DEGREE
		for(int[] outd : ooutdk) outDegree.add(-outd.length);

		/** CREATE ILP and SOLVE IT **/
		Problem problem = new Problem();

		// Objective: minimize in-degree
		Linear linear = new Linear();
		for(int i=0; i<inDegree.size(); i++){
			linear.add(inDegree.get(i), "x"+i);
		}
		problem.setObjective(linear, OptType.MIN);

		// Restriction: in - out = diff
		linear = new Linear();
		for(int i=0; i<inDegree.size(); i++){
			linear.add(inDegree.get(i), "x"+i);
		}
		for(int i=0; i<outDegree.size(); i++){
			linear.add(outDegree.get(i), "y"+i);
		}
		int diff = Functions.unlistSum(ooutdk) - Functions.unlistSum(oindk);
		problem.add(linear, "=", diff);

		// in variables must be integers 0+
		for(int i=0; i<inDegree.size(); i++){
			problem.setVarLowerBound("x"+i, 0);
			problem.setVarType("x"+i, Integer.class);
		}

		// out variables must be integers 0+
		for(int i=0; i<outDegree.size(); i++){
			problem.setVarLowerBound("y"+i, 0);
			problem.setVarType("y"+i, Integer.class);
		}

		// *** solve
		//SolverFactory factory = new SolverFactoryLpSolve();
		SolverFactory factory = new SolverFactoryGLPK();
		Solver solver = factory.get();
		Result result = solver.solve(problem);
		
		logger.debug(result.toString());

		// modify the in- and out-degree
		for(int i=0; i<inDegree.size(); i++) {
			Number c = result.getPrimalValue("x"+i);
			for(Integer elem : oindk.get(i)) elem += c.intValue();
		}
		for(int i=0; i<outDegree.size(); i++) {
			Number c = result.getPrimalValue("y"+i);
			for(int j=0; j<ooutdk.get(i).length; j++) ooutdk.get(i)[j] += c.intValue();
		}

		// plot resulting sequences
		//logger.info("The number of added edges is    : %s", minNumEdges);
		if(debug) {
			logger.debug(String.format("In-degree k-anonymous sequence  : %s [%d arcs]", Functions.getListedString(oindk), Functions.unlistSum(oindk)));
			logger.debug(String.format("Out-degree k-anonymous sequence : %s [%d arcs]", Functions.getListedString(ooutdk), Functions.unlistSum(ooutdk)));
		}

		ArrayList<ArrayList<int[]>> solution = new ArrayList<ArrayList<int[]>>();
		solution.add(oindk);
		solution.add(ooutdk);	

		return solution;
	}
}
