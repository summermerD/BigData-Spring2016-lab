import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.Vectors


/**
  * Created by Ting on 2/24/16.
  */
object HeartRateKMeans {
    def main(args: Array[String]) {

      val sparkConf = new SparkConf().setAppName("SparKMLlib").setMaster("local[*]")

      val sc = new SparkContext(sparkConf)

      // Load and parse the data
      val data = sc.textFile("data/heartRate.txt")
      val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble))).cache()

      // Cluster the data into three classes using KMeans
      val numClusters = 5
      val numIterations = 50
      val clusters = KMeans.train(parsedData, numClusters, numIterations)

      // Evaluate clustering by computing Within Set Sum of Squared Errors
      val WSSSE = clusters.computeCost(parsedData)
      println("Within Set Sum of Squared Errors = " + WSSSE)

      // Save and load model
      clusters.save(sc, "myModelPath")
      val sameModel = KMeansModel.load(sc, "myModelPath")

      // Shows the result
      println("Final Centers: ")
      sameModel.clusterCenters.foreach(println)
      // $example off$

      sc.stop()

      var s: String = "Final Centers\n"
      sameModel.clusterCenters.foreach { case (center) => {

        s += center + "\n"}
      }

      SocketClient.sendCommandToRobot(s)
    }

}
