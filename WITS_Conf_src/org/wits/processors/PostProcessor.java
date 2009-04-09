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
package org.wits.processors;

import org.wits.WITSInstance;
import org.wits.WITSProperties;
import org.wits.debugger.WITSDebugger;
import org.wits.patterns.StringHandler;

/**
 *
 * @author FJ
 */
public class PostProcessor {

    private String uncleanSGML = null;
    private WITSDebugger debugger = null;
    private WITSProperties props = null;
    private WITSInstance witsInstance = null;

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
    public PostProcessor(WITSInstance witsInstance, String uncleanSGML, WITSProperties props) {
        this.witsInstance = witsInstance;
        this.uncleanSGML = uncleanSGML;
        this.props = props;
    }

    /**
     *
     * @return
     */
    public String getProcessedText() {
        debugger.addLineBreak();
        debugger.showDebugMessage("PostProcessor", 0, "Post-Processor Invoked.");
        StringHandler handler = new StringHandler();
        handler.setDebugger(debugger);

        String danglerID = "dangler" + System.currentTimeMillis();

        debugger.showDebugMessage("PostProcessor", uncleanSGML.length(), "Removing WITS placeholders.");
        //Remove the intentional LBs
        //System.out.println("Sending for PATTERN:"+uncleanSGML);

        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n", "\r\n", 0);
        //clean LB inside code
        uncleanSGML = handler.replace(uncleanSGML, "&lt;LB>\r\n", "\r\n", 0);
        uncleanSGML = handler.replace(uncleanSGML, "\r\n<LB>", "\r\n", 0);
        uncleanSGML = handler.replace(uncleanSGML, "<LB><para>", "<para>", 0);
        //System.out.println("--------"+uncleanSGML);
        uncleanSGML = handler.replace(uncleanSGML, "<para><LB>\r\n</para>", "", 0);
        //uncleanSGML = handler.replace(uncleanSGML, "???", "-", 0);

        uncleanSGML = handler.replace(uncleanSGML, "<FLB>", "", 0);
        uncleanSGML = handler.replace(uncleanSGML, "%%%%%", "#", 0);

        //check if there is atleast 1 section

        if (uncleanSGML.indexOf("</sect1>") == -1) {
            StringBuilder _handle2 = new StringBuilder(uncleanSGML);

            if (witsInstance.getOutputType().equals("solbook")) {
                _handle2.insert(0, "<sect1 id=\"" + danglerID + "\"><title>" + props.WITS_DanglerSectionTitle + "</title>");
            }
            if (witsInstance.getOutputType().equals("docbook")) {
                _handle2.insert(0, "<sect1><title>" + props.WITS_DanglerSectionTitle + "</title>");
            }
            _handle2.insert(_handle2.length(), "</sect1>");
            uncleanSGML = _handle2.toString();
            //System.out.println("ADDED DANGLER:"+uncleanSGML);
            debugger.showDebugMessage("PostProcessor", 0, "Adding container <sect1>.");
        }


        debugger.showDebugMessage("PostProcessor", 0, "Post-Processing Done.");
        uncleanSGML = handleUnexpectedSectEnd(uncleanSGML);
        uncleanSGML = handleInitialSectionDangler(uncleanSGML);

        //sometimes there will be orphan . remove them
        uncleanSGML = handler.replace(uncleanSGML, "</screen>.\r\n", "</screen>\r\n", 0);
        uncleanSGML = handler.replace(uncleanSGML, "</para>.\r\n", "</para>\r\n", 0);
        uncleanSGML = handler.replace(uncleanSGML, "</literal>.\r\n", "</literal>\r\n", 0);
        uncleanSGML = handler.replace(uncleanSGML, "</ulink>.\r\n", "</ulink>\r\n", 0);



        //replace special chars
        uncleanSGML = handler.replace(uncleanSGML, "&amp;#035;", "#", 0);
        uncleanSGML = handler.replace(uncleanSGML, "&amp;#095;", "_", 0);
        uncleanSGML = handler.replace(uncleanSGML, "&amp;#092;", "\\", 0);
        uncleanSGML = handler.replace(uncleanSGML, "&amp;#042;", "*", 0);
        uncleanSGML = handler.replace(uncleanSGML, "&amp;#8212;", "-", 0);

        uncleanSGML = handler.replace(uncleanSGML, "<LB>", "", 0);
        uncleanSGML = handler.replace(uncleanSGML, "{excerpt}", "", 0);

        //remove blank para
        uncleanSGML = handler.replace(uncleanSGML, "<para></para>", "", 0);

        //remove screen texts from title
        uncleanSGML = removeScreensFromTitle(uncleanSGML);

        return uncleanSGML;
    }

    private String removeScreensFromTitle(String textBlock) {
        //Check if titles have screen elements.

        int offset = 0;
        StringBuilder _handle = new StringBuilder();

        while (true) {
            int l_loc = textBlock.indexOf("<title>", offset);
            int r_loc = textBlock.indexOf("</title>", l_loc);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(textBlock.substring(offset, textBlock.length()));
                break;
            }

            String titleCandidate = textBlock.substring(l_loc + 7, r_loc);

            if (titleCandidate.indexOf("<screen>") != -1 && titleCandidate.indexOf("</screen>") != -1) {
                int l1 = textBlock.indexOf("<screen>", l_loc);
                int l2 = textBlock.indexOf("</screen>", l1);
                String screenText = textBlock.substring(l1, l2 + 9);
                String titleText = textBlock.substring(l_loc + 7, l1);
                _handle.append(textBlock.substring(offset, l_loc));
                _handle.append("<title>");
                _handle.append(titleText);
                _handle.append("</title>");
                _handle.append(screenText);
                offset = r_loc + 8;

            } else {
                _handle.append(textBlock.substring(offset, r_loc));
                offset = r_loc;
            }
        }
        textBlock = _handle.toString();

        return textBlock;

    }

    private String handleInitialSectionDangler(String textBlock) {
        //Find the first section hit
        int offset = 0;
        StringBuilder _handle = new StringBuilder();
        //long time=Sy
        String danglerID = "dangler" + System.currentTimeMillis();

        while (true) {
            int l_loc = textBlock.indexOf("<sect", offset);

            if (l_loc == -1) {
                _handle.append(textBlock.substring(offset, textBlock.length()));
                break;
            }

            //Found the first section hit.
            //Check if it is section 2
            String sectionCandidate = textBlock.substring(l_loc, l_loc + 6);
            if (sectionCandidate.indexOf("sect1") != -1) {
                //No initial section dangler. Proceed.
                _handle.append(textBlock.substring(offset, textBlock.length()));
                break;
            }

            if (sectionCandidate.indexOf("sect2") != -1) {
                //Add sect1 place holder
                String danglerText = null;

                if (witsInstance.getOutputType().equals("solbook")) {
                    danglerText = "<sect1 id=\"" + danglerID + "\"><title>" + props.WITS_DanglerSectionTitle + "</title>\r\n<para>" + props.WITS_DanglerSectionSummary + "</para>\r\n";
                }
                if (witsInstance.getOutputType().equals("docbook")) {
                    danglerText = "<sect1><title>" + props.WITS_DanglerSectionTitle + "</title>\r\n<para>" + props.WITS_DanglerSectionSummary + "</para>\r\n";
                }
                _handle.append(danglerText);
                _handle.append(textBlock.substring(l_loc, textBlock.length()));
                break;
            }
            if (sectionCandidate.indexOf("sect3") != -1) {
                //Add sect1 and sect2 place holder
                String danglerText = null;

                if (witsInstance.getOutputType().equals("solbook")) {
                    danglerText = "<sect1 id=\"" + danglerID + "\"><title>" + props.WITS_DanglerSectionTitle + "</title>\r\n<para>" + props.WITS_DanglerSectionSummary + "</para>\r\n<sect2 id=\"dangler3\"><title>Enter section title</title><para>Insert section summary here.</para>\r\n";
                }
                if (witsInstance.getOutputType().equals("docbook")) {
                    danglerText = "<sect1><title>" + props.WITS_DanglerSectionTitle + "</title>\r\n<para>" + props.WITS_DanglerSectionSummary + "</para>\r\n<sect2><title>Enter section title</title><para>Insert section summary here.</para>\r\n";
                }
                _handle.append(danglerText);
                _handle.append(textBlock.substring(l_loc, textBlock.length()));
                break;
            }

            offset = l_loc;
        }
        return _handle.toString();
    }

    private String handleUnexpectedSectEnd(String textBlock) {
        int offset = 0;
        StringBuilder _handle = new StringBuilder();

        while (true) {
            int l_loc = textBlock.indexOf("<sect", offset);
            int r_loc = textBlock.indexOf("</sect", l_loc);

            //System.out.println("LOC:" + l_loc + " ROC:" + r_loc);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(textBlock.substring(offset, textBlock.length()));
                break;
            }

            String sectionCandidate = textBlock.substring(l_loc, r_loc);
            // System.out.println("SC:" + sectionCandidate);

            if (sectionCandidate.indexOf("<para>") == -1) {
                //not atleast one para.
                //add empty para tag.
                _handle.append(textBlock.substring(offset, l_loc));
                _handle.append(sectionCandidate);
                _handle.append("<para>" + props.WITS_DanglerSectionSummary + "</para>");
                offset = r_loc;
                continue;
            } else {
                _handle.append(textBlock.substring(offset, l_loc));
                _handle.append(sectionCandidate);
                offset = r_loc;
            }
        }

        return _handle.toString();
    }
}
