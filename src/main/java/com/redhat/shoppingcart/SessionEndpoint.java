/**
 * JBoss, Home of Professional Open Source
 * Copyright 2018, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.shoppingcart;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.infinispan.Cache;
import org.wildfly.swarm.spi.runtime.annotations.Post;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@ApplicationScoped
@Path("/session/{sessionId}")
@Api("Shopping cart session management")
public class SessionEndpoint {

	@Inject
	private Cache<String, Set<Object>> cache;

	@GET
	@Path("/clear")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Clears the session")
	public Response clearSession(@PathParam("sessionId") String sessionID) {
		Set<Object> sessionValues = getCache(sessionID);
		sessionValues.clear();
		return Response.ok(sessionValues).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Return the session content")
	public Response getSession(@PathParam("sessionId") String sessionID) {
		Set<Object> sessionValues = cache.get(sessionID);
		return Response.ok(sessionValues).build();
	}

	@Post
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Creates a new item in the session")
	public Response createItem(@PathParam("sessionId") String sessionID, Object value) {
		Set<Object> sessionValues = getCache(sessionID);
		sessionValues.add(value);
		return Response.ok(sessionValues).build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Replaces an item in the session")
	public Response replaceItem(@PathParam("sessionId") String sessionID, Object value) {
		Set<Object> sessionValues = getCache(sessionID);
		if (sessionValues.contains(value)) {
			return createItem(sessionID, value);
		} else {
			return Response.notModified().entity("session doesn't contain " + value).build();
		}
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Delete an item from the session")
	public Response deleteItem(@PathParam("sessionId") String sessionID, Object value) {
		Set<Object> sessionValues = getCache(sessionID);
		if (sessionValues.contains(value)) {
			sessionValues.remove(value);
			return Response.ok(sessionValues).build();
		} else {
			return Response.notModified().entity("session doesn't contain " + value).build();
		}
	}

	private Set<Object> getCache(String sessionId) {
		Set<Object> sessionValues = cache.get(sessionId);
		if (sessionValues == null) {
			sessionValues = new HashSet<>();
			cache.put(sessionId, sessionValues);
		}
		return sessionValues;
	}

}
