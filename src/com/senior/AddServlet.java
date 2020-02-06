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
		try {
		String api_key = req.getParameter("api_key"); // Integer.parseInt(req.getPara..)
		String adress = req.getParameter("adress");
		
		PrintWriter out  = res.getWriter();
		
		 String origins = adress;
		 origins = origins.replace(" ", "%");
		 origins = origins.replace(",", "|");
		 
		 //System.out.println("Please write your destinations: \n ");
		 //String destinations=ob.readLine();
		 String destinations = origins;
		 
		 String format="xml";
		 String mode = "driving";
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
		   
           ArrayList<Integer> durationMatrixAll = new ArrayList<>();
           ArrayList<Integer> distanceMatrixAll = new ArrayList<>();
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
                   		//System.out.println("duration "+s+": " + ((Node)textDurationList.item(0)).getNodeValue().trim() + " sec");
                   		int result = Integer.parseInt(((Node)textDurationList.item(0)).getNodeValue().trim());
                   		durationMatrixAll.add(result);
                   	}
                   	Node firstDistNode = listofDistance.item(s);
                   	if(firstDistNode.getNodeType() == Node.ELEMENT_NODE) {
                   		Element firstElement = (Element)firstDistNode;
                   		NodeList distanceList = firstElement.getElementsByTagName("value");
                   		Element distanceElement = (Element)distanceList.item(0);
                   		NodeList textDistanceList = distanceElement.getChildNodes();
                   		//System.out.println("distance "+s+": " + ((Node)textDistanceList.item(0)).getNodeValue().trim() + " m");
                       	int result = Integer.valueOf(((Node)textDistanceList.item(0)).getNodeValue().trim());
                       	distanceMatrixAll.add(result);
                   }
               } 
           }
           out.println("\n- - - - - Distance Matrix - - - - -");
           int size = (int) Math.sqrt(totalElements);
           int[][] distanceMatrix = new int[size][size];
           distanceMatrix=to2dArray(distanceMatrixAll);
           for(int[] row : distanceMatrix) {
               printRow(row,res);
           }
           out.println("\n- - - - - Duration Matrix - - - - -");
           int[][] durationMatrix = new int[size][size];
           durationMatrix=to2dArray(durationMatrixAll);
           for(int[] row : durationMatrix) {
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
       	private static int[][] to2dArray(ArrayList<Integer> arrayList) {
               ArrayList<Integer> temp =new ArrayList<>();
               int size = (int) Math.sqrt(arrayList.size());
               int[][] array = new int[size][size];
               int index = 0;
       		for (int i = 0; i < size; i++) {
       			for (int j = 0; j < size; j++){
       				index = (size)*i+j;
       				temp.add(arrayList.get(index));
       			}
       			//System.out.println(temp.toString());
       			for (int x = 0; x < size; x++){
       				array[i][x]=temp.get(x); }
       			temp.clear();
       		}
       		return array;
       	}
       	
           public static void printRow(int[] row, HttpServletResponse res) throws IOException {
        	   PrintWriter out  = res.getWriter();
               for (int i : row) {
                   out.print(i);
                   out.print("\t");
               }
               out.println("\n");
           }
			
}
