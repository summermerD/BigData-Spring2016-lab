/**
 * Created by pradyumnad on 10/07/15.
 */

import edu.umkc.ic.ImageUtils
import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacpp.opencv_features2d._
import org.bytedeco.javacpp.opencv_highgui._
import org.bytedeco.javacpp.opencv_nonfree.{SIFT, SURF}

object OpenCVApp {

  var vocabulary = new Mat()

  def train(): Unit = {
    println("== TRAIN ==")
    val files = List(
      "files/Train/airplanes/image_0001.jpg",
      "files/Train/airplanes/image_0002.jpg",
      "files/Train/ant/image_0001.jpg",
      "files/Train/ant/image_0002.jpg"
    )

    val training_descriptors = new Mat

    for (file <- files) {
      val desc = ImageUtils.descriptors(file)
      training_descriptors.push_back(desc)
    }

    println("Unclustered Features "+training_descriptors.size().asCvSize().toString)
    //Construct BOWKMeansTrainer
    //the number of bags
    val dictionarySize = 100
    //define Term Criteria
    val bowTrainer = new BOWKMeansTrainer(dictionarySize)
    bowTrainer.add(training_descriptors)
    vocabulary = bowTrainer.cluster()

    println("Vocab size : "+vocabulary.size().asCvSize().toString)

  }

  def test(): Unit = {
    println("== TEST ==")
    val test_image = "files/101_ObjectCategories/ant/image_0004.jpg"

    val dictionary = vocabulary

    val matcher = new FlannBasedMatcher()
    val detector = new SIFT()
    val extractor = DescriptorExtractor.create("SIFT")
    val bowDE = new BOWImgDescriptorExtractor(extractor, matcher)
    bowDE.setVocabulary(dictionary)
    println(bowDE.descriptorSize()+" "+bowDE.descriptorType())

    val img = imread(test_image, CV_LOAD_IMAGE_GRAYSCALE)
    if (img.empty()) {
      println("Image is empty")
      -1
    }

    val keypoints = new KeyPoint

    detector.detect(img, keypoints)

    val response_histogram = new Mat
    bowDE.compute(img, keypoints, response_histogram)

    println("Histogram size : "+response_histogram.size().asCvSize().toString)
    println("Histogram : "+response_histogram.asCvMat().toString)
  }

  def main(args: Array[String]) {

    train()

    test()

  }

  def matcher(): Unit = {
    val img_1 = imread("/Users/pradyumnad/KDM/SparkIP/files/101_ObjectCategories/airplanes/image_0001.jpg", CV_LOAD_IMAGE_GRAYSCALE)
    val img_2 = imread("/Users/pradyumnad/Desktop/2008_003703.jpg", CV_LOAD_IMAGE_GRAYSCALE)

    if (img_1.empty() || img_2.empty()) {
      println("Image is empty")
      -1
    }

    //-- Step 1: Detect the keypoints using ORB
    val brisk = new BRISK
    println(brisk)
    val detector = new ORB
    val keypoints_1 = new KeyPoint
    val keypoints_2 = new KeyPoint

    val mask = new Mat

    val descriptors_1 = new Mat
    val descriptors_2 = new Mat
    detector.detectAndCompute(img_1, mask, keypoints_1, descriptors_1)
    detector.detectAndCompute(img_2, mask, keypoints_2, descriptors_2)

    //-- Step 3: Matching descriptor vectors with a brute force matcher
    val matches = new DMatchVectorVector
    val bf = new BFMatcher
    bf.knnMatch(descriptors_2, descriptors_1, matches, 1)

    //-- Draw matches
    val img_matches = new Mat
    drawMatches(img_1, keypoints_1, img_2, keypoints_2, matches, img_matches)
    //-- Show detected matches
    imshow("Matches", img_matches)
    waitKey(0)
  }
}
