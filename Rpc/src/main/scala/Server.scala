import java.io.{File, FileInputStream}
import java.nio.ByteBuffer

import io.netty.buffer.{ByteBuf, Unpooled}
import net.neoremind.kraps.RpcConf
import net.neoremind.kraps.rpc.{RpcCallContext, RpcEndpoint, RpcEnvServerConfig}
import net.neoremind.kraps.rpc.netty.{HippoRpcEnv, HippoRpcEnvFactory}
import org.apache.commons.io.IOUtils
import org.grapheco.commons.util.Profiler.timing
import org.grapheco.hippo.{ChunkedStream, CompleteStream, HippoRpcHandler, ReceiveContext}

object server {
  def main(args: Array[String]): Unit = {
    //    val config = RpcEnvServerConfig(new RpcConf(), "server", args(0), 8878)
    val config = RpcEnvServerConfig(new RpcConf(), "server", "localhost", 8878)
    val rpcEnv = HippoRpcEnvFactory.create(config)
    val endpoint = new MyEndpoint(rpcEnv)
    val handler = new MyStreamHandler()
    rpcEnv.setupEndpoint("server", endpoint)
    rpcEnv.setRpcHandler(handler)
    rpcEnv.awaitTermination()
  }
}

class MyEndpoint(override val rpcEnv: HippoRpcEnv) extends RpcEndpoint {

  override def onStart(): Unit = {
    println("server started...")
  }

  override def receiveAndReply(context: RpcCallContext): PartialFunction[Any, Unit] = {
    case SayHelloRequest(msg) => context.reply(SayHelloResponse(s"$msg response"))
    case ReadFileRequest(path) =>{
      val fis = new FileInputStream(new File(path))
      val content = IOUtils.toString(fis)
      context.reply(content)
    }
  }
}

class MyStreamHandler() extends HippoRpcHandler {

  override def receiveWithBuffer(extraInput: ByteBuffer, context: ReceiveContext): PartialFunction[Any, Unit] = {
    case SayHelloRequest(msg) =>
      context.reply(SayHelloResponse(msg.toUpperCase()))

    case PutFileRequest(totalLength) =>{
      context.reply(PutFileResponse(extraInput.remaining()))
    }

    case ReadFileRequest(path) => {
      val buf = Unpooled.buffer()
      val fis = new FileInputStream(new File(path))
      buf.writeBytes(fis.getChannel, new File(path).length().toInt)
      context.replyBuffer(buf)
    }
  }
  override def openChunkedStream(): PartialFunction[Any, ChunkedStream] = {
    case ReadFileRequest(path) =>
      new ChunkedStream() {
        val fis = new FileInputStream(new File(path))
        val length = new File(path).length()
        var count = 0;

        override def hasNext(): Boolean = {
          count < length
        }

        def nextChunk(buf: ByteBuf): Unit = {
          val written =
            timing(false) {
              buf.writeBytes(fis, 1024 * 1024 * 10)
            }

          count += written
        }

        override def close(): Unit = {
          fis.close()
        }
      }
  }
  override def openCompleteStream(): PartialFunction[Any, CompleteStream] = {
    case ReadFileRequest(path) =>
      val fis = new FileInputStream(new File(path))
      val buf = Unpooled.buffer()
      buf.writeBytes(fis.getChannel, new File(path).length().toInt)

      CompleteStream.fromByteBuffer(buf);
  }
}

case class SayHelloRequest(msg: String)

case class SayHelloResponse(value: Any)

case class PutFileRequest(totalLength:Int)

case class PutFileResponse(written:Int)

case class ReadFileRequest(path: String)