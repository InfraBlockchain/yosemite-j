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

import io.yosemite.util.StringUtils;
import io.yosemite.util.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the token amount and its symbol.
 * For your information, please read <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md">yx.ntoken README</a>.
 * @see TypeSymbol
 */
public class TypeAsset implements EosType.Packer {

    public static final long MAX_AMOUNT = (1L << 62) - 1;

    private final long mAmount;
    private final TypeSymbol mSymbol;
    private volatile String form;

    public TypeAsset(String value) {

        value = value.trim();

        Pattern pattern = Utils.EOSIO_ASSET_PATTERN.get();
        Matcher matcher = pattern.matcher(value);

        if (matcher.find()) {
            String beforeDotVal = matcher.group(1), afterDotVal = matcher.group(2);

            String symbolStr = StringUtils.isEmpty(matcher.group(3)) ? null : matcher.group(3).trim();

            mAmount = Long.valueOf(beforeDotVal + afterDotVal);
            mSymbol = new TypeSymbol(afterDotVal.length(), symbolStr);
        } else {
            this.mAmount = 0;
            this.mSymbol = new TypeSymbol();
        }
    }

    public TypeAsset(long amount) {
        this(amount, new TypeSymbol());
    }

    public TypeAsset(long amount, TypeSymbol symbol) {
        this.mAmount = amount;
        this.mSymbol = symbol;
    }

    public TypeAsset(TypeAsset typeAsset) {
        this.mAmount = typeAsset.mAmount;
        this.mSymbol = typeAsset.mSymbol;
    }

    public boolean isAmountInRange() {
        return -MAX_AMOUNT <= mAmount && mAmount <= MAX_AMOUNT;
    }

    public boolean isValid() {
        return isAmountInRange() && (mSymbol != null) && mSymbol.valid();
    }


    public short decimals() {
        return (mSymbol != null) ? mSymbol.decimals() : 0;
    }

    public long precision() {
        return (mSymbol != null) ? mSymbol.precision() : 0;
    }

    public String symbolName() {
        if (mSymbol != null) {
            return mSymbol.name();
        }

        return "";
    }

    public long getAmount() {
        return mAmount;
    }

    @Override
    public String toString() {
        String form = this.form;
        if (form == null) {
            long precisionVal = precision();
            String result = String.valueOf(mAmount / precisionVal);

            if (decimals() > 0) {
                long fract = mAmount % precisionVal;
                result += "." + String.valueOf(precisionVal + fract).substring(1);
            }

            form = result + " " + symbolName();
            this.form = form;
        }
        return form;
    }

    @Override
    public void pack(EosType.Writer writer) {

        writer.putLongLE(mAmount);

        if (mSymbol != null) {
            mSymbol.pack(writer);
        } else {
            writer.putLongLE(0);
        }
    }

}
