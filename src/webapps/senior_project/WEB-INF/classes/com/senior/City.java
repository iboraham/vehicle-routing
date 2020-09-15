package com.senior;

import java.util.ArrayList;

public class City {
	
	public int id;
	public ArrayList<Integer> distance; //distance to other cities.
	public int desi;
	public int remaining_desi;
	public ArrayList<Double> tourCosts = new ArrayList<>();
	public ArrayList<Integer> kamyonCost = new ArrayList<>();
	public Double parcelCost;
	public String name;
	public String longi;
	public String lati;
	
	City(int id, ArrayList<Integer> d) {
		this.id = id;
		distance = new ArrayList<>(d);
	}
}
