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

import java.util.ArrayList;

public class Functions {

	public static int sum(int[] vec){
		int sum = 0;
		for (int elem : vec) sum += elem;
		return sum;
	}
	
	public static int[] unlist(ArrayList<int[]> list){
		ArrayList<Integer> tempList = new ArrayList<>();
		for (int[] group : list){
			for(int elem : group) tempList.add(elem);
		}
		
		int[] unlisted = new int[tempList.size()];
		for (int i=0; i<unlisted.length; i++) unlisted[i] = tempList.get(i);
		
		return unlisted;
	}
	
	public static int unlistSum(ArrayList<int[]> list){
		int sum = 0;
		for (int[] group : list){
			for(int elem : group) sum += elem;
		}
		
		return sum;
	}
	
	public static String getListedString(ArrayList<int[]> list){
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		
		for (int[] group : list){
			sb.append("{");
			for(int elem : group) sb.append(elem+",");
			sb.append("},");
		}
		
		sb.append("}");
		
		return sb.toString();
	}
	
	public static int[][] uniqueMultiArray(int[][] mArray){
		ArrayList<ArrayList<Integer>> unique = new ArrayList<ArrayList<Integer>>();
		for(int i=0; i<mArray[0].length; i++){
			int a1 = mArray[0][i];
			int a2 = mArray[1][i];
			
			boolean repeated = false;
			for (ArrayList<Integer> entry : unique){
				if (entry.get(0) == a1 && entry.get(1) == a2){
					repeated = true;
					break;
				}
			}
			
			if (!repeated){
				ArrayList<Integer> newEntry = new ArrayList<Integer>(2);
				newEntry.add(a1);
				newEntry.add(a2);
				unique.add(newEntry);
			}
		}
		
		int[][] uniqueArray = new int[2][unique.size()];
		int count = 0;
		for (ArrayList<Integer> entry : unique){
			uniqueArray[0][count] = entry.get(0);
			uniqueArray[1][count] = entry.get(0);
			count++;
		}
		
		return uniqueArray;
	}
}
