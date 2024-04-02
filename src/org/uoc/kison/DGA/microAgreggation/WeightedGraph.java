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
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

/**
 *
 * @author jcasasr
 */
public class WeightedGraph {

    private static final Logger logger = Logger.getLogger(WeightedGraph.class);
    private WeightedEdge[][] adjList = null;
    private int n;
    private int k;
    private double shortestPathLength;

    public WeightedGraph(int n, int k) {
        this.n = n;
        this.k = k;
        this.adjList = new WeightedEdge[n][k];
    }

    /**
     * Accessing methods
     */
    public int getValue(int source, int target) {
        // position of 'target' is: target - (source + k)
        int index = target - (source + k);
        
        WeightedEdge sie = this.adjList[source][index];
        
        if(sie.getTarget() != target) {
            logger.error("ERROR accessing indexed positions on SimpleIntGraph");
            System.exit(0);
        }
        
        return sie.getWeight();
    }

    public void setValue(int source, int target, int value) {
        // position of 'target' is: target - (source + k)
        int index = target - (source + k);
        
        this.adjList[source][index] = new WeightedEdge(target, value);
    }
    
    /**
     * 
     */
    public List<Integer> getShortestPath(int source, int target) {
        boolean debug = false;
        
        int[] cost = new int[n];
        int[] from = new int[n];
        boolean[] done = new boolean[n];
        
        for(int i = 0; i < n; i++) {
            cost[i] = Integer.MAX_VALUE;
            from[i] = -1;
            done[i] = false;
        }
        
        /** mark source edge at cost=0 */
        cost[source] = 0;
        from[source] = source;
        done[source] = true;
        
        // 
        int node = source;
        int total_cost = 0;
        
        
        /** process until target==done */
        int iteration = 0;
        
        if(debug) {
            logger.debug(Arrays.toString(cost));
            logger.debug(Arrays.toString(from));
            logger.debug(Arrays.toString(done));
            logger.debug("");
        }
        
        while(!done[target]) {
            
            iteration ++;
            
            // debug
            if(debug) logger.debug(String.format("*** Iteration %d, node=%d cost=%d", iteration, node, total_cost));
            
            // search adjacent nodes
            for(int j = 0; j < adjList[node].length; j++) {
                if(adjList[node][j] != null) {
                    WeightedEdge sie = adjList[node][j];
                    int edge_target = sie.getTarget();
                    int edge_weight = sie.getWeight();
                    
                    if(!done[edge_target]) {
                        int accumulate_cost = total_cost + edge_weight;
                        if(accumulate_cost < cost[edge_target]) {
                            cost[edge_target] = accumulate_cost;
                            from[edge_target] = node;

                            // debug
                            if(debug) logger.debug(String.format("%d -> %d cost=%d / %d", node, edge_target, edge_weight, accumulate_cost));
                        }
                    }
                }
            }
            
            // debug
            if(debug) {
                logger.debug(Arrays.toString(cost));
                logger.debug(Arrays.toString(from));
                logger.debug(Arrays.toString(done));
            }
            
            // find the smallest cost
            int min_i = -1;
            int min_cost = Integer.MAX_VALUE;
            
            for(int i = 0; i < cost.length; i++) {
                if(!done[i] && cost[i] < min_cost) {
                    min_i = i;
                    min_cost = cost[i];
                }
            }
            done[min_i] = true;
            
            if(debug) {
                logger.debug(String.format("SELECTED %d cost=%d", min_i, min_cost));
                logger.debug("");
            }
            
            // update node & total_cost
            node = min_i;
            total_cost = min_cost;
        }
        
        /** compute the shortest path */
        List<Integer> res = new ArrayList<Integer>();
        
        int final_cost = cost[target];
        int tmp = target;
        res.add(tmp);
        
        while(tmp != source) {
            tmp = from[tmp];
            res.add(tmp);
        }
        Collections.sort(res);
        
        // save shortest path length value
        this.shortestPathLength = final_cost;
        
        if(debug) logger.debug(String.format("SHORTEST PATH: %s [cost=%.2f]", res.toArray(), shortestPathLength));
        
        return res;
    }
    
    public double getShortestPathLength() {
        return this.shortestPathLength;
    }
    
}
