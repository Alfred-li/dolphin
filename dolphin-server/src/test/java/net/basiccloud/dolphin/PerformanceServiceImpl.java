package net.basiccloud.dolphin;


import io.grpc.stub.StreamObserver;

/**
 * test service
 */
@SuppressWarnings("unused")
@DolphinService(protoClass = PerformanceBaselineProto.class)
class PerformanceServiceImpl extends PerformanceBaselineServiceGrpc.PerformanceBaselineServiceImplBase {
    @Override
    public void touch(TouchRequest request, StreamObserver<TouchResponse> responseObserver) {
        responseObserver.onNext(TouchResponse.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void echo(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
        responseObserver.onNext(EchoResponse.newBuilder().setContent(request.getContent()).build());
        responseObserver.onCompleted();
    }
}
