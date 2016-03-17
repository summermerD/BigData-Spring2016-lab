import java.net.InetAddress
import java.nio.file.{Files, Paths}
import java.util.Calendar

import NLPUtils._
import Utils._
import org.apache.spark.SparkConf
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by Ting on 3/16/16.
  */
object GetTestDataFromDevice {

  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir", "F:\\winutils")
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkNaiveBayes").set("spark.driver.memory", "3g").set("spark.executor.memory", "3g")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    val sc = ssc.sparkContext
    val stopWords = sc.broadcast(loadStopWords("/stopwords.txt")).value
    val labelToNumeric = createLabelMap("data/categoryTraining/")
    var model: NaiveBayesModel = null
    val PORT_NUMBER = 9999
    // Training the data

      val training = sc.wholeTextFiles("data/categoryTraining/*")
        .map(rawText => createLabeledDocument(rawText, labelToNumeric, stopWords))
      val X_train = tfidfTransformer(training)
      model = NaiveBayes.train(X_train, lambda = 1.0)


    // Get IP Address of the Machine
    println("IP ADDRESS : :   " + SocketClient.findIpAdd())

    // Socket open for Testing Data
    lazy val address: Array[Byte] = Array(192.toByte, 168.toByte, 1.toByte, 2.toByte)
    val ia = InetAddress.getByAddress(address)

    val lines = ssc.socketTextStream(ia.getHostName, PORT_NUMBER, StorageLevel.MEMORY_ONLY)

    var rate = 0
    val data = lines.map(line => {
      val test = createLabeledDocumentTest(line, labelToNumeric, stopWords)
      println(test.body)
//      val doAnalysis: SentimentAnalyzer = new SentimentAnalyzer
//      rate = doAnalysis.findSentiment(test.body).getRate
      test.body
    })

    data.foreachRDD(rdd => {
      val filteredRDD = rdd.filter(line => line.contains("data"))
      val X_test = tfidfTransformerTest2(sc, filteredRDD)
      val predictionAndLabel = model.predict(X_test)
      println("PREDICTION")

      predictionAndLabel.foreach(x => {
        labelToNumeric.foreach { y => if (y._2 == x) {
          println(y._1)
          val toFile = "8888::" + y._2 + "::" + rate + "::" + Calendar.getInstance.getTime
          println(toFile)
        }
        }
      })
    }
    )

    ssc.start()
    ssc.awaitTermination()

    // val accuracy = 1.0 *  predictionAndLabel.filter(x => x._1 == x._2).count() / X_test.count()

    /*  println("*************Accuracy Report:***********************")
   */
    //   println(accuracy)
    //evaluateModel(predictionAndLabel,"Naive Bayes Results")


  }


}
