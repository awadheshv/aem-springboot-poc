package com.springbootpoc.springbootservices;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.ServletException;

import com.springbootpoc.springbootservices.jersey.JerseyApplication;
import com.springbootpoc.springbootservices.springboot.Application;

import org.glassfish.jersey.servlet.ServletContainer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class Activator implements BundleActivator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);

    private static final String JERSEY_SERVICE_PREFIX = "/jersey";
    private static final String SPRING_SERVICE_PREFIX = "/spring";

    private static final String PROPERTY_CONTEXT_PATH = "context.path";

    private BundleContext bundleContext;
    private HttpService httpService = null;
    private JerseyApplication jerseyApplication;

    ApplicationContext appContext;

    private ServiceTracker tracker;

    @Override
    public void start(BundleContext bundleContext) throws IOException {
        this.bundleContext = bundleContext;
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

        appContext = new SpringApplication(Application.class).run();

        tracker = new ServiceTracker(bundleContext, HttpService.class.getName(), null) {

            @Override
            public Object addingService(ServiceReference serviceRef) {
                httpService = (HttpService) super.addingService(serviceRef);

                Dictionary<String, String> servletParams = new Hashtable<>();
                jerseyApplication = new JerseyApplication(bundleContext);

                try {
                    LOGGER.info("registering jersey at {}", JERSEY_SERVICE_PREFIX);
                    httpService.registerServlet(JERSEY_SERVICE_PREFIX, new ServletContainer(jerseyApplication),
                            servletParams, null);
                    LOGGER.info("deployed jersey");

                    LOGGER.info("registering springboot at {}", SPRING_SERVICE_PREFIX);
                    httpService.registerServlet(SPRING_SERVICE_PREFIX, appContext.getBean(DispatcherServlet.class) ,servletParams, null);

                } catch (ServletException | NamespaceException e) {
                    LOGGER.error("Failed to initialize Jersey servlet", e);
                }

                Dictionary<String, String> props = new Hashtable<>();
                props.put(PROPERTY_CONTEXT_PATH, JERSEY_SERVICE_PREFIX);
                sendEvent("EVENT_JERSEY_DEPLOYED", props);

                return httpService;
            }

            @Override
            public void removedService(ServiceReference serviceReference, Object service) {
                if (httpService == service) {
                    httpService.unregister(JERSEY_SERVICE_PREFIX);
                    httpService.unregister(SPRING_SERVICE_PREFIX);
                    if (jerseyApplication != null) {
                        jerseyApplication.shutdown();
                        jerseyApplication = null;
                    }
                    httpService = null;
                    Dictionary<String, String> props = new Hashtable<>();
                    props.put(PROPERTY_CONTEXT_PATH, JERSEY_SERVICE_PREFIX);
                    sendEvent("EVENT_JERSEY_UNDEPLOYED", props);
                    LOGGER.info("Unregistered Jersey servlet at {}", JERSEY_SERVICE_PREFIX);
                }
                super.removedService(serviceReference, service);
            }
        };

        tracker.open();
    }

    private void sendEvent(String eventId, Dictionary<String, String> properties) {
        ServiceReference serviceReference = bundleContext.getServiceReference(EventAdmin.class.getName());
        if (serviceReference != null) {
            EventAdmin eventAdmin = (EventAdmin) bundleContext.getService(serviceReference);
            eventAdmin.sendEvent(new Event(eventId, properties));
            bundleContext.ungetService(serviceReference);
        }
    }

    @Override
    public void stop(BundleContext bundleContext) {
        SpringApplication.exit(appContext, () -> 0);
    }

}
