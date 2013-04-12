/* ============================================================
 * ApplicationProperties.java
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
