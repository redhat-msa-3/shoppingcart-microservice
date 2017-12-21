package com.redhat.shoppingcart;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.Assertions.assertThat;

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
	public void testHealthCheck() throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, Object> response = ClientBuilder.newClient().target(TARGET).path("/health").request().get(Map.class);
		assertThat(response.get("outcome")).isEqualTo("UP");
	}

}
