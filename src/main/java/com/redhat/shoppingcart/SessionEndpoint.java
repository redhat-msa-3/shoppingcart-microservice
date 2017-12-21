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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.infinispan.Cache;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@ApplicationScoped
@Path("/session/{sessionId}")
@Api(value = "/session")
public class SessionEndpoint {
	
	@Inject
	private Cache<String, Set<Object>> cache;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Return the session content")
	public Response getSession(@PathParam("sessionId") String sessionID) {
		Set<Object> values = new HashSet<>();
		values.add("Teste");
		values.add("Teste 1");
		values.add("Teste 2");
		cache.put(sessionID, values);
		Set<Object> sessionValues = cache.get(sessionID);
		if (sessionValues == null){
			return Response.status(Response.Status.NOT_FOUND).build();	
		}else{
			return Response.ok(sessionValues).build();
		}
	}

}
