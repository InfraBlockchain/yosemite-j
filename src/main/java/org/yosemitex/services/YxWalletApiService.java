package org.yosemitex.services;

import org.yosemitex.data.remote.model.chain.SignedTransaction;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.Collection;
import java.util.List;

public interface YxWalletApiService {

    @POST("/v1/wallet/get_public_keys")
    Call<List<String>> getPublicKeys();

    @POST("/v1/wallet/sign_transaction")
    Call<SignedTransaction> signTransaction(@Body Collection transactionData);
}
