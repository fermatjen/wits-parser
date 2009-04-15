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
package org.wits.parsers.inline;

import java.util.StringTokenizer;
import org.wits.WITSInstance;
import org.wits.debugger.WITSDebugger;
import org.wits.parsers.WITSParser;
import org.wits.patterns.StringHandler;

/**
 *
 * @author FJ
 */
public class ScriptParser implements WITSParser {

    private String uncleanSGML = null;
    private WITSDebugger debugger = null;
    private WITSInstance witsInstance = null;
    private String entityRefs = null;

    /**
     *
     * @param debugger
     */
    public void setDebugger(WITSDebugger debugger) {
        this.debugger = debugger;
    }

    public WITSInstance getWitsInstance() {
        return witsInstance;
    }

    public void setWitsInstance(WITSInstance witsInstance) {
        this.witsInstance = witsInstance;
    }

    /**
     *
     * @param uncleanSGML
     */
    public ScriptParser(WITSInstance witsInstance, String uncleanSGML) {
        this.uncleanSGML = uncleanSGML;
        this.witsInstance = witsInstance;
        entityRefs = new String();
    }

    /**
     *
     * @return
     */
    public String getProcessedText() {
        debugger.addLineBreak();
        debugger.showDebugMessage("ScriptIC", 0, "ScriptIC Invoked.");

        //uncleanSGML = flushSymbols("~");
        //uncleanSGML = flushSymbols("^");
        //uncleanSGML = flushSymbols("+");

        //handle - delete comment
        //strikethrough only for a word
        int offset = 0;
        StringBuilder _handle = new StringBuilder();
        /*
        while (true) {
        int l_loc = uncleanSGML.indexOf("-", offset + 1);
        int r_loc = uncleanSGML.indexOf("-", l_loc + 1);

        //System.out.println("LOC:" + l_loc + " ROC:" + r_loc);

        if (l_loc == -1 || r_loc == -1) {
        _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
        break;
        }

        String scriptCandidate = uncleanSGML.substring(l_loc, r_loc);
        //System.out.println("SCAND:" + scriptCandidate);

        if (scriptCandidate.indexOf(" ") == -1) {
        //process symbols
        _handle.append(uncleanSGML.substring(offset, l_loc));

        offset = r_loc + 1;
        continue;
        } else {
        //omit
        _handle.append(uncleanSGML.substring(offset, l_loc));
        offset = l_loc;
        continue;
        }

        }
         */
        //uncleanSGML = _handle.toString();

        //we do not support figures       

        offset = 0;
        _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("<LB>\r\n!", offset + 1);
            int r_loc = uncleanSGML.indexOf("!", l_loc);

            //System.out.println("LOC:" + l_loc + " ROC:" + r_loc);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            String scriptCandidate = uncleanSGML.substring(l_loc + 6, r_loc);
            //System.out.println("SCAND:" + scriptCandidate);
            //Check if filename and title can be extracted.
            String imageFileName = "Unknown.gif";
            String imageFileExt = ".gif";
            String imageFileNamePrefix = "Unknown";
            String imageTitle = "UnknownTitle";

            if (scriptCandidate.indexOf("|") != -1) {
                StringTokenizer stok = new StringTokenizer(scriptCandidate, "|");
                imageFileName = stok.nextToken();
                imageFileName = imageFileName.substring(1, imageFileName.length());
                String uncleanTitle = stok.nextToken();
                imageTitle = uncleanTitle.substring(7, uncleanTitle.length() - 1);
                if (imageFileName.indexOf(".") != -1) {
                    StringTokenizer stok2 = new StringTokenizer(imageFileName, ".");
                    imageFileNamePrefix = stok2.nextToken();
                    imageFileExt = stok2.nextToken();
                }
            }

            StringHandler handler = new StringHandler();
            handler.setDebugger(debugger);
            imageFileNamePrefix = handler.replace(imageFileNamePrefix, " ", "", 0);
            imageFileNamePrefix = handler.replace(imageFileNamePrefix, "^", "", 0);
            imageFileNamePrefix = handler.replace(imageFileNamePrefix, "#", "", 0);
            imageFileNamePrefix = handler.replace(imageFileNamePrefix, "&", "", 0);
            imageFileNamePrefix = handler.replace(imageFileNamePrefix, "<", "", 0);
            imageFileNamePrefix = handler.replace(imageFileNamePrefix, ">", "", 0);
            imageFileNamePrefix = handler.replace(imageFileNamePrefix, "%", "", 0);
            imageFileNamePrefix = handler.replace(imageFileNamePrefix, "_", "", 0);
            imageFileNamePrefix = handler.replace(imageFileNamePrefix, "*", "", 0);
            imageFileNamePrefix = handler.replace(imageFileNamePrefix, "!", "", 0);

            if (scriptCandidate.indexOf("<LB>") == -1) {
                //process symbols
                _handle.append(uncleanSGML.substring(offset, l_loc));
                String placeholderText = null;

                if (witsInstance.getOutputType().equals("solbook")) {
                    entityRefs += "<!ENTITY " + imageFileNamePrefix + " SYSTEM \"" + "graphics/" + imageFileName + "\" NDATA " + imageFileExt + ">\r\n";

                    placeholderText = "<figure id=\"" + imageFileNamePrefix + "\"><title>" + imageTitle + "</title><mediaobject><imageobject><imagedata entityref=\"" + imageFileNamePrefix + "\"></imageobject><textobject><simpara>" + imageTitle + "</simpara></textobject></mediaobject></figure>";

                } else {
                    placeholderText = "Figure: " + imageFileName;
                }
                _handle.append(placeholderText);

                offset = r_loc + 1;
                continue;
            } else {
                //omit
                _handle.append(uncleanSGML.substring(offset, l_loc));
                offset = l_loc;
                continue;
            }

        }

        uncleanSGML = _handle.toString();

        //we don to support {section} and {column}
        offset = 0;
        _handle = new StringBuilder();


        while (true) {
            int l_loc = uncleanSGML.indexOf("{column:", offset);
            int r_loc = uncleanSGML.indexOf("{column}", l_loc + 1);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            int ll_loc = uncleanSGML.indexOf("}", l_loc);

            String bqCandidate = uncleanSGML.substring(ll_loc + 1, r_loc);

            _handle.append(uncleanSGML.substring(offset, l_loc));
            _handle.append(bqCandidate);

            offset = r_loc;
        }
        uncleanSGML = _handle.toString();
        StringHandler handler = new StringHandler();
        handler.setDebugger(debugger);
        uncleanSGML = handler.replace(uncleanSGML, "{column}", "");

        offset = 0;
        _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("{section:", offset);
            int r_loc = uncleanSGML.indexOf("{section}", l_loc + 1);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            int ll_loc = uncleanSGML.indexOf("}", l_loc);


            String bqCandidate = uncleanSGML.substring(ll_loc + 1, r_loc);

            _handle.append(uncleanSGML.substring(offset, l_loc));
            _handle.append(bqCandidate);

            offset = r_loc;
        }
        uncleanSGML = _handle.toString();
        handler = new StringHandler();
        handler.setDebugger(debugger);
        uncleanSGML = handler.replace(uncleanSGML, "{section}", "");


        //Hnadle info
        offset = 0;
        _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("{info:", offset);
            int r_loc = uncleanSGML.indexOf("{info}", l_loc + 1);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            int ll_loc = uncleanSGML.indexOf("}", l_loc);


            String bqCandidate = uncleanSGML.substring(ll_loc + 1, r_loc);
            bqCandidate = handler.replace(bqCandidate, "<LB>\r\n", " ");

            _handle.append(uncleanSGML.substring(offset, l_loc));
            bqCandidate = bqCandidate.trim();

            //Do not allow screens in note.
            if (bqCandidate.indexOf("<noparse>") != -1 || bqCandidate.indexOf("<mediaobject>") != -1) {
                _handle.append("<para>" + bqCandidate + "</para>");
            } else {
                _handle.append("<note><para>" + bqCandidate + "</para></note>");
            }

            offset = r_loc;
        }
        uncleanSGML = _handle.toString();
        handler = new StringHandler();
        handler.setDebugger(debugger);
        uncleanSGML = handler.replace(uncleanSGML, "{info}", "");

        //Hnadle warning
        offset = 0;
        _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("{warning:", offset);
            int r_loc = uncleanSGML.indexOf("{warning}", l_loc + 1);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            int ll_loc = uncleanSGML.indexOf("}", l_loc);


            String bqCandidate = uncleanSGML.substring(ll_loc + 1, r_loc);

            _handle.append(uncleanSGML.substring(offset, l_loc));
            bqCandidate = handler.replace(bqCandidate, "<LB>\r\n", " ");

            bqCandidate = bqCandidate.trim();

            if (bqCandidate.indexOf("<noparse>") != -1 || bqCandidate.indexOf("<mediaobject>") != -1) {
                _handle.append("<para>" + bqCandidate + "</para>");
            } else {
                _handle.append("<caution><para>" + bqCandidate + "</para></caution>");
            }

            offset = r_loc;
        }
        uncleanSGML = _handle.toString();
        handler = new StringHandler();
        handler.setDebugger(debugger);
        uncleanSGML = handler.replace(uncleanSGML, "{warning}", "");

        //Hnadle note
        offset = 0;
        _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("{note:", offset);
            int r_loc = uncleanSGML.indexOf("{note}", l_loc + 1);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            int ll_loc = uncleanSGML.indexOf("}", l_loc);


            String bqCandidate = uncleanSGML.substring(ll_loc + 1, r_loc);

            _handle.append(uncleanSGML.substring(offset, l_loc));
            //System.out.println("NOTE:"+bqCandidate+"----");
            if (!bqCandidate.endsWith("<LB>\r\n")) {
                //bqCandidate = handler.replace(bqCandidate, "<LB>\r\n", " ");
            }

            bqCandidate = bqCandidate.trim();

            if (bqCandidate.indexOf("<noparse>") != -1 || bqCandidate.indexOf("<mediaobject>") != -1) {
                //System.out.println("NONOTE:"+bqCandidate);
                _handle.append("<para>" + bqCandidate + "</para>");
            } else {
                _handle.append("<note><para>" + bqCandidate + "</para></note>");
            }

            offset = r_loc+6;
        }
        uncleanSGML = _handle.toString();
        handler = new StringHandler();
        handler.setDebugger(debugger);
        //uncleanSGML = handler.replace(uncleanSGML, "{note}", "");

        //Hnadle tip
        offset = 0;
        _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("{tip:", offset);
            int r_loc = uncleanSGML.indexOf("{tip}", l_loc + 1);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            int ll_loc = uncleanSGML.indexOf("}", l_loc);


            String bqCandidate = uncleanSGML.substring(ll_loc + 1, r_loc);

            _handle.append(uncleanSGML.substring(offset, l_loc));
            //bqCandidate = handler.replace(bqCandidate, "<LB>\r\n", " ");

            bqCandidate = bqCandidate.trim();

            if (bqCandidate.indexOf("<noparse>") != -1 || bqCandidate.indexOf("<mediaobject>") != -1) {
                _handle.append("<para>" + bqCandidate + "</para>");
            } else {
                _handle.append("<tip><para>" + bqCandidate + "</para></tip>");
            }

            offset = r_loc;
        }
        uncleanSGML = _handle.toString();
        handler = new StringHandler();
        handler.setDebugger(debugger);
        uncleanSGML = handler.replace(uncleanSGML, "{tip}", "");

        //Update the entity refs
        //System.out.println("SETTING EREF:"+entityRefs);
        witsInstance.setEntityheaders(entityRefs);

        return uncleanSGML;
    }

    private String flushSymbols(String symbol) {
        int offset = 0;
        StringBuilder _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf(symbol, offset + 1);
            int r_loc = uncleanSGML.indexOf(symbol, l_loc + 1);

            //System.out.println("LOC:" + l_loc + " ROC:" + r_loc);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            String scriptCandidate = uncleanSGML.substring(l_loc, r_loc);
            //System.out.println("SCAND:" + scriptCandidate);

            if (scriptCandidate.indexOf(" ") == -1) {
                //process symbols
                _handle.append(uncleanSGML.substring(offset, l_loc));
                _handle.append(uncleanSGML.substring(l_loc + 1, r_loc));

                offset = r_loc + 1;
                continue;
            } else {
                //omit
                _handle.append(uncleanSGML.substring(offset, l_loc));
                offset = l_loc;
                continue;
            }

        }

        uncleanSGML = _handle.toString();
        return uncleanSGML;
    }
}
