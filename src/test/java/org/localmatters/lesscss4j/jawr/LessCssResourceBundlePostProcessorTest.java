package org.localmatters.lesscss4j.jawr;

import junit.framework.TestCase;

public class LessCssResourceBundlePostProcessorTest extends TestCase {
    LessCssResourceBundlePostProcessor postProcessor;

    @Override
    protected void setUp() throws Exception {
        postProcessor = new LessCssResourceBundlePostProcessor();
    }

    public void testPostProcess() {
        String less =
            "@a: 1em;" +
            ".test {" +
            "    font-size: @a;" +
            "}";

        StringBuffer css = postProcessor.postProcessBundle(null, new StringBuffer(less));
        assertEquals(".test{font-size:1em;}", css.toString());
    }
}
