/* ============================================================
 * ComponentProperties.java
 * ============================================================
 * Copyright 2013 University of Alberta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================ 
 */
package ca.ualberta.physics.cssdp.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Throwables;
import com.google.common.io.Files;

/**
 * A component (i.e., a project) may have a specific set of reasonable default
 * properties.
 * 
 * A concrete ComponentProperties needs to create the method (this example
 * assumes "Core" is the component):
 * 
 * <code>
 * public static Core properties() {
 *     return (Core) ComponentProperties.properties(Core.class);
 * }
 * </code>
 * 
 * This allows the users of component properties to simply type:
 * Core.properties().getString("somekey") to retrieve the value of the property.
 * 
 * A ComponentProperty class must have a properties file that lives next to the
 * class with the same name as the class (but all lowercase). For example, the
 * class Core will have the file core.properties existing beside it.
 * 
 * The properties listed internally to core.properties need not specify the
 * "core" prefix. For example, the property test would be identified as "test",
 * and not "core.test" within the file.
 * 
 * Provided by Roddi Potter from previous work based from Mark Gordon
 */
public abstract class ComponentProperties {

	// a map of all components initialized.
	private static Map<String, ComponentProperties> INSTANCES = new ConcurrentHashMap<String, ComponentProperties>();

	// each component has a set of properties defined
	private Properties properties;

	// each component also has a name (the simple name of the class in lower
	// case)
	private String componentName;

	// each component also has a concrete class (for example, Core)
	private Class<?> implementingClass;

	/**
	 * Initializes (if not already) this component property class given. Returns
	 * the initialized properties.
	 * 
	 * @param componentPropertiesClass
	 * @return ComponentProperties containing the properties.
	 */
	protected static ComponentProperties properties(
			Class<?> componentPropertiesClass) {
		String componentName = getComponentNameFromClass(componentPropertiesClass);
		if (INSTANCES.get(componentName) == null) {
			return init(componentPropertiesClass);
		}
		return INSTANCES.get(componentName);
	}

	/**
	 * Convert the concrete component class name into a component property name
	 * 
	 * @param componentPropertiesClass
	 * @return String the name of the component for this concrete class.
	 */
	private static String getComponentNameFromClass(
			Class<?> componentPropertiesClass) {
		return componentPropertiesClass.getSimpleName().toLowerCase();
	}

	/**
	 * Get the ComponentProperties if all you have is the component name. Init
	 * must be called prior to calling this method.
	 * 
	 * @param componentName
	 * @return ComponentProperties
	 * @throws RuntimeException
	 *             if the component has not been initialized with the classname
	 *             yet.
	 */
	protected static ComponentProperties properties(String componentName) {
		if (INSTANCES.get(componentName) == null) {
			throw new RuntimeException("Component '" + componentName
					+ "'must be initialized before acquiring by name");
		}
		return INSTANCES.get(componentName);
	}

	/**
	 * Loads the component properties from the accompanying property file.
	 * 
	 * @param componentPropertiesClass
	 * @return ComponentProperties initialized from the accompanying property
	 *         file.
	 * @throws RuntimeException
	 *             for null class given, property file not found (or can't be
	 *             read), or can't instantiate the concrete component properties
	 *             class
	 */
	protected static ComponentProperties init(Class<?> componentPropertiesClass) {

		if (componentPropertiesClass == null) {
			throw new RuntimeException(
					"Can't load properties without a component property class to load from");
		}

		String componentName = componentPropertiesClass.getSimpleName()
				.toLowerCase();
		String propertyFileName = componentName + ".properties";

		ComponentProperties componentProperties = null;

		if (INSTANCES.get(componentName) == null) {

			Properties props = new Properties();
			try {
				InputStream resourceAsStream = componentPropertiesClass
						.getResourceAsStream(propertyFileName);
				if (resourceAsStream == null) {
					throw new RuntimeException("No " + propertyFileName
							+ " to load");
				}
				props.load(resourceAsStream);
			} catch (IOException e) {
				throw new RuntimeException("Can't find " + propertyFileName
						+ " near " + componentPropertiesClass.getName(), e);
			}

			try {
				componentProperties = (ComponentProperties) componentPropertiesClass
						.newInstance();
				componentProperties.properties = props;
				componentProperties.componentName = componentName;
				componentProperties.implementingClass = componentPropertiesClass;
				INSTANCES.put(componentName, componentProperties);
			} catch (Exception e) {
				throw new RuntimeException(
						"Could not instantiantiate component property class "
								+ componentPropertiesClass.getName(), e);
			}

		}

		return componentProperties;
	}

	/**
	 * @param key
	 * @return String a string type property from this component
	 * @throws RuntimeException
	 *             the key can't be found, the component hasn't been initialized
	 */
	public String getString(String key) {
		return extractComponentProperty(key);
	}

	/**
	 * @param key
	 * @return int an int type property from this component
	 * @throws RuntimeException
	 *             the key can't be found, the component hasn't been initialized
	 */
	public int getInt(String key) {
		return Integer.valueOf(extractComponentProperty(key)).intValue();
	}

	/**
	 * @param key
	 * @return boolean a boolean type property from this component
	 * @throws RuntimeException
	 *             the key can't be found, the component hasn't been initialized
	 */
	public boolean getBoolean(String key) {
		return Boolean.valueOf(extractComponentProperty(key)).booleanValue();
	}

	/**
	 * @param key
	 * @return float a float type property from this component
	 * @throws RuntimeException
	 *             the key can't be found, the component hasn't been initialized
	 */
	public float getFloat(String key) {
		return Float.valueOf(extractComponentProperty(key)).floatValue();
	}

	/**
	 * @param key
	 * @return long a long type property from this component
	 * @throws RuntimeException
	 *             the key can't be found, the component hasn't been initialized
	 */
	public long getLong(String key) {
		return Long.valueOf(extractComponentProperty(key)).longValue();
	}

	/**
	 * Prints a nicely formatted listing of this component properties that are
	 * currently loaded.
	 */
	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		for (String component : ComponentProperties.INSTANCES.keySet()) {
			ComponentProperties componentProperties = ComponentProperties.INSTANCES
					.get(component);
			sb.append("Component Properties loaded for "
					+ componentProperties.componentName + ".properties ---->\n");
			for (Object key : componentProperties.properties.keySet()) {
				sb.append("\t"
						+ key
						+ " = "
						+ componentProperties.properties
								.getProperty((String) key) + "\n");
			}
		}
		return sb.toString();

	};

	/**
	 * Extracts the property from the properties with the given key.
	 * 
	 * @param key
	 * @return String the value of the property in string form
	 * @throws RuntimeException
	 *             if the component isn't initialized, or the property can not
	 *             be found
	 */
	private String extractComponentProperty(String key) {
		String componentName = getComponentNameFromClass(getClass());
		ComponentProperties componentProperties = INSTANCES.get(componentName);

		if (componentProperties == null) {
			throw new RuntimeException(
					"Call init() first, no component properties loaded for "
							+ componentName);
		}

		String propertyValue = componentProperties.properties.getProperty(key);
		if (propertyValue == null) {
			throw new RuntimeException("no property defined for component ["
					+ componentName + "] with key [" + key + "]");
		}

		return propertyValue;
	}

	// purposely set to package protected so that only ApplicationProperties can
	// call this.
	/**
	 * Override the default value of a property for the given key
	 * 
	 * @param key
	 *            the key to override
	 * @param value
	 *            the value to override the key with
	 * @throws RuntimeException
	 *             if the key can't be found in this component's properties
	 */
	void override(String key, String value) {

		if (!properties.containsKey(key)) {
			throw new RuntimeException(
					"Can only override properties defined by component, " + key
							+ " is not defined");
		} else {
			properties.setProperty(key, value);
		}
	}

	/**
	 * Reload the components default values.
	 * 
	 * @param componentName
	 */
	public static void reinitialize(String componentName) {

		ComponentProperties componentProperties = INSTANCES.get(componentName);
		Class<?> componentClass = componentProperties.implementingClass;
		INSTANCES.remove(componentName);
		init(componentClass);

	}

	/**
	 * get all the properties that start with this prefix. For example, if I
	 * have hibernate.connection.url, hibernate.connection.driver a call to
	 * getSet("hibernate") will return a Properties with these two items in it.
	 * 
	 * @param prefix
	 * @return
	 */
	public Properties getSet(String prefix) {

		String componentName = getComponentNameFromClass(getClass());
		ComponentProperties componentProperties = INSTANCES.get(componentName);

		if (componentProperties == null) {
			throw new RuntimeException(
					"Call init() first, no component properties loaded for "
							+ componentName);
		}

		Properties componentPropertiesSubset = new Properties();

		for (String key : componentProperties.properties.stringPropertyNames()) {
			if (key.startsWith(prefix)) {
				componentPropertiesSubset.setProperty(key,
						componentProperties.properties.getProperty(key));
			}
		}

		return componentPropertiesSubset;
	}

	/**
	 * Like getSet(String prefix) but returns a map of key,value, filtering on
	 * the prefix given
	 * 
	 * @param prefix
	 * @return
	 */
	public Map<String, Object> getMap(String prefix) {
		Map<String, Object> propsAsMap = new HashMap<String, Object>();
		Properties props = getSet(prefix);
		for (Object key : props.keySet()) {
			propsAsMap.put(key.toString(), props.get(key));
		}
		return propsAsMap;
	}

	public static void printAll() {

		Set<String> components = INSTANCES.keySet();
		for (String componentName : components) {
			ComponentProperties properties = ComponentProperties
					.properties(componentName);
			System.out.println("Properties for component: " + componentName);
			Set<String> stringPropertyNames = properties.properties
					.stringPropertyNames();
			List<String> keys = new ArrayList<String>(stringPropertyNames);
			Collections.sort(keys);
			for (String key : keys) {
				if (key.contains("pass")) {
					System.out
							.println("..... "
									+ key
									+ "=*********** (password filtered from casual observation)");
				} else {
					System.out.println("..... " + key + "="
							+ properties.getString(key));
				}
			}
		}

	}

	public static void dumpToFile(File file) {
		// clear file.
		try {
			Files.write("".getBytes(), file);
		} catch (IOException e) {
			Throwables.propagate(e);
		}
		Set<String> components = INSTANCES.keySet();
		for (String componentName : components) {
			ComponentProperties properties = ComponentProperties
					.properties(componentName);
			Set<String> stringPropertyNames = properties.properties
					.stringPropertyNames();
			List<String> keys = new ArrayList<String>(stringPropertyNames);
			Collections.sort(keys);
			for (String key : keys) {
				try {
					if (key.contains("pass")) {
						Files.append(componentName + "." + key + "=filtered\n",
								file, Charset.forName("UTF-8"));
					} else {
						Files.append(componentName + "." + key + "="
								+ properties.getString(key) + "\n", file,
								Charset.forName("UTF-8"));
					}
				} catch (IOException e) {
					Throwables.propagate(e);
				}
			}
		}

	}

	public static Properties dump() {
		
		Properties allProps = new Properties();
		
		Set<String> components = INSTANCES.keySet();
		for (String componentName : components) {
			ComponentProperties properties = ComponentProperties
					.properties(componentName);
			Set<String> stringPropertyNames = properties.properties
					.stringPropertyNames();
			List<String> keys = new ArrayList<String>(stringPropertyNames);
			Collections.sort(keys);
			for (String key : keys) {
				allProps.setProperty(componentName + "." + key, properties.getString(key));
			}
		}

		return allProps;
	}

}
