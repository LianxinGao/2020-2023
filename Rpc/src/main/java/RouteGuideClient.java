import com.glx.example.*;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class RouteGuideClient {
    private final RouteGuideGrpc.RouteGuideBlockingStub blockingStub;
    private final RouteGuideGrpc.RouteGuideStub asyncStub;

    public RouteGuideClient(Channel channel){
        blockingStub = RouteGuideGrpc.newBlockingStub(channel);
        asyncStub = RouteGuideGrpc.newStub(channel);

    }

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50010").usePlaintext().build();
        RouteGuideClient client = new RouteGuideClient(channel);
// blocking
//        client.getFeature(1);
//        client.getFeature(2);
//        client.listFeatures();
//        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
// async
//        client.recordRoute(channel);
        client.routeChat();
        channel.awaitTermination(10, TimeUnit.SECONDS);
    }

    public void getFeature(int i){
        Point request = Point.newBuilder().setLatitude(i).setLongitude(i).build();
        Feature feature = blockingStub.getFeature(request);
        System.out.println(feature.getName() + " " + feature.getLocation());
    }

    public void listFeatures(){
        Rectangle request =
                Rectangle.newBuilder()
                        .setP1(Point.newBuilder().setLatitude(100).setLongitude(200).build())
                        .setP2(Point.newBuilder().setLatitude(30).setLongitude(70).build()).build();
        Iterator<Feature> features;
        features = blockingStub.listFeatures(request);
        for (int i = 1; features.hasNext(); i++){
            Feature feature = features.next();
            System.out.println(feature.getName() + feature.getLocation().toString());
        }
    }

    public void recordRoute(final ManagedChannel channel){
        StreamObserver<RouteSummary> responseObserver = new StreamObserver<RouteSummary>() {
            @Override
            public void onNext(RouteSummary summary) {
                System.out.println("client receive: " + summary.getFeatureCount());
            }
            @Override
            public void onError(Throwable t) {
                System.out.println("error....client" + Status.fromThrowable(t));

            }
            @Override
            public void onCompleted() {
                System.out.println("finished...client");
                try {
                    channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        StreamObserver<Point> requestObserver = asyncStub.recordRoute(responseObserver);
        for (int i = 0; i < 3; i++){
            requestObserver.onNext(Point.newBuilder().setLatitude(i).setLongitude(i+1).build());
        }
        requestObserver.onCompleted();
    }

    public void routeChat(){
        StreamObserver<RouteNote> requestObserver = asyncStub.routeChat(
                new StreamObserver<RouteNote>() {
                    @Override
                    public void onNext(RouteNote value) {
                        System.out.println("get from server: " + value.getMessage());
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println("error....client");
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("finished...client");
                    }
                }
        );

        RouteNote[] requests = {
          RouteNote.newBuilder().setMessage("hello1").setLocation(Point.newBuilder().setLatitude(1).setLongitude(1).build()).build(),
                RouteNote.newBuilder().setMessage("hello2").setLocation(Point.newBuilder().setLatitude(2).setLongitude(2).build()).build(),
                RouteNote.newBuilder().setMessage("hello3").setLocation(Point.newBuilder().setLatitude(3).setLongitude(3).build()).build()
        };
        for (RouteNote request:requests){
            requestObserver.onNext(request);
        }
//        requestObserver.onCompleted();
    }
}
