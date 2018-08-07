/*
 * Copyright (c) 2017-2018 PLACTAL.
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.yosemite.util;


public class StringUtils {
    public static boolean isEmpty( CharSequence data ) {
      return ( null == data ) || ( data.length() <= 0);
   }

    /**
     * Converts bytesString to java String.
     * @param hexString hex-value concanated string e.g. 4962061d207573657233
     * @return the result of conversion e.g. I am user3
     */
    public static String convertHexToString(String hexString) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //4962061d207573657233 split into two characters 49, 62, 06, ...
        for (int i = 0; i < hexString.length() - 1; i += 2) {
            String output = hexString.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);

            temp.append(decimal);
        }

        return sb.toString();
    }
}
