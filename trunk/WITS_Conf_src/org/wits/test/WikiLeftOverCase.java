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
public class WikiLeftOverCase implements WITSCase {

    private String source = null;

    public void initCase(String source) {
        this.source = source;
    }

    public String runCase() {

        //check leftover case
        int offset = 0;


        int l1_loc = source.indexOf("\r\nh1.", offset);
        int l2_loc = source.indexOf("\r\nh2.", offset);
        int l3_loc = source.indexOf("\r\nh3.", offset);
        int l4_loc = source.indexOf("\r\nh4.", offset);

        int l7_loc = source.indexOf("{code", offset);
        int l8_loc = source.indexOf("{warning", offset);
        int l9_loc = source.indexOf("{noformat", offset);

        int l10_loc = source.indexOf("\r\n||", offset);
        int l11_loc = source.indexOf("\r\n|", offset);

        int l12_loc = source.indexOf("\r\n!", offset);
        
        
        int l13_loc = source.indexOf("\\\\", offset);
        int l14_loc = source.indexOf("{excerpt", offset);
        int l15_loc = source.indexOf("{section", offset);
        int l16_loc = source.indexOf("{column", offset);

        if(l1_loc != -1 || l2_loc != -1 || l3_loc != -1 || l4_loc != -1 ||
                 l7_loc != -1 || l8_loc != -1 ||
                l9_loc != -1 || l10_loc != -1 || l11_loc != -1 || l12_loc != -1
                || l13_loc != -1 || l14_loc != -1 || l15_loc != -1 || l16_loc != -1){
                return "FAIL";
        }

        return "PASS";

    }
}
