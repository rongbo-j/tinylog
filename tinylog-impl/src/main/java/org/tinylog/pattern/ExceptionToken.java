/*
 * Copyright 2016 Martin Winandy
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

package org.tinylog.pattern;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;

/**
 * Token for outputting the exception or throwable of a log entry.
 */
final class ExceptionToken implements Token {

	private static final String NEW_LINE = System.getProperty("line.separator");
	private static final StackTraceElement[] EMPTY_TRACE = new StackTraceElement[0];

	/** */
	ExceptionToken() {
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return Collections.singleton(LogEntryValue.EXCEPTION);
	}

	@Override
	public void render(final LogEntry logEntry, final StringBuilder builder) {
		Throwable throwable = logEntry.getException();
		if (throwable != null) {
			render(throwable, EMPTY_TRACE, builder);
		}
	}
	
	@Override
	public void apply(final LogEntry logEntry, final PreparedStatement statement, final int index) throws SQLException {
		Throwable throwable = logEntry.getException();
		if (throwable == null) {
			statement.setString(index, null);
		} else {
			StringBuilder builder = new StringBuilder();
			render(throwable, EMPTY_TRACE, builder);
			statement.setString(index, builder.toString());
		}
	}

	/**
	 * Renders a throwable including stack trace and cause throwable.
	 *
	 * @param throwable
	 *            Throwable to render
	 * @param parentTrace
	 *            Stack trace from parent throwable
	 * @param builder
	 *            Output will be appended to this string builder
	 */
	private static void render(final Throwable throwable, final StackTraceElement[] parentTrace, final StringBuilder builder) {
		StackTraceElement[] stackTrace = throwable.getStackTrace();

		int parentIndex = parentTrace.length - 1;
		int childIndex = stackTrace.length - 1;
		int commonElements = 0;
		while (parentIndex >= 0 && childIndex >= 0 && parentTrace[parentIndex].equals(stackTrace[childIndex])) {
			parentIndex -= 1;
			childIndex -= 1;
			commonElements += 1;
		}
		
		builder.append(throwable.getClass().getName());
		String message = throwable.getMessage();
		if (message != null) {
			builder.append(": ");
			builder.append(message);
		}

		for (int i = 0; i < stackTrace.length - commonElements; ++i) {
			builder.append(NEW_LINE);
			builder.append("\tat ");
			builder.append(stackTrace[i]);
		}
		
		if (commonElements > 0) {
			builder.append(NEW_LINE);
			builder.append("\t... ");
			builder.append(commonElements);
			builder.append(" more");
		}

		Throwable cause = throwable.getCause();
		if (cause != null) {
			builder.append(NEW_LINE);
			builder.append("Caused by: ");
			render(cause, stackTrace, builder);
		}
	}

}
