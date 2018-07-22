package org.yosemitex.services;

import org.yosemitex.data.remote.model.api.*;
import org.yosemitex.data.remote.model.chain.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EosChainApiService {

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
