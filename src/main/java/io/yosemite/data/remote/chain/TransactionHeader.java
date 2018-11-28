package io.yosemite.data.remote.chain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.yosemite.crypto.util.BitUtils;
import io.yosemite.crypto.util.HexUtils;
import io.yosemite.data.types.EosType;
import io.yosemite.util.Utils;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class TransactionHeader implements EosType.Packer {
    @Expose
    private String expiration;

    @Expose
    @SerializedName("ref_block_num")
    private int refBlockNum;

    @Expose
    @SerializedName("ref_block_prefix")
    private long refBlockPrefix;

    @Expose
    @SerializedName("max_net_usage_words")
    private long maxNetUsageWords;

    @Expose
    @SerializedName("max_cpu_usage_ms")
    private long maxCpuUsageMs;

    @Expose
    @SerializedName("delay_sec")
    private long delaySec;

    public TransactionHeader() {
    }

    public TransactionHeader(TransactionHeader other) {
        this.expiration = other.expiration;
        this.refBlockNum = other.refBlockNum;
        this.refBlockPrefix = other.refBlockPrefix;
        this.maxNetUsageWords = other.maxNetUsageWords;
        this.maxCpuUsageMs = other.maxCpuUsageMs;
        this.delaySec = other.delaySec;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    private Date getExpirationAsDate(String dateStr) {
        DateFormat sdf = Utils.SIMPLE_DATE_FORMAT_FOR_EOS.get();
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public void setReferenceBlock(String refBlockIdAsSha256) {
        refBlockNum = new BigInteger(1, HexUtils.toBytes(refBlockIdAsSha256.substring(0, 8))).intValue();

        refBlockPrefix = //new BigInteger( 1, HexUtils.toBytesReversed( refBlockIdAsSha256.substring(16,24))).longValue();
                BitUtils.uint32ToLong(HexUtils.toBytes(refBlockIdAsSha256.substring(16, 24)), 0); // BitUtils treats bytes in little endian.
        // so, no need to reverse bytes.
    }

    public int getRefBlockNum() {
        return refBlockNum;
    }

    public long getRefBlockPrefix() {
        return refBlockPrefix;
    }

    public long getMaxNetUsageWords() {
        return maxNetUsageWords;
    }

    public long getMaxCpuUsageMs() {
        return maxCpuUsageMs;
    }

    public long getDelaySec() {
        return delaySec;
    }

    @Override
    public void pack(EosType.Writer writer) {
        writer.putIntLE((int) (getExpirationAsDate(expiration).getTime() / 1000)); // ms -> sec
        writer.putShortLE((short) (refBlockNum & 0xFFFF));
        writer.putIntLE((int)(refBlockPrefix));
        writer.putVariableUInt(maxNetUsageWords);
        writer.putVariableUInt(maxCpuUsageMs);
        writer.putVariableUInt(delaySec);
    }
}
