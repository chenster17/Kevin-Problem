package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.io.OutputStream;

import org.json.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Actor implements HttpHandler
{


    public void Add(){
    }

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
        String actorId = "";
        String res= "";
        if (deserialized.has("actorId"))
        	actorId = deserialized.getString("actorId");
        
        
        try ( Query actor = new Query( "bolt://localhost:7687", "neo4j", "a" ) )
        {
        	res = actor.getActor( actorId );
        }
        
        r.sendResponseHeaders(200, res.length());
        OutputStream os = r.getResponseBody();
        os.write(res.getBytes());
        os.close();
        
    }

    public void handlePut(HttpExchange r) throws IOException, JSONException{
        String body = Utils.convert(r.getRequestBody());
        JSONObject deserialized = new JSONObject(body);
        String actorId;
        String actorN;
        if (deserialized.has("actorId")) {
        	actorId = deserialized.getString("actorId");
        }
        if (deserialized.has("name")) {
        	actorN = deserialized.getString("name");
        }
    }
}
