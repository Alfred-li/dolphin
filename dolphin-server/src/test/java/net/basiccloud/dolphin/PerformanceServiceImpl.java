package net.basiccloud.dolphin;

import com.whtr.dolphin.core.EchoRequest;
import com.whtr.dolphin.core.EchoResponse;
import com.whtr.dolphin.core.PerformanceBaselineProto;
import com.whtr.dolphin.core.PerformanceBaselineServiceGrpc.PerformanceBaselineServiceImplBase;
import com.whtr.dolphin.core.TouchRequest;
import com.whtr.dolphin.core.TouchResponse;

import io.grpc.stub.StreamObserver;

/**
 * test service
 */
@SuppressWarnings("unused")
@DolphinService(protoClass = PerformanceBaselineProto.class)
class PerformanceServiceImpl extends PerformanceBaselineServiceImplBase {
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
