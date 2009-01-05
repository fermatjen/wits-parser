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

import org.wits.debugger.WITSDebugger;
import org.wits.parsers.WITSParser;
import org.wits.patterns.StringHandler;

/**
 *
 * @author FJ
 */
public class ScriptParser implements WITSParser{

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
    public ScriptParser(String uncleanSGML) {
        this.uncleanSGML = uncleanSGML;
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
            int l_loc = uncleanSGML.indexOf("!", offset + 1);
            int r_loc = uncleanSGML.indexOf("!", l_loc + 1);

            //System.out.println("LOC:" + l_loc + " ROC:" + r_loc);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            String scriptCandidate = uncleanSGML.substring(l_loc, r_loc);
            //System.out.println("SCAND:" + scriptCandidate);

            if (scriptCandidate.indexOf("<LB>") == -1) {
                //process symbols
                _handle.append(uncleanSGML.substring(offset, l_loc));
                _handle.append("_Figure Placeholder here._");

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
            _handle.append("<note><para>" + bqCandidate.trim() + "</para></note>");

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
            _handle.append("<caution><para>" + bqCandidate.trim() + "</para></caution>");

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
            bqCandidate = handler.replace(bqCandidate, "<LB>\r\n", " ");
            _handle.append("<note><para>" + bqCandidate.trim() + "</para></note>");

            offset = r_loc;
        }
        uncleanSGML = _handle.toString();
        handler = new StringHandler();
        handler.setDebugger(debugger);
        uncleanSGML = handler.replace(uncleanSGML, "{note}", "");

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
            bqCandidate = handler.replace(bqCandidate, "<LB>\r\n", " ");
            _handle.append("<tip><para>" + bqCandidate.trim() + "</para></tip>");

            offset = r_loc;
        }
        uncleanSGML = _handle.toString();
        handler = new StringHandler();
        handler.setDebugger(debugger);
        uncleanSGML = handler.replace(uncleanSGML, "{tip}", "");

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
