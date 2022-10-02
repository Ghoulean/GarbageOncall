package com.ghoulean.garbageoncall.lambda;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.ghoulean.garbageoncall.dagger.component.ChangeOncallComponent;
import com.ghoulean.garbageoncall.dagger.component.DaggerChangeOncallComponent;
import com.ghoulean.garbageoncall.handler.ChangeOncallHandler;
import com.ghoulean.garbageoncall.model.ChangeOncallLambdaEvent;
import com.google.gson.Gson;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/*
 * Accepts events in the following format:
 * 
 * {
 *   "personIndex": <int>,
 *   "recyclingIndex": <int>
 * }
 */
@Slf4j
public final class ChangeOncallLambda implements RequestStreamHandler {
    @NonNull
    private final ChangeOncallHandler changeOncallHandler;

    public ChangeOncallLambda() {
        ChangeOncallComponent changeOncallUpdateComponent = DaggerChangeOncallComponent.create();
        changeOncallHandler = changeOncallUpdateComponent.changeOncallHandler();
    }

    @Override
    public void handleRequest(@NonNull final InputStream inputStream,
            @NonNull final OutputStream outputStream,
            @NonNull final Context context) {
        log.info("Received request to change oncall");
        final String str = convertStreamToString(inputStream);
        log.info("Received: ", str);
        final List<Integer> indices = convertStringToIndices(str);
        log.info("Converted to: ", indices);
        changeOncallHandler.handle(indices);
    }

    private List<Integer> convertStringToIndices(final String str) {
        ChangeOncallLambdaEvent changeOncallLambdaEvent = new Gson().fromJson(str, ChangeOncallLambdaEvent.class);
        return List.of(changeOncallLambdaEvent.getPersonIndex(), changeOncallLambdaEvent.getRecyclingIndex());
    }

    private String convertStreamToString(final InputStream inputStream) {
        try {
            final BufferedInputStream bis = new BufferedInputStream(inputStream);
            final ByteArrayOutputStream buf = new ByteArrayOutputStream();
            for (int result = bis.read(); result != -1; result = bis.read()) {
                buf.write((byte) result);
            }
            // StandardCharsets.UTF_8.name() > JDK 7
            return buf.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
