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

package org.wits.parsers.block;

import org.wits.debugger.WITSDebugger;
import org.wits.parsers.WITSParser;

/**
 *
 * @author FJ
 */
public class NoteParser implements WITSParser{

    private String uncleanSGML = null;
    private WITSDebugger debugger = null;
    
    /**
     *
     * @param debugger
     */
    public void setDebugger(WITSDebugger debugger){
        this.debugger = debugger;
    }

    /**
     *
     * @param uncleanSGML
     */
    public NoteParser(String uncleanSGML) {
        this.uncleanSGML = uncleanSGML;
    }

    /**
     *
     * @return
     */
    public String getProcessedText() {
        debugger.addLineBreak();
        debugger.showDebugMessage("NoteIC", 0, "NoteIC Invoked.");
        System.out.println("PROCESSING");
        int offset = 0;
        StringBuilder _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("{warning:", offset);

            if (l_loc == -1) {
                //No warnings
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            debugger.showDebugMessage("NoteIC", l_loc, "[WARNING] found.");

            int r_loc = uncleanSGML.indexOf("<LB>", l_loc + 1);

            String warningText = uncleanSGML.substring(l_loc, r_loc);
            //Get warning label
            String warningLabel = warningText.substring(21, warningText.length() - 1);
            //Find if there is any warning text           

            //System.out.println("WARNING_LABEL:" + warningLabel);
            _handle.append(uncleanSGML.substring(offset, l_loc));
            debugger.showDebugMessage("NoteIC", l_loc, "Adding caution tags.");
            _handle.append("<caution><para>");
            _handle.append(warningLabel);
            _handle.append("</para></caution>");
            offset = r_loc;
        }
        debugger.showDebugMessage("NoteIC", uncleanSGML.length(), "Updating Tree...Done.");
        uncleanSGML = _handle.toString();

        //handle note
        offset = 0;
        _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("{note:", offset);

            if (l_loc == -1) {
                //No warnings
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            debugger.showDebugMessage("NoteIC", l_loc, "[NOTE] found.");

            int r_loc = uncleanSGML.indexOf("<LB>", l_loc + 1);

            String warningText = uncleanSGML.substring(l_loc, r_loc);
            //Get warning label
            String warningLabel = warningText.substring(19, warningText.length() - 1);
            //Find if there is any warning text           

            //System.out.println("WARNING_LABEL:" + warningLabel);
            _handle.append(uncleanSGML.substring(offset, l_loc));
            debugger.showDebugMessage("NoteIC", l_loc, "Adding note tags.");
            _handle.append("<note><para>");
            _handle.append(warningLabel);
            _handle.append("</para></note>");
            offset = r_loc;
        }

        uncleanSGML = _handle.toString();

        //handle note
        offset = 0;
        _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("{info:", offset);

            if (l_loc == -1) {
                //No warnings
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            debugger.showDebugMessage("NoteIC", l_loc, "[INFO] found.");
            int r_loc = uncleanSGML.indexOf("{info}", l_loc + 1);

            String warningText = uncleanSGML.substring(l_loc, r_loc);
            //Get warning label
            String warningLabel = warningText.substring(19, warningText.length() - 1);
            //Find if there is any warning text           

            System.out.println("WARNING_LABEL:" + warningLabel);
            _handle.append(uncleanSGML.substring(offset, l_loc));
            debugger.showDebugMessage("NoteIC", l_loc, "Adding info tags.");
            _handle.append("<note><para>");
            _handle.append(warningLabel);
            _handle.append("</para></note>");
            offset = r_loc;
        }
        uncleanSGML = _handle.toString();

        //handle note
        offset = 0;
        _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("{note:", offset);

            if (l_loc == -1) {
                //No warnings
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            debugger.showDebugMessage("NoteIC", l_loc, "[NOTE] found.");

            int r_loc = uncleanSGML.indexOf("<LB>", l_loc + 1);

            String warningText = uncleanSGML.substring(l_loc, r_loc);
            //Get warning label
            String warningLabel = warningText.substring(19, warningText.length() - 1);
            //Find if there is any warning text           

            //System.out.println("WARNING_LABEL:" + warningLabel);
            _handle.append(uncleanSGML.substring(offset, l_loc));
            debugger.showDebugMessage("NoteIC", l_loc, "Adding note tags.");
            _handle.append("<note><para>");
            _handle.append(warningLabel);
            _handle.append("</para></note>");
            offset = r_loc;
        }

        uncleanSGML = _handle.toString();

        //handle tip
        offset = 0;
        _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("{tip:", offset);

            if (l_loc == -1) {
                //No tips
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            debugger.showDebugMessage("NoteIC", l_loc, "[TIP] found.");
            int r_loc = uncleanSGML.indexOf("<LB>", l_loc + 1);

            String warningText = uncleanSGML.substring(l_loc, r_loc);
            //Get warning label
            String warningLabel = warningText.substring(19, warningText.length() - 1);
            //Find if there is any warning text           

            //System.out.println("WARNING_LABEL:" + warningLabel);
            _handle.append(uncleanSGML.substring(offset, l_loc));
            debugger.showDebugMessage("NoteIC", l_loc, "Adding tip tags.");
            _handle.append("<note><para>");
            _handle.append(warningLabel);
            _handle.append("</para></note>");
            offset = r_loc;
        }

        debugger.showDebugMessage("NoteIC", uncleanSGML.length(), "Updating Tree...Done.");
        return uncleanSGML;
    }
}
