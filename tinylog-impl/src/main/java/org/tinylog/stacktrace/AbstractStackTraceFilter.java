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

import java.util.List;

/**
 * Abstract stack trace filter that just loops trough all data of the passed origin stack trace filter.
 */
public abstract class AbstractStackTraceFilter implements StackTraceFilter {

	private final StackTraceFilter origin;
	private final List<String> arguments;

	/**
	 * @param origin
	 *            Origin source stack trace filter
	 * @param arguments
	 *            Configured arguments
	 */
	public AbstractStackTraceFilter(final StackTraceFilter origin, final List<String> arguments) {
		this.origin = origin;
		this.arguments = arguments;
	}

	@Override
	public String getClassName() {
		return origin.getClassName();
	}

	@Override
	public String getMessage() {
		return origin.getMessage();
	}

	@Override
	public List<StackTraceElement> getStackTrace() {
		return origin.getStackTrace();
	}

	@Override
	public StackTraceFilter getCause() {
		return origin.getCause();
	}

	/**
	 * Gets all passed arguments.
	 * 
	 * @return Passed arguments
	 */
	protected final List<String> getArguments() {
		return arguments;
	}

}
