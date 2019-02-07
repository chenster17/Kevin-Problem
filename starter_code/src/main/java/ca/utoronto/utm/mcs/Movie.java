package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.io.OutputStream;

import org.json.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Movie implements HttpHandler
{

    public void handle(HttpExchange r) {
        try {
            if (r.getRequestMethod().equals("GET")) {
                handleGet(r);
            } else if (r.getRequestMethod().equals("PUT")) {
                handlePut(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleGet(HttpExchange r) throws Exception {
        String body = Utils.convert(r.getRequestBody());
        JSONObject deserialized = new JSONObject(body);
        
        String movieId = "";
        JSONObject res = new JSONObject();
        if (deserialized.has("movieId"))
        	movieId = deserialized.getString("movieId");
        
        
        try ( Query movie = new Query( "bolt://localhost:7687", "neo4j", "a" ) )
        {
        	res = movie.getMovie( movieId );
        }

        r.sendResponseHeaders(200, res.toString().length());
        OutputStream os = r.getResponseBody();
        os.write(res.toString().getBytes());
        os.close();
        
    }

    public void handlePut(HttpExchange r) throws IOException, JSONException{
        String body = Utils.convert(r.getRequestBody());
        JSONObject deserialized = new JSONObject(body);
        String movieId;
        String movieN;
        if (deserialized.has("movieId")) {
        	movieId = deserialized.getString("movieId");
        }
        if (deserialized.has("name")) {
        	movieN = deserialized.getString("name");
        }
    }
}
