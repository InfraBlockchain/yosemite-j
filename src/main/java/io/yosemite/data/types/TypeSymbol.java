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

package io.yosemite.data.types;

import io.yosemite.Consts;
import io.yosemite.util.StringUtils;
import io.yosemite.util.Utils;

/**
 * Represents the token symbol, the precision and the name.
 * For your information, please read <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md">yx.ntoken README</a>.
 * The convenion of the symbol name follows
 * <a href="https://developers.eos.io/eosio-cpp/docs/naming-conventions#section-symbols">Naming Conventions of Symbols</a>
 */
public class TypeSymbol implements EosType.Packer {
    private static final int MAX_PRECISION = 18;

    private static String sCoreSymbolString = Consts.DEFAULT_NATIVE_TOKEN_SYMBOL;
    private static int sCoreSymbolPrecision = Consts.DEFAULT_NATIVE_TOKEN_PRECISION;

    private final long mValue;
    private volatile String form;

    public TypeSymbol(TypeSymbol typeSymbol) {
        mValue = typeSymbol.mValue;
    }

    public static void setFeeToken(int precision, String str) {
        sCoreSymbolString = str;
        sCoreSymbolPrecision = precision;
    }

    public static long stringToSymbol(int precision, CharSequence str) {
        long result = 0;

        if (StringUtils.isEmpty(str)) {
            str = sCoreSymbolString;
            precision = sCoreSymbolPrecision;
        }

        for (int index = 0; index < str.length(); index++) {
            long value = (long) str.charAt(index);

            // check range 'A' to 'Z'
            if ((value < 65) || (value > 90)) {
                throw new IllegalArgumentException("invalid symbol string: " + str);
            }

            result |= value << (8 * (1 + index));
        }

        result |= precision;

        return result;
    }

    public static TypeSymbol fromString(String str) {

        String[] split = str.trim().split(",");
        if (split.length != 2) {
            throw new IllegalArgumentException("invalid symbol string: " + str);
        }

        int precision = Utils.parseIntSafely(split[0], 0);
        if (precision > MAX_PRECISION) {
            throw new IllegalArgumentException("precision should be <= " + MAX_PRECISION + ": " + str);
        }

        return new TypeSymbol(precision, split[1]);
    }

    public static boolean validName(String name) {
        for (int index = 0; index < name.length(); index++) {
            int value = (int) name.charAt(index);

            // check range 'A' to 'Z'
            if ((value < 97) || (value > 122)) {
                return false;
            }
        }

        return true;
    }

    public TypeSymbol() {
        this(sCoreSymbolPrecision, sCoreSymbolString);
    }

    public TypeSymbol(String from) {
        mValue = fromString(from).mValue;
    }

    public TypeSymbol(int precision, CharSequence symbolName) {
        mValue = TypeSymbol.stringToSymbol(precision, symbolName);
    }

    public short decimals() {
        return (short) (mValue & 0xFF);
    }

    public long precision() {
        int decimalVal = decimals();
        if (decimalVal > MAX_PRECISION) {
            throw new IllegalArgumentException("precision should be <= " + MAX_PRECISION + ", precision: " + decimalVal);
        }

        long p10 = 1, p = decimalVal;
        while (p > 0) {
            p10 *= 10;
            --p;
        }

        return p10;
    }

    public String name() {
        long v = mValue;
        v >>= 8;

        StringBuilder result = new StringBuilder(8);
        while (v > 0) {
            result.append((char) (v & 0xFF));
            v >>= 8;
        }

        return result.toString();
    }

    public boolean valid() {
        return (decimals() <= MAX_PRECISION) && validName(name());
    }

    @Override
    public String toString() {
        String form = this.form;
        if (form == null) {
            form = decimals() + "," + name();
            this.form = form;
        }
        return form;
    }

    @Override
    public void pack(EosType.Writer writer) {
        writer.putLongLE(mValue);
    }
}
