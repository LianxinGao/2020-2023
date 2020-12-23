import com.glx.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.glx.grpc.FileDownloadGrpc.newBlockingStub;


public class FileDownloadClient {
    private final ManagedChannel channel;
    private final FileDownloadGrpc.FileDownloadBlockingStub blockingStub;
    List<Long> timeCost = new ArrayList<>();

    public FileDownloadClient() {
        this.channel = ManagedChannelBuilder.forAddress("localhost", 50050)
                .maxInboundMetadataSize(1024 * 1024 * 110)
                .maxInboundMessageSize(1024 * 1024 * 110)
                .usePlaintext().build();
        this.blockingStub = newBlockingStub(this.channel);

        Runtime.getRuntime().addShutdownHook(
                new Thread(
                        () -> {
                            try {
                                this.channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                )
        );
    }
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
    public void downloadFile(){
        FileDownloadRequest request = FileDownloadRequest.newBuilder().setPath("testdata/1kb").build();
        Iterator<DataChunk> response;
//        File localTempFile = File.createTempFile("localTempFile", ".txt");
//        ByteSink byteSink = Files.asByteSink(localTempFile, FileWriteMode.APPEND);
        Long start = System.currentTimeMillis();
        response = blockingStub.download(request);
        while (response.hasNext()){
            response.next().getData().toByteArray();
//             byteSink.write(response.next().getData().toByteArray());
        }

//        try( FileReader fr = new FileReader(localTempFile);
//             BufferedReader br = new BufferedReader(fr)){
//            String nextLine;
//            while ((nextLine = br.readLine()) != null){
//                System.out.println(nextLine);
//            }
//        }

        Long res = System.currentTimeMillis() - start;
        timeCost.add(res);
        System.out.println(res);
    }

    public void messageDownload(){
        MessageRequest request = MessageRequest.newBuilder().setData("testdata/100mb").build();
        Long start = System.currentTimeMillis();
        MessageReply reply = blockingStub.messageDownload(request);
        reply.getData();
        Long res = System.currentTimeMillis() - start;
        timeCost.add(res);
        System.out.println(res);
    }

    public static void main(String args[]) throws InterruptedException {
        FileDownloadClient client = new FileDownloadClient();
        for (int i = 0; i < 12; i ++){
            client.downloadFile();
//            client.messageDownload();
        }
        Long maxTime = Collections.max(client.timeCost);
        Long minTime = Collections.min(client.timeCost);
        client.timeCost.remove(maxTime);
        client.timeCost.remove(minTime);

        Long res = 0L;
        for (int i = 0; i < client.timeCost.size(); i++){
            res += client.timeCost.get(i);
        }
        System.out.println(client.timeCost.size() + " avg time :" + (res / 10.0) + "ms");
        client.shutdown();
    }
}
