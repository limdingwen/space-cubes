package com.github.limdingwen.SpaceCubes.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.github.limdingwen.SpaceCubes.Debug;
import com.github.limdingwen.SpaceCubes.SystemNativesHelper;

public class PluginEngine {
	static ArrayList<SpaceCubesPlugin> plugins = new ArrayList<SpaceCubesPlugin>();
	
	public static void loadPlugins() {
		// Get plugins folder
		
		File pluginsFolder = new File(SystemNativesHelper.defaultDirectory() + "/plugins");
		
		if (!pluginsFolder.exists()) {
			pluginsFolder.mkdirs();
		}
		
		// Get plugins from plugins folder
		
		File[] pluginFolderFindings = pluginsFolder.listFiles();
		ArrayList<File> pluginJars = new ArrayList<File>();
		
		for (int i = 0; i < pluginFolderFindings.length; i++) {
			if (checkJarExtension(pluginFolderFindings[i])) {
				pluginJars.add(pluginFolderFindings[i]);
			}
		}
		
		// Load the plugins (search for plugin.txt in the .jar)
		
		Debug.info("Loading plugins...");

		for (int i = 0; i < pluginJars.size(); i++) {
			Debug.info("Loading " + pluginJars.get(i).getName());
			
			JarFile plugin = null;
			SpaceCubesPlugin pluginObject = null;
			
			try {
				 plugin = new JarFile(pluginJars.get(i).getAbsolutePath());
			} catch (IOException e) {
				Debug.error("Not a real jar file! Skipping.");
				continue;
			}
			
			try {
				pluginObject = loadPlugin(plugin, pluginJars.get(i));
				
				plugins.add(pluginObject);
			} catch (ClassNotFoundException e) {
				Debug.error("Main class not found! Reason: " + e.getMessage());
				
				e.printStackTrace();
				
				continue;
			}			
		}
		
		// Enable plugins
		
		for (int i = 0; i < plugins.size(); i++) {
			SpaceCubesPlugin plugin = plugins.get(i);
			
			// Call plugin enable
			
			plugin.onEnable();
			
			Debug.info("Enabled plugin " + plugin.name);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static SpaceCubesPlugin loadPlugin(JarFile jar, File rawJar) throws ClassNotFoundException {
		Enumeration<JarEntry> contents = jar.entries();
		Class<? extends SpaceCubesPlugin> mainClass = null;
		String mainClassName = null;
		
		ArrayList<JarEntry> cons = new ArrayList<JarEntry>();
		
		while (contents.hasMoreElements()) {
			cons.add(contents.nextElement());
		}
		
		for (int i = 0; i < cons.size(); i++) {
			// Search for main.txt
			
			JarEntry entry = cons.get(i);
			
			if (entry.getName().equals("main.txt")) {
				try {					
					InputStream stream = jar.getInputStream(entry);
					BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
					
					mainClassName = reader.readLine();
					
				} catch (IOException e) {
					Debug.error("Error getting main.txt!");
				}
			}
		}
		
		if (mainClassName == null)
			throw new ClassNotFoundException("main.txt not found.");
		
		for (int i = 0; i < cons.size(); i++) {
			// Search for main class
			
			if (mainClassName.equals(cons.get(i).getName().replaceAll("/", ".").replaceAll(".class", ""))) {
				try {
					URLClassLoader loader = new URLClassLoader(new URL[] {rawJar.toURI().toURL()});
					mainClass = (Class<SpaceCubesPlugin>) loader.loadClass(mainClassName);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		
		SpaceCubesPlugin plugin = null;
		
		if (mainClass == null)
			throw new ClassNotFoundException("Class not found!");
		
		try {
			Constructor<SpaceCubesPlugin>[] ctors = (Constructor<SpaceCubesPlugin>[]) mainClass.getDeclaredConstructors();
			Constructor<SpaceCubesPlugin> ctor = null;
			for (int i = 0; i < ctors.length; i++) {
			    ctor = ctors[i];
			    if (ctor.getGenericParameterTypes().length == 0)
				break;
			}
						
			plugin = (SpaceCubesPlugin) ctor.newInstance(new Object[] {});
		} catch (InstantiationException e) {
			Debug.error("Cannot init class");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Debug.error("Cannot access class");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		if (plugin == null) {
			throw new ClassNotFoundException("Class is not inited.");
		}
		
		return plugin;
	}
	
	public static boolean checkJarExtension(File file) {
		return file.getName().toLowerCase().endsWith(".jar");
	}
	
	public static void disablePlugins() {
		for (int i = 0; i < plugins.size(); i++) {
			SpaceCubesPlugin plugin = plugins.get(i);
			
			// Call disable method
			
			plugin.onDisable();
			
			Debug.info("Disabled plugin " + plugin.name);
		}
	}
}
