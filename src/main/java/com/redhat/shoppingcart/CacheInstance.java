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
import javax.enterprise.inject.Produces;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

@ApplicationScoped
public class CacheInstance {

	private static Cache<String, Set<String>> INSTANCE;

	@Produces
	@ApplicationScoped
	public static synchronized Cache<String, Set<String>> getCache() {
		if (INSTANCE == null) {
			GlobalConfiguration gc = GlobalConfigurationBuilder.defaultClusteredBuilder()
					// Use this line for testing in Kubernetes. But it requires
					// additional configuration:
					// oc policy add-role-to-user view
					// system:serviceaccount:$(oc project -q):default -n $(oc
					// project -q)
					// And setting KUBERNETES_NAMESPACE env variable to
					// your namespace
					.transport().defaultTransport()
					.addProperty("configurationFile", "/default-configs/default-jgroups-kubernetes.xml")

					// Or use, multicast stack to simplify local testing:
					// .transport().defaultTransport().addProperty("configurationFile",
					// "default-configs/default-jgroups-udp.xml")
					.build();
			// And here are per-cache configuration, e.g. eviction, replication
			// scheme etc.
			ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
			configurationBuilder.clustering().cacheMode(CacheMode.REPL_ASYNC);

			DefaultCacheManager manager = new DefaultCacheManager(gc, configurationBuilder.build());
			INSTANCE = manager.getCache();
		}
		return INSTANCE;
	}

}