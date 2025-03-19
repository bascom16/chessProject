package exception;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientException extends Exception {
    final private int statusCode;

    public ClientException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public static ClientException fromJson(InputStream stream) throws IOException {
        String json = new Scanner(stream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
        //System.out.println("JSON Input: " + json);

        JsonResponse response = new Gson().fromJson(json, JsonResponse.class);
        if (response == null) {
            throw new IOException("Unable to parse exception JSON");
        }
        return new ClientException(response.status(), response.message());
    }

    private record JsonResponse(int status, String message) {}

    public int statusCode() {
        return statusCode;
    }
}
