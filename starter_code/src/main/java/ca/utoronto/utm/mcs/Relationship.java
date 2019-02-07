package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.io.OutputStream;

import org.json.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


public class Relationship implements HttpHandler
{
	public void handle(HttpExchange r) throws IOException{
		try {
            if (r.getRequestMethod().equals("GET")) {
                handleGet(r);
            } else if (r.getRequestMethod().equals("PUT")) {
                handlePut(r);
            }
		} catch(Exception e) {
			e.printStackTrace();
			r.sendResponseHeaders(500,-1);
		}
		
	}
	
	public void handleGet(HttpExchange r) throws Exception{
		String body = Utils.convert(r.getRequestBody());
		JSONObject deserialized = new JSONObject(body);
		
		String actorId = "";
		String movieId = "";
		JSONObject res = new JSONObject();
		
		if (deserialized.has("actorId") && deserialized.has("movieId") && deserialized.length() == 2) {
        	actorId = deserialized.getString("actorId");
        	movieId = deserialized.getString("movieId");
        }
		else{
			r.sendResponseHeaders(400, -1);
			return;
		}
	}
	
	public void handlePut(HttpExchange r) throws Exception{
		String body = Utils.convert(r.getRequestBody());
		JSONObject deserialized = new JSONObject(body);
		
		String actorId = "";
		String movieId = "";
		JSONObject res = new JSONObject();
		
		if (deserialized.has("actorId") && deserialized.has("movieId") && deserialized.length() == 2) {
        	actorId = deserialized.getString("actorId");
        	movieId = deserialized.getString("name");
        }
		else{
			r.sendResponseHeaders(400, -1);
			return;
		}
	}
}
