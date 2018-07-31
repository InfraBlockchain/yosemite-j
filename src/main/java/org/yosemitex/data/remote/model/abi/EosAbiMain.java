package org.yosemitex.data.remote.model.abi;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
