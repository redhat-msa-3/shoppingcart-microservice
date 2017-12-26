package com.redhat.shoppingcart;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class ShoppingcartTest {

	private static final String TARGET = "http://localhost:8080";
	private static final String SESSION_ID = "test";

	@Deployment(testable = false)
	public static Archive<?> createDeployment() throws Exception {
		Archive<?> archive = ShrinkWrap.createFromZipFile(WebArchive.class, new File("target/shoppingcart.war"));
		return archive;
	}

	@Test
	@RunAsClient
	public void testCors() throws IOException {
		Response response = ClientBuilder.newClient().target(TARGET).path("/session/1").request().get();
		assertThat(response.getHeaderString("Access-Control-Allow-Origin")).isEqualTo("*");
		assertThat(response.getHeaderString("Access-Control-Allow-Methods"))
				.isEqualTo("GET, POST, PUT, DELETE, OPTIONS");
	}

	@Test
	@RunAsClient
	public void testHealthCheck() throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, Object> response = ClientBuilder.newClient().target(TARGET).path("/health").request()
				.get(Map.class);
		assertThat(response.get("outcome")).isEqualTo("UP");
	}

	@Test
	@RunAsClient
	public void testSwaggerJson() throws IOException {
		Response response = ClientBuilder.newClient().target(TARGET).path("/swagger.json").request().get();
		assertThat(response.getStatusInfo()).isNotEqualTo(Response.Status.NOT_FOUND.getStatusCode());
	}

	@Test
	@RunAsClient
	public void testRedirectToApiDocs() throws IOException {
		Response response = ClientBuilder.newClient().target(TARGET).path("/").request().get();
		assertThat(response.getStatusInfo()).isEqualTo(Response.Status.TEMPORARY_REDIRECT);
	}

	@Test
	@RunAsClient
	public void testApiDocs() throws IOException {
		Response response = ClientBuilder.newClient().target(TARGET).path("/api-docs/").request().get();
		assertThat(response.getStatusInfo()).isNotEqualTo(Response.Status.NOT_FOUND.getStatusCode());
	}

	@Test
	@RunAsClient
	@SuppressWarnings({ "unchecked" })
	public void testPostGet() throws IOException {
		String valueAsString = "valueAsString";
		Date valueAsDate = new Date();
		int intValue = 100;
		TestClass testClass = new TestClass(intValue, valueAsString, valueAsDate);
		Builder clientBuilder = ClientBuilder.newClient().target(TARGET).path("/session/" + SESSION_ID)
				.request(MediaType.APPLICATION_JSON);
		// Test String entity
		List<Object> postResponse = clientBuilder.post(Entity.text(valueAsString), List.class);
		System.out.println(postResponse);
		assertThat(postResponse.get(0)).isEqualTo(valueAsString);
		testClearSession();
		// Test Number entity
		postResponse = clientBuilder.post(Entity.text(intValue), List.class);
		System.out.println(postResponse);
		assertThat(postResponse.get(0)).isEqualTo(String.valueOf(intValue));
		testClearSession();
		// Test Date entity
		String date = new SimpleDateFormat("dd/MM/yyyy").format(valueAsDate);
		postResponse = clientBuilder.post(Entity.text(date), List.class);
		System.out.println(postResponse);
		assertThat(postResponse.get(0)).isEqualTo(date);
		testClearSession();
		// Test JSON entity
		postResponse = clientBuilder.post(Entity.json(testClass), List.class);
		System.out.println(postResponse);
		assertThat(postResponse.size()).isEqualTo(1);
		testClearSession();
	}

	@Test
	@RunAsClient
	@SuppressWarnings("unchecked")
	public void testDelete() {
		String valueAsString = "valueAsString";
		int intValue = 100;
		TestClass testClass = new TestClass(intValue, valueAsString, new Date());
		Builder clientBuilder = ClientBuilder.newClient().target(TARGET).path("/session/" + SESSION_ID)
				.request(MediaType.APPLICATION_JSON);
		// Test String entity
		List<Object> postResponse = clientBuilder.post(Entity.text(valueAsString), List.class);
		System.out.println(postResponse);
		assertThat(postResponse.get(0)).isEqualTo(valueAsString);
		postResponse = clientBuilder.build("DELETE", Entity.text(valueAsString)).invoke(List.class);
		assertThat(postResponse.size()).isEqualTo(0);
		// Test Integer entity
		postResponse = clientBuilder.post(Entity.text(intValue), List.class);
		System.out.println(postResponse);
		assertThat(postResponse.get(0)).isEqualTo(String.valueOf(intValue));
		postResponse = clientBuilder.build("DELETE", Entity.text(intValue)).invoke(List.class);
		assertThat(postResponse.size()).isEqualTo(0);
		// Test Class entity
		postResponse = clientBuilder.post(Entity.json(testClass), List.class);
		System.out.println(postResponse);
		assertThat(postResponse.size()).isEqualTo(1);
		postResponse = clientBuilder.build("DELETE", Entity.json(testClass)).invoke(List.class);
		assertThat(postResponse.size()).isEqualTo(0);
		// Test non-existent value
		postResponse = clientBuilder.post(Entity.text(intValue), List.class);
		System.out.println(postResponse);
		assertThat(postResponse.get(0)).isEqualTo(String.valueOf(valueAsString));
		Response nonExitingResponse = clientBuilder.build("DELETE", Entity.text("anotherValue")).invoke();
		assertThat(nonExitingResponse.getStatus()).isEqualTo(Status.NOT_MODIFIED.getStatusCode());
	}

	@Test
	@RunAsClient
	public void testClearSession() {
		Response response = ClientBuilder.newClient().target(TARGET).path("/session/" + SESSION_ID + "/clear").request()
				.get();
		assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
	}

	@XmlRootElement
	@SuppressWarnings("unused")
	private class TestClass {

		private int id;
		private String name;
		private Date date;

		public TestClass(int id, String name, Date date) {
			super();
			this.id = id;
			this.name = name;
			this.date = date;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public Date getDate() {
			return date;
		}

	}

}
