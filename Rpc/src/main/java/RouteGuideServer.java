import com.glx.example.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class RouteGuideServer {
    private final int port = 50010;
    private Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        RouteGuideServer server = new RouteGuideServer();
        server.start();
        System.out.println("started....");
        server.blockUntilShutdown();
    }
    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new RouteGuideService())
                .build()
                .start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                RouteGuideServer.this.stop();
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
    // 实现定义的服务
    private static class RouteGuideService extends RouteGuideGrpc.RouteGuideImplBase{
        public int count = 0;
        private final Collection<Feature> features = new ArrayList<Feature>();

        @Override
        public void getFeature(Point request, StreamObserver<Feature> responseObserver) {
            Feature feature = Feature.newBuilder().setName("name" + count)
                    .setLocation(request).build();
            count += 1;
            features.add(feature);
            responseObserver.onNext(feature);
            responseObserver.onCompleted();
        }

        @Override
        public void listFeatures(Rectangle request, StreamObserver<Feature> responseObserver) {
            System.out.println(request.getP1().toString() + request.getP2().toString());
            for (Feature feature:features){
                responseObserver.onNext(feature);
            }
            responseObserver.onCompleted();
        }

        @Override
        public StreamObserver<Point> recordRoute(final StreamObserver<RouteSummary> responseObserver) {
            return new StreamObserver<Point>() {
                int pointCount;
                int featureCount;
                int distance = 0;
                Point previous;
                final long startTime = System.nanoTime();

                @Override
                public void onNext(Point point) {
                    System.out.println("get from clinet: " + point.getLongitude());
                    pointCount += 1;
                    featureCount += 1;
                    distance += 1;
                    previous = point;
                }

                @Override
                public void onError(Throwable t) {
                    System.out.println("error....recordRoute cancelled" + Status.fromThrowable(t));
                }

                @Override
                public void onCompleted() {
                    long seconds = NANOSECONDS.toSeconds(System.nanoTime() - startTime);
                    responseObserver.onNext(
                            RouteSummary.newBuilder().setPointCount(pointCount)
                                    .setFeatureCount(featureCount)
                                    .setDistance(distance)
                                    .setElapsedTime((int) seconds).build());
                    responseObserver.onCompleted();
                }
            };
        }

        @Override
        public StreamObserver<RouteNote> routeChat(final StreamObserver<RouteNote> responseObserver) {
            return new StreamObserver<RouteNote>() {
                @Override
                public void onNext(RouteNote value) {
                    System.out.println("get from client: " + value.getMessage());
                    RouteNote note = RouteNote.newBuilder()
                            .setLocation(Point.newBuilder().setLatitude(11)
                                    .setLongitude(22).build())
                            .setMessage("receive: "+ value.getMessage() + ", hello client!").build();
                    responseObserver.onNext(note);
                }

                @Override
                public void onError(Throwable t) {
                    System.out.println("recordRoute cancelled :" + Status.fromThrowable(t));
                }

                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                }
            };
        }
    }
}
