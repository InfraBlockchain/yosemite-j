package io.yosemite.data.remote.api;

import com.google.gson.annotations.Expose;
import io.yosemite.util.StringUtils;

public class GetTableRequest {
    private static final int DEFAULT_FETCH_LIMIT = 10;

    @Expose
    private boolean json = true;

    @Expose
    private String code;

    @Expose
    private String scope;

    @Expose
    private String table;

    @Expose
    private String table_key = "";

    @Expose
    private String lower_bound = "";

    @Expose
    private String upper_bound = "";

    @Expose
    private int limit;


    public GetTableRequest(String scope, String code, String table, String tableKey, String lowerBound, String upperBound, int limit) {
        this.scope = scope;
        this.code = code;
        this.table = table;

        this.table_key = StringUtils.isEmpty(tableKey) ? "" : tableKey;
        this.lower_bound = StringUtils.isEmpty(lowerBound) ? "" : lowerBound;
        this.upper_bound = StringUtils.isEmpty(upperBound) ? "" : upperBound;
        this.limit = limit <= 0 ? DEFAULT_FETCH_LIMIT : limit;
    }
}
