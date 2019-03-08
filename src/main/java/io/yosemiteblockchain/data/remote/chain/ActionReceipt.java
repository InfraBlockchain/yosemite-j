package io.yosemiteblockchain.data.remote.chain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.yosemiteblockchain.data.types.TypeAccountName;

import java.util.Map;

public class ActionReceipt {

    @Expose
    private String receiver;

    @Expose
    @SerializedName("act_digest")
    private String actDigest;

    @Expose
    @SerializedName("global_sequence")
    private long globalSequence;

    @Expose
    @SerializedName("recv_sequence")
    private long recvSequence;

    @Expose
    @SerializedName("auth_sequence")
    private Map<TypeAccountName, Long> authSequence;

    @Expose
    @SerializedName("code_sequence")
    private long codeSequence;

    @Expose
    @SerializedName("abi_sequence")
    private long abiSequence;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getActDigest() {
        return actDigest;
    }

    public void setActDigest(String actDigest) {
        this.actDigest = actDigest;
    }

    public long getGlobalSequence() {
        return globalSequence;
    }

    public void setGlobalSequence(long globalSequence) {
        this.globalSequence = globalSequence;
    }

    public long getRecvSequence() {
        return recvSequence;
    }

    public void setRecvSequence(long recvSequence) {
        this.recvSequence = recvSequence;
    }

    public Map<TypeAccountName, Long> getAuthSequence() {
        return authSequence;
    }

    public void setAuthSequence(Map<TypeAccountName, Long> authSequence) {
        this.authSequence = authSequence;
    }

    public long getCodeSequence() {
        return codeSequence;
    }

    public void setCodeSequence(long codeSequence) {
        this.codeSequence = codeSequence;
    }

    public long getAbiSequence() {
        return abiSequence;
    }

    public void setAbiSequence(long abiSequence) {
        this.abiSequence = abiSequence;
    }
}
