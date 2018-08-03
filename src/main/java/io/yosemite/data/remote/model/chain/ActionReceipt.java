package io.yosemite.data.remote.model.chain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
    @SerializedName("code_sequence")
    private long codeSequence;

    @Expose
    @SerializedName("abi_sequence")
    private long abiSequence;
}
