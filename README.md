# CityRelatednessViaNews

* Author: Yingjie Hu
* Email: yjhu.geo@gmail.com


### Overall description
This project provides software tools for examining city relatedness based on their co-occurrences in news articles. News articles are rich sources of information, and cities are frequently mentioned in news articles. By examining the content of news articles, their publishing dates, and the mentioned cities, we can explore the diverse city relations from spatial, temporal, and semantic perspectives. A study came out from this project is published in the following article:

Hu, Y., Ye, X., & Shaw, S. L. (2017). Extracting and analyzing semantic relatedness between cities using news articles. International Journal of Geographical Information Science, 31(12), 2427-2451.

With city relatedness extracted from news articles under different semantic topics, we can construct and visualize city networks. The figure below shows the city relatedness under the topics of "Politics" and "Science and Technology" respectively. <br />

<img align="center" src="https://github.com/YingjieHu/CityRelatednessViaNews/blob/master/Figures/CityRelatedness.png" width="600" />


### Repository organization
The "Source" folder contains the source code of the developed java programs. The "Release" folder contains the compiled software tools that can be directly used in your project. 


### News Retriever
In the folder "NewsRetriever", a Java program is developed for retrieving news articles from the Guardian using Guardian API. You can run this program by starting a terminal window (a command line window), and type: "java -jar NewsRetriever.jar". In the "config.json" file, you can specify the path of the cities you want to study, the starting year (e.g., 2010), the ending year (e.g., 2011), and your API key(s). Note that you must first obtain an API key from the Guardian (http://open-platform.theguardian.com/documentation/) before you can retrieve the data from the Guardian. In the "cities.csv" file, you can specify the cities whose relatedness you would like to study. The NewsRetriever functions by retrieving news articles from the Guardian based on the cities and the years you have specified.










