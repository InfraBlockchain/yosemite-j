
package io.yosemiteblockchain.data.remote.history.transaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Header {

    @Expose
    private Integer timestamp;

    @Expose
    private String producer;

    @Expose
    private Integer confirmed;

    @Expose
    private String previous;

    @Expose
    @SerializedName("transaction_mroot")
    private String transactionMroot;

    @Expose
    @SerializedName("action_mroot")
    private String actionMroot;

    @Expose
    @SerializedName("schedule_version")
    private Integer scheduleVersion;

    @Expose
    @SerializedName("new_producers")
    private Object newProducers;

    @Expose
    @SerializedName("header_extensions")
    private List<Object> headerExtensions;

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public Integer getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Integer confirmed) {
        this.confirmed = confirmed;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getTransactionMroot() {
        return transactionMroot;
    }

    public void setTransactionMroot(String transactionMroot) {
        this.transactionMroot = transactionMroot;
    }

    public String getActionMroot() {
        return actionMroot;
    }

    public void setActionMroot(String actionMroot) {
        this.actionMroot = actionMroot;
    }

    public Integer getScheduleVersion() {
        return scheduleVersion;
    }

    public void setScheduleVersion(Integer scheduleVersion) {
        this.scheduleVersion = scheduleVersion;
    }

    public Object getNewProducers() {
        return newProducers;
    }

    public void setNewProducers(Object newProducers) {
        this.newProducers = newProducers;
    }

    public List<Object> getHeaderExtensions() {
        return headerExtensions;
    }

    public void setHeaderExtensions(List<Object> headerExtensions) {
        this.headerExtensions = headerExtensions;
    }

}
