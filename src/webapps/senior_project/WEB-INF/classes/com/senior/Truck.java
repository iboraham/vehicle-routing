package com.senior;

import java.util.ArrayList;

public class Truck {
	public int id;
	public int capacity;
	public ArrayList<Integer> route;
	public int totalCapacity;
	public int distance;
	public double cost;
	public int type; //0:Tır 1:Kamyon 2:Parsel
	
	Truck() {
		totalCapacity = 16896;
		capacity = 16896;
		cost=13.80;
		type = 0;
		route = new ArrayList<>();
	}
	
	Truck(Truck c) {
		this.id = c.id;
		this.capacity=c.capacity;
		this.route = new ArrayList<>(c.route);
		this.totalCapacity = c.totalCapacity;
		this.distance = c.distance;
		this.cost = c.cost;
		this.type = c.type;
	}
	
	Truck(ArrayList<Integer> route,int type,int id) {
		this.id = id;
		cost=13.80;
		this.type = type;
		this.route = new ArrayList<>(route);
	}
	
	public void calculateTruckCost(ArrayList<City> data,int[] depots) {
		double totalCost = 13.80;
		int point1;
		if (this.type != 2) {
			int index_of_depot=0;
			point1 = this.route.get(0)-1;
			for(int i=0; i<depots.length;i++) {
				if( (point1+1) == depots[i]){
					index_of_depot = i;
				}
			}
			int cns =0;
			double dns;
			int last_plate = (this.route.get(this.route.size()-1))-1;
			
			double sefer = 0;
			if(this.type == 0) {
				sefer = data.get(last_plate).tourCosts.get(index_of_depot);
			} else {
				sefer = data.get(last_plate).kamyonCost.get(index_of_depot);
			}
			totalCost += sefer;
			
			if(this.type == 0) {
				cns = 175;
				dns = 3.9;
			} else {
				cns = 120;
				dns = 3.2;
			}
			
			double dropCost = (this.route.size()-2)*cns;
			
			totalCost += dropCost;
			
			int minus_distance=data.get(point1).distance.get(last_plate);
			double distanceCost = dns*(distance-minus_distance);
			
			totalCost += distanceCost;
			
			this.cost = totalCost;
		} else {
			totalCost = (this.totalCapacity-this.capacity) * data.get(this.route.get(1)-1).parcelCost;
			//System.out.println("This is parcel "+totalCost);
			this.cost = totalCost;
		}
		
	}
	
	public void calculateTruckDistance(ArrayList<City> data) {
		int totalDistance=0;
		if(this.type!=2) {
			int point1, point2;
			for(int i=0;i<(route.size()-1);i++) {
				point1 = route.get(i)-1;
				point2 = route.get(i+1)-1;
				totalDistance += data.get(point1).distance.get(point2);
			}
			
			this.distance = totalDistance;
		} else {
			totalDistance = data.get(33).distance.get(this.route.get(1)-1);
			this.distance = totalDistance;
		}
	}
	
}
