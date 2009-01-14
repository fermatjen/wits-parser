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

package org.wits.test;

/**
 *
 * @author FJ
 */
public class CodeHandler {

    private String sourceText = null;

    public CodeHandler(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getProcessedText(){

        int offset = 0;
        StringBuilder _handle = new StringBuilder();
        
        while (true) {
            int l_loc = sourceText.indexOf("<screen>", offset);
            int r_loc = sourceText.indexOf("</screen>", l_loc);
            
            if (l_loc == -1 || r_loc == -1) {                
                _handle.append(sourceText.substring(offset, sourceText.length()));
                break;
            }

            _handle.append(sourceText.substring(offset, l_loc));
            //_handle.append(sourceText.substring(r_loc + 9, r_loc + 10));
            offset = r_loc + 9;
        }
        sourceText = _handle.toString();

        offset = 0;
        _handle = new StringBuilder();

        while (true) {
            int l_loc = sourceText.indexOf("<literal>", offset);
            int r_loc = sourceText.indexOf("</literal>", l_loc);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(sourceText.substring(offset, sourceText.length()));
                break;
            }

            _handle.append(sourceText.substring(offset, l_loc));
            //_handle.append(sourceText.substring(r_loc + 9, r_loc + 10));
            offset = r_loc + 10;
        }

        sourceText = _handle.toString();
        //System.out.println(sourceText);
        return sourceText;
    }

    

}
