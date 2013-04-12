/* ============================================================
 * EntityManagerStore.java
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
package ca.ualberta.physics.cssdp.dao;

import javax.persistence.EntityManager;

/**
 * A simple interface which allows us to gain access to the EntityManager (via a
 * ThreadLocal) and remove it from that ThreadLocal when the unit of work is
 * done. Typcially, a call to get() will be made at the start of an http request
 * and a call to remove() at the end of an HTTP request.
 */
public interface EntityManagerStore {

	public void remove();

	public EntityManager get();

}
