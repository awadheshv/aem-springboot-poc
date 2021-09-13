package com.springbootpoc.springbootservices.jersey;

import java.util.Calendar;
import java.util.LinkedHashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.springbootpoc.springbootservices.OsgiUtils;

import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/default")
public class DefaultResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultResource.class);
    private static Gson gson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();
    private SlingSettingsService settingsService;

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public String info() {
        if (settingsService == null) {
            LOGGER.info(" injecting settingsService.");
            settingsService = OsgiUtils.getOSGIService(SlingSettingsService.class);
        }

        LinkedHashMap<String, Object> info = new LinkedHashMap<>();
        info.put("java.version", System.getProperty("java.version"));
        info.put("timestamp", Calendar.getInstance().getTime().toString());

        if (settingsService == null) {
            info.put("error", "settingsService is null.");
        } else {
            info.put("slingId", settingsService.getSlingId());
            info.put("runModes", settingsService.getRunModes().toString());
        }
        return gson.toJson(info);
    }
}
