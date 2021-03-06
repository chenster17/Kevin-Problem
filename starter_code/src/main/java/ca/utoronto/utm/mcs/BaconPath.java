package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.io.OutputStream;

import org.json.*;
import org.neo4j.driver.v1.exceptions.NoSuchRecordException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class BaconPath implements HttpHandler{
	
    public void handle(HttpExchange r) throws IOException {
        try {
            if (r.getRequestMethod().equals("GET")) {
                handleGet(r);
            } 
        } catch (JSONException e){ //catch improper inputs
        	e.printStackTrace();
        	r.sendResponseHeaders(400, -1);
        } catch (Exception e) {
            e.printStackTrace();
            r.sendResponseHeaders(500, -1);
        }
    }

	private void handleGet(HttpExchange r) throws Exception {

        String body = Utils.convert(r.getRequestBody());
        JSONObject deserialized = new JSONObject(body);
        
        String actorId = "";
        JSONObject res = new JSONObject();
        if(deserialized.has("actorId") && deserialized.get("actorId") instanceof String){
        	actorId = deserialized.getString("actorId");
        }
        
        else{
        	r.sendResponseHeaders(400,-1);
        	return;
        }
        
        
        try ( BaconQuery actor = new BaconQuery( "bolt://localhost:7687", "neo4j", "a" ) )
        {
        	res = actor.getBaconPath( actorId );
        } catch (ActorNotFoundException e){ //if the actorid doesn't exist
        	r.sendResponseHeaders(400, -1);
        	return;
        } catch (NoSuchRecordException e){
        	r.sendResponseHeaders(404, -1);
        	return;
        } catch (Exception e){
        	throw e;
        }

        r.sendResponseHeaders(200, res.toString().length());
        OutputStream os = r.getResponseBody();
        os.write(res.toString().getBytes());
        os.close();
        
    }
}