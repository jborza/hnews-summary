package com.jborza.hnews.demo;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

public class MarkdownConverter {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public MarkdownConverter() {
        MutableDataSet options = new MutableDataSet();
        this.parser = Parser.builder(options).build();
        this.renderer = HtmlRenderer.builder(options).build();
    }

    public String convertToHtml(String markdown) {
        return renderer.render(parser.parse(markdown));
    }
}