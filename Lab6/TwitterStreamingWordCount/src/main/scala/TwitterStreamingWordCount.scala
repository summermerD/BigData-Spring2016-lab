import org.apache.spark.SparkConf
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}


object TwitterStreamingWordCount {

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
    val sparkConf = new SparkConf().setAppName("STweetsApp").setMaster("local[*]")
    //Create a Streaming Context with 2 second window
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    //Using the streaming context, open a twitter stream (By the way you can also use filters)
    //Stream generates a series of random tweets
    val stream = TwitterUtils.createStream(ssc, None, filters)
    stream.print()
    //Map : Retrieving text
    val getText = stream.flatMap(status => status.getText.split(" "))

    //Finding the top word used on 6 second window
    val topCounts3 = getText.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(6))
      .map{case (word, count) => (count, word)}
      .transform(_.sortByKey(false))


    // Print popular words
    topCounts3.foreachRDD(rdd => {
      val topList = rdd.take(20)
      println("\nPopular words used in last 6 seconds (%s total):".format(rdd.count()))
      topList.foreach{case (count, word) => println("%s (%s times)".format(word, count))}


      var s:String="Popular words used in last 6 seconds: \nWords:Count \n"
      topList.foreach{case(count,word)=>{

        s+=word+" : "+count+"\n"

      }}
      SocketClient.sendCommandToRobot(s)
    })

    //
     ssc.start()

    ssc.awaitTermination()
  }
}
