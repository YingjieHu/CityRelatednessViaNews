package edu.utk.gsda;

import java.util.Enumeration;
import java.util.Hashtable;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;

public class CityNetworkDataExtractor {
	
	public static void main(String[] args) 
	{
		
		try 
		{
			// Specify the semantic topic under which the city relatedness data will be extracted   
			String targetTopic = "Politics";	
			
			//String targetTopic = "Humaninterest";		
			//String targetTopic = "Scienceandtechnology";		
			//String targetTopic = "Artscultureandentertainment";
			
			int targetIndex = -1;
			
			In labelIndexInput = new In("label-index.txt");
			for(int labelIndex = 0;labelIndex<17;labelIndex++)
			{
				if(targetTopic.equals(labelIndexInput.readLine().trim()))
				{
					targetIndex = labelIndex;
					break;
				}
			}
			labelIndexInput.close();
			// label reading finisheds
			
			
			// begin to construct the proximity and term files for PFNet
			int maximumWeight = -1;
					
			In relatednessData = new In("Guardian_combined_result.csv");
			Hashtable<String, Integer> relatednessTable = new Hashtable<>();
			Hashtable<String, Integer> cityTable = new Hashtable<>();
			
			String inputLine = relatednessData.readLine();
			while((inputLine = relatednessData.readLine()) != null)
			{
				String[] info = inputLine.split(",");
				String[] cityPair = info[0].split("-");
				int weight = Integer.parseInt(info[(targetIndex+1)*3+1]);
				
				//if(weight< averageWeight) continue;
				
				int convertedWeight = (int)(Math.log(weight) / Math.log(2));
				if(convertedWeight < 1) continue; // if two cities co-occur in 1 news article (log_2(1) = 0), then we ignore this city pair
				if(convertedWeight > maximumWeight) maximumWeight = convertedWeight;
				cityTable.put(cityPair[0], 0);
				cityTable.put(cityPair[1], 0);
				
				relatednessTable.put(cityPair[0]+"-"+cityPair[1], convertedWeight);
			}
			relatednessData.close();
			
			Out pathFinderFileOutput = new Out("PathFinder_"+targetTopic+".prx.txt");
			Out pathTermsOutput = new Out("terms.txt");
			
			pathFinderFileOutput.println("data");
			pathFinderFileOutput.println("distances");
			pathFinderFileOutput.println(cityTable.size()+" nodes");
			pathFinderFileOutput.println("0 decimal places");
			pathFinderFileOutput.println("0 minimum value");
			pathFinderFileOutput.println((maximumWeight+1)+" maximum value");
			pathFinderFileOutput.println("full matrix");
			
			Enumeration<String> cityEnumeration = cityTable.keys();
			String[] cityArray = new String[cityTable.size()];
			int index = 0;
			while (cityEnumeration.hasMoreElements()) 
			{
				String thisCity = (String) cityEnumeration.nextElement();
				cityArray[index] = thisCity;
				pathTermsOutput.println(thisCity);
				index++;
			}
			
			String outputMatrixRow = "";
			for(int i=0;i<cityArray.length;i++)
			{
				outputMatrixRow = "";
				for(int j=0;j<cityArray.length;j++)
				{
					int thisWeight = 0; 
					if(i!=j)
					{
						Integer thisWeightObject = relatednessTable.get(cityArray[i]+"-"+cityArray[j]);
						if(thisWeightObject == null)
							thisWeightObject = relatednessTable.get(cityArray[j]+"-"+cityArray[i]);
						
						if(thisWeightObject != null)
							thisWeight = (maximumWeight+1) - thisWeightObject.intValue();
						else 
							thisWeight = (maximumWeight+1);
						
					}
					outputMatrixRow += thisWeight+" ";
				}
				outputMatrixRow = outputMatrixRow.trim();
				pathFinderFileOutput.println(outputMatrixRow);
			}

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

}
