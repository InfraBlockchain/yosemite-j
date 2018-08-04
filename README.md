# Yosemite J - Yosemite Chain Java API

This version is currently working with Yosemite Chain `yosemite-master` branch and `e9816f3`commit.
The project's goal would be something like Java version of `cleos` of `EOS.IO` which provides convenient interfaces for the useful commands such as pushing action including some native actions of Yostemite Public Blockchain.

The first step would be to implement a wrapper for basic HTTP APIs and then the other convenient interface would be built on top of it.

## Requirements
Java 8

## Getting Started

### Blockchain Setup
Please refer to the [Yosemite Blockchain Guide](https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/yosemite_bios/yosemite_bios_testnet_permissioned.md) for getting prepared.
You should be aware of HTTP endpoints for blockchain node and `keosd` for your wallet access.

Public testnet(http://testnet.yosemitelabs.org:8888) is already given for developers and it is recommended to run `keosd` in secure environment. 

### Dependency

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
compile ('org.yosemite:yosemitej:0.1.0-SNAPSHOT')
```

#### Maven
```
<dependency>
  <groupId>org.yosemite</groupId>
  <artifactId>yosemitej</artifactId>
  <version>0.1.0-SNAPSHOT</version>
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
`YosemiteJ` is a helper class that encapsulates complexities of set of APIs to do useful actions.

```java
YosemiteApiRestClient apiClient = new YosemiteApiClientFactory.createYosemiteApiClient("http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900");
YosemiteJ yxj = new YosemiteNativeTokenJ(apiClient);
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

### Issuing Native Token
Please refer to the [Yosemite Blockchain Guide](https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/yosemite_bios/yosemite_bios_testnet_permissioned.md) before you do actions related to native tokens.
You are required to provide appropriate setups for accounts for this action.
```java
PushedTransaction pushedTransaction = yxj.issueNativeToken("producer.a", "10000.0000 DKRW", "sysdepo", "memo", new String[]{"sysdepo@active"}).join();
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


## References 

## Supported HTTP APIs

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