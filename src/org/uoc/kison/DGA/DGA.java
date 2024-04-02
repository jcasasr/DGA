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

package org.uoc.kison.DGA;

import org.apache.log4j.Logger;
import org.uoc.kison.DGA.utils.ElapsedTime;
import org.uoc.kison.DGA.utils.Functions;
import org.uoc.kison.DGA.utils.UtilsGraph;
import org.uoc.kison.objects.SimpleIntGraph;

public class DGA {

	private static final Logger logger = Logger.getLogger(DGA.class);
	UtilsGraph utilsGraph;

	public DGA() {
		utilsGraph = new UtilsGraph();
	}

	public SimpleIntGraph dga(SimpleIntGraph graph, int[] dink, int[] doutk) {
		ElapsedTime et = new ElapsedTime();

		// Directed graphs only
		if (!utilsGraph.isDirected(graph)) {
			logger.error("Graph is not directed!");
			return null;
		}

		logger.info(String.format("In-degree k-anonymous seq  [k=%s] [%d]",
				utilsGraph.getKAnonymityValueFromDegreeSequence(dink), Functions.sum(dink)));
		logger.info(String.format("Out-degree k-anonymous seq [k=%s] [%d]",
				utilsGraph.getKAnonymityValueFromDegreeSequence(doutk), Functions.sum(doutk)));

		// modifiy original graph to anonymize it 
		SimpleIntGraph gk = GraphReconstruction.doReconstruction(graph, dink, doutk);

		// show k-anonymity value
		utilsGraph.getIndependentKAnonymityValueFromDigraph(gk);
		utilsGraph.getCompleteKAnonymityFromDigraph(gk);
                
                // summary graph properties
                logger.info(String.format("Original graph  : %s vertices and %s edges", graph.getNumNodes(), graph.getNumEdges()));
                logger.info(String.format("Anonymized graph: %s vertices and %s edges", gk.getNumNodes(), gk.getNumEdges()));

		// show results edgeIntersection(g, gk); printEdgesAdded(g, gk);
		et.stop();
		logger.info(String.format("Total running time: %s", et.getElapsedTime()));

		return(gk);
	}
}
