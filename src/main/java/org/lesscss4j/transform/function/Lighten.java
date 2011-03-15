/*
Copyright © 2010 to Present, Local Matters, Inc.
All rights reserved.
*/
package org.lesscss4j.transform.function;

import org.lesscss4j.error.FunctionException;
import org.lesscss4j.model.expression.ConstantColor;
import org.lesscss4j.model.expression.ConstantExpression;
import org.lesscss4j.model.expression.ConstantNumber;
import org.lesscss4j.model.expression.Expression;

public class Lighten extends AbstractColorFunction {
    @Override
    protected Expression evaluate(ConstantColor color, ConstantNumber value) {
        if (value.getUnit() != null && !value.getUnit().equals("%")) {
            throw new FunctionException("Argument 2 for function '%s' must be a percentage: %s", value.toString());
        }

        float[] hsla = color.toHSL();

        ConstantColor newColor = new ConstantColor();
        newColor.setHSL(hsla[0], hsla[1], hsla[2] + ((float)value.getValue() / 100.0f));
        return new ConstantExpression(newColor);
    }

}