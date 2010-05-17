/**
 * File: LessCssCompilerImpl.java
 *
 * Author: David Hay (dhay@localmatters.com)
 * Creation Date: Apr 20, 2010
 * Creation Time: 12:44:39 PM
 *
 * Copyright 2010 Local Matters, Inc.
 * All Rights Reserved
 *
 * Last checkin:
 *  $Author$
 *  $Revision$
 *  $Date$
 */
package org.lesscss4j.compile;

import java.io.IOException;
import java.io.OutputStream;

import org.lesscss4j.model.StyleSheet;
import org.lesscss4j.output.StyleSheetWriter;
import org.lesscss4j.output.StyleSheetWriterImpl;
import org.lesscss4j.parser.LessCssStyleSheetParser;
import org.lesscss4j.parser.StyleSheetParser;
import org.lesscss4j.parser.StyleSheetResource;
import org.lesscss4j.transform.StyleSheetEvaluationContext;
import org.lesscss4j.transform.StyleSheetTransformer;
import org.lesscss4j.transform.Transformer;

public class LessCssCompilerImpl implements LessCssCompiler {
    private StyleSheetParser _styleSheetParser = new LessCssStyleSheetParser();
    private StyleSheetWriter _styleSheetWriter = new StyleSheetWriterImpl();
    private Transformer<StyleSheet> _styleSheetTransformer;

    public Transformer<StyleSheet> getStyleSheetTransformer() {
        if (_styleSheetTransformer == null) {
            _styleSheetTransformer = StyleSheetTransformer.createDefaultTransformer();
        }
        return _styleSheetTransformer;
    }

    public void setStyleSheetTransformer(Transformer<StyleSheet> styleSheetTransformer) {
        _styleSheetTransformer = styleSheetTransformer;
    }

    public StyleSheetParser getStyleSheetParser() {
        return _styleSheetParser;
    }

    public void setStyleSheetParser(StyleSheetParser styleSheetParser) {
        _styleSheetParser = styleSheetParser;
    }

    public StyleSheetWriter getStyleSheetWriter() {
        return _styleSheetWriter;
    }

    public void setStyleSheetWriter(StyleSheetWriter styleSheetWriter) {
        _styleSheetWriter = styleSheetWriter;
    }


    public void compile(StyleSheetResource input, OutputStream output) throws IOException {
        StyleSheet styleSheet = getStyleSheetParser().parse(input);

        StyleSheetEvaluationContext context = new StyleSheetEvaluationContext();
        context.setResource(input);

        getStyleSheetTransformer().transform(styleSheet, context);

        getStyleSheetWriter().write(output, styleSheet);
    }
}
