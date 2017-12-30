package edu.utk.gsda;

import java.util.Vector;

import edu.princeton.cs.algs4.In;


public class PlaceReader
{
	public static Vector<String> readPlaces(String filePath)
	{
		try
		{
			Vector<String> citiesVector = new Vector<String>();
			
			In input = new In(filePath);
			
			String inputLine = null;
			while((inputLine = input.readLine()) != null)
			{
				inputLine = inputLine.trim();
				if(inputLine.length()>0)
					citiesVector.add(inputLine);
			}
			
			return citiesVector;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
