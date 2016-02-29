import org.apache.spark.SparkConf
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by Ting on 2/27/16.
  */
object GetTrainingData {

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
    //Create a Streaming Context with 10 second window
    val ssc = new StreamingContext(sparkConf, Seconds(20))
    //Using the streaming context, open a twitter stream (By the way you can also use filters)
    //Stream generates a series of random tweets

    val stream = TwitterUtils.createStream(ssc, None, filters)
//    stream.print()

    //Receive training data. Select three categories: food, sports, and other

    val trainingFoodStream = stream.filter(_.getHashtagEntities.mkString.contains("food")).map(_.getText)
    val trainingSportStream = stream.filter(_.getHashtagEntities.mkString.contains("sport")).map(_.getText)
    val trainingOthersStream = stream.filter(!_.getHashtagEntities.mkString.contains("food")).filter(!_.getHashtagEntities.mkString.contains("sport")).map(_.getText)

    val trainingFood = trainingFoodStream.foreachRDD(rdd =>
      { val count = rdd.count()
        if (count > 0){
          rdd.repartition(1).saveAsTextFile("data/training/hashtag.food")
        }
      })
    val trainingSport = trainingSportStream.foreachRDD(rdd =>
      { val count = rdd.count()
        if (count > 0) {
          rdd.repartition(1).saveAsTextFile("data/training/hashtag.sport")
        }
      })
    val trainingOthers = trainingOthersStream.foreachRDD((rdd,time) =>
      { val count = rdd.count()
        if (count > 0) {
          rdd.repartition(1).saveAsTextFile("data/training/hashtag.other")
          val temp = count
        }
      })

    ssc.start()
    ssc.awaitTermination();
  }

}
