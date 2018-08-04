package io.yosemite.services;

import io.yosemite.data.remote.model.api.AbiJsonToBinRequest;
import io.yosemite.data.remote.model.api.AbiJsonToBinResponse;
import io.yosemite.data.remote.model.api.GetRequiredKeysRequest;
import io.yosemite.data.remote.model.api.GetRequiredKeysResponse;
import io.yosemite.data.remote.model.chain.*;
import io.yosemite.data.remote.model.response.chain.TableRow;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.Map;

public interface YosemiteChainApiService {

    @POST("/v1/chain/get_info")
    Call<Info> getInfo();

    @POST("/v1/chain/get_block")
    Call<Block> getBlock(String blockNumberOrId);

    @POST("/v1/chain/get_table_rows")
    Call<TableRow> getTableRows(@Body Map<String, String> requestFields);

    @POST("v1/chain/abi_json_to_bin")
    Call<AbiJsonToBinResponse> abiJsonToBin(@Body AbiJsonToBinRequest req);

    @POST("v1/chain/push_transaction")
    Call<PushedTransaction> pushTransaction(@Body PackedTransaction transaction);

    @POST("v1/chain/get_required_keys")
    Call<GetRequiredKeysResponse> getRequiredKeys(@Body GetRequiredKeysRequest req);
}
