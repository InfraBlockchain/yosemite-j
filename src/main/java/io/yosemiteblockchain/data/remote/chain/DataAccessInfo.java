package io.yosemiteblockchain.data.remote.chain;

import com.google.gson.annotations.Expose;
import io.yosemiteblockchain.data.types.TypeAccountName;
import io.yosemiteblockchain.data.types.TypeScopeName;


public class DataAccessInfo {
    //public enum Type { read, write };

    @Expose
    private String type; // access type

    @Expose
    private TypeAccountName code;

    @Expose
    private TypeScopeName scope;

    @Expose
    private long sequence; // uint64_t
}
