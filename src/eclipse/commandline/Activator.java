package eclipse.commandline;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	private static Bundle bundle;
	
	 public static Bundle getMyBundle() {
	      return bundle;
	  }
	
    // The plug-in ID
    public static final String PLUGIN_ID = "eclipse.commandline";

    // The shared instance
    private static Activator plugin;

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * The constructor
     */
    public Activator() {
    }

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        bundle = context.getBundle();
        plugin = this;
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }
}
