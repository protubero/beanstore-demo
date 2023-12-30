package de.protubero.beanstoredemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.protubero.beanstoredemo.app.Application;

public class AppTest {
	
	public static final String POST = "POST";
	public static final String PATCH = "PATCH";
	
	static String URL = "http://localhost:7001";

	private ObjectMapper mapper = new ObjectMapper();
	private HttpClient client = HttpClient.newHttpClient();
	
	@Test
	public void test(@TempDir File tempDir) throws InterruptedException  {
		// start server with new file
		Application.start(new File(tempDir, "testfile.kryo"), 7001);

		// check initial object
		ArrayNode todoArray = GET_ARRAY("todos");
		assertEquals(1, todoArray.size());
		JsonNode initialTodo = todoArray.get(0);

		assertTodoObj(initialTodo, "Read BeanStore docs", false);
		
		// assert count is 1
		assertCountIs(1);
		
		int todoId = createToDo("Paint the wall", false);
		assertCountIs(2);

		JsonNode tempTodo = GET("todos/" + todoId);
		assertTodoObj(tempTodo, "Paint the wall", false);
		
		// update todo
		updateToDo(todoId, "New Text", true);
		
		tempTodo = GET("todos/" + todoId);
		assertTodoObj(tempTodo, "New Text", true);
		
		// test CQRS command
		lowerToDoTexts();
		Thread.sleep(1000l);
		assertTodoObj(GET("todos/" + todoId), "new text", true);
				
		// search
		Thread.sleep(500l);
		ArrayNode searchResult = search("text");
		assertEquals(1, searchResult.size());
		
		assertEquals("new text", searchResult.get(0).get("text").asText());
		assertEquals(todoId, searchResult.get(0).get("_id").asInt());
		
		
		// delete
		deleteToDo(todoId);
		assertCountIs(1);
		Thread.sleep(500l);
		searchResult = search("text");
		assertEquals(0, searchResult.size());
		todoArray = GET_ARRAY("todos");
		assertEquals(1, todoArray.size());
		
	}

	private void deleteToDo(int todoId) {

		HttpRequest getRequest;
		try {
			getRequest = HttpRequest
					.newBuilder(new URI(URL + "/todos/" + todoId))
					.DELETE()
					.build();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		
		HttpResponse<String> getResponse;
		try {
			getResponse = client
					  .send(getRequest, BodyHandlers.ofString());
			assertEquals(200, getResponse.statusCode());
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		} 
	}

	private ArrayNode search(String text) {

		String param;
		try {
			param = URLEncoder.encode(text, "UTF-8").replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e1) {
			throw new RuntimeException(e1);
		}
		
		HttpRequest getRequest;
		try {
			getRequest = HttpRequest
					.newBuilder(new URI(URL + "/search?query=" + param))
					.GET()
					.build();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		
		HttpResponse<String> getResponse;
		try {
			getResponse = client
					  .send(getRequest, BodyHandlers.ofString());
			assertEquals(200, getResponse.statusCode());
			
			return (ArrayNode) mapper.readTree(getResponse.body());
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		} 
		
	}
	
	private void lowerToDoTexts() {
		ObjectNode commandNode = mapper.createObjectNode();
		commandNode.put("command", "lower");
		POST("command", commandNode);				
	}
	
	private void updateToDo(int id, String text, boolean done) {
		ObjectNode toDoNode = createToDoJsonObj(text, done);
		HttpHeaders headers = PATCH("todos/" + id, toDoNode);		
	}
	
	private int createToDo(String text, boolean done) {
		ObjectNode toDoNode = createToDoJsonObj(text, done);
		HttpHeaders headers = POST("todos", toDoNode);
		Optional<String> newId = headers.firstValue("newid");
		assertTrue(newId.isPresent());
		return Integer.parseInt(newId.get());
	}

	private ObjectNode createToDoJsonObj(String text, boolean done) {
		ObjectNode toDoNode = mapper.createObjectNode();
		toDoNode.put("done", done);
		toDoNode.put("text", text);
		return toDoNode;
	}

	private void assertTodoObj(JsonNode todoNode, String text, boolean done) {
		if (done) {
			assertTrue(todoNode.get("done").asBoolean());
		} else {
			assertFalse(todoNode.get("done").asBoolean());
		}
		assertEquals(text, todoNode.get("text").asText());
		assertTrue(todoNode.get("_id").isInt());
		assertEquals("todo", todoNode.get("_type").asText());
		assertNotNull(todoNode.get("createdAt"));
	}

	private void assertCountIs(int expectedCount) {
		JsonNode countNode = GET("count");
		assertTrue(countNode.get("count").isInt());
		assertEquals(expectedCount, countNode.get("count").asInt());
	}

	private HttpHeaders POST(String path, JsonNode obj) {
		return POST(path, obj, 200);
	}
	
	private HttpHeaders POST(String path, JsonNode obj, int expectedStatus) {
		return PATCH_OR_POST(POST, path, obj, expectedStatus);
	}

	private HttpHeaders PATCH(String path, JsonNode obj) {
		return PATCH(path, obj, 200);
	}

	private HttpHeaders PATCH(String path, JsonNode obj, int expectedStatus) {
		return PATCH_OR_POST(PATCH, path, obj, expectedStatus);
	}
	
	private HttpHeaders PATCH_OR_POST(String patchOrPost, String path, JsonNode obj, int expectedStatus) {
		HttpRequest postRequest;
		try {
			Builder postRequestBuilder = HttpRequest
					.newBuilder(new URI(URL + "/" + path));
			postRequestBuilder = postRequestBuilder.method(patchOrPost, BodyPublishers.ofString(obj.toString()));
			postRequest = postRequestBuilder.header("Content-Type", "application/json")
					.build();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		
		HttpResponse<Void> postResponse;
		try {
			postResponse = client
					  .send(postRequest, BodyHandlers.discarding());
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		} 
		
		assertEquals(expectedStatus, postResponse.statusCode());
		
		return postResponse.headers();
	}
	
	
	private ArrayNode GET_ARRAY(String path) {
		return GET_ARRAY(path, 200); 
	}	
	
	private ArrayNode GET_ARRAY(String path, int expectedStatus) {
		JsonNode result = GET(path, expectedStatus);
		assertEquals(true, result.isArray(), "array expected");
		return (ArrayNode) result;
	}
	
	private JsonNode GET(String path) {
		return GET(path, 200); 
	}	
	
	private JsonNode GET(String path, int expectedStatus) {

		HttpRequest getRequest;
		try {
			getRequest = HttpRequest
					.newBuilder(new URI(URL + "/" + path))
					.GET()
					.build();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		
		HttpResponse<String> getResponse;
		try {
			getResponse = client
					  .send(getRequest, BodyHandlers.ofString());
			assertEquals(expectedStatus, getResponse.statusCode());
			
			return mapper.readTree(getResponse.body());
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		} 
		
	}
}
