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

package org.wits.writer.solbookwriter;

import org.wits.WITSProperties;
import org.wits.processors.ContentDetector;

/**
 *
 * @author FJ
 */
public class SolChapterWriter {

    private String textBlock = null;
    private String header1 = null;
    private String header2 = null;
    private String header3 = null;
    private String footer1 = null;
    private WITSProperties props = null;

    /**
     *
     * @param textBlock
     */
    public SolChapterWriter(String textBlock, WITSProperties props) {
        this.textBlock = textBlock;
        this.props = props;
        header1 = "<!--" + WITSProperties.WITS_BrandName + " " + WITSProperties.WITS_VersionName + "--> \r\n<!DOCTYPE CHAPTER PUBLIC \"-//Sun Microsystems//DTD SolBook 3.5//EN\">\r\n";

        //content auto detection
        String chapterTitle = props.WITS_ChapterTitle;
        ContentDetector detector = new ContentDetector(textBlock);
        chapterTitle = detector.getChapterTitle();

        String chapHighlights = props.WITS_ChapterHighlights;
        if (chapHighlights.indexOf("Enter") != -1) {
            chapHighlights = chapterTitle;
        }

        header2 = "<chapter><title>" + chapterTitle + "</title>\r\n";
        header3 = "<highlights><para>" + chapHighlights + "</para></highlights>\r\n";

        footer1 = "</chapter>";

    }

    /**
     *
     * @return
     */
    public String getPartialChapterBody() {
        StringBuilder chapterBody = new StringBuilder();
        chapterBody.append(header2);
        chapterBody.append(header3);
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
        chapterBody.append(header2);
        chapterBody.append(header3);
        chapterBody.append(textBlock);
        chapterBody.append(footer1);
        return chapterBody.toString();
    }
}
