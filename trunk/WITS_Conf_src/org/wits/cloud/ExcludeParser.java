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
package org.wits.cloud;

import java.util.StringTokenizer;
import org.wits.debugger.WITSDebugger;
import org.wits.patterns.StringHandler;

/**
 *
 * @author FJ
 */
public class ExcludeParser {

    private String uncleanSGML = null;
    private WITSDebugger debugger = null;

    /**
     *
     * @param debugger
     */
    public void setDebugger(WITSDebugger debugger) {
        this.debugger = debugger;
    }

    /**
     *
     * @param uncleanSGML
     */
    public ExcludeParser(String uncleanSGML) {
        this.uncleanSGML = uncleanSGML;
    }

    /**
     *
     * @param section
     */
    public void scrapNotSupportedSections(String section) {
        //scrap only the title and anchors
        StringBuilder _handle = new StringBuilder();
        int offset = 0;

        while (true) {
            int l_loc = uncleanSGML.indexOf("<LB>\r\n" + section + ".", offset);
            int r_loc = uncleanSGML.indexOf("<LB>", l_loc + 1);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            debugger.showDebugMessage("ExcludeIC", l_loc, "Found Unsupported Heading.");

            _handle.append(uncleanSGML.substring(offset, l_loc));

            debugger.showDebugMessage("ExcludeIC", l_loc, "Scrapping Heading Title...Done");

            offset = r_loc;
        }
        uncleanSGML = _handle.toString();
    }

    private int getWordCount(String text, String pattern) {

        int i = 0;
        int count = 0;

        while (true) {

            int index = text.indexOf(pattern, i);
            if (index != -1) {
                i = index + 1;
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    /**
     *
     * @return
     */
    public String getProcessedText() {

        debugger.addLineBreak();
        StringHandler handler = new StringHandler();
        handler.setDebugger(debugger);
        //We are excluding some wiki markups in SGML
        //we don't honour heading at level higher than h3  
        debugger.showDebugMessage("ExcludeIC", 0, "ExcludeIC Invoked.");

        debugger.showDebugMessage("ExcludeIC", 0, "Flattening Unsupported Headings...");

        //comment who ami to scrap? just flatten.
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\nh4.", "<LB>\r\nh3.");
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\nh5.", "<LB>\r\nh3.");
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\nh6.", "<LB>\r\nh3.");
        //scrapNotSupportedSections("h4");
        //scrapNotSupportedSections("h5");
        //scrapNotSupportedSections("h6");
        debugger.showDebugMessage("ExcludeIC", 0, "Flattening Unsupported Headings...Done");

        //Exclude Top Links      
        debugger.showDebugMessage("ExcludeIC", 0, "Excluding Top Links.");

        uncleanSGML = handler.replace(uncleanSGML, "{anchor:top}", "");
        uncleanSGML = handler.replace(uncleanSGML, "[Top|#top]", "");
        // uncleanSGML = handler.replace(uncleanSGML, "{panel}", "");

        debugger.showDebugMessage("ExcludeIC", 0, "Excluding hidden content.");

        //Check WITS Targets
        int startTagCount = getWordCount(uncleanSGML, "#WITSTarget:START");
        int endTagCount = getWordCount(uncleanSGML, "#WITSTarget:END");

        if (startTagCount != endTagCount) {
            //Fatal Error
            System.out.println("   FATAL ERROR: You have used WITS Targets. The count of START handlers and END handlers do not macth");
            System.exit(0);
        } else {
            if (startTagCount > 0) {
                System.out.println("   WITS Targets [Source]: " + startTagCount);
            }
        }
        //exclude hidden content
        int offset = 0;
        StringBuilder _handle = new StringBuilder();


        while (true) {
            int l_loc = uncleanSGML.indexOf("{excerpt:hidden=true", offset);
            int r_loc = uncleanSGML.indexOf("{excerpt}", l_loc);
            //System.out.println(uncleanSGML);
            //System.out.println("Excerpt at: "+l_loc);
            //System.out.println("Excerpt end at: "+r_loc);

            if (l_loc == -1 || r_loc == -1) {
                debugger.showDebugMessage("ExcludeIC", 0, "Hidden content - None.");
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            debugger.showDebugMessage("ExcludeIC", l_loc, "Found hidden content.");
            //hidden excerpt is present
            debugger.showDebugMessage("ExcludeIC", l_loc, "Excluding hidden content.");

            //Check WITS targets in the hidden content
            String witsTarget = uncleanSGML.substring(l_loc + 21, r_loc);

            if (witsTarget.indexOf("<LB>\r\n") != -1) {
                witsTarget = handler.replace(witsTarget, "<LB>\r\n", "").trim();
            }

            if (witsTarget.indexOf("#WITSTarget:START") != -1) {
                //Get the WITS targets
                //System.out.println("WITSSTART:" + witsTarget);
                StringTokenizer stok = new StringTokenizer(witsTarget, "=");
                stok.nextToken();
                String targetAttrs = stok.nextToken();

                //if (witsTarget.indexOf("START") != -1) {
                //Start of target text
                _handle.append(uncleanSGML.substring(offset, l_loc));
                _handle.append("<LB>\r\n<LB>\r\n");
                _handle.append("<WITSTarget id=\"" + targetAttrs + "\">");
                _handle.append("<LB>\r\n");

            } else if (witsTarget.indexOf("#WITSTarget:END") != -1) {
                //end of target text
                //System.out.println("WITSEND:" + witsTarget);
                _handle.append(uncleanSGML.substring(offset, l_loc));
                _handle.append("<LB>\r\n");
                _handle.append("</WITSTarget>");
                _handle.append("<LB>\r\n<LB>\r\n");
            } else {
                _handle.append(uncleanSGML.substring(offset, l_loc));
            }
            //_handle.append(uncleanSGML.substring(r_loc + 9, r_loc + 10));
            offset = r_loc + 9;
        }

        uncleanSGML = _handle.toString();

        //System.out.println("--------------------" + uncleanSGML);
        //remove section anchors
        uncleanSGML = removeSectionAnchors(uncleanSGML);

        //Exclude not supported pattern
        debugger.showDebugMessage("ExcludeIC", uncleanSGML.length(), "Excluding pattern {TOC}.");
        uncleanSGML = excludePattern(uncleanSGML, "toc:");
        debugger.showDebugMessage("ExcludeIC", uncleanSGML.length(), "Excluding pattern {TOC}.");
        uncleanSGML = excludePattern(uncleanSGML, "toc-zone");
        debugger.showDebugMessage("ExcludeIC", uncleanSGML.length(), "Excluding pattern {toc-zone.");
        uncleanSGML = excludePattern(uncleanSGML, ":space");
        debugger.showDebugMessage("ExcludeIC", uncleanSGML.length(), "Excluding pattern {anchor.");
        uncleanSGML = excludePattern(uncleanSGML, "anchor:");

        //exclude hr
        offset = 0;
        _handle = new StringBuilder();
        while (true) {
            int l_loc = uncleanSGML.indexOf("----", offset);

            if (l_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            //System.out.println("EXCLUDING HR");
            _handle.append(uncleanSGML.substring(offset, l_loc + 2));
            offset = l_loc + 4;
        }
        uncleanSGML = _handle.toString();

        return uncleanSGML;
    }

    /**
     *
     * @param textBlock
     * @return
     */
    public String removeSectionAnchors(String textBlock) {
        int offset = 0;
        StringBuilder _handle = new StringBuilder();

        while (true) {
            int l_loc = textBlock.indexOf("{anchor:", offset);
            int r_loc = textBlock.indexOf("}", l_loc);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(textBlock.substring(offset, textBlock.length()));
                break;
            }

            //ignore anchor.
            _handle.append(textBlock.substring(offset, l_loc));

            offset = r_loc + 1;
        }

        offset = 0;

        textBlock = _handle.toString();
        _handle = new StringBuilder();

        while (true) {
            int l_loc = textBlock.indexOf("{excerpt-include:", offset);
            int r_loc = textBlock.indexOf("}", l_loc);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(textBlock.substring(offset, textBlock.length()));
                break;
            }

            //ignore anchor.
            _handle.append(textBlock.substring(offset, l_loc));

            offset = r_loc + 1;
        }

        return _handle.toString();
    }

    /**
     *
     * @param textBlock
     * @param pattern
     * @return
     */
    public String excludePattern(String textBlock, String pattern) {
        //In the wiki you can have {toc:minLevel=2|maxLevel=2|location=top|type=list}
        //In SGML, it should go away. Sorry
        //System.out.println("Excluding pattern......"+pattern);
        //System.out.println("TB Length......"+textBlock.length());

        int offset = 0;
        StringBuilder _handle = new StringBuilder();

        while (true) {
            //int l_loc = textBlock.indexOf("<LB>\r\n{", offset);
            //updated on May 19. fixed a section id bug. We no longer support section ids.
            int l_loc = textBlock.indexOf("<LB>\r\n{", offset);
            //sometimes it could be in the beginning
            int l_loc2 = textBlock.indexOf("{", offset);
            if (l_loc == -1) {
                l_loc = l_loc2;
            }

            int r_loc = textBlock.indexOf("<LB>\r\n", l_loc + 1);

            //System.out.println("LOC:"+l_loc+" ROC:"+r_loc+"OFFSET:"+offset);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(textBlock.substring(offset, textBlock.length()));
                break;
            }

            String excludeCandidate = textBlock.substring(l_loc, r_loc);
            //System.out.println("TOCCA:"+excludeCandidate);
            //System.out.println("PATTERN:"+pattern);

            if (excludeCandidate.indexOf(pattern) == -1) {
                _handle.append(textBlock.substring(offset, r_loc));
                offset = r_loc;
                continue;
            }
            //markup found
            //System.out.println("MARKUP FOUND");

            _handle.append(textBlock.substring(offset, l_loc));

            offset = r_loc;
        }

        return _handle.toString();
    }
}
