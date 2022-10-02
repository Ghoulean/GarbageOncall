package com.ghoulean.garbageoncall.lambda;

import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.ghoulean.garbageoncall.dagger.component.DaggerOncallUpdateComponent;
import com.ghoulean.garbageoncall.dagger.component.OncallUpdateComponent;
import com.ghoulean.garbageoncall.handler.OncallUpdateHandler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings({ "checkstyle:visibilitymodifier" })
public final class OncallUpdateLambda implements RequestStreamHandler {

    @NonNull
    private final OncallUpdateHandler oncallUpdateHandler;

    public OncallUpdateLambda() {
        OncallUpdateComponent oncallUpdateComponent = DaggerOncallUpdateComponent.create();
        oncallUpdateHandler = oncallUpdateComponent.oncallUpdateHandler();
    }

    @Override
    public void handleRequest(@NonNull final InputStream inputStream,
            @NonNull final OutputStream outputStream,
            @NonNull final Context context) {
        log.info("Received request to update oncall");
        oncallUpdateHandler.handle();
    }
}
