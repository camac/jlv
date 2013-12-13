package com.github.rd.jlv;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.github.rd.jlv.ui.preferences.PreferenceManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class JlvActivator extends AbstractUIPlugin {

//	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String LOG4J_PROPERTIES_PATH = "config/log4j.properties";

	private static JlvActivator plugin;

	private static PreferenceManager preferenceManager;

	private static Map<ImageType, Image> images = new HashMap<>();

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		preferenceManager = new PreferenceManager(getPreferenceStore());

		Properties fileProperties = new Properties();
		fileProperties.load(new FileInputStream(getAbsolutePath(LOG4J_PROPERTIES_PATH)));
		String logLocation = plugin.getStateLocation().toString();
		fileProperties.put("log.dir", logLocation);
		PropertyConfigurator.configure(fileProperties);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		releaseResources();
		super.stop(context);
	}

	public static JlvActivator getDefault() {
		return plugin;
	}

	public static PreferenceManager getPreferenceManager() {
		return preferenceManager;
	}

	public static String getAbsolutePath(String filePath) throws IOException {
		URL confUrl = getDefault().getBundle().getEntry(filePath);
		return FileLocator.toFileURL(confUrl).getFile();
	}

	public static Image getImage(ImageType imageType) {
		if (!images.containsKey(imageType)) {
			images.put(imageType, getImageDescriptor(imageType.getPath()).createImage());
		}
		return images.get(imageType);
	}

	private static ImageDescriptor getImageDescriptor(String relativePath) {
		return imageDescriptorFromPlugin(StringConstants.JLV_PLUGIN_ID, relativePath);
	}

	private void releaseResources() {
		preferenceManager.dispose();

		for (Image image : images.values()) {
			image.dispose();
		}
	}
}
