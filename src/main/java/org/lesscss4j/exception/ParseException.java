/**
 * File: ParseException.java
 *
 * Author: David Hay (dhay@localmatters.com)
 * Creation Date: Apr 26, 2010
 * Creation Time: 3:17:48 PM
 *
 * Copyright 2010 Local Matters, Inc.
 * All Rights Reserved
 *
 * Last checkin:
 *  $Author$
 *  $Revision$
 *  $Date$
 */
package org.lesscss4j.exception;

import java.util.Collection;

import org.lesscss4j.parser.ParseError;

public class ParseException extends RuntimeException {
    private Collection<ParseError> _errors;

    public ParseException(Collection<ParseError> errors) {
        _errors = errors;
    }

    public ParseException(String message, Collection<ParseError> errors) {
        super(message);
        _errors = errors;
    }

    public ParseException(String message, Collection<ParseError> errors, Throwable cause) {
        super(message, cause);
        _errors = errors;
    }

    public ParseException(Collection<ParseError> errors, Throwable cause) {
        super(cause);
        _errors = errors;
    }

    public Collection<ParseError> getErrors() {
        return _errors;
    }

    public void setErrors(Collection<ParseError> errors) {
        _errors = errors;
    }

    @Override
    public String getMessage() {
        String superMessage = super.getMessage();
        StringBuilder message = new StringBuilder(superMessage == null ? "" : superMessage);
        for (ParseError error : getErrors()) {
            message.append("\n");
            message.append(error.getHeader());
            message.append(' ');
            message.append(error.getMessage());
        }
        return message.toString();
    }
}