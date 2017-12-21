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

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.infinispan.Cache;
import org.wildfly.swarm.health.Health;
import org.wildfly.swarm.health.HealthStatus;

@ApplicationScoped
@Path("/app")
public class HealthEndpoint {
	
	@Inject
	private Cache<String, Set<Object>> cache;
	

	@GET
	@Health
	@Path("/cache")
	public HealthStatus getCacheStatus() {
		String cacheName = "cache";
		if (cache.getCacheManager().getStatus().allowInvocations()){
			return HealthStatus.named(cacheName).up();
		}else{
			return HealthStatus.named(cacheName).down();
		}
	}

}
