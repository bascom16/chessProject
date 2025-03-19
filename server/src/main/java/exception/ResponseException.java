package exception;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

public class ResponseException extends Exception {
    final private int statusCode;

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public String toJson() {
      return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
    }

    public static ResponseException fromJson(InputStream stream) throws IOException {
        String json = new Scanner(stream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
        //System.out.println("JSON Input: " + json);

        JsonResponse response = new Gson().fromJson(json, JsonResponse.class);

        if (response == null) {
            throw new IOException("Unable to parse exception JSON");
        }

        return new ResponseException(response.status(), response.message());
    }

    private record JsonResponse(int status, String message) {}

    public int statusCode() {
    return statusCode;
  }
}
