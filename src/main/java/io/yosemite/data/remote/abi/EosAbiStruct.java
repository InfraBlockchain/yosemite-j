package io.yosemite.data.remote.abi;

import com.google.gson.annotations.Expose;

import java.util.List;

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
