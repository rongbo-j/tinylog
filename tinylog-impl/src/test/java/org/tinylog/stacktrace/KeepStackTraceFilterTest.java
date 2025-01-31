/*
 * Copyright 2019 Martin Winandy
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.tinylog.stacktrace;

import java.util.Arrays;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KeepStackTraceFilter}.
 */
public final class KeepStackTraceFilterTest {

	/**
	 * Verifies that the class name of the source throwable will be looped through.
	 */
	@Test
	public void keepClassName() {
		RuntimeException exception = new RuntimeException();

		KeepStackTraceFilter filter = create(exception, "");
		assertThat(filter.getClassName()).isEqualTo(RuntimeException.class.getName());
	}

	/**
	 * Verifies that the message of the source throwable will be looped through.
	 */
	@Test
	public void keepMessage() {
		RuntimeException exception = new RuntimeException("Hello World!");

		KeepStackTraceFilter filter = create(exception, "");
		assertThat(filter.getMessage()).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that a null cause of the source throwable will be looped through.
	 */
	@Test
	public void loopTroughNullCause() {
		RuntimeException exception = new RuntimeException("Hello World!");

		KeepStackTraceFilter parentFilter = create(exception, "");
		KeepStackTraceFilter childFilter = parentFilter.getCause();

		assertThat(childFilter).isNull();
	}

	/**
	 * Verifies that an existing cause of the source throwable will be looped through.
	 */
	@Test
	public void keepExistingCause() {
		RuntimeException exception = new RuntimeException("Hello Heaven!", new NullPointerException("Hello Hell!"));

		KeepStackTraceFilter parentFilter = create(exception, "");
		KeepStackTraceFilter childFilter = parentFilter.getCause();

		assertThat(childFilter).isNotNull();
		assertThat(childFilter.getClassName()).isEqualTo(NullPointerException.class.getName());
		assertThat(childFilter.getMessage()).isEqualTo("Hello Hell!");
	}
	
	/**
	 * Verifies that all stack trace elements will be removed, if list of packages and classes is an empty string.
	 */
	@Test
	public void empty() {
		RuntimeException exception = new RuntimeException();
		
		KeepStackTraceFilter filter = create(exception);
		assertThat(filter.getStackTrace()).isEmpty();
	}

	/**
	 * Verifies that a single package can be kept on stack trace.
	 */
	@Test
	public void singlePackage() {
		RuntimeException exception = new RuntimeException();
		
		KeepStackTraceFilter filter = create(exception, "org.tinylog");
		assertThat(filter.getStackTrace()).hasSize(1);
	}

	/**
	 * Verifies that incomplete package names will be remove all elements from stack trace.
	 */
	@Test
	public void incompletePackage() {
		RuntimeException exception = new RuntimeException();
		
		KeepStackTraceFilter filter = create(exception, "o");
		assertThat(filter.getStackTrace()).isEmpty();
	}

	/**
	 * Verifies that a multiple packages can be kept on stack trace.
	 */
	@Test
	public void multiplePackages() {
		RuntimeException exception = new RuntimeException();
		int elements = exception.getStackTrace().length;
	
		KeepStackTraceFilter filter = create(exception, "com", "java", "javax", "jdk", "org", "sun");
		assertThat(filter.getStackTrace()).hasSize(elements);
	}

	/**
	 * Verifies that a single class can be kept on stack trace.
	 */
	@Test
	public void singleClass() {
		RuntimeException exception = new RuntimeException();
		
		KeepStackTraceFilter filter = create(exception, KeepStackTraceFilterTest.class.getName());
		assertThat(filter.getStackTrace()).hasSize(1);
	}

	/**
	 * Creates a new {@link KeepStackTraceFilter} instance.
	 * 
	 * @param throwable
	 *            Source throwable to pass
	 * @param packagesAndClasses
	 *            Packages and classes to pass
	 * @return Created instance
	 */
	private KeepStackTraceFilter create(final Throwable throwable, final String... packagesAndClasses) {
		StackTraceFilterAdapter origin = new StackTraceFilterAdapter(throwable);
		return new KeepStackTraceFilter(origin, Arrays.asList(packagesAndClasses));
	}

}
