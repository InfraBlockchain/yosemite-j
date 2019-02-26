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

import com.google.gson.annotations.Expose;
import io.yosemite.Consts;
import io.yosemite.crypto.ec.EosPublicKey;

import java.util.ArrayList;
import java.util.List;


public class TypeAuthority implements EosType.Packer {

    @Expose
    private int threshold;

    @Expose
    private List<TypeKeyWeight> keys;

    @Expose
    private List<TypePermissionAndWeight> accounts;

    @Expose
    private List<TypeWaitWeight> waits;

    public TypeAuthority(int threshold, List<TypeKeyWeight> keyWeightList,
                         List<TypePermissionAndWeight> permissionAndWeightList, List<TypeWaitWeight> waitWeightList) {
        this.threshold = threshold;

        if (keyWeightList == null) {
            keys = new ArrayList<>();
        } else {
            keys = keyWeightList;
        }

        if (permissionAndWeightList == null) {
            accounts = new ArrayList<>();
        } else {
            accounts = permissionAndWeightList;
        }

        if (waitWeightList == null) {
            waits = new ArrayList<>();
        } else {
            waits = waitWeightList;
        }
    }

    private static <T> List<T> createList(T item) {
        ArrayList<T> retList = new ArrayList<>();
        retList.add(item);

        return retList;
    }

    public TypeAuthority(TypeKeyWeight oneKey, long uint32DelaySec) {
        this(1, createList(oneKey), null, null);

        if (uint32DelaySec > 0) {
            threshold = 2;
            waits = createList(new TypeWaitWeight(uint32DelaySec, 1));
        }
    }

    public TypeAuthority(int threshold, TypePublicKey pubKey, String permission) {
        this(threshold,
                (null == pubKey ? null : createList(new TypeKeyWeight(pubKey, (short) 1))),
                (null == permission ? null : createList(new TypePermissionAndWeight(permission))), null);
    }

    @Override
    public void pack(EosType.Writer writer) {

        writer.putIntLE(threshold);

        // keys
        writer.putCollection(keys);

        // accounts
        writer.putCollection(accounts);

        // waits
        writer.putCollection(waits);
    }

    public static TypeAuthorityBuilder Builder() {
        return new TypeAuthorityBuilder();
    }

    /**
     * Builder for the authority used as the source of new account creation.
     */
    public static class TypeAuthorityBuilder {

        private int threshold;
        private int weightSum;
        private final List<TypeKeyWeight> keyWeightList = new ArrayList<>();
        private final List<TypePermissionAndWeight> permissionAndWeightList = new ArrayList<>();

        public TypeAuthority build() {
            if (threshold == 0) threshold = weightSum;
            return new TypeAuthority(threshold, keyWeightList, permissionAndWeightList, null);
        }

        /**
         * Set threshold for the weights.
         * @param threshold integer value
         */
        public TypeAuthorityBuilder setThreshold(int threshold) {
            if (threshold <= 0) throw new IllegalArgumentException("wrong threshold");
            this.threshold = threshold;
            return this;
        }

        /**
         * Add the public key and weight 1
         * @param publicKey public key string
         */
        public TypeAuthorityBuilder addPublicKey(String publicKey) {
            return addPublicKey(publicKey, (short)1);
        }

        /**
         * Add the public key and weight
         * @param publicKey public key string
         * @param weight integer value
         */
        public TypeAuthorityBuilder addPublicKey(String publicKey, int weight) {
            if (weight <= 0) throw new IllegalArgumentException("wrong weight");

            keyWeightList.add(new TypeKeyWeight(TypePublicKey.from(new EosPublicKey(publicKey)), (short)weight));
            weightSum += weight;
            return this;
        }

        /**
         * Add the account name for the 'active' permission with weight 1
         * @param accountName account name
         */
        public TypeAuthorityBuilder addAccount(String accountName) {
            if (accountName == null) throw new IllegalArgumentException("accountName cannot be null.");

            return addAccount(accountName, Consts.ACTIVE_PERMISSION_NAME, (short)1);
        }

        /**
         * Add the account name for the specified permission with weight 1
         * @param accountName account name
         * @param permissionName the name of the permission
         */
        public TypeAuthorityBuilder addAccount(String accountName, String permissionName) {
            if (accountName == null) throw new IllegalArgumentException("accountName cannot be null.");
            if (permissionName == null) throw new IllegalArgumentException("permissionName cannot be null.");

            return addAccount(accountName, permissionName, (short)1);
        }

        /**
         * Add the account name for the 'active' permission with speicifed weight
         * @param accountName account name
         * @param weight integer value
         */
        public TypeAuthorityBuilder addAccount(String accountName, int weight) {
            return addAccount(accountName, Consts.ACTIVE_PERMISSION_NAME, weight);
        }

        /**
         * Add the account name for the specified permission and weight.
         * @param accountName account name
         * @param permissionName the name of the permission
         * @param weight integer value
         */
        public TypeAuthorityBuilder addAccount(String accountName, String permissionName, int weight) {
            if (accountName == null) throw new IllegalArgumentException("accountName cannot be null.");
            if (permissionName == null) throw new IllegalArgumentException("permissionName cannot be null.");
            if (weight <= 0) throw new IllegalArgumentException("wrong weight");

            permissionAndWeightList.add(new TypePermissionAndWeight(accountName, permissionName, (short)weight));
            weightSum += weight;
            return this;
        }
    }
}
