# CityRelatednessViaNews

* Author: Yingjie Hu
* Email: yjhu.geo@gmail.com


### Overall description
This project provides software tools for examining city relatedness based on their co-occurrences in news articles. News articles are rich sources of information, and cities are frequently mentioned in news articles. By examining the content of news articles, their publishing dates, and the mentioned cities, we can explore the diverse city relations from spatial, temporal, and semantic perspectives. A study came out from this project is published in the following article:

Hu, Y., Ye, X., & Shaw, S. L. (2017). Extracting and analyzing semantic relatedness between cities using news articles. International Journal of Geographical Information Science, 31(12), 2427-2451. DOI: https://doi.org/10.1080/13658816.2017.1367797 

The extracted city relatedness allow us to construct and visualize city networks under different semantic topics. The figure below shows the city networks constructed under the topics of "Politics" and "Science and Technology" respectively. 

<p align="center">
<img align="center" src="https://github.com/YingjieHu/CityRelatednessViaNews/blob/master/Figures/CityRelatedness.png" width="600" />
</p>


### Repository organization
The "Source" folder contains the source code of the developed java programs. The "Release" folder contains the compiled software tools that can be directly used. 


### News Retriever
In the folder "NewsRetriever", a Java program is developed for retrieving news articles from the Guardian using the Guardian API. You can run this program by starting a terminal (a command line window), and type: 

"java -jar NewsRetriever.jar". 

In the "config.json" file, you can specify the path of the city file (which contains the cities you want to study), the starting year (e.g., 2010), the ending year (e.g., 2011), and your API key(s). Note that you must first obtain an API key from the Guardian (http://open-platform.theguardian.com/documentation/) before you can retrieve its news data. The NewsRetriever functions by retrieving news articles from the Guardian based on the cities and the years you have specified. Four files will be generated after the NewsRetriever finishes running:
- City_Cooccurrence_NewsArticles.csv: this file stores the news articles retreived based on city co-occurrences
- City_Cooccurrence_Count.csv: this file stores the count of news articles retrieved based on city co-occurrences
- City_Individual_NewsArticles.csv: this file stores the news articles that contain individual cities
- City_Individual_Count.csv: this file stores the count of the news articles that contain individual cities


### Labeled Latent Dirichlet Allocation (LLDA)
In the folder "LLDA", a program is provided to run the LLDA model which is trained using a training dataset (a set of labeled news articles) and which can then infer topics from any unseen news articles. The LLDA software toolbox "tmt-0.4.0.jar" was originally developed by the Stanford NLP group (https://nlp.stanford.edu/software/tmt/tmt-0.4/), and you may want to refer to their original papers if you use this tool. I include their software tool here to make this project standalone. One critical thing is that this toolbox will run into an error if Java 8 or above versions are used. Thus, jdk 1.7 is provided here so that you can install Java 7 if necessary.

The "Guardian_Training_News.csv" file provides a sample of training data, and the "Train_LLDA.scala" file provides the training script. To train a LLDA model, run the following code in a terminal: 

java -jar tmt-0.4.0.jar Train_LLDA.scala

The training process will produce a trained model in a folder named similar to "llda-cvb0-94bad065-17-25ed240e-32138829". Some ancillary files, such as term counts, will also be generated. With the trained LLDA model, we can now use it to infer topics from unseen news articles. The "Guardian_Testing_News.csv" file provides a sample of testing data, and the "Test_LLDA.scala" file provides the testing script. Before running the testing, you need to slightly edit the "Test_LLDA.scala" file. Open this file, and find the line: val modelPath = file("llda-cvb0-94bad065-17-25ed240e-32138829"); You need to replace the name of the model "llda-cvb0-94bad065..." with the name of your trained model from the last step (each time the training process will generate a random series number). Once you have added the name of your own model, close and save "Test_LLDA.scala". Now, run the following code in terminal:

java -jar tmt-0.4.0.jar Test_LLDA.scala

When the program has finished running, you can open the folder of your model (named like "llda-cvb0-94bad065..."), and the inferred results (topics) are in the following files:
- "Guardian_Testing_News-document-topic-distributuions.csv". This file contains the topic distributions for each document (i.e., each news article). Please note that the topics are in the form of indices without the names of the topics. To know the textual label of each topic, you can open one of the snapshot folders (The LLDA model generates a snapshot after it runs every 50 iterations), such as "01000", and the topic labels are in the file "label-index.txt".

- "Guardian_Testing_News-top-terms.csv". This file contains the top terms of each topic, and can be used to generate word clouds. An example figure is shown as below: 
<p align="center">
<img align="center" src="https://github.com/YingjieHu/CityRelatednessViaNews/blob/master/Figures/WordCloud.png" width="700" />
</p>

- "Guardian_Testing_News-usage.csv". This file contains the distribution of each topic among the documents and words.


### Pathfinder Network 
The folder "PFNet" contains the software tools for performing network pruning. The file "Guardian_combined_result.csv" contains the city co-occurrence data extracted from a set of Guardian news articles, with rows for city pairs and columns for different semantic topics. The file "label-index.txt" contains the labels of the semantic topics. The program "NetworkDataExtractor.jar" can extract the data suitable to be input into the Pathfinder algorithm. In a terminal, type:

java -jar NetworkDataExtractor.jar

As an example, this code will extract data under the semantic topic of "Politics", and you should see two files "PathFinder_Politics.prx.txt" (the proximity file) and "terms.txt" (the term file). These two files can be used as input for the Pathfinder algorithm. If you want to extract data under other semantic topics or customize the program, the source code is provided in the "Source" folder. Type the following command in a terminal to start the Pathfinder software:

java -jar JPathfinder.jar

Click "New Directory" button in the GUI to set up the working directory, and then click "Add Proximity Data" button to add the "PathFinder_Politics.prx.txt" file (the term file will be automatically added). Click "Derive Network" to obtain the network based on the city relatedness. You can click the "Display Network" button to quickly visualize the network. You can then click the "Network Link List" button to save the derived network into a csv file that can be put into a network visualization tool Gephi. More details about the usage of JPathfinder can be found at http://interlinkinc.net/, and a user manual is also included in the folder. Information about Gephi can be found at https://gephi.org/users/download/.      









