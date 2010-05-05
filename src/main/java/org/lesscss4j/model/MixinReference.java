/**
 * File: MixinReference.java
 *
 * Author: David Hay (dhay@localmatters.com)
 * Creation Date: Apr 28, 2010
 * Creation Time: 9:59:58 AM
 *
 * Copyright 2010 Local Matters, Inc.
 * All Rights Reserved
 *
 * Last checkin:
 *  $Author$
 *  $Revision$
 *  $Date$
 */
package org.lesscss4j.model;

import java.util.ArrayList;
import java.util.List;

import org.lesscss4j.model.expression.Expression;

public class MixinReference extends AbstractElement implements DeclarationElement {
    private Selector _selector;
    private List<Expression> _arguments = new ArrayList<Expression>();

    public MixinReference() {
    }

    public MixinReference(MixinReference copy) {
        _selector = copy._selector.clone();
        _arguments = new ArrayList<Expression>(copy._arguments); // todo: deep copy necessary?
    }

    public Selector getSelector() {
        return _selector;
    }

    public void setSelector(Selector selector) {
        _selector = selector;
    }

    public List<Expression> getArguments() {
        return _arguments;
    }

    public void addArgument(Expression expression) {
        if (_arguments == null) {
            _arguments = new ArrayList<Expression>();
        }
        _arguments.add(expression);
    }

    @Override
    public String toString() {
        return getSelector().toString();
    }

    @Override
    public MixinReference clone() {
        return new MixinReference(this);
    }
}
