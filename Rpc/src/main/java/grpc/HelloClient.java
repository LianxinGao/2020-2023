//package grpc;
//
//import com.cloud.grpc.hello.HelloGrpc;
//import com.cloud.grpc.hello.HelloRequest;
//import com.cloud.grpc.hello.HelloResponse;
//import io.grpc.ManagedChannel;
//import io.grpc.ManagedChannelBuilder;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//public class HelloClient {
//
//    //远程连接管理器,管理连接的生命周期
//    private final ManagedChannel channel;
//    private final HelloGrpc.HelloBlockingStub blockingStub;
//
//    public HelloClient(String host, int port) {
//        //初始化连接
//        channel = ManagedChannelBuilder.forAddress(host, port)
//                .maxInboundMessageSize(1024*1024*110)
//                .maxInboundMetadataSize(1024*1024*110)
//                .usePlaintext()
//                .build();
//        //初始化远程服务Stub
//        blockingStub = HelloGrpc.newBlockingStub(channel);
//    }
//
//
//    public void shutdown() throws InterruptedException {
//        //关闭连接
//        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
//    }
//
//    public String sayHello(String name) {
//        //构造服务调用参数对象
//        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
//        //调用远程服务方法
//        HelloResponse response = blockingStub.sayHello(request);
//        //返回值
//        return response.getMessage();
//    }
//
//
//    public static void main(String[] args) throws InterruptedException {
//        HelloClient client = new HelloClient("127.0.0.1", 50051);
//        List<Long> lst = new ArrayList();
//        for (int i = 0; i < 12; i++){
//            Long start = System.currentTimeMillis();
//            String content = client.sayHello("Java client");
//            lst.add(System.currentTimeMillis() - start);
////            System.out.println("time cost: " + (System.currentTimeMillis() - start));
//        }
//        System.out.println(lst);
//        lst.remove(Collections.max(lst));
//        lst.remove(Collections.min(lst));
//        System.out.println(lst);
//
//        Long res = 0L;
//        for (int j=0; j<10; j++){
//            res += lst.get(j);
//        }
//        System.out.println(res / 10.0);
//
////        Long start = System.currentTimeMillis();
////        //服务调用
////        String content = client.sayHello("Java client");
////        //打印调用结果
////        System.out.println("time cost: " + (System.currentTimeMillis() - start));
//////        System.out.println(content);
//        //关闭连接
//        client.shutdown();
//    }
//
//}