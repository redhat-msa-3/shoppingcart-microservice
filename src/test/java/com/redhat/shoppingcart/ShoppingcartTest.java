package com.redhat.shoppingcart;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

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
		assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
	}

	@Test
	@RunAsClient
	public void testApiDocs() throws IOException {
		Response response = ClientBuilder.newClient().target(TARGET).path("/api-docs/").request().get();
		assertThat(response.getStatusInfo()).isNotEqualTo(Response.Status.NOT_FOUND.getStatusCode());
	}

}
