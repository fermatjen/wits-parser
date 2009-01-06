/******************************************************************************
 *
 *
 * WITS - Wiki to Structured Markup Converter.
 *
 * Copyright (C) 2009 by Frank Jennings (fermatjen@yahoo.com).
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation under the terms of the GNU General Public License is hereby
 * granted. No representations are made about the suitability of this software
 * for any purpose. It is provided "as is" without express or implied warranty.
 * See the GNU General Public License for more details.
 *
 * Documents produced by WITS converter are derivative works derived from the
 * input used in their production; they are not affected by this license.
 *
 */

package org.wits.writer.docbookwriter;

import org.wits.WITSProperties;
import org.wits.processors.ContentDetector;

/**
 *
 * @author FJ
 */
public class DocChapterWriter {

    private String textBlock = null;
    private String header1 = null;
    private String header2 = null;
    private String header3 = null;
    private String footer1 = null;
    
    private WITSProperties props = null;
    private String header0;

    /**
     *
     * @param textBlock
     */
    public DocChapterWriter(String textBlock, WITSProperties props) {
        this.textBlock = textBlock;
        this.props = props;
        //header1 = "<!--" + WITSProperties.WITS_BrandName + " " + WITSProperties.WITS_VersionName + "--> \r\n<!DOCTYPE CHAPTER PUBLIC \"-//Sun Microsystems//DTD SolBook 3.5//EN\">\r\n";
        header1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<chapter version=\"5.0\"\r\n    xmlns=\"http://docbook.org/ns/docbook\"\r\n    xmlns:xlink=\"http://www.w3.org/1999/xlink\"\r\n    xmlns:xi=\"http://www.w3.org/2001/XInclude\"\r\n    xmlns:svg=\"http://www.w3.org/2000/svg\"\r\n    xmlns:m=\"http://www.w3.org/1998/Math/MathML\"\r\n    xmlns:html=\"http://www.w3.org/1999/xhtml\"\r\n    xmlns:db=\"http://docbook.org/ns/docbook\">";
        header0 = "<title>" + props.WITS_ChapterTitle + "</title>\r\n";
        
                //content auto detection
        String chapterTitle = props.WITS_ChapterTitle;
        ContentDetector detector = new ContentDetector(textBlock);
        chapterTitle = detector.getChapterTitle();
        
        header2 = "<chapter><title>" + chapterTitle + "</title>\r\n";

        //header3 = "<highlights><para>" + props.WITS_ChapterHighlights + "</para></highlights>\r\n";

        footer1 = "</chapter>";

    }

    /**
     *
     * @return
     */
    public String getPartialChapterBody() {
        StringBuilder chapterBody = new StringBuilder();
        chapterBody.append(header2);
        //chapterBody.append(header3);
        chapterBody.append(textBlock);
        chapterBody.append(footer1);
        return chapterBody.toString();
    }

    /**
     *
     * @return
     */
    public String getChapterBody() {
        StringBuilder chapterBody = new StringBuilder();
        chapterBody.append(header1);
        chapterBody.append(header0);
        //chapterBody.append(header3);
        chapterBody.append(textBlock);
        chapterBody.append(footer1);
        return chapterBody.toString();
    }
}
