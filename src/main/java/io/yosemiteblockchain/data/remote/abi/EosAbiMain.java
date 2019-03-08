package io.yosemiteblockchain.data.remote.abi;

import com.google.gson.annotations.Expose;

import java.util.List;


public class EosAbiMain {

    @Expose
    public List<EosAbiTypeDef> types;

    @Expose
    public List<EosAbiAction> actions;

    @Expose
    public List<EosAbiStruct> structs;

    @Expose
    public List<EosAbiTable> tables;
}
