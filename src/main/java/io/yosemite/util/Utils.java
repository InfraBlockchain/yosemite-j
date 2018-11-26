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

import com.google.gson.GsonBuilder;
import io.yosemite.data.remote.chain.TransactionExtension;
import io.yosemite.data.remote.history.transaction.Timestamp;
import io.yosemite.data.util.*;

import java.io.Closeable;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class Utils {

    public final static ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_FOR_EOS =
            ThreadLocal.withInitial(() -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                return sdf;
            });

    public final static ThreadLocal<Pattern> EOSIO_ASSET_PATTERN =
            ThreadLocal.withInitial(() -> {
                return Pattern.compile("^([0-9]+)\\.?([0-9]*)([ ][a-zA-Z0-9]{1,7})?$");//\\s(\\w)$");
            });

    public static void closeSilently(Closeable c) {
        if (null != c) {
            try {
                c.close();
            } catch (Throwable t) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            }
        }
    }

    public static long parseLongSafely(String content, int defaultValue) {
        if (null == content) return defaultValue;

        try {
            return Long.parseLong(content);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static int parseIntSafely(String content, int defaultValue) {
        if (null == content) return defaultValue;

        try {
            return Integer.parseInt(content);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static GsonBuilder createYosemiteJGsonBuilder() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(String.class, new StringTypeAdapter())
                .registerTypeAdapter(TransactionExtension.class, new TransactionExtensionTypeAdapter())
                .registerTypeAdapterFactory(new GsonYosemiteTypeAdapterFactory())
                .registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY)
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation();
    }

    public static String prettyPrintJson(Object object) {
        return createYosemiteJGsonBuilder().setPrettyPrinting().create().toJson(object);
    }

    public static String makeWebAssembly128BitIntegerAsHexString(long valueHigh, long valueLower) {
        BigInteger bigInteger = makeWebAssembly128BitInteger(valueHigh, valueLower);
        return String.format("0x%032x", bigInteger);
    }

    public static BigInteger makeWebAssembly128BitInteger(long valueHigh, long valueLower) {
        BigInteger wasm128BitInteger = BigInteger.valueOf(Long.reverseBytes(valueLower));
        wasm128BitInteger = wasm128BitInteger.shiftLeft(64);
        wasm128BitInteger = wasm128BitInteger.add(BigInteger.valueOf(Long.reverseBytes(valueHigh)));
        return wasm128BitInteger;
    }

    /**
     * Converts "yyyy-MM-dd'T'HH:mm:ss" string to java Date.
     * @param eosTimestamp "yyyy-MM-dd'T'HH:mm:ss"
     * @return java Date instance
     * @throws ParseException if eosTimestamp is wrong-formatted
     */
    public static Date convertTimestampToDate(String eosTimestamp) throws ParseException {
        DateFormat sdf = Utils.SIMPLE_DATE_FORMAT_FOR_EOS.get();
        return sdf.parse(eosTimestamp);
    }
}
