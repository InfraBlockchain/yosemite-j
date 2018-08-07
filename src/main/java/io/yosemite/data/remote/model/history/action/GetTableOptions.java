package io.yosemite.data.remote.model.history.action;

public class GetTableOptions {
    /**  The maximum number of rows to return */
    private int limit;

    /** JSON representation of lower bound value of key, defaults to first */
    private String lowerBound;

    /** JSON representation of upper bound value value of key, defaults to last */
    private String upperBound;

    /**
     * Index number, 1 - primary (first), 2 - secondary indexPosition (in order defined by multi_index), 3 - third indexPosition, etc.
     * Number or name of indexPosition can be specified, e.g. 'secondary' or '2'.
     */
    private String indexPosition;

    /** The key type of --indexPosition, primary only supports (i64), all others support (i64, i128, i256, float64, float128) */
    private String keyType;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(String lowerBound) {
        this.lowerBound = lowerBound;
    }

    public String getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(String upperBound) {
        this.upperBound = upperBound;
    }

    public String getIndexPosition() {
        return indexPosition;
    }

    public void setIndexPosition(String indexPosition) {
        this.indexPosition = indexPosition;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }
}
