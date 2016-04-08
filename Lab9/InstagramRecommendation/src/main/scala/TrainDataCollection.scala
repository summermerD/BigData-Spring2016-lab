import java.awt.image.BufferedImage
import java.awt.{Graphics2D, Image}
import java.io.{BufferedReader, File, InputStreamReader, PrintStream}
import java.net.URL
import javax.imageio.ImageIO

import net.liftweb.json
import net.liftweb.json._
import org.apache.commons.validator.UrlValidator
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

/**
  * Created by Ting on 3/24/16.
  */

object TrainDataCollection {
  val PATH = "instadata/"
  val PATHR = "instadata/recommendation"
  val fileR = new PrintStream(PATHR + ".txt")

  @throws(classOf[Exception])
  def main(args: Array[String]) {

    val tags = List("dog", "cat", "mouse", "rabbit", "squirrel", "bird", "fox", "tortoise", "fish")
    var tagId: Map[String, Int] = Map()
    var count: Int = 1
    fileR.println("username;caption;tag;tagId;link")

    tags.foreach(f => {
      tagId += (f -> count)
      count = count + 1
    })
    tags.foreach(f => {
      sendGet(f, tagId(f))
    })
  }

  @throws(classOf[Exception])
  private def sendGet(tag: String, tagId: Int) {
    val url: String = "https://api.instagram.com/v1/tags/" + tag + "/media/recent?access_token=2242837681.1677ed0.40e41f58cf2e456fa6859e67d701f9af&count=100"
    val client: HttpClient = new DefaultHttpClient
    val request: HttpGet = new HttpGet(url)
    //request.addHeader("User-Agent", USER_AGENT)
    val response: HttpResponse = client.execute(request)
    println("\nSending 'GET' request to URL : " + url)
    println("Response Code : " + response.getStatusLine.getStatusCode)
    val rd: BufferedReader = new BufferedReader(new InputStreamReader(response.getEntity.getContent))
    val result: StringBuffer = new StringBuffer
    var line: String = ""
    while ((({
      line = rd.readLine;
      line
    })) != null) {
      result.append(line)
    }
    //println(result.toString)
    val jsonValue = json.parse(result.toString)
    val jsonFields = jsonValue.children
    val f2 = jsonFields(2)
    val f2list = f2.children
    /* println("JSON VALUE")
     val fstring = compactRender(f2)
     println(fstring)
     println("\n \n")*/

    val file = new File(PATH + tag)
    file.mkdirs()

    var imgcount = 1

    /*f2list.foreach(ff => {*/
    for (i <- 0 to f2list.length - 1) {
      val ff = f2list(i)
      val link = ff \\ "images" \\ "standard_resolution" \\ "url"
      val username = ff \\ "user" \\ "username"

      val caption = ff \\ "caption" \\ "text"
      //  println(username, link)

      var image: Image = null
      val imgLink = compactRender(link).toString
      var imgLinknew = imgLink.replace("\"", "")


      var newimg = imgLinknew

      println(newimg)

      val urlValidator = new UrlValidator()

      //valid URL
      //newimg="https://scontent.cdninstagram.com/t51.2885-15/s640x640/sh0.08/e35/10467878_1004723532955765_1059999733_n.jpg"
      if (urlValidator.isValid(newimg)) {
        println("url is valid")

        val url = new URL(newimg)
        image = ImageIO.read(url)


        val ext = "jpg"
        val fileI = new File(PATH + tag + "/" + imgcount + "." + ext)
        ImageIO.write(toBufferedImage(image), ext, fileI) // ignore returned boolean

        fileR.println(compactRender(username) + ";" + compactRender(caption) + ";" + tag + ";" + tagId + ";" + compactRender(link))
        println(compactRender(username) + ";" + compactRender(caption) + ";" + tag + ";" + tagId + ";" + compactRender(link))
        imgcount = imgcount + 1

      } else {
        println("url is invalid");
      }
    }

  }

  private def toBufferedImage(src: Image): BufferedImage = {
    val w: Int = src.getWidth(null)
    val h: Int = src.getHeight(null)
    val `type`: Int = BufferedImage.TYPE_INT_RGB
    val dest: BufferedImage = new BufferedImage(w, h, `type`)
    val g2: Graphics2D = dest.createGraphics
    g2.drawImage(src, 0, 0, null)
    g2.dispose
    return dest
  }

}
