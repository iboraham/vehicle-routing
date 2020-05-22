package com.senior;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

public class Solution {
	public int id;
	public ArrayList<Truck> truckPop = new ArrayList<>();
	public double totalCost;
	public int totalDistance;
	public ArrayList<City> data = new ArrayList<>();
	public int[] depots;
	public int two_opt_change1;
	public int two_opt_change2;
	
	Solution(ArrayList<Truck> trucks, ArrayList<City> data, int[] depots) {
		this.depots = depots.clone();
		id = 0;
		this.truckPop = new ArrayList<>(trucks);
		this.data = new ArrayList<>(data);
		totalDistance = calculateTotalDistance();
		this.calculateTotalCost();
	}
	
	Solution(ArrayList<Truck> trucks, ArrayList<City> data, int id,int[] depots) {
		this.depots = depots.clone();
		this.id= id;
		this.truckPop = new ArrayList<>(trucks);
		this.data = new ArrayList<>(data);
		totalDistance = calculateTotalDistance();
		this.calculateTotalCost();
	}

	Solution(){	
		
	}
	
	public void calculateTotalCost() {
		double t=0;
		for(int i=0;i<truckPop.size();i++) {
			//System.out.println("\ncsot "+ truckPop.get(i).cost + " id: "+this.id);
			//System.out.println("t before "+ t+" i:"+i+" type: "+truckPop.get(i).type);
			t += truckPop.get(i).cost;
			
			if(truckPop.get(i).type == 0) {
				t += 2000;
			} else if(truckPop.get(i).type == 1) {
				t += 1500;
			} else {
				t+=0;
			}
			//System.out.println("t "+ t+" i:"+i+" type: "+truckPop.get(i).type);
		}
		this.totalCost = t;
	}

	public int calculateTotalDistance() {
		totalDistance=0;
		for(int i=0;i<truckPop.size();i++) {
			totalDistance+=truckPop.get(i).distance;
		}
		return totalDistance;
	}
	
	public Solution swap(int id) {
		ArrayList<Double> tmp = new ArrayList<>();
		Solution sol = new Solution(this);
		for(int i=0;i<sol.truckPop.size();i++) {
			tmp.add(sol.truckPop.get(i).cost);
		}
		int index=tmp.indexOf(Collections.max(tmp));
		Truck costlyTruck = new Truck(sol.truckPop.get(index));
		ArrayList<Double> temp = new ArrayList<>();
		for(int i=1;i<(costlyTruck.route.size()-1);i++) {
			costlyTruck = new Truck(sol.truckPop.get(index));
			Collections.swap(costlyTruck.route, i, (i+1));
			costlyTruck.calculateTruckDistance(data);
			costlyTruck.calculateTruckCost(data, depots);		
			temp.add(costlyTruck.cost);
		}
		double min_cost = Collections.min(temp);
		int index_of_min_cost = temp.indexOf(min_cost);
		
		Solution result = new Solution();
		result.id = id;
		result.depots=depots;
		result.data=data;
		for(int i=0;i<sol.truckPop.size();i++) {
			result.truckPop.add(new Truck());
			for(int j=0;j<sol.truckPop.get(i).route.size();j++) {
				result.truckPop.get(i).route.add(sol.truckPop.get(i).route.get(j));
			}
			result.truckPop.get(i).calculateTruckCost(data, depots);
			result.truckPop.get(i).calculateTruckDistance(data);
		}
		
		
		Collections.swap(result.truckPop.get(index).route, (index_of_min_cost+1), (index_of_min_cost+2));

		return result;	
	}
	
	Solution(Solution c) {
		this.id= c.id;
		for(int i=0;i<c.truckPop.size();i++){
			this.truckPop.add(new Truck(c.truckPop.get(i)));
		}
		this.data = new ArrayList<>(c.data);
		this.totalDistance = c.totalDistance;
		this.totalCost = c.totalCost;
		this.depots = c.depots;
		this.two_opt_change1=c.two_opt_change1;
		this.two_opt_change2=c.two_opt_change2;
	}
	
	public Solution copy_from(Solution c) {
		this.id= c.id;
		this.truckPop = new ArrayList<>(c.truckPop);
		this.data = new ArrayList<>(c.data);
		this.totalDistance = c.totalDistance;
		this.totalCost = c.totalCost;
		this.depots = c.depots;
		return c;
	}

	public Solution two_opt(int id, int[] tabuList, Solution best) {
		Solution sol = new Solution(this);
		sol.id = id;
		/*for(int i=0;i<sol.truckPop.size();i++) {
			tmp.add(sol.truckPop.get(i).cost);
		}
		int index=tmp.indexOf(Collections.max(tmp));
		*/
		
		int min_j=0, min_i =0, min_index = 0;
		double min_cost=999999;
		
		for(int index=0;index<sol.truckPop.size();index++){
			Solution temp = new Solution(sol);
			/*System.out.println("IN: "+
					sol.truckPop.get(index).route+" distance: "+sol.truckPop.get(index).distance+" cost: "+sol.truckPop.get(index).cost); */
			for(int i=2;i<sol.truckPop.get(index).route.size()-2;i++) {
				for(int j=i+2;j<sol.truckPop.get(index).route.size();j++) {
					temp.truckPop.set(index, new Truck(sol.truckPop.get(index)));
					Truck costlyTruck = temp.truckPop.get(index);
					Collections.swap(costlyTruck.route, i, j );
					costlyTruck.calculateTruckDistance(data);
					costlyTruck.calculateTruckCost(data, depots);
					temp.calculateTotalCost();
					temp.calculateTotalDistance();
					/*System.out.println(costlyTruck.route+" distance: "+temp.totalDistance+" "
							+ "cost: "+temp.totalCost + " degisen: "+i +"  " +temp.truckPop.get(index).route.get(i)+" min cost = "+min_cost);*/
					if(temp.totalCost <= min_cost) {
						if(temp.totalCost < best.totalCost) {
							min_i = i;
							min_j = j;
							min_cost = temp.totalCost;
							min_index = index;
						} else if(check_value(costlyTruck.route.get(i),tabuList) == 0 &
								check_value(costlyTruck.route.get(j),tabuList) == 0) {
							min_i = i;
							min_j = j;
							min_cost = temp.totalCost;
							min_index = index;
						} else {
							//System.out.println(" DDDDDDDDDDD ");
						}
					}
				}
			}
		}
		//System.out.println(min_i+ " "+min_j);
		
		Collections.swap(sol.truckPop.get(min_index).route, (min_i), (min_j));
		sol.two_opt_change1 = sol.truckPop.get(min_index).route.get(min_i);
		sol.two_opt_change2 = sol.truckPop.get(min_index).route.get(min_j);
		//System.out.println("\n\n two_opt_change: "+sol.two_opt_change1+sol.two_opt_change2);
		for(int i=0;i<sol.truckPop.size();i++) {
			sol.truckPop.get(i).calculateTruckDistance(data);
			sol.truckPop.get(i).calculateTruckCost(data,depots);
		}
		sol.calculateTotalCost();
		sol.calculateTotalDistance();
		/*System.out.println("OUT: "+
				sol.truckPop.get(min_index).route+" distance: "+sol.truckPop.get(min_index).distance+" cost: "+sol.truckPop.get(min_index).cost);*/
		return sol;	
	}

	private int check_value(int value, int[] list) {
		for(int i=0; i<list.length;i++) {
			if(value == list[i]) {
				return 1;
			}
		}
		return 0;
	}
	
	public String addToResponse(String htmlResponse) {
			
	        // build HTML code
			htmlResponse += "<div class=\"container\">";
			htmlResponse += "<div class=\"row row-cols-2\">";
	        htmlResponse += "<h1>Truck Routes" + "<br/>";
	        htmlResponse += "<h2> Solution id:" +this.id + "<br/>";   
	        htmlResponse += "Total Solution Distance is: " + this.totalDistance + "<br/>";   
	        double roundOff = Math.round(this.totalCost * 100.0) / 100.0;
	        htmlResponse += "Total Solution Cost is: " + roundOff+" Turkish Liras" + "</h2>";
	        htmlResponse += "</div></div>";
	        htmlResponse += "<div class=container>";
	        htmlResponse += "<div class=row>";
	        for(int i=0;i<truckPop.size();i++){
	        	htmlResponse += "<div class=\"col-sm-12\">";
	        	htmlResponse += "<p>";
	        	htmlResponse += "Truck "+i+":"+this.truckPop.get(i).route.toString()+" <br/>";
	        	htmlResponse += "Truck "+i+" Route with City Names ---> ";
	        	for(int j=0;j<this.truckPop.get(i).route.size();j++) {
		        	int plaka=this.truckPop.get(i).route.get(j)-1;
		        	htmlResponse += data.get(plaka).name +"->"; }
	        	htmlResponse +=" <br/>";
	        	htmlResponse += "Truck Type: "+this.truckPop.get(i).type + " <br/>";
	        	htmlResponse += "Total Area: "+(this.truckPop.get(i).totalCapacity-this.truckPop.get(i).capacity) + " <br/>";
	        	double roundOffTruck = Math.round(this.truckPop.get(i).cost * 100.0) / 100.0;
	        	htmlResponse += "Truck Cost: "+ roundOffTruck + " <br/> <br/>";
	        	htmlResponse += "</p>";
	        	htmlResponse += "</div>";
	        }
	        htmlResponse += "</div></div>";
	        return htmlResponse;
	}
	
	public void check_lorry() {
		for(int i=0; i<this.truckPop.size();i++) {
			if((this.truckPop.get(i).totalCapacity-this.truckPop.get(i).capacity) <= 9216 & this.truckPop.get(i).type == 0) {
				this.truckPop.get(i).type = 1;
				this.truckPop.get(i).calculateTruckCost(data, depots);
			}
		}
		this.calculateTotalCost();
	}

	public Solution insertion(int id) {
		Solution sol = new Solution(this);
		sol.id = id;
		int min_i=0,min_j=0,min_x = 0,min_y=0;
		int min_dist = 999999;
		for(int i=0;i<sol.truckPop.size();i++) {
			if(sol.truckPop.get(i).route.size()>2) {
				Truck truck1 = new Truck(sol.truckPop.get(i));
				int min_dist_2=999999;
				int min_i_2 = 0,min_j_2 = 0,min_x_2 = 0,min_y_2 = 0;
				for(int x=2;x<truck1.route.size();x++) {
					int first=truck1.route.get(x)-1;
					int min_dist_1=999999;
					int min_i_1 = 0,min_j_1 = 0,min_x_1 = 0,min_y_1 = 0;
					for(int j=0;j<sol.truckPop.size();j++) {
						if(i!=j) {
							if(sol.truckPop.get(j).route.size()>2) {
								Truck truck2 = new Truck(sol.truckPop.get(j));
								int min_dist_0 = 999999;
								int min_i_0 = 0,min_j_0 = 0,min_x_0 = 0,min_y_0 = 0;
								for(int y=1;y<truck2.route.size();y++) {
									int second=truck2.route.get(y)-1;
									int cur_dist = sol.data.get(first).distance.get(second);
									if(cur_dist < min_dist_0) {
										min_dist_0 = cur_dist;
										min_i_0 = i;
										min_j_0 = j;
										min_x_0 = x;
										min_y_0 = y;
									}
								}
								if(min_dist_0 < min_dist_1) {
									min_dist_1 = min_dist_0;
									min_i_1 = min_i_0;
									min_j_1 = min_j_0;
									min_x_1 = min_x_0;
									min_y_1 = min_y_0;
								}
							}
						}
					}
					if(min_dist_1 < min_dist_2) {
						min_dist_2 = min_dist_1;
						min_i_2 = min_i_1;
						min_j_2 = min_j_1;
						min_x_2 = min_x_1;
						min_y_2 = min_y_1;
					}
				}
				if(min_dist_2 < min_dist) {
					min_dist = min_dist_2;
					min_i = min_i_2;
					min_j = min_j_2;
					min_x = min_x_2;
					min_y = min_y_2;
				}
			}
		}
		/*System.out.println("\nIN: "+
		sol.truckPop.get(min_i).route+" distance: "+sol.truckPop.get(min_i).distance+" cost: "+sol.truckPop.get(min_i).cost);
		System.out.println(sol.truckPop.get(min_j).route+" distance: "+sol.truckPop.get(min_j).distance+" cost: "+sol.truckPop.get(min_j).cost);*/
		
		Truck truck1 = sol.truckPop.get(min_i);
		Truck truck2 = sol.truckPop.get(min_j);
		int first = truck1.route.get(min_x)-1;
		int secondplus1 = truck2.route.get(min_y+1)-1;
		int secondminus1 = truck2.route.get(min_y-1)-1;
		if (sol.data.get(first).distance.get(secondplus1 ) >= sol.data.get(first).distance.get(secondminus1)) {
			sol.truckPop.get(min_i).route.remove(min_x);
			sol.truckPop.get(min_j).route.add(min_y, sol.truckPop.get(min_i).route.get(min_x));
		} else {
			sol.truckPop.get(min_i).route.remove(min_x);
			sol.truckPop.get(min_j).route.add(min_y+1, sol.truckPop.get(min_i).route.get(min_x));
		}
		
		for(int i=0;i<sol.truckPop.size();i++) {
			sol.truckPop.get(i).calculateTruckDistance(data);
			sol.truckPop.get(i).calculateTruckCost(data,depots);
		}
		sol.calculateTotalCost();
		sol.calculateTotalDistance();
		System.out.println("\nOUT: "+
		sol.truckPop.get(min_i).route+" distance: "+sol.truckPop.get(min_i).distance+" cost: "+sol.truckPop.get(min_i).cost);
		System.out.println(sol.truckPop.get(min_j).route+" distance: "+sol.truckPop.get(min_j).distance+" cost: "+sol.truckPop.get(min_j).cost);
		return sol;	
	}
	
	public Solution parcel(int id, Solution best) {
		Solution sol = new Solution(this);
		sol.id = id;
		int max = 0,max_truck = 0;
		Double max_delta = 0.00;
		for(int i=0;i<sol.truckPop.size();i++) {
			Truck truck = sol.truckPop.get(i);
			if(truck.route.size() > 2) {
				for(int j=2;j<truck.route.size();j++) {
					Truck temp = new Truck(truck);
					int city = temp.route.get(j);
					temp.route.remove(j);
					temp.calculateTruckDistance(data);
					temp.calculateTruckCost(data, depots);
					Double pcost =parcel_cost(city);
					Double delta = truck.cost-temp.cost-pcost;
					if(delta > max_delta & delta > 0) {
							//System.out.println("\n"+delta);
							max=j;
							max_truck = i;
							max_delta = delta;
							//System.out.println("max c: "+max_delta);
						}
					//}
				}
			}
		}
		if(max_delta > 0.0) {
			int max_city = sol.truckPop.get(max_truck).route.get(max);
			sol.truckPop.get(max_truck).route.remove(max);
			//System.out.println("cost : "+sol.truckPop.get(max_truck).cost);
			ArrayList<Integer> route = new ArrayList<>();
			int truckID = sol.truckPop.size();
			route.add(34);
			route.add(max_city);
			int type = 2;
			sol.truckPop.add(new Truck(route,type,truckID));
			Truck last = sol.truckPop.get(sol.truckPop.size() -1);
			int desi = data.get(last.route.get(1)-1).desi;
			sol.truckPop.get(max_truck).capacity +=  desi;
			last.capacity = last.totalCapacity - desi;
			for(int i=0;i<sol.truckPop.size();i++) {
				sol.truckPop.get(i).calculateTruckDistance(data);
				sol.truckPop.get(i).calculateTruckCost(data, depots);
			}
			//System.out.println("cost : "+sol.truckPop.get(max_truck).cost);
			//System.out.println(last.cost);
			sol.calculateTotalDistance();
			sol.calculateTotalCost(); 
		}
		
		return sol;
	}

	private Double parcel_cost(int city) {
		Double cost = 0.00;
		cost = data.get(city-1).parcelCost * data.get(city-1).remaining_desi;
		return cost;
	}
	
	
}
