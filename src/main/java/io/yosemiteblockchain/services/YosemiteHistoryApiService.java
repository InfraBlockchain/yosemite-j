package io.yosemiteblockchain.services;

import io.yosemiteblockchain.data.remote.history.action.Actions;
import io.yosemiteblockchain.data.remote.history.transaction.Transaction;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface YosemiteHistoryApiService {

    @GET("/transaction/{txId}")
    Call<Transaction> getTransaction(@Path("txId") String txId);

    @GET("/account/{accountName}/actions/received")
    Call<Actions> getActions(@Path("accountName") String accountName, @Query("start") long start, @Query("offset") int offset);
}
