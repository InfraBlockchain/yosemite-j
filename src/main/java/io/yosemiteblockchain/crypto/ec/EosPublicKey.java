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
package io.yosemiteblockchain.crypto.ec;

import io.yosemiteblockchain.crypto.digest.Ripemd160;
import io.yosemiteblockchain.crypto.util.BitUtils;
import io.yosemiteblockchain.util.RefValue;

import java.util.Arrays;

public class EosPublicKey {
    private static final String PREFIX_YOSEMITE = "YOS";
    private static final String LEGACY_PREFIX_EOS = "EOS";
    private static final String PREFIX = "PUB";

    private static final int CHECK_BYTE_LEN = 4;

    private final long mCheck;
    private final CurveParam mCurveParam;
    private final byte[] mData;
    private String mBase58Str;

    public static class IllegalPubKeyFormatException extends IllegalArgumentException {
        public IllegalPubKeyFormatException(String pubkeyStr) {
            super("invalid public key : " + pubkeyStr);
        }
    }

    public EosPublicKey(byte[] data) {
        this(data, EcTools.getCurveParam(CurveParam.SECP256_K1));
    }

    public EosPublicKey(byte[] data, CurveParam curveParam) {
        mData = Arrays.copyOf(data, 33);
        mCurveParam = curveParam;
        mCheck = BitUtils.uint32ToLong(Ripemd160.from(mData, 0, mData.length).bytes(), 0);
    }

    public EosPublicKey(String base58Str) {
        RefValue<Long> checksumRef = new RefValue<>();

        String[] parts = EosEcUtil.safeSplitEosCryptoString(base58Str);

        if (parts.length == 3) {
            mCurveParam = EosEcUtil.getCurveParamFrom(parts[1]);
        } else if (parts.length == 1) {
            mCurveParam = EcTools.getCurveParam(CurveParam.SECP256_K1);
        } else {
            throw new IllegalPubKeyFormatException(base58Str);
        }

        int curveParamType = mCurveParam.getCurveParamType();

        if (curveParamType == CurveParam.SECP256_K1) {

            String prefix = null;

            if (base58Str.startsWith(PREFIX_YOSEMITE)) {
                prefix = PREFIX_YOSEMITE;
            } else if (base58Str.startsWith(LEGACY_PREFIX_EOS)) {
                prefix = LEGACY_PREFIX_EOS;
            }

            if (prefix != null) {
                mData = EosEcUtil.getBytesIfMatchedRipemd160(base58Str.substring(prefix.length()), null, checksumRef);
            } else {
                throw new IllegalPubKeyFormatException(base58Str);
            }
        } else if (curveParamType == CurveParam.SECP256_R1 && PREFIX.equals(parts[0])) {
            mData = EosEcUtil.getBytesIfMatchedRipemd160(parts[2], parts[1], checksumRef);
        } else {
            throw new IllegalPubKeyFormatException(base58Str);
        }

        mCheck = checksumRef.data;

        mBase58Str = base58Str;
    }

    public byte[] getBytes() {
        return mData;
    }

    @Override
    public String toString() {

        if (mBase58Str == null) {
            boolean isR1 = mCurveParam.isType(CurveParam.SECP256_R1);

            mBase58Str = EosEcUtil.encodeEosCrypto(isR1 ? PREFIX : PREFIX_YOSEMITE, isR1 ? mCurveParam : null, mData);
        }

        return mBase58Str;

//        byte[] postfixBytes = isR1 ? EosEcUtil.PREFIX_R1.getBytes() : new byte[0] ;
//        byte[] toDigest = new byte[mData.length + postfixBytes.length];
//        System.arraycopy( mData, 0, toDigest, 0, mData.length);
//
//        if ( postfixBytes.length > 0) {
//            System.arraycopy(postfixBytes, 0, toDigest, mData.length, postfixBytes.length);
//        }
//
//        byte[] digest = Ripemd160.from( toDigest ).bytes();
//        byte[] result = new byte[ CHECK_BYTE_LEN + mData.length];
//
//        System.arraycopy( mData, 0, result, 0, mData.length);
//        System.arraycopy( digest, 0, result, mData.length, CHECK_BYTE_LEN);
//
//        if ( isR1 ){
//            return EosEcUtil.concatEosCryptoStr(PREFIX , EosEcUtil.PREFIX_R1, Base58.encode( result ) );
//        }
//        else {
//            return LEGACY_PREFIX + Base58.encode( result ) ;
//        }
    }

    @Override
    public int hashCode() {
        return (int) (mCheck & 0xFFFFFFFFL);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;

        if (null == other || getClass() != other.getClass())
            return false;

        return BitUtils.areEqual(this.mData, ((EosPublicKey) other).mData);
    }

    public boolean isCurveParamK1() {
        return (mCurveParam == null || CurveParam.SECP256_K1 == mCurveParam.getCurveParamType());
    }
}