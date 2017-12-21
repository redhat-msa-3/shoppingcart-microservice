package com.redhat.shoppingcart;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
	public void testMyComponent() throws IOException {
		List<String> response = ClientBuilder.newClient().target(TARGET).path("/session/1").request().get(List.class);
		System.out.println(response);
		//assertThat(response.getEntity().).isEqualTo("ECHO:");
	}

}
