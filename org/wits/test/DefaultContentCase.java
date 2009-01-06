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
public class DefaultContentCase implements WITSCase{

    private String source = new String();

    public void initCase(String source) {
        this.source = source;
    }


    public String runCase() {

        int offset = 0;

        int l1_loc = source.indexOf("http//wikis.sun.com/mywiki", offset);

        if(l1_loc != -1){
            return "FAIL";
        }

        return "PASS";
    }

}
