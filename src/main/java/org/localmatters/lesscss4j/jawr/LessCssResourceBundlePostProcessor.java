package org.localmatters.lesscss4j.jawr;

import net.jawr.web.exception.BundlingProcessException;
import net.jawr.web.resource.bundle.postprocess.BundleProcessingStatus;
import net.jawr.web.resource.bundle.postprocess.ResourceBundlePostProcessor;
import org.apache.commons.io.input.CharSequenceReader;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.io.output.WriterOutputStream;
import org.localmatters.lesscss4j.compile.DefaultLessCssCompilerFactory;
import org.localmatters.lesscss4j.compile.LessCssCompiler;
import org.localmatters.lesscss4j.parser.InputStreamStyleSheetResource;

import java.io.IOException;
import java.io.InputStream;

public class LessCssResourceBundlePostProcessor implements ResourceBundlePostProcessor {
    private LessCssCompiler compiler;

    public LessCssResourceBundlePostProcessor() {
        compiler = new DefaultLessCssCompilerFactory().create();
    }

    public StringBuffer postProcessBundle(BundleProcessingStatus bundleProcessingStatus, StringBuffer stringBuffer) {
        try {
            String encoding = "UTF-8";
            InputStream input = new ReaderInputStream(new CharSequenceReader(stringBuffer), encoding);
            StringBuilderWriter writer = new StringBuilderWriter(stringBuffer.length());

            compiler.compile(new InputStreamStyleSheetResource(input), new WriterOutputStream(writer, encoding), null);

            return new StringBuffer(writer.toString());
        } catch (IOException e) {
            throw new BundlingProcessException(e);
        }
    }
}
