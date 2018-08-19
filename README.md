# Yosemite J - Yosemite Chain Java API

This version is currently working with Yosemite Chain `yosemite-master` branch and `e9816f3`commit.
The project's goal would be something like Java version of `cleos` of `EOS.IO` which provides convenient interfaces for the useful commands such as pushing action including some native actions of Yostemite Public Blockchain.

The first step would be to implement a wrapper for basic HTTP APIs and then the other convenient interface would be built on top of it.

## Requirements
* Java 8
* Gradle

# Getting Started

## Blockchain Setup
Please refer to the [Yosemite Blockchain Guide](https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/yosemite_bios/yosemite_bios_testnet_permissioned.md) for getting prepared.
You should be aware of HTTP endpoints for blockchain node and `keosd` for your wallet access.

Public testnet(http://testnet.yosemitelabs.org:8888) is already given for developers and it is recommended to run `keosd` in secure environment. 

## Dependency

#### Build and add archive
1. Build the library project
```
> git clone git@github.com:YosemiteLabs/yosemite-j.git
> cd yosemite-j 
> ./gradlew build
```

2. Get your archive
```
build/libs/yosemitej-{version}.jar
```

#### Gradle
```
compile ('org.yosemite:yosemitej:0.2.0-SNAPSHOT')
```

#### Maven
```
<dependency>
  <groupId>org.yosemite</groupId>
  <artifactId>yosemitej</artifactId>
  <version>0.2.0-SNAPSHOT</version>
</dependency>
```

## Using HTTP APIs
Let's assume we have the following setups
* chain HTTP endpoint: http://testnet.yosemitelabs.org:8888
* wallet HTTP endpoint: http://127.0.0.1:8900

_Please make sure that you have opened and unlocked your wallet_

First create the http service.
```java
YosemiteApiRestClient apiClient = new YosemiteApiClientFactory.createYosemiteApiClient("http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900");
```

### To Send Synchronous Requests
```java
Info info = apiClient.getInfo().execute();
```

### To Send Asynchronous Requests
Asynchronous calls use `Executors.newCachedThreadPool()` to handle requests.
```java
Future<Info> infoFuture = apiClient.getInfo().executeAsync();

// ...

Info info = infoFuture.get();
```

## Using YosemiteJ
`YosemiteJ` classes are helper classes that encapsulates complexities of set of APIs to do useful actions. Since `YosemiteJ` is an abstract class,
you should use the following concrete classee under `io.yosemite.services.yxcontracts`.

* YosemiteSystemJ
* YosemiteDigitalContractJ
* YosemiteNativeTokenJ
* YosemiteTokenJ

```java
import io.yosemite.services.yxcontracts;

YosemiteApiRestClient apiClient = new YosemiteApiClientFactory.createYosemiteApiClient("http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900");
YosemiteSystemJ yxj = new YosemiteSystemJ(apiClient);
```

### Setting transaction expiration time
Even if a transaction is successfully accepted by the Yosemite Chain, there is a possiblility that the transaction is failed to be in the irreversible block.
For such pending transaction, it can be expired. The dapps can set the expiration time of transaction in milliseconds.
```java
// set transaction expiration time as 30 seconds
YosemiteApiRestClient apiClient = new YosemiteApiClientFactory.createYosemiteApiClient("http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900", 30000);
```

### Pushing action
If you want to push an action to a deployed contract on the blockchain, you can use `pushAction` method.
API calls are asynchronously composed using `CompletableFuture` in each method.

```java
// contract and authorization information
String contract = "yx.ntoken";
String action = "transfer";
String data = "{\"from\":\"user1\",\"to\":\"user2\",\"quantity\":\"1000.0000 DKRW\",\"memo\":\"test\"}";
String[] permissions = new String[]{"user1@active"};

PushedTransaction pushedTransaction = yxj.pushAction(contract, action, data, permissions).join();

String txId = pushedTransaction.getTransactionId();
```

### Getting the list of actions
```java
YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
        "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");
Actions result = apiClient.getActions(Consts.YOSEMITE_DIGITAL_CONTRACT_CONTRACT, -1, -20).execute();
System.out.println("LastIrreversibleBlock : " + result.getLastIrreversibleBlock());
for (Action action : result.getActions()) {
    System.out.println(action.getAccountActionSeq() + " " + action.getBlockNum());
}
```
* https://developers.eos.io/eosio-cpp/docs/exchange-deposit-withdraw#section-polling-account-history

# Yosemite Actions

## System Actions

### Create a new account
```java
import io.yosemite.services.yxcontracts;

YosemiteApiRestClient apiClient = new YosemiteApiClientFactory.createYosemiteApiClient("http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900");
YosemiteSystemJ yxj = new YosemiteSystemJ(apiClient);

PushedTransaction pushedTransaction = yxj.createAccount("identity", "user1account",
                "EOS8Ledj...vr9gj",
                "EOS8Ledj...vr9gj",
                new String[]{"identity@active"}).join();
``` 

## Native Token Actions

### Issuing Native Token
```java
import io.yosemite.services.yxcontracts;

YosemiteApiRestClient apiClient = new YosemiteApiClientFactory.createYosemiteApiClient("http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900");
YosemiteJ yxj = new YosemiteNativeTokenJ(apiClient);
PushedTransaction pushedTransaction = yxj.issueNativeToken("servprovider", "1000000.0000 DKRW", "sysdepo", "memo", new String[]{"sysdepo@active"}).join();
```

### Redeeming Native Token
```java
PushedTransaction pushedTransaction = yxj.redeemNativeToken("100000.0000 DKRW", "sysdepo", "memo", new String[]{"sysdepo@active"}).join();
```

### Transferring Native Token
#### Without specifying issuer and fee payer
```java
PushedTransaction pushedTransaction = yxj.transferNativeToken("user1", "user2", "100000.0000 DKRW", "memo", null).join();
```

#### Without specifying issuer but with fee payer
```java
PushedTransaction pushedTransaction = yxj.transferNativeTokenWithPayer("user1", "user2", "100000.0000 DKRW", "servprovider", "memo", null).join();
```

#### With specifying issuer but no fee payer
```java
PushedTransaction pushedTransaction = yxj.ntransferNativeToken("user1", "user2", "100000.0000 DKRW", "sysdepo", "memo", null).join();
```

#### With specifying issuer and fee payer
```java
PushedTransaction pushedTransaction = yxj.ntransferNativeTokenWithPayer("user1", "user2", "100000.0000 DKRW", "sysdepo", "servprovider", "memo", null).join();
```

### Getting Native Token Statistics of System Depository
```java
TableRow tableRow = yxj.getNativeTokenStats("sysdepo").join();
```

### Getting Native Token Balance of Account
```java
TableRow tableRow = yxj.getNativeTokenAccountBalance("user1").join();
```

### Getting Total Native Token Balance of Account
```java
TableRow tableRow = yxj.getNativeTokenAccountTotalBalance("user1").join();
```

## Token Actions

### Creating Token
```java
PushedTransaction pushedTransaction = yxj.createToken("BTC", 4, "d2", new String[]{"d2@active"}).join();
```

### Issuing Token
```java
PushedTransaction pushedTransaction = yxj.issueToken("user1", "100000.0000 BTC", "d2", "my memo", new String[]{"d2@active"}).join();
```

### Redeeming Token
```java
PushedTransaction pushedTransaction = yxj.redeemToken("20000.0000 BTC", "d2", "my memo", new String[]{"d2@active"}).join();
```

### Transferring Token
#### Without fee payer
```java
PushedTransaction pushedTransaction = yxj.transferToken("user1", "user2", "100.0000 BTC", "d2", "my memo", new String[]{"user1@active"}).join();
```

#### With fee payer
```java
PushedTransaction pushedTransaction = yxj.transferToken("user1", "user2", "100.0000 BTC", "d2", "servprovider", "my memo", new String[]{"user1@active"}).join();
```

### Getting Token Statistics of Issuer
```java
TableRow tableRow = yxj.getTokenStats("BTC", 4, "d2").join();
for (Map<String, ?> row : tableRow.getRows()) {
    //...
}
```

### Getting Token Balance of Account
* Balance for user1
```java
TableRow tableRow = yxj.getTokenAccountBalance("BTC", 4, "d2", "user1").join();
for (Map<String, ?> row : tableRow.getRows()) {
    // There must be only one row.
    logger.debug(row.toString());
}
```

## Digital Contract Actions
* use YosemiteDigitalContractJ class to call action methods
```java
import io.yosemite.services.yxcontracts;

YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
        "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");

YosemiteDigitalContractJ yxj = new YosemiteDigitalContractJ(apiClient);
```
* Allmost all of methods return Java 8 CompletableFuture. That means you can get the result synchronously or asynchronously.

### Creating Digital Contract
* Even if createDigitalContract method returns successfully, the digital contract you have created is not actually created on the YosemiteChain.
* The create action must be included in the block and finally confirmed by other block producers, which it is called the action is irreversible.
* You must check the finality or irreversibility of the action so that the digital contract is actually created.
* The program must poll(check regurarly) to confirm the irreversibility with [getActions method](#getting-the-list-of-actions).
```java
List<String> signers = Arrays.asList("user1", "user2");
// prepare expiration time based on UTC time-zone
Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
calendar.add(Calendar.HOUR, 48); // contract will be expired after 2 days
Date expirationTime = calendar.getTime();

PushedTransaction pushedTransaction = yxj.createDigitalContract("servprovider", 11, "test1234", "",
        signers, expirationTime, (short)0, new String[]{"servprovider@active"}).join();
```

### Adding Additional Signers
```java
List<String> newSigners = Collections.singletonList("user3");
pushedTransaction = yxj.addSigners("servprovider", 11, newSigners, new String[]{"servprovider@active"}).join();
```

### Signing Digital Contract
```java
pushedTransaction = yxj.signDigitalDocument("servprovider", 11, "user2", "", new String[]{"user2@active"}).join();
pushedTransaction = yxj.signDigitalDocument("servprovider", 11, "user3", "I am user3", new String[]{"user3@active"}).join();
```
### Updating Additional Document Hash of Digital Contract
```java
pushedTransaction = yxj.updateAdditionalDocumentHash("servprovider", 11, "added after signing", new String[]{"servprovider@active"}).join();
```

### Removing Digital Contract
```java
pushedTransaction = yxj.removeDigitalContract("servprovider", 11, new String[]{"servprovider@active"}).join();
```

### Getting Created Digital Contract
* It's the same as querying from RAM database.
```java
import io.yosemite.data.remote.chain.TableRow;

TableRow tableRow = yxj.getCreatedDigitalContract("servprovider", 11).join();
for (Map<String, ?> row : tableRow.getRows()) {
    ...

    // There must be only one row.
    break;
}
```

### Getting Signer's Information
```java
import io.yosemite.data.remote.chain.TableRow;
import io.yosemite.util.StringUtils;

TableRow signerInfoTable = yxj.getSignerInfo("user3", "servprovider", 11).join();
for (Map<String, ?> row : signerInfoTable.getRows()) {
    logger.debug(StringUtils.convertHexToString((String) row.get("signerinfo")));

    // There must be only one row.
    break;
}
```

## References 

## Supported EOS.IO HTTP APIs

#### Chain
 - [x] get_info
 - [x] get_block
 - [ ] get_block_header_state
 - [ ] get_account
 - [ ] get_abi
 - [ ] get_code
 - [ ] get_raw_code_and_abi
 - [ ] get_table_rows
 - [ ] get_currency_balance
 - [x] abi_json_to_bin
 - [ ] abi_bin_to_json
 - [x] get_required_keys
 - [ ] get_currency_stats
 - [ ] get_producers
 - [ ] push_block
 - [x] push_transaction
 - [ ] push_transactions

#### Wallet
 - [ ] create
 - [ ] open
 - [ ] lock
 - [ ] lock_all
 - [ ] unlock
 - [ ] import_key
 - [ ] list_wallets
 - [ ] list_keys
 - [x] get_public_keys
 - [ ] set_timeout
 - [x] sign_transaction
 - [ ] set_dir
 - [ ] set_eosio_key
 - [ ] sign_digest
 - [ ] create_key

#### History
 - [x] get_actions
 - [x] get_transaction
 - [x] get_key_accounts
 - [x] get_controlled_accounts

#### Producer
 - [ ] pause
 - [ ] resume
 - [ ] paused
 - [ ] get_greylist
 - [ ] add_greylist_accounts
 - [ ] remove_grelist_accounts
 
### YosemiteJ APIs
[API references](https://github.com/YosemiteLabs/yosemite-j) - Not available yet
