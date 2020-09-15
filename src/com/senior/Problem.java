package com.senior;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Problem {
	
	static int kmConstant = 500;
	static int max_counter = 1000;
	static int max_stalling = 10;
	public int x;
	public Solution best = new Solution();
	
	

	Problem(int[] depots ,ArrayList<City> data, ArrayList<Integer> destinations, PrintWriter writer){
		
		
		//HAzırlık
		destinations=clear_depot_cities(destinations,depots);
		
		ArrayList<Integer> temp = new ArrayList<>();
		
		ArrayList<Truck> truckPop = new ArrayList<>();
		truckPop.add(new Truck());
		
		this.x = 0;
		//16k'dan çokları ayırma
		for(int i = 0 ; i<destinations.size(); i++) {
			int index = destinations.get(i);
			while(data.get(index).remaining_desi>truckPop.get(x).totalCapacity) {
					truckPop.add(new Truck());
					for(int j=0;j<depots.length;j++) {
						temp.add(data.get(depots[j]-1).distance.get(index));
					}
					truckPop.get(x).route.add(data.get(index).distance.indexOf(Collections.min(temp))+1);
					truckPop.get(x).route.add(index+1);
					truckPop.get(x).calculateTruckDistance(data);
					truckPop.get(x).calculateTruckCost(data, depots);
					data.get(index).remaining_desi = data.get(index).remaining_desi - truckPop.get(x).totalCapacity;
					truckPop.get(x).capacity=0;
					this.x = this.x +1;
					truckPop.get(x).id = x;
			}
		}	
		
		//Go depot
		int currentPos=go_depot(destinations,data,depots);
		truckPop.get(x).route.add(currentPos+1);
		
		//heuristic
		this.go_heuristic(truckPop,destinations,data,currentPos,depots,writer);
		
		//distance and cost
		for(int i=0;i<truckPop.size();i++) {
			truckPop.get(i).calculateTruckDistance(data);
			truckPop.get(i).calculateTruckCost(data,depots);
		}
		
		//define initial result as Solution class.
		Solution initial = new Solution(truckPop,data,depots);
		
		//initial.printSolution(writer);
		
		//Tabu search
		Tabu_Search tabu = new Tabu_Search(initial,max_counter,max_stalling,writer);
		this.best=tabu.best;
	}
	
	public void go_heuristic(ArrayList<Truck> truckPop, ArrayList<Integer> destinations, ArrayList<City> data, int currentPos, int[] depots, PrintWriter writer) {
		ArrayList<Integer> neighbors = new ArrayList<>(destinations);
		while(neighbors.size()!= 0) {
			//System.out.println(neighbors.toString());
			//System.out.println(neighbors.toString());
			int min_distance = 9999999;
			int min_destination=9999, min_i=99;
			/*writer.println(
					"<br/> truck: "+x + "<br/>" +
					"rem capacity: "+ truckPop.get(x).capacity
					);*/
			//Finding best neighbor with condition
			for(int i = 0 ;i<neighbors.size(); i++) {
				int plate = neighbors.get(i);
				int distance = data.get(currentPos).distance.get(plate);
				int desi = data.get(plate).remaining_desi;
				boolean condition = distance < min_distance & truckPop.get(x).capacity >= desi /*& distance <= kmConstant*/;				
				if(condition) {
					min_i = i;
					min_distance = distance;
					min_destination = plate;
				} 
			}
			//System.out.println(min_destination);
			//boolean posCondition = (currentPos == 33) | (currentPos == 40) | (currentPos == 5);
			//adding to route phase
			if(min_i != 99) {
				truckPop.get(x).capacity = truckPop.get(x).capacity- data.get(min_destination).remaining_desi;
				/*writer.println(
						" min dest: "+ min_destination +"<br/>"+
						" desi: "+data.get(min_destination).remaining_desi
						);*/
				data.get(min_destination).remaining_desi = 0;  
				truckPop.get(x).route.add(min_destination+1);
				neighbors.remove(min_i);
			} /*else if(posCondition & data.get(currentPos).distance.get(neighbors.get(0)) > kmConstant ) {
				System.out.println("IM in else if");
				int dest = neighbors.get(0)+1;
				System.out.println(dest-1);
				truckPop.add(new Truck());
				x++;
				truckPop.get(x).route.add(34);
				truckPop.get(x).route.add(dest);
				truckPop.get(x).type = 2;
				truckPop.get(x).calculateTruckDistance(data);
				truckPop.get(x).calculateTruckCost(data, depots);
				neighbors.remove(0);
			}*/ else {
				//If any destination couldn't make with condition
				//System.out.println("Im in else");
				truckPop.add(new Truck());
				this.x = this.x +1;
				currentPos=go_depot(neighbors,data,depots);
				truckPop.get(x).route.add(currentPos+1);
			}
		}
	}

	
	private static int go_depot(ArrayList<Integer> destinations, ArrayList<City> data, int[] depots) {
		int pos=0;
		int min_distance=99999999;
		for(int i=0;i<depots.length;i++) {
			for(int j=0;j<destinations.size();j++){
				int distance = data.get(depots[i]-1).distance.get(j);
				if(distance < min_distance) {
					min_distance = distance;
					pos=depots[i]-1;
				}
			}
		} 
		return pos;
	}
	
	private static ArrayList<Integer> clear_depot_cities(ArrayList<Integer> destinations, int[] depots) {
		
		for(int i=0;i<depots.length;i++) {
			for(int j=0;j<destinations.size();j++) {
				if(depots[i] == (destinations.get(j)+1)) {
					destinations.remove(j);
				}
			}
		}
		return destinations;
	}
 
 }
