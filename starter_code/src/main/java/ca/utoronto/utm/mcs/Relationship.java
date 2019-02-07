package ca.utoronto.utm.mcs;

import java.io.IOException;

public class Relationship implements HttpHandler
{
	public void handle(HttpExchange r){
		try {
            if (r.getRequestMethod().equals("GET")) {
                handleGet(r);
            } else if (r.getRequestMethod().equals("PUT")) {
                handlePut(r);
            }
	}
}
}