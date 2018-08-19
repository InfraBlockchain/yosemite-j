package io.yosemite.data.remote.chain;

import com.google.gson.annotations.Expose;

import java.util.List;
import java.util.Map;

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

    public void setRows(List<Map<String, ?>> rows) {
        this.rows = rows;
    }

    public Boolean getMore() {
        return more;
    }

    public void setMore(Boolean more) {
        this.more = more;
    }
}
