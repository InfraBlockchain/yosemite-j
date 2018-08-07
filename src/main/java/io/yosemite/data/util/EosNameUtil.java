package io.yosemite.data.util;

/**
 * From libraries/chain/include/eosio/chain/name.hpp
 * @author Eugene Chung
 */
public class EosNameUtil {
    public static long charToSymbol(char c) {
        if (c >= 'a' && c <= 'z') return (c - 'a') + 6;
        if (c >= '1' && c <= '5') return (c - '1') + 1;
        return 0;
    }

    public static long stringToName(String str) {
        long name = 0;
        for (int i = 0; i < str.length() && i < 12; ++i) {
            name |= (charToSymbol(str.charAt(i)) & 0x1f) << (64 - 5 * (i + 1));
        }

        if (str.length() == 13) {
            name |= charToSymbol(str.charAt(12)) & 0x0F;
        }
        return name;
    }
}
