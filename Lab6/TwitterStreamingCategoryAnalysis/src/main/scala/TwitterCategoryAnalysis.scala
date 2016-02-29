import org.apache.spark.SparkConf
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import NLPUtils._
import Utils._

/**
  * Created by Ting on 2/26/16.
  */

object TwitterCategoryAnalysis {

  def main(args: Array[String]) {

    val filters = args

    // Set the system properties so that Twitter4j library used by twitter stream
    // can use them to generate OAuth credentials

    System.setProperty("twitter4j.oauth.consumerKey", "R2v2WMKrF7UGipifRcMkOyjT1")
    System.setProperty("twitter4j.oauth.consumerSecret", "InkVklJfUsJPQyA17GzGks9uzFSwUnRY9HqsR9m4vZ5Et3sW2d")
    System.setProperty("twitter4j.oauth.accessToken", "3630687739-9y2qw6YKOMgeApmq09DKOuYosm2piadUy8aa96n")
    System.setProperty("twitter4j.oauth.accessTokenSecret", "IBjoDz21BTBaXwnJ13jy2A0hOFaYzCYHmNRxCrhLLJong")

    //Create a spark configuration with a custom name and master
    // For more master configuration see  https://spark.apache.org/docs/1.2.0/submitting-applications.html#master-urls
    val sparkConf = new SparkConf().setAppName("STweetsApp").setMaster("local[*]").setAppName("TwitterStreamingCategoryAnalysis").set("spark.driver.memory", "3g").set("spark.executor.memory", "3g")
    //Create a Streaming Context with 2 second window
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    //Using the streaming context, open a twitter stream (By the way you can also use filters)
    //Stream generates a series of random tweets

    val stream = TwitterUtils.createStream(ssc, None, filters)
//    stream.print()

    //Testing data
    val testing = stream.filter(!_.getHashtagEntities.isEmpty).map(_.getText)

    val testingData = testing.foreachRDD(rdd =>
    { val count = rdd.count()
      if (count > 0){
        rdd.saveAsTextFile("data/testing/")
      }
    })

    ssc.start()
    ssc.awaitTerminationOrTimeout(10)

    //Train tweets datasets
    val sc = ssc.sparkContext
    val stopWords = sc.broadcast(loadStopWords("/stopwords.txt")).value
    val labelToNumeric = createLabelMap("data/training/")
    println(labelToNumeric)
    var model: NaiveBayesModel = null

    val training = sc.wholeTextFiles("data/training/*")
      .map(rawText => createLabeledDocument(rawText, labelToNumeric, stopWords))
    val X_train = tfidfTransformer(training)
    X_train.foreach(vv => println(vv))

    model = NaiveBayes.train(X_train, lambda = 1.0)


    val lines=sc.wholeTextFiles("data/testing/*")
    val data = lines.map(line => {

      val test = createLabeledDocumentTest(line._2, labelToNumeric, stopWords)
      println(test.body)
      test

    })


    val X_test = tfidfTransformerTest(sc, data)

    val predictionAndLabel = model.predict(X_test)
    println("PREDICTION")
    predictionAndLabel.foreach(x => {
      labelToNumeric.foreach { y => if (y._2 == x) {
        println(y._1)
      }
      }
    })





  }

}
