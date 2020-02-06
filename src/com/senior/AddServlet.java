package com.senior;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;



public class AddServlet extends HttpServlet
{

	public void service(HttpServletRequest req,HttpServletResponse res) 
	{	
		String api_key = req.getParameter("api_key"); // Integer.parseInt(req.getPara..)
		String adress = req.getParameter("adress");
		String mode = req.getParameter("mode");
		String unit = req.getParameter("unit");
		

		
		findMatrices(api_key,adress,res,mode,unit);
		

       	
		}
	
	
     private void findMatrices(String api_key, String adress, HttpServletResponse res, String mode, String unit) {
       	try {
       	PrintWriter out  = res.getWriter();
    		
   		 String origins = adress;
   		 origins = origins.replace(" ", "%");
   		 origins = origins.replace(",", "|");
   		 
   		 String destinations = origins;
   		 
   		 String format="xml";
   		 String language= "tr-TR";
   		 String url = "https://maps.googleapis.com/maps/api/distancematrix/"+format+"?origins="+origins+"&destinations="+destinations+
   				 "&mode="+mode+"&language="+language+"&key="+api_key;
   		 out.println(url);
   		 URL obj = new URL(url);
   		 HttpURLConnection con = (HttpURLConnection) obj.openConnection();
   		 int responseCode = con.getResponseCode();
   		 out.println("Response Code : " + responseCode);
   		  BufferedReader in = new BufferedReader(
   			 new InputStreamReader(con.getInputStream()));
   			 String inputLine;
   			 StringBuffer response = new StringBuffer();
   			 while ((inputLine = in.readLine()) != null) {
   			   response.append(inputLine);
   			 } 
   			in.close();
   			//print in String
   			out.println(response.toString());
   		        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
   		         .parse(new InputSource(new StringReader(response.toString())));
   			doc.getDocumentElement().normalize();
              out.println ("Root element of the doc is " +
                      doc.getDocumentElement().getNodeName());
              NodeList listofElements = doc.getElementsByTagName("element"); 
              NodeList listofDuration = doc.getElementsByTagName("duration"); 
              NodeList listofDistance = doc.getElementsByTagName("distance"); 
   		   
              ArrayList<Float> durationMatrixAll = new ArrayList<>();
              ArrayList<Float> distanceMatrixAll = new ArrayList<>();
              int totalElements = listofElements.getLength(); 
              out.println("Total number of elements : " + (int) Math.sqrt(totalElements)+"x"+(int)Math.sqrt(totalElements));
              for (int s=0; s<listofElements.getLength() ; s++) {

                      Node firstElNode = listofElements.item(s);
                      if(firstElNode.getNodeType() == Node.ELEMENT_NODE) {
                      	Node firstDurNode = listofDuration.item(s);
                      	if(firstDurNode.getNodeType() == Node.ELEMENT_NODE) {
                      		Element firstElement = (Element)firstDurNode;
                      		NodeList durationList = firstElement.getElementsByTagName("value"); 
                      		Element durationElement = (Element)durationList.item(0);
                      		NodeList textDurationList = durationElement.getChildNodes();
                      		float result=0;
                      		if(unit != "meter") {
                          		result = Integer.parseInt(((Node)textDurationList.item(0)).getNodeValue().trim());
                      			result = result/60;
                          		durationMatrixAll.add(result);
                      		} else {
                      			result = (int) result;
                              	result = Integer.valueOf(((Node)textDurationList.item(0)).getNodeValue().trim());
                              	distanceMatrixAll.add(result);
                      		}
                      	}
                      	Node firstDistNode = listofDistance.item(s);
                      	if(firstDistNode.getNodeType() == Node.ELEMENT_NODE) {
                      		Element firstElement = (Element)firstDistNode;
                      		NodeList distanceList = firstElement.getElementsByTagName("value");
                      		Element distanceElement = (Element)distanceList.item(0);
                      		NodeList textDistanceList = distanceElement.getChildNodes();
                      		float result=0;
                      		if(unit != "meter") {
                              	result = Integer.valueOf(((Node)textDistanceList.item(0)).getNodeValue().trim());
                      			result = (float) result/1000;
                              	distanceMatrixAll.add(result);
                      		} else {
                      			result = (int) result;
                              	result = Integer.valueOf(((Node)textDistanceList.item(0)).getNodeValue().trim());
                              	distanceMatrixAll.add(result);
                      		}
                      }
                  } 
              }
              out.println("\n- - - - - Distance Matrix - - - - -");
              int size = (int) Math.sqrt(totalElements);
              float[][] distanceMatrix = new float[size][size];
              distanceMatrix=to2dArray(distanceMatrixAll);
              for(float[] row : distanceMatrix) {
                  printRow(row,res);
              }
              out.println("\n- - - - - Duration Matrix - - - - -");
              float[][] durationMatrix = new float[size][size];
              durationMatrix=to2dArray(durationMatrixAll);
              for(float[] row : durationMatrix) {
                  printRow(row,res);
              }}
              catch (SAXParseException err) {
           	   PrintWriter out;
   			try {
   				out = res.getWriter();
   	            out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
   	            out.println(" " + err.getMessage ());
   			} catch (IOException e) {
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   			}
              }catch (SAXException e) {
                  Exception x = e.getException ();
                  ((x == null)? e :x).printStackTrace();
              }catch (Throwable t) {
                  t.printStackTrace ();
              }
		
	}
		private static float[][] to2dArray(ArrayList<Float> durationMatrixAll) {
               ArrayList<Float> temp =new ArrayList<>();
               int size = (int) Math.sqrt(durationMatrixAll.size());
               float[][] array = new float[size][size];
               int index = 0;
       		for (int i = 0; i < size; i++) {
       			for (int j = 0; j < size; j++){
       				index = (size)*i+j;
       				temp.add(durationMatrixAll.get(index));
       			}
       			//System.out.println(temp.toString());
       			for (int x = 0; x < size; x++){
       				array[i][x]=temp.get(x); }
       			temp.clear();
       		}
       		return array;
       	}
       	
           public static void printRow(float[] row, HttpServletResponse res) throws IOException {
        	   PrintWriter out  = res.getWriter();
               for (float i : row) {
                   out.print(i);
                   out.print("\t");
               }
               out.print("\n");
           }
			
}
