
package io.yosemiteblockchain.data.remote.history.transaction;

import com.google.gson.annotations.Expose;

public class Data {

    @Expose
    private Header header;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

}
