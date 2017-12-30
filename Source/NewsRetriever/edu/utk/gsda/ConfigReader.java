package edu.utk.gsda;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class ConfigReader 
{
	public static JSONObject readConfigFile()
	{
		try 
		{
			StdOut.println("Reading the configuration parameters...");
			
			String configString = "";
			In configIn = new In("config.json");
			String inputString = null;
			while((inputString = configIn.readLine()) != null) 
			{
				int index = inputString.indexOf("#");
				if(index != -1)
					configString += inputString.substring(0, index);
				else
					configString += inputString;
			}
			
			JSONObject configObject = new JSONObject(configString);
			
			
			if(configObject.has("cityFile") && !configObject.isNull("cityFile"))
			{
				StdOut.println("The city file is: "+configObject.getString("cityFile"));
			}
			else 
			{
				StdOut.println("The cityFile parameter is not configured correctly. Program exits.");
				return null;
			}
			
			if(configObject.has("startingYear") && !configObject.isNull("startingYear"))
			{
				StdOut.println("The starting year is: "+configObject.getString("startingYear"));
			}
			else 
			{
				StdOut.println("The startingYear parameter is not configured correctly. Program exits.");
				return null;
			}
			
			if(configObject.has("endingYear") && !configObject.isNull("endingYear"))
			{
				StdOut.println("The ending year is: "+configObject.getString("endingYear"));
			}
			else 
			{
				StdOut.println("The endingYear parameter is not configured correctly. Program exits.");
				return null;
			}
			
			int startingYear = Integer.parseInt(configObject.getString("startingYear"));
			int endingYear = Integer.parseInt(configObject.getString("endingYear"));
			if(startingYear > endingYear)
			{
				StdOut.println("Starting year should not be larger than ending year. Program exits.");
				return null;
			}
			
			
			if(configObject.has("apiKey") && !configObject.isNull("apiKey"))
			{
				JSONArray apiKeyArray = configObject.getJSONArray("apiKey");
				String apiKeyString = "";
				for(int i=0;i<apiKeyArray.length();i++)
					apiKeyString += apiKeyArray.getString(i)+"; ";
				
				StdOut.println("The ending year is: "+ apiKeyString);
			}
			else 
			{
				StdOut.println("The apiKey parameter is not configured correctly. Program exits.");
				return null;
			}
			
			
			StdOut.println("Parameter reading finishes");
			StdOut.println("---------------------------------------");
			return configObject;
			
			
			
		} 
		catch (Exception e) 
		{
			StdOut.println("An error happened when reading the configuration file. Program exits.");
			return null;
		}
	}

}
