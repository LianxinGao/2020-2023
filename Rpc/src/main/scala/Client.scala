import java.io.{File, FileInputStream}

import io.netty.buffer.Unpooled
import net.neoremind.kraps.RpcConf
import net.neoremind.kraps.rpc.{RpcAddress, RpcEnvClientConfig}
import net.neoremind.kraps.rpc.netty.{HippoEndpointRef, HippoRpcEnvFactory}
import org.apache.commons.io.IOUtils

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object client {
  def main(args: Array[String]): Unit = {
    val config =RpcEnvClientConfig(new RpcConf(), "client")
    val rpcEnv = HippoRpcEnvFactory.create(config)
    //    val endpointRef = rpcEnv.setupEndpointRef(new RpcAddress(args(0), 8878), "server")
    val endpointRef = rpcEnv.setupEndpointRef(new RpcAddress("localhost", 8878), "server")
    val res = sayHelloHippoRpcTest(endpointRef)
        println(res)
//    readFileTest(endpointRef, "testdata/100mb")
    //    readFileTestKraps(endpointRef, "testdata/100mb")
    //    readFileByGetInputStream(endpointRef, args(1))
    //    readFileByGetChunkedInputStream(endpointRef, args(1))
    //    rpcEnv.shutdown()
    System.exit(0)
  }
  def sayHelloSparkRpcTest(endpointRef:HippoEndpointRef): Any ={
    val res = Await.result(endpointRef.ask[SayHelloResponse](SayHelloRequest("hello")), Duration.Inf)
    res.value
  }
  def sayHelloHippoRpcTest(endpointRef:HippoEndpointRef): Any ={
    val res = Await.result(endpointRef.askWithBuffer[SayHelloResponse](SayHelloRequest("hello")),Duration.Inf)
    res.value
  }
  //  def putFileTest(endpointRef:HippoEndpointRef): Int ={
  //    val res = Await.result(endpointRef.askWithBuffer[PutFileResponse](PutFileRequest(new File("./testdata/input/1k").length().toInt), {
  //      val buf = Unpooled.buffer(1024)
  //      val fos = new FileInputStream(new File("./testdata/input/1k"));
  //      buf.writeBytes(fos.getChannel, new File("./testdata/input/1k").length().toInt)
  //      fos.close()
  //      buf
  //    }), Duration.Inf)
  //    res.written
  //  }

  def readFileTest(endpointRef:HippoEndpointRef, path:String): String ={
    val costTimes = ArrayBuffer[Long]()
    for(i <- 1 to 12){
      val startTime = System.currentTimeMillis()
      val res = Await.result(endpointRef.ask(ReadFileRequest(path), (buf)=>{
        val bs = new Array[Byte](buf.remaining())
        buf.get(bs)
        bs
      }), Duration.Inf)
      val costTime = System.currentTimeMillis() - startTime
      costTimes += costTime
      println(s"Hippo Spend time:$costTime ms")
    }
    evaluate(costTimes, 1)
    "READ OK"
  }
  def readFileTestKraps(endpointRef:HippoEndpointRef, path:String): String ={
    val costTimes = ArrayBuffer[Long]()
    for(i <- 1 to 12){
      val startTime = System.currentTimeMillis()
      val res = Await.result(endpointRef.ask[String](ReadFileRequest(path)), Duration.Inf)
      val costTime = System.currentTimeMillis() - startTime
      costTimes += costTime
      println(s"Kraps Spend time:$costTime ms")
    }
    evaluate(costTimes, 1)
    "Kraps READ OK"
  }

  def readFileByGetInputStream(endpointRef:HippoEndpointRef, path:String): Unit ={
    val costTimes = ArrayBuffer[Long]()
    for(i <- 1 to 12){
      val startTime = System.currentTimeMillis()
      val is = endpointRef.getInputStream(ReadFileRequest(path), Duration.Inf)
      val res = IOUtils.toByteArray(is)
      val costTime = System.currentTimeMillis() - startTime
      costTimes += costTime
      println(s"GetInputStream Spend time:$costTime ms")
    }
    evaluate(costTimes, 1)
  }
  def readFileByGetChunkedInputStream(endpointRef:HippoEndpointRef, path:String): Unit ={
    val costTimes = ArrayBuffer[Long]()
    for(i <- 1 to 12){
      val startTime = System.currentTimeMillis()
      val is = endpointRef.getChunkedInputStream(ReadFileRequest(path), Duration.Inf)
      val res = IOUtils.toByteArray(is)
      val costTime = System.currentTimeMillis() - startTime
      costTimes += costTime
      //      println(s"GetChunkedInputStream Spend time:$costTime ms")
    }
    evaluate(costTimes, 1)
  }
  def evaluate(costTimes:ArrayBuffer[Long], times:Int): Unit ={
    val maxTime = costTimes.max / times.toFloat
    val minTime = costTimes.min / times.toFloat
    costTimes -= costTimes.max
    costTimes -= costTimes.min
    val averageTime = costTimes.sum / 10.0 / times.toFloat
    println(s"min time: $minTime ms")
    println(s"max time: $maxTime ms")
    println(s"avg time: $averageTime ms")
  }
}