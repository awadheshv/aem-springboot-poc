package com.springbootpoc.springbootservices;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class OsgiUtils {
    private OsgiUtils() {

    }

    @SuppressWarnings("unchecked")
    public static <T> T getOSGIService(Class<T> clazz) {
        BundleContext bundleContext = FrameworkUtil.getBundle(OsgiUtils.class).getBundleContext();
        ServiceReference reference = bundleContext.getServiceReference(clazz.getName());
        if (reference != null) {
            return (T) bundleContext.getService(reference);
        }
        return null;
    }

}
