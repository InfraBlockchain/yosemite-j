package io.yosemiteblockchain.data.remote.abi;

import com.google.gson.annotations.Expose;

public class EosAbiTypeDef {
    @Expose
    public String new_type_name; // fixed_string32

    @Expose
    public String type;
}
