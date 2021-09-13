package com.springbootpoc.springbootservices.jersey;


import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.google.common.base.Throwables;
import com.springbootpoc.springbootservices.OsgiUtils;

public class JerseyApplication extends ResourceConfig {
    private ResourceResolver resolver;

    public JerseyApplication(final BundleContext bundleContext) {

        registerClasses(DefaultResource.class);

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                // bind the ResourceResolverFactory service
                // ResourceResolverFactory resourceResolverFactory = OsgiUtils.getOSGIService(bundleContext, ResourceResolverFactory.class);
                // bind(resourceResolverFactory).to(ResourceResolverFactory.class);

                // try {
                //     resolver = resourceResolverFactory.getServiceResourceResolver(null);
                //     bind(resolver).to(ResourceResolver.class);
                // } catch (LoginException e) {
                //     Throwables.propagate(e);
                // }

                bindFactory(getOSGIServiceFactory(DefaultResource.class)).to(DefaultResource.class);

            }
        });
    }

    public void shutdown() {
        resolver.close();
    }

    private <T> Factory<T> getOSGIServiceFactory(final Class<T> clazz) {
        return new Factory<T>() {
            private T service;

            @Override
            public T provide() {
                if (service == null) {
                    service = OsgiUtils.getOSGIService(clazz);
                }
                return service;
            }

            @Override
            public void dispose(T instance) {
                // Empty for now
            }
        };
    }

}
