package de.fraunhofer.iao.querimonia.rest.restobjects.textominado;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class Response<T> extends ResponseEntity<Response.OutputParcel<T>> {

    public Response(T output, String nameOfTool, String path, FrontendException exception) {
        super(new OutputParcel<T>(output, nameOfTool, path, exception), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public Response(T output, String nameOfTool, String path) {
        super(new OutputParcel<T>(output, nameOfTool, path), HttpStatus.OK);
    }

    public static class OutputParcel<T> {
        public long epochOfResponse;
        public String nameOfTool;
        public String path;
        public T payload;
        public FilteredFrontendException exception = null;

        public OutputParcel(T payload, String nameOfTool, String path) {
            this.epochOfResponse = Instant.now().toEpochMilli();
            this.nameOfTool = nameOfTool;
            this.payload = payload;
            this.path = path;
        }

        public OutputParcel(T payload, String nameOfTool, String path, FrontendException exception) {
            this.epochOfResponse = Instant.now().toEpochMilli();
            this.nameOfTool = nameOfTool;
            this.payload = payload;
            this.path = path;
            this.exception = new FilteredFrontendException(exception.name, exception.description, exception.getStackTraceString());
        }
    }

    private static class FilteredFrontendException {
        @SuppressWarnings("unused")
        public String name;
        @SuppressWarnings("unused")
        public String description;
        @SuppressWarnings("unused")
        public String stackTrace = "";

        public FilteredFrontendException(String name, String description, String stackTrace) {
            this.name = name;
            this.description = description;
            this.stackTrace = stackTrace;

        }
    }
}
