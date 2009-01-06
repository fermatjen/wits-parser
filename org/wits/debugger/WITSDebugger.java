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

package org.wits.debugger;

/**
 *
 * @author FJ
 */
public class WITSDebugger {

    private boolean isDebuggingOn = false;
    private boolean isWarningOn = true;
    private String debugString = null;

    /**
     *
     * @param isDebuggingOn
     * @param isWarningOn
     */
    public WITSDebugger(boolean isDebuggingOn, boolean isWarningOn) {
        this.isDebuggingOn = isDebuggingOn;
        this.isWarningOn = isWarningOn;
        debugString = new String("WITS Parser 0.1>>>>>>>>>>>>>>>>>>>>>\r\n");
    }
    
   
    /**
     *
     * @param interceptor
     * @param message
     */
    public void showDebugMessage(String interceptor, String message) {
        showDebugMessage(interceptor, -1, message);
    }

    /**
     *
     */
    public void addLineBreak() {
        if (isDebuggingOn) {
           // System.out.println("");
            debugString = debugString+"\r\n";
        }
    }
    
    /**
     *
     * @return
     */
    public String getDebugString(){
        debugString = debugString + "\r\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>";
        return debugString;
    }

    /**
     *
     * @param interceptor
     * @param line
     * @param message
     */
    public void showWarningMessage(String interceptor, int line, String message) {
        StringBuilder paddedText = new StringBuilder(interceptor + " Pos:" + line);
        int maxPad = 35;
        int curPad = paddedText.length();
        if (curPad < maxPad) {
            int diff = maxPad - curPad;
            for (int i = 0; i < diff; i++) {
                paddedText.insert(curPad, " ");
            }
        }
        if (isWarningOn) {
            //System.out.println("WARNING:" + paddedText.toString() + "- " + message);
            debugString = debugString+"WARNING:" + paddedText.toString() + "- " + message+"\r\n";
        }
    }

    /**
     *
     * @param interceptor
     * @param line
     * @param message
     */
    public void showDebugMessage(String interceptor, int line, String message) {
        StringBuilder paddedText = new StringBuilder(interceptor + " Pos:" + line);
        int maxPad = 35;
        int curPad = paddedText.length();
        if (curPad < maxPad) {
            int diff = maxPad - curPad;
            for (int i = 0; i < diff; i++) {
                paddedText.insert(curPad, " ");
            }
        }
        if (isDebuggingOn) {
            //System.out.println("WITSMSG:" + paddedText.toString() + "- " + message);
            debugString = debugString+"WITSMSG:" + paddedText.toString() + "- " + message+"\r\n";
        }
    }
}
