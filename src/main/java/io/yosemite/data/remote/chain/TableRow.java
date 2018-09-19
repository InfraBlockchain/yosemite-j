package io.yosemite.data.remote.chain;

import com.google.gson.annotations.Expose;

import java.util.List;
import java.util.Map;

/**
 * Represents the list of rows in the table of the smart contract.
 * Each row is mapped to the json format.
 */
public class TableRow {

    @Expose
    private List<Map<String, ?>> rows;

    @Expose
    private Boolean more;

    public TableRow() {
    }

    public List<Map<String, ?>> getRows() {
        return rows;
    }

    public Boolean getMore() {
        return more;
    }
}
