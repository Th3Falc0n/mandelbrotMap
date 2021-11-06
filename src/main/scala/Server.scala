package online.sachara.mandelbrot

import cats.effect.{ ExitCode, IO, IOApp }
import org.http4s.{ HttpRoutes, MediaType }
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.dsl.io._
import org.http4s.headers.`Content-Type`

import java.awt.image.BufferedImage

case class Complex(real: Double, imag: Double) {
  def abs = (real * real + imag * imag).abs

  def square = Complex(real * real - imag * imag, 2 * real * imag)

  def +(that: Complex) = Complex(real + that.real, imag + that.imag)
}

object Complex {
  def zero: Complex = Complex(0, 0)
}

object Server extends IOApp {
  def isInSet(c: Complex, z: Int): Int = {
    def iter(z: Complex, n: Int): Int = {
      if (n == 0) 0
      else if (z.abs > 2) n
      else iter(z.square + c, n - 1)
    }
    val max = 1000
    (255 * (iter(Complex.zero, max) / max.toDouble)).toInt
  }

  def generateImage(x: Double, y: Double, z: Int): BufferedImage = {
    val img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB)
    val zx = x / math.pow(2, z)
    val zy = y / math.pow(2, z)
    val zm = 1.0 / math.pow(2, z)

    for (i <- 0 until 256; j <- 0 until 256) {
      val value = isInSet(Complex(zx + (i / 256.0) * zm, zy + (j / 256.0) * zm), z)
      if (value == 0) {
        img.setRGB(i, j, 0x000000)
      }
      else {
        img.setRGB(i, j, value << 16 | (0xFF - (0xFF & value)))
      }
    }
    img
  }

  def encodeImage(img: BufferedImage) = {
    import java.io.ByteArrayOutputStream
    val baos = new ByteArrayOutputStream
    javax.imageio.ImageIO.write(img, "png", baos)
    baos.toByteArray
  }

  //create a function that creates a Response from index.html
  def index = {
    val index = scala.io.Source.fromFile("index.html").mkString
    Ok(index).map(_.withContentType(`Content-Type`(MediaType.text.html)))
  }

  private val service = HttpRoutes.of[IO] {
    case GET -> Root / "test" =>
      Ok("test")
    case GET -> Root / "tiles" / IntVar(z) / LongVar(x) / LongVar(y) =>
      Ok(encodeImage(generateImage(x, y, z))).map(_.withContentType(`Content-Type`(MediaType.image.png)))
    case GET -> Root =>
      index
  }

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(service.orNotFound)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
