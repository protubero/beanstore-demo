package de.protubero.beanstoredemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import de.protubero.beanstoredemo.app.Application;

public class AppTest {

	
	@SuppressWarnings("unchecked")
	@Test
	public void test() throws URISyntaxException, IOException, InterruptedException, ParseException {
		// start server
		Application.main(null);
		
		HttpClient client = HttpClient.newHttpClient();
		
		JSONObject obj = new JSONObject();
        obj.put("text", "Wikinger");
		
		HttpRequest postRequest = HttpRequest
				.newBuilder(new URI("http://localhost:7000/todos"))
				.POST(BodyPublishers.ofString(obj.toJSONString()))
				.header("Content-Type", "application/json")
				.build();
		
		HttpResponse<Void> postResponse = client
				  .send(postRequest, BodyHandlers.discarding()); 
		
		assertEquals(200, postResponse.statusCode());
		Optional<String> newIdHeaderValue = postResponse.headers().firstValue("newid");
		
		assertTrue(newIdHeaderValue.isPresent());
		
		long newId = Long.parseLong(newIdHeaderValue.get());
		
		HttpRequest getRequest = HttpRequest
				.newBuilder(new URI("http://localhost:7000/todos/" + newId))
				.GET()
				.build();
		
		HttpResponse<String> getResponse = client
				  .send(getRequest, BodyHandlers.ofString()); 
		
		assertEquals(200, getResponse.statusCode());
		
		JSONObject parsed = (JSONObject) new JSONParser().parse(getResponse.body());
		assertEquals("Wikinger", parsed.get("text"));
		
	}
}
