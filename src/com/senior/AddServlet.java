package com.senior;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;



public class AddServlet extends HttpServlet
{

	private static int max_number_of_depot = 100;

	public void service(HttpServletRequest req,HttpServletResponse res) throws IOException, ServletException 
	{	
		long startTime = System.nanoTime();
		String depots_str = req.getParameter("depots");
		String demand =req.getParameter("demand");
		String api_key = req.getParameter("api");

		//depots
		int[] depotsx = new int[max_number_of_depot];
        String[] depots_strarr= new String[max_number_of_depot];	
		depots_strarr=replacements(depots_str);
        depotsx=str_to_array(depots_strarr);
        
        PrintWriter writer = res.getWriter();
        
        ArrayList <Integer> depotsList = new ArrayList<>();
        
        for(int i=0;i<depotsx.length;i++) {
        	if(depotsx[i] != 0){
        		depotsList.add(depotsx[i]);
        	}
        }
        int[] depots = new int[depotsList.size()];
        for(int i=0;i<depotsList.size();i++) {
        	depots[i] = depotsList.get(i);
        }
        
        //data making
        res.setContentType("text/html");
        String dataFileName = "/WEB-INF/illerArasiMesafe.csv";
        ArrayList<City> data = read_data(dataFileName);
        
        String tourCostFileName = "/WEB-INF/seferBedeli.csv";
        data = read_tourCost(data,depots,tourCostFileName);
        
        String lorryCostFileName = "/WEB-INF/lorryBedeli.csv";
        data = read_lorryCost(data,depots,lorryCostFileName,writer);
        
        
        String parcel = "/WEB-INF/parcelCost5.csv";
		data = read_parcel(data,depots,parcel);
		
		String cityNames = "/WEB-INF/cityName.csv";
		data = read_names(data,cityNames);
		
		String latLong = "/WEB-INF/enlem-boylam.csv";
		data = read_latLong(data,latLong);

        ArrayList<Integer> destinations = add_desi(data,demand);   
        
        //result
        Problem sol = new Problem(depots,data,destinations,writer);
        String htmlResponse = "<!DOCTYPE html>\n" + 
        		"<html lang=\"en\">\n" + 
        		"  <head>\n" + 
        		"    <meta charset=\"UTF-8\" />\n" + 
        		"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" + 
        		"    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\" />\n" + 
        		"    <link\n" + 
        		"      rel=\"stylesheet\"\n" + 
        		"      href=\"https://unpkg.com/leaflet@1.6.0/dist/leaflet.css\"\n" + 
        		"      integrity=\"sha512-xwE/Az9zrjBIphAcBb3F6JVqxf46+CDLwfLMHloNu6KEQCAWi6HcDUbeOfBIptF7tcCzusKFjFw2yuvEpDL9wQ==\"\n" + 
        		"      crossorigin=\"\"\n" + 
        		"    />\n" +
        		"<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css\" integrity=\"sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh\" crossorigin=\"anonymous\">" +
        		"    <script\n" + 
        		"      src=\"https://unpkg.com/leaflet@1.6.0/dist/leaflet.js\"\n" + 
        		"      integrity=\"sha512-gZwIG9x3wUXg2hdXF6+rVkLF/0Vi9U8D2Ntg4Ga5I5BZpVkVxlJWbSQtXPSiUTtC0TjtGOmxa1AJPuV0CPthew==\"\n" + 
        		"      crossorigin=\"\"\n" + 
        		"    ></script>\n" + 
        		"    <title>API Deneme</title>\n" + 
        		"  </head>\n" + 
        		"  <body>\n";
        htmlResponse+=addNavBar(sol); 
        htmlResponse=sol.best.addToResponse(htmlResponse);
        
        if(api_key != "") {
	        htmlResponse +=
	        		"    <h2>Visualize</h2>\n";
	        htmlResponse += add_button_for_each_truck(sol);
	        
	        htmlResponse += "<div class=\"container\">\n";
	        int counter=0;
	        int mapid = 0;
	        while(counter<sol.best.truckPop.size()){
	        	htmlResponse +=		"  <div class=\"row\">";
		        for(int i=0;i<2;i++){
			        htmlResponse +=
			        		"    <div class=\"col\" id=\"mapid"+mapid+"\"></div>\n";
			        mapid++;}
			    htmlResponse += "    </div>\n";
			    counter++;}
			htmlResponse += "  </div>";
			        
	        for(int i=0;i<sol.best.truckPop.size();i++){
		        htmlResponse +=
		        		"    <style>\n" + 
		        		"      #mapid"+i+" {\n" + 
		        		"        height: 180px;\n" +
		        		"      }\n" + 
		        		"    </style>\n";}
	        htmlResponse += addfunc_for_each_button(sol,api_key);
        } else {
        	htmlResponse += "<div class = 'col' id='link'>" +
        			"<p class = 'text-center'> If you want to see your truck routes. Please turn back and write your api key." +
        			" About api key follow <a href=\"https://openrouteservice.org/\" target=\"_blank\">this</a> link.</p>"+
        			"</div>";
        }
        
        htmlResponse += ""
        		+ "<script src=\"https://code.jquery.com/jquery-3.4.1.slim.min.js\" integrity=\"sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n\" crossorigin=\"anonymous\"></script>\n" + 
        		"<script src=\"https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js\" integrity=\"sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo\" crossorigin=\"anonymous\"></script>\n" + 
        		"<script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js\" integrity=\"sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6\" crossorigin=\"anonymous\"></script>";
        htmlResponse += "</body></html> ";
        writer.println(htmlResponse);
		//Time elapsed
		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
		writer.println("\nExecution time in milliseconds : " + 
				timeElapsed / 1000000+"ms");
		}

	
	private String addNavBar(Problem sol) {
		String nav="<nav class=\"navbar navbar-expand-lg navbar-light bg-light\">\n" + 
				"      <a class=\"navbar-brand\" href=\"routes.html\">Routing App</a>\n" + 
				"      <button\n" + 
				"        class=\"navbar-toggler\"\n" + 
				"        type=\"button\"\n" + 
				"        data-toggle=\"collapse\"\n" + 
				"        data-target=\"#navbarSupportedContent\"\n" + 
				"        aria-controls=\"navbarSupportedContent\"\n" + 
				"        aria-expanded=\"false\"\n" + 
				"        aria-label=\"Toggle navigation\"\n" + 
				"      >\n" + 
				"        <span class=\"navbar-toggler-icon\"></span>\n" + 
				"      </button>\n" + 
				"\n" + 
				"      <div class=\"collapse navbar-collapse\" id=\"navbarSupportedContent\">\n" + 
				"        <ul class=\"navbar-nav mr-auto\">\n" + 
				"          <li class=\"nav-item active\">\n" + 
				"            <a class=\"nav-link\" href=\"routes.html\"\n" + 
				"              >Home <span class=\"sr-only\">(current)</span></a\n" + 
				"            >\n" + 
				"          </li>\n" + 
				"          <li class=\"nav-item\">\n" + 
				"            <a class=\"nav-link\" href=\"aboutus.html\">About us</a>\n" + 
				"          </li>\n" + 
				"        </ul>\n" + 
				"        <form class=\"form-inline my-2 my-lg-0\">\n" + 
				"          <input\n" + 
				"            class=\"form-control mr-sm-2\"\n" + 
				"            type=\"search\"\n" + 
				"            placeholder=\"Search\"\n" + 
				"            aria-label=\"Search\"\n" + 
				"          />\n" + 
				"          <button class=\"btn btn-outline-success my-2 my-sm-0\" type=\"submit\">\n" + 
				"            Search\n" + 
				"          </button>\n" + 
				"        </form>\n" + 
				"      </div>\n" + 
				"    </nav>";
		return nav;
	}


	private String addfunc_for_each_button(Problem sol,String api_key) {
		String script ="    <script>\n";
		for(int i=0;i<sol.best.truckPop.size();i++){
			script +=
	        		"      function mfunc"+i+"() {\n" +
	        		"        var mymap"+i+" = L.map(\"mapid"+i+"\").setView(["+40.766666+", "+29.916668+"], 4);\n" + 
	        		"        const attribution =\n" + 
	        		"          '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors';\n" + 
	        		"        const tileUrl = \"https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png\";\n" + 
	        		"        const tiles = L.tileLayer(tileUrl, { attribution });\n" + 
	        		"        tiles.addTo(mymap"+i+");\n";
			for(int j=0;j<sol.best.truckPop.get(i).route.size();j++){
				int city = sol.best.truckPop.get(i).route.get(j);
				script +="        L.marker(["+sol.best.data.get(city-1).lati+", "+sol.best.data.get(city-1).longi+"]).addTo(mymap"+i+");\n";
	        		
			}
	        script +=		
	        		"        let request = new XMLHttpRequest();\n" + 
	        		"\n" + 
	        		"        request.open(\n" + 
	        		"          \"POST\",\n" + 
	        		"          \"https://api.openrouteservice.org/v2/directions/driving-car/geojson\"\n" + 
	        		"        );\n" + 
	        		"\n" + 
	        		"        request.setRequestHeader(\n" + 
	        		"          \"Accept\",\n" + 
	        		"          \"application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8\"\n" + 
	        		"        );\n" + 
	        		"        request.setRequestHeader(\"Content-Type\", \"application/json\");\n" + 
	        		"        request.setRequestHeader(\n" + 
	        		"          \"Authorization\",\n" + 
	        		"          \""+api_key+"\"\n" + 
	        		"        );\n" + 
	        		"\n" + 
	        		"        request.onreadystatechange = function () {\n" + 
	        		"          if (this.readyState === 4) {\n" + 
	        		"            console.log(\"Status:\", this.status);\n" + 
	        		"            console.log(\"Headers:\", this.getAllResponseHeaders());\n" + 
	        		"            console.log(\"Body:\", this.responseText);\n" + 
	        		"            var geojsonFeature = JSON.parse(this.responseText);\n" + 
	        		"            L.geoJSON(geojsonFeature).addTo(mymap"+i+");\n" + 
	        		"          }\n" + 
	        		"        };\n" + 
	        		"\n" + 
	        		"        const body =\n" + 
	        		"          '{\"coordinates\":[[' +\n";
			script += add_info_for_each_func(sol,i);
			int lastCity = sol.best.truckPop.get(i).route.get(sol.best.truckPop.get(i).route.size()-1);
			script +=
	        		sol.best.data.get(lastCity-1).longi+"+','+"+sol.best.data.get(lastCity-1).lati+"+']],\"instructions_format\":\"html\",\"units\":\"km\"}';\n" + 
	        		"\n" + 
	        		"        request.send(body);\n" + 
	        		"      }\n";
		}
		script +=
		"    </script>";
		return script;
	}


	private String add_info_for_each_func(Problem sol, int truckID) {
		String info="";
		for(int i=0;i<(sol.best.truckPop.get(truckID).route.size()-1);i++){
			int cityID = sol.best.truckPop.get(truckID).route.get(i);
			info +=
	        		"          "+sol.best.data.get(cityID-1).longi+" +\n" + 
	        		"          ',' +\n" + 
	        		"          "+sol.best.data.get(cityID-1).lati+" +\n" + 
	        		"          '],[' +\n";
		}
		return info;
	}


	private String add_button_for_each_truck(Problem sol) {
		String button ="";
		for(int i=0;i<sol.best.truckPop.size();i++){
			button +="    <button onclick=\"mfunc"+i+"()\">\n" + 
	        		"      Truck"+i+"\n" + 
	        		"    </button>\n";
		}
		return button;
	}


	private ArrayList<City> read_latLong(ArrayList<City> data, String latLong) throws IOException {
		ServletContext context = getServletContext();
        InputStream is = context.getResourceAsStream(latLong);    
        if (is != null) {
        	InputStreamReader isr = new InputStreamReader(is);
        	BufferedReader reader = new BufferedReader(isr);
        	String text;
        	int i =0;
        	while ((text = reader.readLine()) != null) {
        		String[] str=text.split(";");
        		data.get(i).lati = str[0];
        		data.get(i).longi = str[1];
        		i++;
        		
        	}
        }
		return data;
	}


	private ArrayList<City> read_names(ArrayList<City> data, String cityNames) throws NumberFormatException, IOException {
		ServletContext context = getServletContext();
        InputStream is = context.getResourceAsStream(cityNames);    
        if (is != null) {
        	InputStreamReader isr = new InputStreamReader(is);
        	BufferedReader reader = new BufferedReader(isr);
        	String text;
        	int i =0;
        	while ((text = reader.readLine()) != null) {
        		data.get(i).name = text;
        		i++;
        		
        	}
        }
		return data;
	}


	private ArrayList<Integer> add_desi(ArrayList<City> data, String demand) {
		int index;
		ArrayList<Integer> destinations = new ArrayList<>();

    	String[] str = demand.split(" ");
    	
    	for (int i=0;i<str.length;i++) {
    		String[] d = str[i].split(";");
    		index = Integer.parseInt(d[0])-1;
    		destinations.add(index);
    		int score;
    		try {
    			score=Integer.parseInt(d[1]);
    		} catch(Exception err) {
    			Double x =Double.parseDouble(d[1]);
    			score=(int) Math.ceil(x);
    		}	
    		data.get(index).desi = score;
    		data.get(index).remaining_desi =score;
    	}
		return destinations;
	}


	public static String[] replacements(String str) {
    	 String[] depots_strarr;
    	 str=str.replace(" ", "");
    	 depots_strarr = str.split(",");
    	 return depots_strarr;
		
     }


     public static int[] str_to_array(String[] str) {
     	 int[] arr = new int[max_number_of_depot];
         int size = str.length;
         for(int i=0; i<size; i++) {
             arr[i] = Integer.parseInt(str[i]);
         }
         return arr;
 	}

     
	
                   
    private ArrayList<City> read_data(String dataFileName) throws IOException {
    		ArrayList<City> data = new ArrayList<>();
    		
    		ArrayList<Integer> temp = new ArrayList<>();
    		Map<String, City> details = new HashMap<>();
    		int counter=0;
    		int distance=0;
    		int id;
    		ServletContext context = getServletContext();
            InputStream is = context.getResourceAsStream(dataFileName);    
            if (is != null) {
            	InputStreamReader isr = new InputStreamReader(is);
            	BufferedReader reader = new BufferedReader(isr);
            	String text;
            	int i =0;
            	while ((text = reader.readLine()) != null) {
        			counter++;
        			temp.clear();
        			id=counter;
        			String[] str = text.split(";"); 
        			for (String a : str) {
        				distance=Integer.parseInt(a);
            			temp.add(distance);
        			}
        			details.put("City" + i, new City(id, temp) );
        			i++;
                }
            
            }

    		for(int j = 0;j<counter ;j++) {
    			data.add(details.get("City"+j));
    		}
    		return data;
    	}
       	
    private ArrayList<City> read_tourCost(ArrayList<City> data,int[] depots, String tourCost) throws IOException,NumberFormatException {
    		ServletContext context = getServletContext();
            InputStream is = context.getResourceAsStream(tourCost);    
            if (is != null) {
            	InputStreamReader isr = new InputStreamReader(is);
            	BufferedReader reader = new BufferedReader(isr);
            	String text;
            	int i =0;
            	while ((text = reader.readLine()) != null) {
            		String[] str = text.split(";");
            		for(int k = 0;k<str.length;k++) {
            			Double score=Double.parseDouble(str[k]);
            			data.get(i).tourCosts.add(score);
            		}
            		i++;
            		
            	}
            }
    		
    		return data;
    	}


	private ArrayList<City> read_lorryCost(ArrayList<City> data,int[] depots,String lorryCostFileName, PrintWriter writer) throws IOException,NumberFormatException {
		ServletContext context = getServletContext();
        InputStream is = context.getResourceAsStream(lorryCostFileName);
        if (is != null) {
        	InputStreamReader isr = new InputStreamReader(is);
        	BufferedReader reader = new BufferedReader(isr);
        	String text;
        	int i =0;
        	while ((text = reader.readLine()) != null) {
        		String[] str = text.split(";");
        		for(int k = 0;k<str.length;k++) {
        			int score = 0;
        			if(i == 0 & k==0) {
        				score = 1535;
        			} else {
	        			score=Integer.parseInt(str[k]);
        			}
        			data.get(i).kamyonCost.add(score);
        		}
        		i++;
        	}
        }
		
		return data;
	}
	
	private ArrayList<City> read_parcel(ArrayList<City> data, int[] depots, String parcel) throws IOException,NumberFormatException {
		
		ServletContext context = getServletContext();
        InputStream is = context.getResourceAsStream(parcel);
        if (is != null) {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);
            String text;
            int city;
            Double cost;
            int i =0;
            while ((text = reader.readLine()) != null) {
            	String[] str = text.split(";");
            	if(i == 0) {
            		cost = 4.09;
            	} else {
    	        	cost = Double.parseDouble(str[0]);
            	}
				city = Integer.parseInt(str[1])-1;
				data.get(city).parcelCost = cost;
				i++;
            }
        }
		return data;
	}
}
