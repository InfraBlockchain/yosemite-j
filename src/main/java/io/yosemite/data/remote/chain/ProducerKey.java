package io.yosemite.data.remote.chain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.yosemite.data.types.TypeAccountName;

/**
 * @author Eugene Chung
 */
public class ProducerKey {
    @Expose
    @SerializedName("producer_name")
    private TypeAccountName producerName;

    @Expose
    @SerializedName("block_signing_key")
    private String blockSigningKey;

    public TypeAccountName getProducerName() {
        return producerName;
    }

    public void setProducerName(TypeAccountName producerName) {
        this.producerName = producerName;
    }

    public String getBlockSigningKey() {
        return blockSigningKey;
    }

    public void setBlockSigningKey(String blockSigningKey) {
        this.blockSigningKey = blockSigningKey;
    }
}
