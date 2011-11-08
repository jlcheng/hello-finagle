package com.twitter.finagle.example.misc


import com.twitter.finagle.Service
import com.twitter.finagle.builder.Server
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http._
import com.twitter.util.Future
import java.net.InetSocketAddress
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.handler.codec.http.QueryStringDecoder
import org.jboss.netty.util.CharsetUtil.UTF_8
import scala.actors.Actor
import scala.collection.JavaConversions.asJavaList

/**
 * @author jcheng
 *
 */
object Tutorial2 {
  def main(args: Array[String]) {

    /**
     * Slight improvement over Tutorial1 by using RichHttp decorator. Our service now gets
     * invoked with com.twitter.finagle.http.Request objects, 
     */
    class EchoService extends Service[Request, Response] {
      def apply(request: Request) = {
        val response = Response()
        response.setContentType(MediaType.Html, UTF_8.name)
        // Compare to Tut1, easy way of getting request parameters, i.e., request.getIntParam(name)
        val responseContent =
          "id = " + request.getIntParam("id") +
          ", message = " + request.getParam("message") +
          ", all params = " + request.params
        // This is how you write to response
        response.setContent(ChannelBuffers.copiedBuffer(responseContent, UTF_8))
        Future.value(response)
      }
    }
    
    val echoServer: Server = ServerBuilder()
      .codec(RichHttp[Request](Http()))
      .bindTo(new InetSocketAddress(8080))
      .name("EchoServer")
      .build(new EchoService())

    val mediator: TestMediator = new TestMediator(server = echoServer)
    val testBot = new TestBot2(mediator)
    mediator.testBot = testBot
    testBot  start()
    mediator start()
    mediator ! Start

  }
}

class TestBot2(val mediator: TestMediator) extends Actor {
  val testHttpClient = new DefaultHttpClient()

  def say(msg: Object) { // convenience method for prettier println
    println("[TestBot]: " + msg)
  }

  def act() {
    loop {
      react {
        case MakeRequest(id, message) =>
          try {
            val params = Seq(new BasicNameValuePair("id", String.valueOf(id)),
              new BasicNameValuePair("message", message))
            val query = URLEncodedUtils.format(params, UTF_8.name)

            say("Sending request: [%s]".format(query))

            val res = testHttpClient.execute(new HttpGet("http://localhost:8080/?" + query))

            say("   Got response: " + EntityUtils.toString(res.getEntity(), UTF_8.name))
          } catch {
            case t: Throwable =>
              t.printStackTrace()
          } finally {
            mediator ! Finished(id)
          }
        case Stop =>
          say("Stopping the TestBot")
          exit()
      }
    }
  }
}
