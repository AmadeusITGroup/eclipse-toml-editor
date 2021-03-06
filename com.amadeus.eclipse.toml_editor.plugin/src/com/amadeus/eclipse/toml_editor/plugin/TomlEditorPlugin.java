package com.amadeus.eclipse.toml_editor.plugin;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author Zbigniew KACPRZAK
 */
public class TomlEditorPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "com.amadeus.eclipse.toml_editor.plugin"; //$NON-NLS-1$

    // The shared instance
    private static TomlEditorPlugin plugin;
    
    /**
     * The constructor
     */
    public TomlEditorPlugin() {
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static TomlEditorPlugin getDefault() {
        return plugin;
    }

    protected void initializeImageRegistry(ImageRegistry registry) {
        String[] images = {
            "icons/outl_section.gif",
            "icons/outl_key.gif",
            "icons/parent.gif",
            "icons/toml.png",
            "icons/folder.gif",
            "icons/folder_blue.gif",
            "icons/plugin_dep.gif",
            "icons/tree.gif",
            "icons/outl_plus.gif",
            "icons/outl_minus.gif",
            "icons/outl_expand.gif",
            "icons/outl_up.gif",
            "icons/outl_down.gif",
            "icons/outl_refresh.gif",
        };
        for (String imageID : images) {
            IPath path = new Path(imageID);
            URL url = FileLocator.find(getBundle(), path, null);
            ImageDescriptor desc = ImageDescriptor.createFromURL(url);
            registry.put(imageID, desc);
        }
     }

	public static IEclipsePreferences getPreferency() {
		return InstanceScope.INSTANCE.getNode(TomlEditorPlugin.PLUGIN_ID);
	}

}
