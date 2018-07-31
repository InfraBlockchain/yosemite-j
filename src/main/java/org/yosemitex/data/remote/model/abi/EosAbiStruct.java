package org.yosemitex.data.remote.model.abi;

import com.google.gson.annotations.Expose;

import java.util.List;
import java.util.Map;

public class EosAbiStruct {

    @Expose
    public String name;

    @Expose
    public String base;

    @Expose
    public List<EosAbiField> fields;

    @Override
    public String toString() {
        return "Struct name: " + name + ", base: " + base ;
    }
}
