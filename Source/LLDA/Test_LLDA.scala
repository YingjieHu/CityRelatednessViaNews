
// tells Scala where to find the TMT classes
import scalanlp.io._;
import scalanlp.stage._;
import scalanlp.stage.text._;
import scalanlp.text.tokenize._;
import scalanlp.pipes.Pipes.global._;

import edu.stanford.nlp.tmt.stage._;
import edu.stanford.nlp.tmt.model.lda._;
import edu.stanford.nlp.tmt.model.llda._;

// the path of the model to load
val modelPath = file("llda-cvb0-94bad065-17-25ed240e-32138829");

println("Loading "+modelPath);
val lldaModel = LoadCVB0LabeledLDA(modelPath);
val model = lldaModel.asCVB0LDA;
//TrainCVB0LabeledLDA
// Or, for a Gibbs model, use:
// val model = LoadGibbsLDA(modelPath);

// A new dataset for inference.  (Here we use the same dataset
// that we trained against, but this file could be something new.)
val source = CSVFile("Guardian_Testing_News.csv")~> IDColumn(1);

val text = {
  source ~>                              // read from the source file
  Column(4) ~>                           // select column containing text
  TokenizeWith(model.tokenizer.get)      // tokenize with existing model's tokenizer
}

// Base name of output files to generate
val output = file(modelPath, source.meta[java.io.File].getName.replaceAll(".csv",""));

// turn the text into a dataset ready to be used with LDA
val dataset = LDADataset(text, termIndex = model.termIndex);

println("Writing document distributions to "+output+"-document-topic-distributions.csv");
val perDocTopicDistributions = InferCVB0DocumentTopicDistributions(model, dataset);
CSVFile(output+"-document-topic-distributuions.csv").write(perDocTopicDistributions);

println("Writing topic usage to "+output+"-usage.csv");
val usage = QueryTopicUsage(model, dataset, perDocTopicDistributions);
CSVFile(output+"-usage.csv").write(usage);

println("Estimating per-doc per-word topic distributions");
val perDocWordTopicDistributions = EstimatePerWordTopicDistributions(
  model, dataset, perDocTopicDistributions);

println("Writing top terms to "+output+"-top-terms.csv");
val topTerms = QueryTopTerms(model, dataset, perDocWordTopicDistributions, numTopTerms=50);
CSVFile(output+"-top-terms.csv").write(topTerms);

