package io.yosemiteblockchain.data.remote.chain;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * @author Eugene Chung
 */
public class ProducerScheduleType {
    @Expose
    int version;

    @Expose
    List<ProducerKey> producers;
}
