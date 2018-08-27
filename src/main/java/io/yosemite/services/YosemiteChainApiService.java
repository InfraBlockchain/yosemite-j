package io.yosemite.services;

import io.yosemite.data.remote.api.AbiJsonToBinRequest;
import io.yosemite.data.remote.api.AbiJsonToBinResponse;
import io.yosemite.data.remote.api.GetRequiredKeysRequest;
import io.yosemite.data.remote.api.GetRequiredKeysResponse;
import io.yosemite.data.remote.chain.*;
import io.yosemite.data.remote.chain.TableRow;
import io.yosemite.data.remote.chain.account.Account;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.Map;

public interface YosemiteChainApiService {

    @GET("/v1/chain/get_info")
    Call<Info> getInfo();

    @POST("/v1/chain/get_block")
    Call<Block> getBlock(@Body Map<String, String> requestFields);

    @POST("/v1/chain/get_account")
    Call<Account> getAccount(@Body Map<String, String> requestFields);

    @POST("/v1/chain/get_table_rows")
    Call<TableRow> getTableRows(@Body Map<String, String> requestFields);

    @POST("v1/chain/abi_json_to_bin")
    Call<AbiJsonToBinResponse> abiJsonToBin(@Body AbiJsonToBinRequest req);

    @POST("v1/chain/push_transaction")
    Call<PushedTransaction> pushTransaction(@Body PackedTransaction transaction);

    @POST("v1/chain/get_required_keys")
    Call<GetRequiredKeysResponse> getRequiredKeys(@Body GetRequiredKeysRequest req);
}
