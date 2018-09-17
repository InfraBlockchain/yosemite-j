package io.yosemite.services;

import io.yosemite.data.remote.history.action.Actions;
import io.yosemite.data.remote.history.transaction.Transaction;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface YosemiteHistoryApiService {

    @GET("/transaction/{txId}")
    Call<Transaction> getTransaction(@Path("txId") String txId);

    @GET("/account/{accountName}/actions/received")
    Call<Actions> getActions(@Path("accountName") String accountName, @Query("start") Integer start, @Query("offset") Integer offset);
}
