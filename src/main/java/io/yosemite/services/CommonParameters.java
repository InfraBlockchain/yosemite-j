package io.yosemite.services;

import io.yosemite.Consts;
import io.yosemite.data.types.TypePermissionLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains the API-level common parameters.
 */
public class CommonParameters {
    private List<TypePermissionLevel> permissions = new ArrayList<>();
    private List<String> publicKeys = new ArrayList<>();
    private String delegatedTransactionFeePayer;
    private String transactionVoteTarget;
    private int txExpirationInMillis = -1;

    public static CommonParametersBuilder Builder() {
        return new CommonParametersBuilder();
    }

    /**
     * Builder for the API-level common parameters.
     */
    public static class CommonParametersBuilder {

        private CommonParameters commonParameters = new CommonParameters();

        public CommonParameters build() {
            commonParameters.publicKeys = Collections.unmodifiableList(commonParameters.publicKeys);
            return commonParameters;
        }

        /**
         * Add the account name which needs the signature for 'active' permission.
         * @param accountName account name
         */
        public CommonParametersBuilder addPermission(String accountName) {
            if (accountName == null) {
                throw new IllegalArgumentException("accountName cannot be null.");
            }
            return addPermission(accountName, Consts.ACTIVE_PERMISSION_NAME);
        }

        /**
         * Add the account name which needs the signature for the specific permission.
         * @param accountName account name
         * @param permissionName the name of the permission; usually active
         */
        public CommonParametersBuilder addPermission(String accountName, String permissionName) {
            if (accountName == null) {
                throw new IllegalArgumentException("accountName cannot be null.");
            }
            if (permissionName == null) {
                throw new IllegalArgumentException("permissionName cannot be null.");
            }
            commonParameters.permissions.add(new TypePermissionLevel(accountName, permissionName));
            return this;
        }

        /**
         * Add the public key of the account to sign the transaction.
         * If it's not provided, performance problem would be occurred.
         * @param publicKey public key string
         */
        public CommonParametersBuilder addPublicKey(String publicKey) {
            if (publicKey == null) {
                throw new IllegalArgumentException("publicKey cannot be null.");
            }
            commonParameters.publicKeys.add(publicKey);
            return this;
        }

        /**
         * Set the account name that pays the transaction fee.
         * The transaction with this setting should be provided signature of the fee payer account before being pushed to the blockchain.
         *
         * @param delegatedTransactionFeePayer fee payer account name
         */
        public CommonParametersBuilder setDelegatedTransactionFeePayer(String delegatedTransactionFeePayer) {
            commonParameters.delegatedTransactionFeePayer = delegatedTransactionFeePayer;
            return this;
        }

        /**
         * Set the transaction vote target account for PoT.
         *
         * @param transactionVoteTarget The account name to vote to
         */
        public CommonParametersBuilder setTransactionVoteTarget(String transactionVoteTarget) {
            commonParameters.transactionVoteTarget = transactionVoteTarget;
            return this;
        }

        /**
         * Set the transaction expiration time.
         *
         * @param txExpirationInMillis expiration time in milliseconds
         */
        public CommonParametersBuilder setTxExpirationInMillis(int txExpirationInMillis) {
            if (txExpirationInMillis < 0) {
                throw new IllegalArgumentException("txExpirationInMillis cannot be negative.");
            }
            commonParameters.txExpirationInMillis = txExpirationInMillis;
            return this;
        }
    }

    public List<TypePermissionLevel> getPermissions() {
        return permissions;
    }

    public List<String> getPublicKeys() {
        return publicKeys;
    }

    public String getDelegatedTransactionFeePayer() {
        return delegatedTransactionFeePayer;
    }

    public String getTransactionVoteTarget() {
        return transactionVoteTarget;
    }

    public int getTxExpirationInMillis() {
        return txExpirationInMillis;
    }
}
