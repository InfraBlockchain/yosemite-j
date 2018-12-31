package io.yosemite.services;

import io.yosemite.Consts;
import io.yosemite.data.types.TypePermission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains the API-level transaction parameters.
 */
public class TransactionParameters {
    private final List<TypePermission> permissions = new ArrayList<>();
    private List<String> publicKeys = new ArrayList<>();
    private String delegatedTransactionFeePayer;
    private String transactionVoteTarget;
    private int txExpirationInMillis = -1;

    void setDelegatedTransactionFeePayer(String delegatedTransactionFeePayer) {
        this.delegatedTransactionFeePayer = delegatedTransactionFeePayer;
    }

    public static TransactionParametersBuilder Builder() {
        return new TransactionParametersBuilder();
    }

    /**
     * Builder for the API-level transaction parameters.
     */
    public static class TransactionParametersBuilder {

        private final TransactionParameters txParameters = new TransactionParameters();

        public TransactionParameters build() {
            txParameters.publicKeys = Collections.unmodifiableList(txParameters.publicKeys);
            return txParameters;
        }

        /**
         * Add the account name which needs the signature for 'active' permission.
         * @param accountName account name
         */
        public TransactionParametersBuilder addPermission(String accountName) {
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
        public TransactionParametersBuilder addPermission(String accountName, String permissionName) {
            if (accountName == null) {
                throw new IllegalArgumentException("accountName cannot be null.");
            }
            if (permissionName == null) {
                throw new IllegalArgumentException("permissionName cannot be null.");
            }
            txParameters.permissions.add(new TypePermission(accountName, permissionName));
            return this;
        }

        /**
         * Add the public key of the account to sign the transaction.
         * If it's not provided, performance problem would be occurred.
         * @param publicKey public key string
         */
        public TransactionParametersBuilder addPublicKey(String publicKey) {
            if (publicKey == null) {
                throw new IllegalArgumentException("publicKey cannot be null.");
            }
            txParameters.publicKeys.add(publicKey);
            return this;
        }

        /**
         * Set the account name that pays the transaction fee.
         * The transaction with this setting should be provided signature of the fee payer account before being pushed to the blockchain.
         *
         * @param delegatedTransactionFeePayer fee payer account name
         */
        public TransactionParametersBuilder setDelegatedTransactionFeePayer(String delegatedTransactionFeePayer) {
            txParameters.delegatedTransactionFeePayer = delegatedTransactionFeePayer;
            return this;
        }

        /**
         * Set the transaction vote target account for PoT.
         *
         * @param transactionVoteTarget The account name to vote to
         */
        public TransactionParametersBuilder setTransactionVoteTarget(String transactionVoteTarget) {
            txParameters.transactionVoteTarget = transactionVoteTarget;
            return this;
        }

        /**
         * Set the transaction expiration time.
         *
         * @param txExpirationInMillis expiration time in milliseconds
         */
        public TransactionParametersBuilder setTxExpirationInMillis(int txExpirationInMillis) {
            if (txExpirationInMillis < 0) {
                throw new IllegalArgumentException("txExpirationInMillis cannot be negative.");
            }
            txParameters.txExpirationInMillis = txExpirationInMillis;
            return this;
        }
    }

    public List<TypePermission> getPermissions() {
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
