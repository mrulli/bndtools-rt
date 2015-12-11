package org.bndtools.rt.caffeine;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.BundleTracker;

public class CaffeineActivator implements BundleActivator {

	private BundleTracker<?> tracker;	
	private LogService log = null;
	
	@Override
	public void start(BundleContext context) throws Exception {
		tracker = new BundleTracker<Object>(context, Bundle.STARTING, null) {
			public Object addingBundle(Bundle bundle, BundleEvent event) {
				
				if(log == null){
					ServiceReference<?> logRef = context.getServiceReference(LogService.class.getName());
					if (logRef != null) 
						log = (LogService) context.getService(logRef);
				}

				if (Constants.ACTIVATION_LAZY.equals(bundle.getHeaders().get(Constants.BUNDLE_ACTIVATIONPOLICY))) {
					try {
						if(log != null) log.log(LogService.LOG_INFO, "Coercing lazy bundle to start...");
						bundle.start();
						if(log != null) log.log(LogService.LOG_INFO, "... " + getBundleInfo(bundle) + " started!");
					} catch (BundleException e) {
						if(log != null) log.log(LogService.LOG_DEBUG, e.getMessage());
					}
					return bundle;
				}
				return null;
			}
		};
		tracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		tracker.close();
	}

	private String getBundleInfo(final Bundle b){
		return b.getSymbolicName() + ";version='" + b.getVersion().toString() + "'";
	}
	
}
