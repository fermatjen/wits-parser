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
public class DocBookWriter {

    private String textBlock = null;
    private String header1 = null;
    private String header2 = null;
    private String header3 = null;
    private final String footer1 = "</chapter>";
    private final String footer2 = "</book>";
    private WITSProperties props = null;

    /**
     *
     * @param textBlock
     */
    public DocBookWriter(String textBlock, WITSProperties props) {
        this.textBlock = textBlock;
        this.props = props;

        //content auto detection
        String chapterTitle = props.WITS_ChapterTitle;
        ContentDetector detector = new ContentDetector(textBlock);
        chapterTitle = detector.getChapterTitle();

        String bookTitle = props.WITS_BookTitle;
        if (bookTitle.indexOf("Enter your") != -1) {
            bookTitle = chapterTitle;
        }

        String chapHighlights = props.WITS_ChapterHighlights;
        if (chapHighlights.indexOf("Enter") != -1) {
            chapHighlights = chapterTitle;
        }

        //header1 = "<!--" + WITSProperties.WITS_BrandName + " " + WITSProperties.WITS_VersionName + "--> \r\n<!DOCTYPE BOOK PUBLIC \"-//Sun Microsystems//DTD SolBook 3.5//EN\" [\r\n<!ENTITY abstract SYSTEM \"abstract.sgm\">\r\n<!ENTITY typeconv SYSTEM \"typeconv.sgm\">\r\n<!ENTITY sundocs SYSTEM \"sundocs.sgm\">\r\n<!ENTITY fr-other-trademarks SYSTEM \"fr-other-trademarks.sgm\">\r\n<!ENTITY other-trademarks SYSTEM \"other-trademarks.sgm\">\r\n<!ENTITY sun-trademarks SYSTEM \"sun-trademarks.sgm\">\r\n<!ENTITY preface SYSTEM \"preface.sgm\">\r\n<!ENTITY fr-legal SYSTEM \"fr-legal.sgm\">\r\n<!ENTITY legal SYSTEM \"legal.sgm\">\r\n]>\r\n";
        //header2 = "<book id=\"REPLACE-WITH-SHORTNAME\" lang=\"en\">\r\n<title>" + props.WITS_BookTitle + "</title>\r\n<bookinfo>\r\n<authorgroup><author><firstname>" + props.WITS_AuthorName + "</firstname></author></authorgroup>\r\n<pubsnumber>" + props.WITS_PubsNumber + "</pubsnumber>\r\n<releaseinfo>" + props.WITS_ReleaseInfo + "</releaseinfo>\r\n<pubdate>" + props.WITS_PubDate + "</pubdate>\r\n<publisher><publishername>" + props.WITS_PubName + "</publishername></publisher>\r\n<copyright><year>" + props.WITS_CopyrightYear + "</year></copyright>\r\n<abstract><para>" + props.WITS_BookAbstract + "</para></abstract>\r\n<legalnotice><para>" + props.WITS_LegalNotice + "</para></legalnotice>\r\n</bookinfo>\r\n";
        header1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<book version=\"5.0\"\r\n    xmlns=\"http://docbook.org/ns/docbook\"\r\n    xmlns:xlink=\"http://www.w3.org/1999/xlink\"\r\n    xmlns:xi=\"http://www.w3.org/2001/XInclude\"\r\n    xmlns:svg=\"http://www.w3.org/2000/svg\"\r\n    xmlns:m=\"http://www.w3.org/1998/Math/MathML\"\r\n    xmlns:html=\"http://www.w3.org/1999/xhtml\"\r\n    xmlns:db=\"http://docbook.org/ns/docbook\">";
        header2 = "<info><title>" + bookTitle + "</title><author><personname><firstname>" + props.WITS_AuthorName + "</firstname><surname>" + props.WITS_AuthorName + "</surname></personname><affiliation><orgname>Org</orgname></affiliation></author><pubdate>" + props.WITS_PubDate + "</pubdate></info>";



        header3 = "<chapter><title>" + chapterTitle + "</title>\r\n<highlights><para>" + chapHighlights + "</para></highlights>\r\n";

    }

    /**
     *
     * @return
     */
    public String getPartialBookBody() {
        StringBuilder bookBody = new StringBuilder();
        bookBody.append(header1);
        bookBody.append(header2);
        bookBody.append(textBlock);
        bookBody.append(footer2);
        return bookBody.toString();
    }

    /**
     *
     * @return
     */
    public String getBookBody() {
        StringBuilder bookBody = new StringBuilder();
        bookBody.append(header1);
        bookBody.append(header2);
        bookBody.append(header3);
        bookBody.append(textBlock);
        bookBody.append(footer1);
        bookBody.append(footer2);
        return bookBody.toString();
    }
}
