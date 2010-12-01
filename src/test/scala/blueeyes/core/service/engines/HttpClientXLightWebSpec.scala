package blueeyes.core.service.engines

import org.specs.Specification
import org.specs.util._
import blueeyes.core.http._
import blueeyes.core.service._
import blueeyes.core.service.HttpClientHandler
import blueeyes.core.service.RestPathPatternImplicits
import blueeyes.util.Future
import blueeyes.util.FutureImplicits

class HttpClientXLightWebSpec extends Specification with RestPathPatternImplicits with HttpResponseHandlerCombinators {
  val duration = 250
  val retries = 10
  val skip = true
  
  private val httpClient = new HttpClientXLightWebEnginesString {
  }

  def skipper(): () => Unit = skip match {
    case true => skip("Will use Skalatra")
    case _ => () => Unit
  }

  "Support GET requests with status OK" in {
    val h = protocol("http") {
      host("www.google.com") {
        port(80) {
          path[String, String]("/") {
            get[String, String] { (res: HttpResponse[String]) =>
	      Future[String](res.content.getOrElse[String](""))
	    }
          }
        }
      }
    }

    val f = h(httpClient) 
    f.value must eventually(beSomething)
  }

/*  
  "Support GET requests with status OK" in {
    skipper()()

    println("RUNNING")
    val f = apply(HttpRequest[String](HttpMethods.GET, "http://localhost/test/echo.php"))
    f.deliverTo((res: HttpResponse[String]) => {})
    f.value must eventually(retries, new Duration(duration))(beSomething)
    f.value.get.status.code must eventually(be(HttpStatusCodes.OK))
  }

  "Support GET requests with status Not Found" in {
    skipper()()
    val f = apply(HttpRequest[String](HttpMethods.GET, "http://localhost/bogus"))
    f.deliverTo((res: HttpResponse[String]) => {})
    f.value must eventually(retries, new Duration(duration))(beSomething)
    f.value.get.status.code must be(HttpStatusCodes.NotFound)
  }

  "Support GET requests with query params" in {
    skipper()()
    val f = apply(HttpRequest[String](HttpMethods.GET, "http://localhost/test/echo.php?param1=a&param2=b"))
    f.deliverTo((res: HttpResponse[String]) => {})
    f.value must eventually(retries, new Duration(duration))(beSomething)
    f.value.get.content.get.trim must eventually(equalIgnoreSpace("param1=a&param2=b"))
    f.value.get.status.code must be(HttpStatusCodes.OK)
  }

  "Support POST requests with query params" in {
    skipper()()
    val f = apply(HttpRequest[String](HttpMethods.POST, "http://localhost/test/echo.php?param1=a&param2=b"))
    f.deliverTo((res: HttpResponse[String]) => {})
    f.value must eventually(retries, new Duration(duration))(beSomething)
    f.value.get.content.get.trim must eventually(equalIgnoreSpace("param1=a&param2=b"))
    f.value.get.status.code must be(HttpStatusCodes.OK)
  }

  "Support POST requests with request params" in {
    skipper()()
    val f = apply(HttpRequest[String](HttpMethods.POST, "http://localhost/test/echo.php",
                                            parameters=Map('param1 -> "a", 'param2 -> "b")))
    f.deliverTo((res: HttpResponse[String]) => {})
    f.value must eventually(retries, new Duration(duration))(beSomething)
    f.value.get.content.get.trim must eventually(equalIgnoreSpace("param1=a&param2=b"))
    f.value.get.status.code must be(HttpStatusCodes.OK)
  }

  "Support POST requests with body" in {
    skipper()()
    val content = "Hello, world"
    val f = apply(HttpRequest[String](HttpMethods.POST, "http://localhost/test/echo.php", content=Some(content)))
    f.deliverTo((res: HttpResponse[String]) => {})
    f.value must eventually(retries, new Duration(duration))(beSomething)
    f.value.get.content.get.trim must eventually(equalIgnoreSpace(content))
    f.value.get.status.code must be(HttpStatusCodes.OK)
  }

  "Support POST requests with body and request params" in {
    skipper()()
    val content = "Hello, world"
    val f = apply(HttpRequest[String](HttpMethods.POST, "http://localhost/test/echo.php",
                                             content=Some(content), 
                                             parameters=Map('param1 -> "a", 'param2 -> "b")))
    f.deliverTo((res: HttpResponse[String]) => {})
    f.value must eventually(retries, new Duration(duration))(beSomething)
    f.value.get.content.get.trim must equalIgnoreSpace("param1=a&param2=b" + content)
    f.value.get.status.code must be(HttpStatusCodes.OK)
  }

  "Support PUT requests with body" in {
    skipper()()
    val content = "Hello, world"
    val f = apply(HttpRequest[String](HttpMethods.PUT, "http://localhost/test/echo.php",
                                            content=Some(content), 
                                            headers=Map(`Content-Length`(100))))
    f.deliverTo((res: HttpResponse[String]) => {})
    f.value must eventually(retries, new Duration(duration))(beSomething)
    f.value.get.status.code must be(HttpStatusCodes.OK)
  }

  "Support GET requests with header" in {
    skipper()()
    val f = apply(HttpRequest[String](HttpMethods.GET, "http://localhost/test/echo.php?headers=true",
                                            headers=Map("Fooblahblah" -> "washere", "param2" -> "1")))
    f.deliverTo((res: HttpResponse[String]) => {})
    f.value must eventually(retries, new Duration(duration))(beSomething)
    f.value.get.content.get.trim must include("Fooblahblah: washere")
    f.value.get.status.code must be(HttpStatusCodes.OK)
  }

  "Support POST requests with Content-Type: text/html & Content-Length: 100" in {
    skipper()()
    val content = "<html></html>"
    val f = apply(HttpRequest[String](HttpMethods.POST, "http://localhost/test/echo.php",
                                             content=Some(content), 
                                             headers=Map(`Content-Type`(text/html), `Content-Length`(100))))
    f.deliverTo((res: HttpResponse[String]) => {})
    f.value must eventually(retries, new Duration(duration))(beSomething)
    f.value.get.content.get.trim must beEqual(content)
    f.value.get.status.code must be(HttpStatusCodes.OK)
  }

  "Support POST requests with large payload" in {
    skipper()()
    val content = Array.fill(1024*1000)(0).toList.mkString("")
    val f = apply(HttpRequest[String](HttpMethods.POST, "http://localhost/test/echo.php",
                                             content=Some(content)))
    f.deliverTo((res: HttpResponse[String]) => {})
    f.value must eventually(retries, new Duration(duration))(beSomething)
    f.value.get.content.get.trim must beEqual(content)
    f.value.get.status.code must be(HttpStatusCodes.OK)
  }

  "Support HEAD requests" in {
    skipper()()
    val f = apply(HttpRequest[String](HttpMethods.HEAD, "http://localhost/test/echo.php"))
    f.deliverTo((res: HttpResponse[String]) => {})
    f.value must eventually(retries, new Duration(duration))(beSomething)
    f.value.get.status.code must be(HttpStatusCodes.OK)
  }

  "Support CONNECT requests" in {
    skip("CONNECT method TBD")
    val f = apply(HttpRequest[String](HttpMethods.CONNECT, "http://localhost/test/echo.php?headers=true",
                                                headers=Map("Fooblahblah" -> "washere")))
    f.deliverTo((res: HttpResponse[String]) => {})
    f.value must eventually(retries, new Duration(duration))(beSomething)
    f.value.get.status.code must be(HttpStatusCodes.OK)
  }

  "Support POST requests with empty body" in {
    skipper()()
    val f = apply(HttpRequest[String](HttpMethods.POST, "http://localhost/test/echo.php"))
    f.deliverTo((res: HttpResponse[String]) => {})
    f.value must eventually(retries, new Duration(duration))(beSomething)
    f.value.get.status.code must be(HttpStatusCodes.OK)
  }

  "Support GET requests of 1000 requests" in {
    skipper()()
    val total = 1000
    val duration = 1000
    val futures = (0 until total).map { i =>
      apply(HttpRequest[String](HttpMethods.GET, "http://localhost/test/echo.php?test=true"))
    }

    val responses = futures.foldLeft(0) { 
      (acc, f) => {
        f.deliverTo((res: HttpResponse[String]) => {})
        f.value must eventually(retries, new Duration(duration))(beSomething)
        f.value.get.status.code must be(HttpStatusCodes.OK)
        acc + 1
      }
    }

    responses must beEqual(total)
  }
*/


}
