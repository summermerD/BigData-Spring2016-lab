import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Mayanka on 10-Mar-16.
  */
object RecommendationSystem {

  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir", "F:\\winutils")
    val conf = new SparkConf().setMaster("local[*]").setAppName("SimpleRecommendation")
      .set("spark.executor.memory", "2g")

    val sc = new SparkContext(conf)

    var userMapping: Map[Char, Int] = Map()

    val users = List('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')
    for (i <- 1 to 26) {
      userMapping += (users(i - 1) -> i)
    }
    val USERID = sc.broadcast(userMapping)
    val tags = List("rice", "tempura", "toast", "bibimap", "sushi", "spaghetti", "sausage", "oden", "omelet", "jiaozi")
    var tagId: Map[Int, String] = Map()
    var count: Int = 1

    tags.foreach(f => {
      tagId += (count -> f)
      count = count + 1
    })

    val CATEGORYID = sc.broadcast()
    // load personal ratings
    val recoData = sc.textFile("instadata2/recommendation.txt")
    val ratings = recoData.map(f => {
      //username;caption;tag;tagId;link
      val d = f.split(";")


      val username = d(0).replaceAll("[^a-zA-Z]", "").toLowerCase
      println(username)

      val caption = d(1).replaceAll("[^a-zA-Z]", " ").toLowerCase
      println(caption)

      val sentimentAnalyzer: SentimentAnalyzer = new SentimentAnalyzer
      val tweetWithSentiment: TweetWithSentiment = sentimentAnalyzer.findSentiment(caption)
      //User id, Movie Id, Rating
      println(USERID.value(username(0)))
      println(d(3).toInt)
      println(tweetWithSentiment.getRating)
      Rating(USERID.value(username(0)), d(3).toInt, tweetWithSentiment.getRating.toDouble)
    })

    // Build the recommendation model using ALS
    val rank = 12
    val numIterations = 20
    val model = ALS.train(ratings, rank, numIterations, 0.1)

    val myRatedMovieIds = ratings.filter(f => f.user == 1).map(_.product)

    val recommendations = model.predict(myRatedMovieIds.map((1, _))).collect()

    var i = 1
    println("Movies recommended for you:")
    recommendations.foreach { r =>
      println(r)
      println("%2d".format(i) + ": " + tagId(r.product))
      i += 1
    }

    // clean up
    sc.stop()

  }

}
