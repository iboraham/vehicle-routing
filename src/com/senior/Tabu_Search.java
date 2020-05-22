package com.senior;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Tabu_Search {
	public Solution best = new Solution();
	public ArrayList<Solution> pop = new ArrayList<>();
	public int[] tabuList = new int[20];
	public int max;
	
	Tabu_Search(Solution initial,int max, int max_stalling, PrintWriter writer) {
		
		int tabuCounter=0;
		Solution temp;
		this.max = max;
		best.totalCost = 999999999;
		initial.check_lorry();
		
		temp = new Solution(initial.two_opt(1,tabuList,best));
		if(temp.totalCost < this.best.totalCost) {
			this.best.copy_from(temp);
		}
		temp.check_lorry();
		pop.add(temp);
		tabuList[tabuCounter % tabuList.length]=temp.two_opt_change1;
		tabuCounter+=1;
		tabuList[tabuCounter % tabuList.length]=temp.two_opt_change2;
		tabuCounter+=1;
		int stalling=0;
		//writer.println(tabuList.length);
		for(int i=2; i<=max;i++) {
			temp = new Solution(temp.two_opt(i,tabuList,best));
			//temp.print_solution();
			//temp = temp.parcel(temp.id, temp);
			//temp.print_solution();
			//stalling += 1;
			// -----Finding Best
			ArrayList<Double> list = new ArrayList<>();
			for(int j=0;j<pop.size();j++) {
				//pop.get(i).printSolution(writer);
				list.add(pop.get(j).totalCost);
			}
			int minIndex = list.indexOf(Collections.min(list));
			best = new Solution(pop.get(minIndex));
			best.calculateTotalDistance();
			best.calculateTotalCost();
			best.id = minIndex+1;
			// -----Finding Best
			if(max_stalling == stalling) {
				writer.println("Stopped by max stalling!" + " i: "+i);
				break;
			}
			temp.check_lorry();
			pop.add(temp);
			tabuList[tabuCounter % tabuList.length]=temp.two_opt_change1;
			tabuCounter+=1;
			tabuList[tabuCounter % tabuList.length]=temp.two_opt_change2;
			tabuCounter+=1;
			//writer.println(Arrays.toString(tabuList));
			//System.out.println("\n\ntabu_list: "+Arrays.toString(tabuList));
		}
		//Calculate total Dest
		int totalDest = 0;
		for(int i=0;i<initial.truckPop.size();i++) {
			totalDest += initial.truckPop.get(i).route.size();
		}
		//After Two opt tabu search add parsels
		int t =pop.size()+1;
		temp = new Solution(this.best.parcel(t, this.best));
		temp.check_lorry();
		pop.add(temp);
		for(int i=0; i<=totalDest;i++) {
			t++;
			temp = new Solution(temp.parcel(t, temp));
			//temp.printSolution(writer);
			temp.check_lorry();
			pop.add(temp);
		}
		// -----Finding Best
		ArrayList<Double> list = new ArrayList<>();
		for(int i=0;i<pop.size();i++) {
			//pop.get(i).printSolution(writer);
			list.add(pop.get(i).totalCost);
		}
		int minIndex = list.indexOf(Collections.min(list));
		best = new Solution(pop.get(minIndex));
		best.calculateTotalDistance();
		best.calculateTotalCost();
		// -----Finding Best
	}
}
