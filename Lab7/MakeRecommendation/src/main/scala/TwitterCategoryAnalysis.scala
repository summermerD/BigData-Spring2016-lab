import java.io.File

import org.apache.commons.io.FileUtils
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import NLPUtils._
import Utils._

import scala.collection.mutable.ArrayBuffer

/**
  * Created by Ting on 2/26/16.
  */

class TwitterCategoryAnalysis {

  def CategoryAnalysis() : Int = {

    val sparkConf = new SparkConf().setAppName("TwitterCategoryRecommendation").setMaster("local[*]").set("spark.driver.memory", "3g").set("spark.executor.memory", "3g")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    val sc = ssc.sparkContext

    //Train tweets datasets
    val stopWords = sc.broadcast(loadStopWords("/stopwords.txt")).value
    val labelToNumeric = createLabelMap("data/categoryTraining/")
    println(labelToNumeric)

    var model: NaiveBayesModel = null

    val training = sc.wholeTextFiles("data/categoryTraining/*")
      .map(rawText => createLabeledDocument(rawText, labelToNumeric, stopWords))
    val X_train = tfidfTransformer(training)
    X_train.foreach(vv => println(vv))

    model = NaiveBayes.train(X_train, lambda = 1)


    val lines=sc.wholeTextFiles("data/testing/*")

    val data = lines.map(line => {

      val test = createLabeledDocumentTest(line._2, labelToNumeric, stopWords)
      println(test.body)
      test

    })

    val X_test = tfidfTransformerTest(sc, data)

    val predictionAndLabel = model.predict(X_test)



    println("PREDICTION")

    var returnValue = 0.0
    returnValue = predictionAndLabel.first()

    sc.stop()
    returnValue.toInt
  }
}
