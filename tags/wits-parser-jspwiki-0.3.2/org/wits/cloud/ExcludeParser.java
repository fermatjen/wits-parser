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
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n!!!!", "<LB>\r\n!!!");
        
        //scrapNotSupportedSections("h4");
        //scrapNotSupportedSections("h5");
        //scrapNotSupportedSections("h6");
        debugger.showDebugMessage("ExcludeIC", 0, "Flattening Unsupported Headings...Done");

        //Exclude Top Links      
        debugger.showDebugMessage("ExcludeIC", 0, "Excluding TOC");

        uncleanSGML = handler.replace(uncleanSGML, "[{TableOfContents }]", "");
        //uncleanSGML = handler.replace(uncleanSGML, "[Top|#top]", "");
        // uncleanSGML = handler.replace(uncleanSGML, "{panel}", "");

        //debugger.showDebugMessage("ExcludeIC", 0, "Excluding hidden content.");
        //exclude hidden content (Access control text between [{....}]
        int offset = 0;
        StringBuilder _handle = new StringBuilder();
        
        while (true) {
            int l_loc = uncleanSGML.indexOf("[{", offset);
            int r_loc = uncleanSGML.indexOf("}]", l_loc);



            if (l_loc == -1 || r_loc == -1) {
                debugger.showDebugMessage("ExcludeIC", 0, "Hidden content - None.");
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            debugger.showDebugMessage("ExcludeIC", l_loc, "Found hidden content.");
            //hidden excerpt is present
            debugger.showDebugMessage("ExcludeIC", l_loc, "Excluding hidden content.");
            
            _handle.append(uncleanSGML.substring(offset, l_loc));
            String hiddenContent = uncleanSGML.substring(l_loc, r_loc);
            if(hiddenContent.toLowerCase().indexOf("image src") != -1){
                //maybe an image
                _handle.append("__Figure Placeholder Here__");
            }

            //_handle.append(uncleanSGML.substring(r_loc + 2, r_loc + 10));
            offset = r_loc + 2;
        }
        uncleanSGML = _handle.toString();
        


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

            offset = r_loc+1;
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
