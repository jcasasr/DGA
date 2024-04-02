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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.apache.log4j.Logger;
import org.uoc.kison.DGA.utils.ConfigValues;
import org.uoc.kison.DGA.utils.UtilsGraph;
import org.uoc.kison.objects.SimpleIntGraph;

public class GraphReconstruction {

    protected static final Logger logger = Logger.getLogger(GraphReconstruction.class);

    /**
     *
     * @param g: original graph
     * @param dink: k-degree anonymous in sequence
     * @param doutk: k-degree anonymous out sequence
     */
    public static SimpleIntGraph doReconstruction(SimpleIntGraph g, int[] dink, int[] doutk) {
        boolean debug = true;
        ConfigValues configValues = new ConfigValues();

        UtilsGraph utilsGraph = new UtilsGraph();
        SimpleIntGraph gk = utilsGraph.copyGraph(g);

        // compute vertices which have to modify their in- and out-degree
        int[] din = utilsGraph.inDegree(g);
        int[] ddin = new int[din.length];
        for (int i = 0; i < ddin.length; i++) {
            ddin[i] = dink[i] - din[i];
        }
        int[] dout = utilsGraph.outDegree(g);
        int[] ddout = new int[dout.length];
        for (int i = 0; i < ddout.length; i++) {
            ddout[i] = doutk[i] - dout[i];
        }

        // create of vector of nodes which need to increase their degree
        ArrayList<Integer> vin = new ArrayList<>();
        for (int i = 0; i < ddin.length; i++) {
            while (ddin[i] > 0) {
                vin.add(i);
                ddin[i] = ddin[i] - 1;
            }
        }

        ArrayList<Integer> vout = new ArrayList<>();
        for (int i = 0; i < ddout.length; i++) {
            while (ddout[i] > 0) {
                vout.add(i);
                ddout[i] = ddout[i] - 1;
            }
        }

        int maxSourceIter = vout.size();
        int numSourceIter = 0;
        if (debug) {
            logger.debug(String.format("V out [%s elements]: %s", vout.size(), Arrays.toString(vout.toArray())));
            logger.debug(String.format("V in  [%s elements]: %s", vin.size(), Arrays.toString(vin.toArray())));
        }

        // info
        logger.info(String.format("Starting graph reconstrucion process [%s edges must be added]...", maxSourceIter));

        int index_nodes = 0;
        int index_edges = 0;
        
        // add new arcs
        ArrayList<Integer> vin_nodupli;
        for (int s : vout) {
            numSourceIter++;
            // trying to add an edge
            boolean done = false;
            vin_nodupli = new ArrayList<>(new LinkedHashSet<>(vin));

            logger.info(String.format("*** Iteration %s / %s [vout = %s]", numSourceIter, maxSourceIter, s));
            logger.debug(String.format("v out: %s", s));
            logger.debug(String.format("V in : %s", Arrays.toString(vin_nodupli.toArray())));

            int j = 0;
            while (j < vin_nodupli.size() & !done) {
                int t = vin_nodupli.get(j);
                if ((s != t) && (!gk.getEdges(s).contains(t))) {
                    gk.addEdge(s, t);
                    done = true;
                    // remove first ocurrence of 't' in vin
                    vin.remove((Integer) t);
                    logger.info(String.format("Arc (%s,%s) added!", s, t));
                }
                j++;
            }

            //if we cannot add an edge, we swap 2 edges and then we create a new edge
            if (!done) {
                logger.info("It's not possible to add an edge (" + s + ",?)... starting swap operation...");
                int numSwapTries = 0;
                int numExtTries = 0;
                j = 0;
                vin_nodupli = new ArrayList<>(new LinkedHashSet<>(vin));

                while (j < vin_nodupli.size() && !done) {
                    index_nodes = index_nodes % vin_nodupli.size();
                    int t = vin_nodupli.get(index_nodes);

                    if (s != t) {
			// find an edge to swap
                        // (s,t), (a,b) -> (s,b),(a,t)
                        for (int i = 0; i < gk.getNumEdges(); i++) {
                            int[] e = utilsGraph.getEdge(gk, index_edges);
                            int a = e[0];
                            int b = e[1];

                            numSwapTries++;

                            if ((s != a) && (s != b) && (s != t) && (a != b) && (a != t) && (b != t)) {
                                if (!gk.getEdges(s).contains(b) && !gk.getEdges(a).contains(t)) {
                                    // edge swap
                                    gk.deleteEdge(s, t);
                                    gk.deleteEdge(a, b);
                                    gk.addEdge(s, b);
                                    gk.addEdge(a, t);
                                    logger.info(String.format("Arcs (%s,%s),(%s,%s) -> (%s,%s),(%s,%s) swapped! [after %s tries]", s, t, a, b, s, b, a, t, numSwapTries));

                                    // add new one
                                    gk.addEdge(s, t);
                                    logger.info(String.format("Arc (%s,%s) added!", s, t));

                                    done = true;

                                    // remove first ocurrence of 't' in vin
                                    vin.remove((Integer) t);
                                    break;
                                }
                            }
                            index_edges++;
                            index_edges = index_edges % gk.getNumEdges();
                        }
                    } else {
			// s==t
                        // find (a,b) and: delete (a,b) and create (a,s),(s,b)
                        for (int i = 0; i < gk.getNumEdges(); i++) {
                            int[] e = utilsGraph.getEdge(gk, index_edges);
                            int a = e[0];
                            int b = e[1];

                            numExtTries++;

                            if ((s != a) && (s != b) && (a != b)) {
                                if (!gk.getEdges(a).contains(s) && !gk.getEdges(s).contains(b)) {
                                    // edge
                                    gk.deleteEdge(a, b);
                                    gk.addEdge(a, s);
                                    gk.addEdge(s, b);
                                    logger.info(String.format("Arcs (%s,%s) -> (%s,%s),(%s,%s) extended! [after %s tries]", a, b, a, s, s, b, numExtTries));

                                    done = true;
                                    // remove first ocurrence of 't' in vin
                                    vin.remove((Integer) t);
                                    break;
                                }
                            }
                            index_edges++;
                            index_edges = index_edges % gk.getNumEdges();
                        }
                    }
                    j++;
                    
                    index_nodes++;
                    index_nodes = index_nodes % vin_nodupli.size();
                }

                // FUCK
                if (!done) {
                    logger.error("EDGE SWAP is not able to modify the graph's structure!");
                    if (configValues.getExitOnEdgeModificationError()) {
                        return null;
                    }
                }
            }
        }

        return gk;
    }
}
