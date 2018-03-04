package edu.jsu.mcis;

import java.io.*;
import java.util.*;
import com.opencsv.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Converter {
    
    /*
    
        Consider the following CSV data:
        
        "ID","Total","Assignment 1","Assignment 2","Exam 1"
        "111278","611","146","128","337"
        "111352","867","227","228","412"
        "111373","461","96","90","275"
        "111305","835","220","217","398"
        "111399","898","226","229","443"
        "111160","454","77","125","252"
        "111276","579","130","111","338"
        "111241","973","236","237","500"
        
        The corresponding JSON data would be similar to the following (tabs and other whitespace
        have been added for clarity).  Note the curly braces, square brackets, and double-quotes!
        
        {
            "colHeaders":["ID","Total","Assignment 1","Assignment 2","Exam 1"],
            "rowHeaders":["111278","111352","111373","111305","111399","111160","111276","111241"],
            "data":[[611,146,128,337],
                    [867,227,228,412],
                    [461,96,90,275],
                    [835,220,217,398],
                    [898,226,229,443],
                    [454,77,125,252],
                    [579,130,111,338],
                    [973,236,237,500]
            ]
        }
    
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        //initialize object and read info and place info into object
        JSONObject jsonObject = new JSONObject();
	//seperates data
        CSVParser parser = new CSVParser();
        BufferedReader bReader = new BufferedReader(new StringReader(csvString));
        CSVReader reader = new CSVReader(new StringReader(csvString));
        JSONArray rowHeaders = new JSONArray();
        JSONArray colHeaders = new JSONArray();
        JSONArray data = new JSONArray();
        jsonObject.put("rowHeaders", rowHeaders);
        jsonObject.put("colHeaders", colHeaders);
        jsonObject.put("data", data);
        
        try {
            List<String[]> full = reader.readAll();
            Iterator<String[]> iterator = full.iterator();
            String[] headings = iterator.next();
            
  	    //create two loops that read headers and then the info
            for (String heading : headings) {
                colHeaders.add(heading);
            }
            
            String line = bReader.readLine();
            while ((line = bReader.readLine()) != null) {
		//seperate data then store into a list
                String[] parsedData = parser.parseLine(line);
                JSONArray rowData = new JSONArray();
                rowHeaders.add(parsedData[0]);
                rowData.add(new Long(parsedData[1]));
                rowData.add(new Long(parsedData[2]));
                rowData.add(new Long(parsedData[3]));
                rowData.add(new Long(parsedData[4]));
                data.add(rowData);
            }

            String jsonString = JSONValue.toJSONString(jsonObject);
        }
        catch(IOException e) { return e.toString(); }
        
        return jsonObject.toString();
    }
    
    public static String jsonToCsv(String jsonString) {
        
        String csvString = "";
        try {

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject)parser.parse(jsonString);
            
            JSONArray colHeaders = (JSONArray) jsonObject.get("colHeaders");
            JSONArray rowHeaders = (JSONArray) jsonObject.get("rowHeaders");
            JSONArray data = (JSONArray) jsonObject.get("data");
            //sepeate columns into appropiate format
            for(int i=0; i < colHeaders.size(); i++){
                if(i != colHeaders.size()-1){
                    csvString += "\""+ colHeaders.get(i)+ "\""+",";
                }
                else{
                    csvString+= "\""+ colHeaders.get(i)+ "\"";
                }
                
            }
            csvString += "\n";
            //seperate rows into appropriate format
            for(int i=0; i < rowHeaders.size(); i++){
                ArrayList rows = new ArrayList<String>();
                String row = "\"" + rowHeaders.get(i) + "\"";
                rows.add(row);
                JSONArray sub = (JSONArray) data.get(i);
                
                for(int j=0; j < sub.size(); j++){
                    String indData = "\"" + sub.get(j) + "\"";
                    rows.add(indData);
                }
                csvString += rows.toString().replace("[", "").replace("]", "").replace(", ", ",") + "\n";
            }
        }
        catch(ParseException e) { return e.toString(); }
        
        return csvString.trim();
    }
	
}
