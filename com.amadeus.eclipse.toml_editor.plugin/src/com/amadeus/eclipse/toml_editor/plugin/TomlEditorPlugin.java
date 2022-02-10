package com.amadeus.eclipse.toml_editor.plugin;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
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
        };
        for (String imageID : images) {
            IPath path = new Path(imageID);
            URL url = FileLocator.find(getBundle(), path, null);
            ImageDescriptor desc = ImageDescriptor.createFromURL(url);
            registry.put(imageID, desc);
        }
     }
}
