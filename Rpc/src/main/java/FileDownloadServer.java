import com.glx.grpc.*;
import com.google.protobuf.ByteString;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import io.netty.util.CharsetUtil;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileDownloadServer {
    private Server server;
    private void start() throws IOException {
        int port = 50050;
        server = ServerBuilder.forPort(port)
                .addService(new FileDownloadImpl())
                .maxInboundMetadataSize(1024 * 1024 * 110)
                .maxInboundMessageSize(1024 * 1024 * 110)
                .build()
                .start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                FileDownloadServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }
    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        final FileDownloadServer server = new FileDownloadServer();
        server.start();
        System.out.println("started....");
        server.blockUntilShutdown();
    }

    static class FileDownloadImpl extends FileDownloadGrpc.FileDownloadImplBase{
        @Override
        public void download(FileDownloadRequest request, StreamObserver<DataChunk> responseObserver) {
            try {
               FileInputStream fis = new FileInputStream(request.getPath());
               BufferedInputStream bis = new BufferedInputStream(fis);
               int bufferSize = 1024 * 1024;
               byte[] buffer = new byte[bufferSize];
               int length;
               // 调用一次onNext就发送一次消息
               while ((length = bis.read(buffer, 0, bufferSize)) != -1){
                   responseObserver.onNext(
                           DataChunk.newBuilder().setData(ByteString.copyFrom(buffer, 0, length))
                                   .build()
                   );
               }
               responseObserver.onCompleted();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void messageDownload(MessageRequest request, StreamObserver<MessageReply> responseObserver) {
            try {
                FileInputStream fis = new FileInputStream(request.getData());
                String res = IOUtils.toString(fis, CharsetUtil.UTF_8);
                responseObserver.onNext(MessageReply.newBuilder().setData(res).build());
                responseObserver.onCompleted();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
