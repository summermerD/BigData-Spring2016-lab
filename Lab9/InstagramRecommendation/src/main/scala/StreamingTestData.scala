import java.awt.image.BufferedImage
import java.awt.{Graphics2D, Image}
import java.io._
import java.net.{InetAddress, ServerSocket, URL}
import javax.imageio.ImageIO

import com.google.common.io.BaseEncoding
import net.liftweb.json
import net.liftweb.json._
import org.apache.commons.validator.UrlValidator
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient


/**
  * Created by Mayanka on 15-Mar-16.
  */
object StreamingTestData {

  def main(args: Array[String]) {

    val tags = List("accordion", "airplanes", "anchor", "ant", "barrel", "bass", "beaver", "binocular", "bonsai")
    val server = new ServerSocket(1234)
    println(InetAddress.getLocalHost.getHostAddress)
    while (true) {
      val s = server.accept()
      while (s.isConnected) {
        val out = new PrintStream(s.getOutputStream)
        val r = new scala.util.Random(tags.length-1)
        val imageString = sendGet(tags(r.nextInt()), 1)
        imageString.foreach(f => {
          out.println(f)
          out.flush()
        })
        s.close()
      }

    }
  }

  @throws(classOf[Exception])
  private def sendGet(tag: String, tagId: Int): List[String] = {
    val url: String = "https://api.instagram.com/v1/tags/" + tag + "/media/recent?access_token=2242837681.1677ed0.40e41f58cf2e456fa6859e67d701f9af&count=1"
    val client: HttpClient = new DefaultHttpClient
    val request: HttpGet = new HttpGet(url)
    val response: HttpResponse = client.execute(request)
    println("\nSending 'GET' request to URL : " + url)
    println("Response Code : " + response.getStatusLine.getStatusCode)
    val rd: BufferedReader = new BufferedReader(new InputStreamReader(response.getEntity.getContent))
    val result: StringBuffer = new StringBuffer
    var line: String = ""
    while ( {
      line = rd.readLine
      line
    } != null) {
      result.append(line)
    }
    val jsonValue = json.parse(result.toString)
    val jsonFields = jsonValue.children
    val f2 = jsonFields(2)
    val f2list = f2.children
    var listOfimagelist = List[String]()
    f2list.foreach(ff => {

      val link = ff \\ "images" \\ "standard_resolution" \\ "url"
      val username = ff \\ "user" \\ "username"
      val caption = ff \\ "caption" \\ "text"
      var image: Image = null
      val imgLink = compactRender(link).toString
      val imgLinknew = imgLink.replace("\"", "")
      val newimg = imgLinknew

      println(newimg)
      val urlValidator = new UrlValidator()

      if (urlValidator.isValid(newimg)) {
        println("url is valid")

        val url = new URL(newimg)
        image = ImageIO.read(url)
        val bImage = toBufferedImage(image)

        val bos = new ByteArrayOutputStream()
        ImageIO.write(bImage, "jpg", bos)

        val imageBytes: Array[Byte] = bos.toByteArray
        val imageString = BaseEncoding.base64().encode(imageBytes)
        listOfimagelist = imageString :: listOfimagelist

        /*val outputStream = new FileOutputStream("imageOld.jpg")
        bos.writeTo(outputStream)

        println("Image String " + imageString)

        val imageByte2 = BaseEncoding.base64().decode(imageString)
        val bis = new ByteArrayInputStream(imageByte2)
        val image2 = ImageIO.read(bis)
        val ext = "jpg"
        val fileI = new File("image.jpg")
        ImageIO.write(toBufferedImage(image2), ext, fileI) // ignore returned boolean
        */ bos.close()
        // bis.close()
      }
      else
        println("URL is invalid")
    })
    listOfimagelist
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
