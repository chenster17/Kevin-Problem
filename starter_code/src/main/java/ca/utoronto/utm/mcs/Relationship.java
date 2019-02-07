package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.io.OutputStream;

import org.json.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


public class Relationship implements HttpHandler
{
	public void handle(HttpExchange r){
		try {
            if (r.getRequestMethod().equals("GET")) {
            	return;
                //handleGet(r);
            } else if (r.getRequestMethod().equals("PUT")) {
            	return;
                //handlePut(r);
            }
	}
}
}