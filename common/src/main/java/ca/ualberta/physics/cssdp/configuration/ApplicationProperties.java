package ca.ualberta.physics.cssdp.configuration;

import java.util.Properties;
import java.util.Stack;
import java.util.Map.Entry;

/**
 * Allows for the overriding of ComponentProperties by referencing the component
 * property by component name dot key = value. For example, if you wish to
 * override "core" component's "test.property", in another Properties map define
 * the value "core.test.property" and give it the new value and then call
 * overrideDefaults(Properties).
 * 
 * Provided by Roddi Potter from previous work based from Mark Gordon
 * 
 */
public class ApplicationProperties {

	private static Stack<String> overriddenComponents = new Stack<String>();

	/**
	 * Override any(all) ComponentProperties default values with the given set
	 * of properties. If the component or key is not valid (i.e., the component
	 * does not have a defined default property for the override) an exception
	 * should be expected. All overrides must have a component property defined.
	 * 
	 * @param overrides
	 */
	public static void overrideDefaults(Properties overrides) {

		for (Entry<Object, Object> property : overrides.entrySet()) {

			String fullKey = (String) property.getKey();
			String value = (String) property.getValue();

			String componentName = fullKey.substring(0, fullKey.indexOf("."));

			ComponentProperties properties = ComponentProperties
					.properties(componentName);

			String key = fullKey.substring(fullKey.lastIndexOf(componentName + ".")
					+ (componentName + ".").length());

			properties.override(key, value);

			if (!overriddenComponents.contains(componentName)) {
				overriddenComponents.push(componentName);
			}
			
		}

		ComponentProperties.printAll();
		
	}

	/**
	 * Drop all overrides.  Useful for tests.
	 */
	public static void dropOverrides() {
		for (String componentName : overriddenComponents) {
			ComponentProperties.reinitialize(componentName);
		}
	}

}
