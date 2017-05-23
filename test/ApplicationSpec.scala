import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "render the index page" in new WithApplication{
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Inventory")
    }

    "render the search page" in new WithApplication{
      val search = route(app, FakeRequest(GET, "/search")).get

      status(search) must equalTo(OK)
      contentType(search) must beSome.which(_ == "text/html")
      contentAsString(search) must contain ("<input")
    }

    "render the scan barcode page" in new WithApplication{
      val scan = route(app, FakeRequest(GET, "/scan")).get

      status(scan) must equalTo(OK)
      contentType(scan) must beSome.which(_ == "text/html")
      contentAsString(scan) must contain ("<input")
    }

    "render the things list page" in new WithApplication{
      val things = route(app, FakeRequest(GET, "/things")).get

      status(things) must equalTo(OK)
      contentType(things) must beSome.which(_ == "text/html")
    }

    "render the barcodes list page" in new WithApplication{
      val barcodes = route(app, FakeRequest(GET, "/barcodes")).get

      status(barcodes) must equalTo(OK)
      contentType(barcodes) must beSome.which(_ == "text/html")
    }

    "render the labels list page" in new WithApplication{
      val labels = route(app, FakeRequest(GET, "/labels")).get

      status(labels) must equalTo(OK)
      contentType(labels) must beSome.which(_ == "text/html")
    }

    "render the create label page" in new WithApplication{
      val newLabel = route(app, FakeRequest(GET, "/labels/new")).get

      status(newLabel) must equalTo(OK)
      contentType(newLabel) must beSome.which(_ == "text/html")
      contentAsString(newLabel) must contain ("<form")
    }

    "render the create thing page without type" in new WithApplication{
      val newThing = route(app, FakeRequest(GET, "/things/new")).get

      status(newThing) must equalTo(SEE_OTHER)
    }

    "render the create thing page with atomic type" in new WithApplication{
      val newThing = route(app, FakeRequest(GET, "/things/new?thing_type=atomic")).get

      status(newThing) must equalTo(OK)
      contentType(newThing) must beSome.which(_ == "text/html")
      contentAsString(newThing) must contain ("<form")
    }
  }
}
