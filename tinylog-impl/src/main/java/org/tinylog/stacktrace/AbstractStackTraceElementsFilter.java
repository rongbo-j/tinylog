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

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract stack trace filter for filtering stack trace elements by configurable packages and classes.
 */
public abstract class AbstractStackTraceElementsFilter extends AbstractStackTraceFilter {

	/**
	 * @param origin
	 *            Origin source stack trace filter
	 * @param arguments
	 *            Configured packages and classes
	 */
	public AbstractStackTraceElementsFilter(final StackTraceFilter origin, final List<String> arguments) {
		super(origin, arguments);
	}

	@Override
	public List<StackTraceElement> getStackTrace() {
		List<StackTraceElement> currentTrace = super.getStackTrace();
		List<StackTraceElement> newTrace = new ArrayList<StackTraceElement>(currentTrace.size());

		for (StackTraceElement element : currentTrace) {
			if (shouldKept(element.getClassName(), getArguments())) {
				newTrace.add(element);
			}
		}

		return newTrace;
	}

	/**
	 * Tests if the stack trace element of a passed class name should be kept.
	 * 
	 * @param className
	 *            Fully-qualified class name to match
	 * @param filters
	 *            Filters to apply
	 * @return {@code true} if the stack trace element of the passed class name should be kept, {@code false} if not
	 */
	protected abstract boolean shouldKept(String className, List<String> filters);

	/**
	 * Tests if a passed filter matches a given fully-qualified class name.
	 * 
	 * @param className
	 *            Fully-qualified class name to match
	 * @param filter
	 *            Filter to apply
	 * @return {@code true} if the filter matches the given class name, {@code false} if not
	 */
	protected boolean match(final String className, final String filter) {
		return className.startsWith(filter) && (filter.length() == className.length() || className.charAt(filter.length()) == '.');
	}

}
