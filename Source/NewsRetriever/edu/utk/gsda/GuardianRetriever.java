package edu.utk.gsda;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdOut;


public class GuardianRetriever
{

	public static void main(String[] args)
	{	
		try
		{
			// Step 1: reading configuration
			JSONObject configObject = ConfigReader.readConfigFile();
			
			if(configObject ==  null)
				return;
			
			String startingYear = configObject.getString("startingYear");
			String endingYear = configObject.getString("endingYear");
			
			String cityFile = configObject.getString("cityFile");
			JSONArray apiKeyArray = configObject.getJSONArray("apiKey");
			
			
			// Step 2: begin to retrieve data 
			String targetURL = "https://content.guardianapis.com/search?page-size=200&from-date="+startingYear+"-01-01&to-date="+endingYear+"-12-31&show-fields=bodyText&type=article&show-tags=keyword";
			
            Vector<String> citiesVector = PlaceReader.readPlaces(cityFile);
            int cityCount = citiesVector.size();
            
            
            // Retrieving city co-occurrence news ------------------------------------     
			Out cityCooccurrenceNews = new Out("City_Cooccurrence_NewsArticles.csv");
			cityCooccurrenceNews.println("city,pubDate,sectionName,category,title,fulltext");
			
			Out newsInteraction = new Out("City_Cooccurrence_Count.csv");
			newsInteraction.println("city,newsCount");
			
			// Swap keys dynamically when multiple API keys are provided
			int keyIndex = 0;
			int keyCount = apiKeyArray.length();
			for (int i = 0; i < (cityCount-1); i++)
				    for(int j=i+1;j<cityCount;j++)
				    {
					    String cityA = citiesVector.get(i);
					    String cityB = citiesVector.get(j);
					    
					    String queryString = "&q=%22"+cityA.replaceAll(" ", "%20")+"%22%20AND%20%22"+cityB.replaceAll(" ", "%20")+"%22";
					      
					    keyIndex++;
					    int thisKey = keyIndex % keyCount;
					    
					    Thread.sleep(2000); // sleep 2 seconds to avoid over frequent requests
					    String result = null;
					    try
					    {
						    result = URLRequest.sendGet(targetURL+queryString+"&api-key="+apiKeyArray.getString(thisKey));
					    } 
					    catch (Exception ee)
					    {
						    Thread.sleep(5000);
						    keyIndex++;
						    thisKey = keyIndex % keyCount;
						    try
						    {
						    	result = URLRequest.sendGet(targetURL+queryString+"&api-key="+apiKeyArray.getString(thisKey));
						    }
						    catch(Exception eee)
						    {
						    	Thread.sleep(5000);
							    keyIndex++;
							    thisKey = keyIndex % keyCount;
							    try
							    {
							    	result = URLRequest.sendGet(targetURL+queryString+"&api-key="+apiKeyArray.getString(thisKey));
							    }
							    catch(Exception eeee)
							    {
							    	StdOut.println(cityA+"-"+cityB+" failed, skipped ..."); 	
							    	continue;
							    }
						    }
						    
					    }
					    
					    
					    // convert the retrieved result 
					    JSONObject jsonObject = new JSONObject(result);
					    jsonObject = jsonObject.getJSONObject("response");
					    int totalPage = jsonObject.getInt("pages");
					 
					    int totalNumber =  jsonObject.getInt("total"); 
					    
					    newsInteraction.println(cityA+"-"+cityB+","+totalNumber);
					    StdOut.println("There are "+totalNumber+" news about "+cityA+" and "+cityB);
					    
					    for(int k=1;k<=totalPage;k++)
					    {
						    StdOut.println("Retrieving page "+k+"...");
							Thread.sleep(2000);
							
							// for page 1 directly use existing retrieved data
							if(k!=1)
							{
								keyIndex++;
								thisKey = keyIndex % keyCount;
								try
								{
									result = URLRequest.sendGet(targetURL+queryString+"&page="+k+"&api-key="+apiKeyArray.getString(thisKey));
								} 
								catch (Exception ee)
								{
									Thread.sleep(5000);
									keyIndex++;
									thisKey = keyIndex % keyCount;
									try 
									{
										result = URLRequest.sendGet(targetURL+queryString+"&page="+k+"&api-key="+apiKeyArray.getString(thisKey));
									} 
									catch (Exception eee) 
									{
										Thread.sleep(5000);
										keyIndex++;
										thisKey = keyIndex % keyCount;
										try 
										{
											result = URLRequest.sendGet(targetURL+queryString+"&page="+k+"&api-key="+apiKeyArray.getString(thisKey));
										} 
										catch (Exception eeee) 
										{
											StdOut.println(cityA+"-"+cityB+", page "+k+" failed, skipped ...");
											continue;
										}
									}
								}
							}
							// first page situation handled
							
							try 
							{
								jsonObject = new JSONObject(result);
								jsonObject = jsonObject.getJSONObject("response");
							} 
							catch (Exception eeee) 
							{
								StdOut.println(cityA+"-"+cityB+", page "+k+" failed, skipped ...");
								continue;
							}
							
							JSONArray docs = jsonObject.getJSONArray("results");
							for(int docIndex = 0;docIndex<docs.length();docIndex++)
							{
								JSONObject thisDoc = docs.getJSONObject(docIndex);
								
								String publicationDate = "";
								if(!thisDoc.isNull("webPublicationDate")) publicationDate =thisDoc.getString("webPublicationDate");
								
								String sectionName = "";
								if(!thisDoc.isNull("sectionName")) sectionName = thisDoc.getString("sectionName").replaceAll("[^a-zA-Z]"," ").replaceAll("\\s+"," ").trim();
								
								String webTitle = "";
								if(!thisDoc.isNull("webTitle")) webTitle = thisDoc.getString("webTitle").replaceAll("[^a-zA-Z]"," ").replaceAll("\\s+"," ").trim();
								
								String bodyText ="";
								if(!thisDoc.isNull("fields"))
								{
									bodyText = thisDoc.getJSONObject("fields").getString("bodyText").replaceAll("[^a-zA-Z]"," ").replaceAll("\\s+"," ").trim();
								}
								if(bodyText.length()<2) continue;
								
								String category = "";
								if(!thisDoc.isNull("tags"))
								{
									JSONArray keywordArray = thisDoc.getJSONArray("tags");
									for(int keywordIndex=0;keywordIndex<keywordArray.length();keywordIndex++)
									{
										category += keywordArray.getJSONObject(keywordIndex).getString("id")+" ";
									}
								}
								category = category.trim();

								
								cityCooccurrenceNews.println(cityA+"-"+cityB+","+publicationDate+","+sectionName+","+category+","+webTitle+","+bodyText);
								StdOut.println("Saving "+ cityA+"-"+cityB+","+publicationDate+","+sectionName+","+category+","+webTitle+","+bodyText);

							}
						}
				    }
			
			cityCooccurrenceNews.close();
			newsInteraction.close();

			StdOut.println("Finished retrieving city co-occurrence news articles.");
			// Retrieving city co-occurrence news finished ------------------------------------     
			
			
			
			
			// Retrieving individual city news ------------------------------------     
			Out cityIndividualNews = new Out("City_Individual_NewsArticles.csv");
			cityIndividualNews.println("city,pubDate,sectionName,category,title,fulltext");
						
			Out cityIndividualCount = new Out("City_Individual_Count.csv");
			cityIndividualCount.println("city,newsCount");
						
			for (int i = 0; i < cityCount; i++)
		    {
			    String city = citiesVector.get(i);
			    String queryString = "&q=%22"+city.replaceAll(" ", "%20")+"%22";
			    
			    keyIndex++;
			    int thisKey = keyIndex % keyCount;
			    
			    Thread.sleep(2000); // sleep 2 seconds to avoid over frequent requests
			    String result = null;
			    try
			    {
				    result = URLRequest.sendGet(targetURL+queryString+"&api-key="+apiKeyArray.getString(thisKey));
			    } 
			    catch (Exception ee)
			    {
				    Thread.sleep(5000);
				    keyIndex++;
				    thisKey = keyIndex % keyCount;
				    try
				    {
				    	result = URLRequest.sendGet(targetURL+queryString+"&api-key="+apiKeyArray.getString(thisKey));
				    }
				    catch(Exception eee)
				    {
				    	Thread.sleep(5000);
					    keyIndex++;
					    thisKey = keyIndex % keyCount;
					    try
					    {
					    	result = URLRequest.sendGet(targetURL+queryString+"&api-key="+apiKeyArray.getString(thisKey));
					    }
					    catch(Exception eeee)
					    {
					    	StdOut.println(city+" failed, skipped ..."); 	
					    	continue;
					    }
				    }  
			    }
			    
			    
			    // convert the retrieved result 
			    JSONObject jsonObject = new JSONObject(result);
			    jsonObject = jsonObject.getJSONObject("response");
			    int totalPage = jsonObject.getInt("pages");
			 
			    int totalNumber = jsonObject.getInt("total"); 
			    
			    cityIndividualCount.println(city+","+totalNumber);
			    StdOut.println("There are "+totalNumber+" news about "+ city);
			    
			    for(int k=1;k<=totalPage;k++)
			    {
				    StdOut.println("Retrieving page "+k+"...");
					Thread.sleep(2000);
					
					// for page 1 directly use existing retrieved data
					if(k!=1)
					{
						keyIndex++;
						thisKey = keyIndex % keyCount;
						try
						{
							result = URLRequest.sendGet(targetURL+queryString+"&page="+k+"&api-key="+apiKeyArray.getString(thisKey));
						} 
						catch (Exception ee)
						{
							Thread.sleep(5000);
							keyIndex++;
							thisKey = keyIndex % keyCount;
							try 
							{
								result = URLRequest.sendGet(targetURL+queryString+"&page="+k+"&api-key="+apiKeyArray.getString(thisKey));
							} 
							catch (Exception eee) 
							{
								Thread.sleep(5000);
								keyIndex++;
								thisKey = keyIndex % keyCount;
								try 
								{
									result = URLRequest.sendGet(targetURL+queryString+"&page="+k+"&api-key="+apiKeyArray.getString(thisKey));
								} 
								catch (Exception eeee) 
								{
									StdOut.println(city+", page "+k+" failed, skipped ...");
									continue;
								}
							}
						}
					}
					// first page situation handled
					
					jsonObject = new JSONObject(result);
					jsonObject = jsonObject.getJSONObject("response");
					JSONArray docs = jsonObject.getJSONArray("results");
					for(int docIndex = 0;docIndex<docs.length();docIndex++)
					{
						JSONObject thisDoc = docs.getJSONObject(docIndex);
						
						String publicationDate = "";
						if(!thisDoc.isNull("webPublicationDate")) publicationDate =thisDoc.getString("webPublicationDate");
						
						String sectionName = "";
						if(!thisDoc.isNull("sectionName")) sectionName = thisDoc.getString("sectionName").replaceAll("[^a-zA-Z]"," ").replaceAll("\\s+"," ").trim();
						
						String webTitle = "";
						if(!thisDoc.isNull("webTitle")) webTitle = thisDoc.getString("webTitle").replaceAll("[^a-zA-Z]"," ").replaceAll("\\s+"," ").trim();
						
						String bodyText ="";
						if(!thisDoc.isNull("fields"))
						{
							bodyText = thisDoc.getJSONObject("fields").getString("bodyText").replaceAll("[^a-zA-Z]"," ").replaceAll("\\s+"," ").trim();
						}
						if(bodyText.length()<2) continue;
						
						String category = "";
						if(!thisDoc.isNull("tags"))
						{
							JSONArray keywordArray = thisDoc.getJSONArray("tags");
							for(int keywordIndex=0;keywordIndex<keywordArray.length();keywordIndex++)
							{
								category += keywordArray.getJSONObject(keywordIndex).getString("id")+" ";
							}
						}
						category = category.trim();

						
						cityIndividualNews.println(city+","+publicationDate+","+sectionName+","+category+","+webTitle+","+bodyText);
						StdOut.println("Saving "+ city+","+publicationDate+","+sectionName+","+category+","+webTitle+","+bodyText);

					}
				}
		    }
	
			cityIndividualNews.close();
			cityIndividualCount.close();

			StdOut.println("Finished retrieving individual city news articles.");
			// Retrieving individual city news finished ------------------------------------     			
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
