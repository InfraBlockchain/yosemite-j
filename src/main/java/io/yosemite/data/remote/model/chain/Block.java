package io.yosemite.data.remote.model.chain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Block {

    @Expose
    private String timestamp;

    @Expose
    private String producer;

    @Expose
    private Long confirmed;

    @Expose
    private String previous;

    @Expose
    @SerializedName("transaction_mroot")
    private String transactionMerkleRoot;

    @Expose
    @SerializedName("action_mroot")
    private String actionMerkleRoot;

    @Expose
    @SerializedName("schedule_version")
    private String scheduleVersion;

    @Expose
    @SerializedName("new_producers")
    private String newProducers;

    @Expose
    @SerializedName("producer_signature")
    private String producerSignature;

    @Expose
    private String id;

    @Expose
    @SerializedName("block_num")
    private Long blockNum;

    @Expose
    @SerializedName("ref_block_prefix")
    private Long refBlockPrefix;

    public Block() {
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public Long getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Long confirmed) {
        this.confirmed = confirmed;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getTransactionMerkleRoot() {
        return transactionMerkleRoot;
    }

    public void setTransactionMerkleRoot(String transactionMerkleRoot) {
        this.transactionMerkleRoot = transactionMerkleRoot;
    }

    public String getActionMerkleRoot() {
        return actionMerkleRoot;
    }

    public void setActionMerkleRoot(String actionMerkleRoot) {
        this.actionMerkleRoot = actionMerkleRoot;
    }

    public String getScheduleVersion() {
        return scheduleVersion;
    }

    public void setScheduleVersion(String scheduleVersion) {
        this.scheduleVersion = scheduleVersion;
    }

    public String getNewProducers() {
        return newProducers;
    }

    public void setNewProducers(String newProducers) {
        this.newProducers = newProducers;
    }

    public String getProducerSignature() {
        return producerSignature;
    }

    public void setProducerSignature(String producerSignature) {
        this.producerSignature = producerSignature;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(Long blockNum) {
        this.blockNum = blockNum;
    }

    public Long getRefBlockPrefix() {
        return refBlockPrefix;
    }

    public void setRefBlockPrefix(Long refBlockPrefix) {
        this.refBlockPrefix = refBlockPrefix;
    }
}