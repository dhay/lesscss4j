/**
 * File: StyleSheetFactory.java
 *
 * Author: David Hay (dhay@localmatters.com)
 * Creation Date: Apr 21, 2010
 * Creation Time: 11:27:01 AM
 *
 * Copyright 2010 Local Matters, Inc.
 * All Rights Reserved
 *
 * Last checkin:
 *  $Author$
 *  $Revision$
 *  $Date$
 */
package org.lesscss4j.factory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.tree.Tree;
import org.apache.commons.io.FilenameUtils;
import org.lesscss4j.exception.ParseException;
import org.lesscss4j.model.BodyElement;
import org.lesscss4j.model.Media;
import org.lesscss4j.model.Page;
import org.lesscss4j.model.RuleSet;
import org.lesscss4j.model.StyleSheet;
import org.lesscss4j.model.expression.Expression;
import org.lesscss4j.parser.FileStyleSheetResource;
import org.lesscss4j.parser.StyleSheetParser;
import org.lesscss4j.parser.StyleSheetResource;
import org.lesscss4j.parser.StyleSheetTree;
import org.lesscss4j.parser.UrlStyleSheetResource;

import static org.lesscss4j.parser.antlr.LessCssLexer.*;

public class StyleSheetFactory extends AbstractObjectFactory<StyleSheet> {
    private ObjectFactory<RuleSet> _ruleSetFactory;
    private ObjectFactory<Media> _mediaFactory;
    private ObjectFactory<Page> _pageFactory;
    private ObjectFactory<Expression> _expressionFactory;
    private StyleSheetParser _styleSheetParser;
    private Pattern _importCleanupPattern = Pattern.compile("(?i:u\\s*r\\s*l\\(\\s*)?['\"](.*)['\"](\\s*\\))?");

    public StyleSheetParser getStyleSheetParser() {
        return _styleSheetParser;
    }

    public void setStyleSheetParser(StyleSheetParser styleSheetParser) {
        _styleSheetParser = styleSheetParser;
    }

    public ObjectFactory<Expression> getExpressionFactory() {
        return _expressionFactory;
    }

    public void setExpressionFactory(ObjectFactory<Expression> expressionFactory) {
        _expressionFactory = expressionFactory;
    }

    public ObjectFactory<RuleSet> getRuleSetFactory() {
        return _ruleSetFactory;
    }

    public void setRuleSetFactory(ObjectFactory<RuleSet> ruleSetFactory) {
        _ruleSetFactory = ruleSetFactory;
    }

    public ObjectFactory<Media> getMediaFactory() {
        return _mediaFactory;
    }

    public void setMediaFactory(ObjectFactory<Media> mediaFactory) {
        _mediaFactory = mediaFactory;
    }

    public ObjectFactory<Page> getPageFactory() {
        return _pageFactory;
    }

    public void setPageFactory(ObjectFactory<Page> pageFactory) {
        _pageFactory = pageFactory;
    }

    public StyleSheet create(Tree styleSheetNode) {
        StyleSheet stylesheet = new StyleSheet();
        if (styleSheetNode == null) {
            return stylesheet;
        }

        StyleSheetResource resource = null;
        if (styleSheetNode instanceof StyleSheetTree) {
            resource = ((StyleSheetTree) styleSheetNode).getResource();
        }

        for (int idx = 0, numChildren = styleSheetNode.getChildCount(); idx < numChildren; idx++) {
            Tree child = styleSheetNode.getChild(idx);
            processStyleSheetNode(stylesheet, child, resource);
        }

        return stylesheet;
    }

    protected void processStyleSheetNode(StyleSheet stylesheet, Tree child, StyleSheetResource resource) {
        switch (child.getType()) {
            case CHARSET:
                String charset = child.getChild(0).getText();
                charset = charset.replaceFirst("['\"]\\s*(\\S*)\\s*['\"]", "$1");
                if (charset.length() > 0) {
                    stylesheet.setCharset(charset);
                }
                break;

            case IMPORT:
                handleImport(stylesheet, child, resource);
                break;

            case VAR:
                Tree exprNode = child.getChild(1);
                Expression expr = getExpressionFactory().create(exprNode);
                if (expr != null) {
                    stylesheet.setVariable(child.getChild(0).getText(), expr);
                }
                break;

            case MIXIN_MACRO:
            case RULESET:
                RuleSet ruleSet = getRuleSetFactory().create(child);
                if (ruleSet != null) {
                    stylesheet.addBodyElement(ruleSet);
                }
                break;

            case MEDIA_SYM:
                Media media = getMediaFactory().create(child);
                if (media != null) {
                    stylesheet.addBodyElement(media);
                }
                break;

            case PAGE_SYM:
                Page page = getPageFactory().create(child);
                if (page != null) {
                    stylesheet.addBodyElement(page);
                }
                break;

            default:
                handleUnexpectedChild("Unexpected stylesheet child:", child);
                break;
        }
    }

    protected void handleImport(StyleSheet stylesheet, Tree importNode, StyleSheetResource resource) {
        String path = cleanImportPath(importNode.getChild(0).getText());

        if (!stylesheet.getImports().contains(path)) {
            stylesheet.addImport(path);
            StyleSheet imported = importStylesheet(path, resource);
            mergeStyleSheets(imported, stylesheet);
        }
    }

    protected void mergeStyleSheets(StyleSheet src, StyleSheet dest) {
        for (BodyElement element : src.getBodyElements()) {
            dest.addBodyElement(element);
        }

        for (String importPath : src.getImports()) {
            dest.addImport(importPath);
        }

        for (Iterator<String> iter = src.getVariableNames(); iter.hasNext(); ) {
            String varName = iter.next();
            // todo: check for collisions?
            dest.setVariable(varName, src.getVariable(varName));
        }
    }

    protected StyleSheet importStylesheet(String path, StyleSheetResource relativeTo) {
        try {
            StyleSheetResource importResource = getImportResource(path, relativeTo);
            return getStyleSheetParser().parse(importResource);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
            // todo: throw ImportException
        }
    }

    protected StyleSheetResource getImportResource(String path, StyleSheetResource relativeTo)
        throws MalformedURLException {
        String extension = FilenameUtils.getExtension(path);
        if (extension == null || (!extension.equals("css") && !extension.equals("less"))) {
            path = path + ".less";
        }
        URL importUrl = new URL(relativeTo.getUrl(), path);
        if ("file".equals(importUrl.getProtocol())) {
            return new FileStyleSheetResource(importUrl.getPath());
        }
        else {
            return new UrlStyleSheetResource(importUrl);
        }
    }

    protected String cleanImportPath(String path) {
        Matcher matcher = _importCleanupPattern.matcher(path);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        else {
            throw new ParseException("Unsupported import path:" + path, null);
        }
    }

    public static ObjectFactory<StyleSheet> createDefaultObjectFactory() {
        ExpressionFactory expressionFactory = new ExpressionFactory();

        DeclarationFactory declarationFactory = new DeclarationFactory();
        declarationFactory.setExpressionFactory(expressionFactory);

        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        ruleSetFactory.setSelectorFactory(new SelectorFactory());
        ruleSetFactory.setDeclarationFactory(declarationFactory);
        ruleSetFactory.setExpressionFactory(expressionFactory);

        MediaFactory mediaFactory = new MediaFactory();
        mediaFactory.setRuleSetFactory(ruleSetFactory);
        mediaFactory.setExpressionFactory(expressionFactory);

        PageFactory pageFactory = new PageFactory();
        pageFactory.setDeclarationFactory(declarationFactory);
        pageFactory.setExpressionFactory(expressionFactory);

        StyleSheetFactory styleSheetFactory = new StyleSheetFactory();
        styleSheetFactory.setRuleSetFactory(ruleSetFactory);
        styleSheetFactory.setMediaFactory(mediaFactory);
        styleSheetFactory.setPageFactory(pageFactory);
        styleSheetFactory.setExpressionFactory(expressionFactory);

        return styleSheetFactory;
    }

}
