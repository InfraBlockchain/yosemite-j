package io.yosemite.services;

import io.yosemite.data.remote.model.api.AbiJsonToBinReq;
import io.yosemite.data.remote.model.api.AbiJsonToBinRes;
import io.yosemite.data.remote.model.api.GetRequiredKeysReq;
import io.yosemite.data.remote.model.api.GetRequiredKeysRes;
import io.yosemite.data.remote.model.chain.Block;
import io.yosemite.data.remote.model.chain.Info;
import io.yosemite.data.remote.model.chain.PackedTransaction;
import io.yosemite.data.remote.model.chain.PushedTransaction;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface YosemiteChainApiService {

    @POST("/v1/chain/get_info")
    Call<Info> getInfo();

    @POST("/v1/chain/get_block")
    Call<Block> getBlock(String blockNumberOrId);

    @POST("v1/chain/abi_json_to_bin")
    Call<AbiJsonToBinRes> abiJsonToBin(@Body AbiJsonToBinReq req);

    @POST("v1/chain/push_transaction")
    Call<PushedTransaction> pushTransaction(@Body PackedTransaction transaction);

    @POST("v1/chain/get_required_keys")
    Call<GetRequiredKeysRes> getRequiredKeys(@Body GetRequiredKeysReq req);
}
