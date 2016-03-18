import java.io.{IOException, FileWriter}
import java.net.InetAddress
import java.nio.file.{Files, Paths}
import java.util.Calendar

import NLPUtils._
import Utils._
import org.apache.spark.SparkConf
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable.ArrayBuffer

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
    lazy val address: Array[Byte] = Array(192.toByte, 168.toByte, 1.toByte, 5.toByte)
    val ia = InetAddress.getByAddress(address)

    val lines = ssc.socketTextStream(ia.getHostName, PORT_NUMBER, StorageLevel.MEMORY_ONLY)

    val data = lines.map(line => {
      val test = createLabeledDocumentTest(line, labelToNumeric, stopWords)
      println(test.body)

      test.body
    })


    var rate = 0
    data.foreachRDD(rdd => {
      println(rdd.take(1))
      if(rdd.take(1).length != 0) {
        val X_test = tfidfTransformerTest2(sc, rdd)
        val predictionAndLabel = model.predict(X_test)
        println("PREDICTION")
//        val doAnalysis: SentimentAnalyzer = new SentimentAnalyzer
//        val rate = doAnalysis.findSentiment(rdd.toString())
        val toFile = "8888::" + predictionAndLabel.first().toInt + "::" + rate + "::" + System.currentTimeMillis / 1000 + "\n"
        println(toFile)
        try {
          val filename: String = "data/results/rating.txt"
          val fw2: FileWriter = new FileWriter(filename, true)
          fw2.write(toFile)
          fw2.close
        }
        catch {
          case ioe: IOException => {
            System.err.println("IOException: " + ioe.getMessage)
          }
        }
      }

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
