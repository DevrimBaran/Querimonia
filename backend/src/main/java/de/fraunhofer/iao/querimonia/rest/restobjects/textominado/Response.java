package de.fraunhofer.iao.querimonia.rest.restobjects.textominado;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;


public class Response extends ResponseEntity<Response.OutputParcel<List<Match>>> {

    public Response(List<Match> output, String nameOfTool, String path, FrontendException exception) {
        super(new OutputParcel<>(output, nameOfTool, path, exception), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @JsonCreator
    public Response(@JsonProperty("payload") List<Match> output, @JsonProperty("nameOfTool") String nameOfTool, @JsonProperty("path") String path) {
        super(new OutputParcel<>(output, nameOfTool, path), HttpStatus.OK);
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
