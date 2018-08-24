package io.yosemite.services;

import io.yosemite.data.remote.chain.Transaction;
import io.yosemite.data.remote.history.action.Actions;
import io.yosemite.data.remote.history.controlledaccounts.ControlledAccounts;
import io.yosemite.data.remote.history.keyaccounts.KeyAccounts;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.Map;

public interface YosemiteHistoryApiService {

    @GET("/v1/history/get_actions")
    Call<Actions> getActions(@Body Map<String, Object> requestFields);

    @GET("/v1/history/get_transaction")
    Call<Transaction> getTransaction(@Body Map<String, String> requestFields);

    @GET("/v1/history/get_key_accounts")
    Call<KeyAccounts> getKeyAccounts(@Body Map<String, String> requestFields);

    @GET("/v1/history/get_controlled_accounts")
    Call<ControlledAccounts> getControlledAccounts(@Body Map<String, String> requestFields);

}
