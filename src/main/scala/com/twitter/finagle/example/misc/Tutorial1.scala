package com.twitter.finagle.example.misc

import java.net.InetSocketAddress

import scala.actors.Actor._
import scala.actors.Actor
import scala.collection.JavaConversions.asMap
import scala.collection.JavaConversions.asJavaList

import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.util.CharsetUtil

import com.twitter.finagle.builder.Server
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.Http
import com.twitter.finagle.Service
import com.twitter.util.Future

/**
 * @author jcheng
 *
 */
object Tutorial1 {
  def main(args: Array[String]) {

    /**
     * A very simple server that simply echos back request parameters.
     */    
    class EchoService extends Service[HttpRequest, HttpResponse] {
      def apply(request: HttpRequest) = {
        val response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
        // This is how you parse request parameters
        val params = new QueryStringDecoder(request.getUri()).getParameters()
        val responseContent = params.toString()
        // This is how you write to response        
        response.setContent(ChannelBuffers.copiedBuffer(responseContent, CharsetUtil.UTF_8))
        Future.value(response)
      }
    }
    val echoServer: Server = ServerBuilder()
      .codec(Http())
      .bindTo(new InetSocketAddress(8080))
      .name("EchoServer")
      .build(new EchoService())

    val mediator: TestMediator = new TestMediator(server = echoServer)
    val testBot = actor {
      val testHttpClient = new DefaultHttpClient()

      def say(msg: Object) { // convenience method for prettier println
        println("[TestBot]: %s".format(msg))
      }

      loop {
        react {
          case MakeRequest(id, message) =>
            try {
              val params = Seq(new BasicNameValuePair("id", String.valueOf(id)),
                new BasicNameValuePair("message", message))
              val query = URLEncodedUtils.format(params, "UTF-8")

              say("Sending request: [%s]".format(query))

              val res = testHttpClient.execute(new HttpGet("http://localhost:8080/?" + query))

              say("   Got response: " + EntityUtils.toString(res.getEntity()))
            } finally {
              mediator ! Finished(id)
            }
          case Stop =>
            say("Stopping the TestBot")
            exit()
        }
      }
    }
    mediator.testBot = testBot
    testBot.start()
    mediator.start()
    mediator ! Start
  }
}

class TestMediator(var testBot: Actor = null, val server: Server) extends Actor {
  def act = {
    def say(msg: String) { // convenience method for prettier println
      println("[TestMediator]: " + msg)
    }

    loop {
      react {
        case Start =>
          testBot ! MakeRequest(0, "Hello")

        case Finished(0) =>
          testBot ! MakeRequest(1, "This is request #1")

        case Finished(1) =>
          testBot ! Stop

          try {
            server.close()
          } catch {
            case t: Throwable =>
              t.printStackTrace()
          }
          say("Stopping the Test Mediator")
          exit()
      }
    }
  }
}

case class MakeRequest(id: Int, message: String)
case class Finished(id: Int)
case object Start
case object Stop

