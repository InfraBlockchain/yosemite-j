# Yosemite J - Yosemite Chain Java API

This version is currently working with Yosemite Chain `yosemite-master` branch and `e9816f3`commit.
The project's goal would be something like Java version of `cloes` in `EOS` which provides convenient interfaces for the useful commands such as pushing action including some native actions.

The first step would be to implement a wrapper for basic HTTP APIs and then the other convenient interface would be built on top of it.

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
yosemite-j/build/libs/yxj-{version}.jar
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
YxApiRestClient apiClient = new YxApiRestClientImpl("http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900");
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

## Using Yxj
`Yxj` is a helper class that encapsulates complexities of set of APIs to do useful actions.

```java
YxApiRestClient apiClient = new YxApiRestClientImpl("http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900");

Yxj yxj = new Yxj(apiClient);
```

### Pushing action
If you want to push an action to a deployed contract on the blockchain, you can use `pushAction` method.
API calls are asynchronously composed using `CompletableFuture` in each method.

```java
// contract and authorization information
String contract = "yx.token";
String action = "transfer";
String data = "{\"from\":\"user\",\"to\":\"tester\",\"quantity\":\"1000.0000 DKRW\",\"memo\":\"test\"}";
String[] permissions = new String[]{"user@active"};

PushedTransaction pushedTransaction = yxj.pushAction(contract, action, data, permissions).join();

String txId = pushedTransaction.getTransactionId();
``` 

### Example: Issuing Native Token
Please refer to the [Yosemite Blockchain Guide](https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/yosemite_bios/yosemite_bios_testnet_permissioned.md) before you do actions related to native tokens.
You are required to provide appropriate setups for accounts for this action.
```java
PushedTransaction pushedTransaction = yxj.issueNativeToken("producer.a", "10000.0000 DKRW", "sysdepo", "memo", new String[]{"sysdepo@active"}).join();
```

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
 - [ ] get_actions
 - [ ] get_transaction
 - [ ] get_key_accounts
 - [ ] get_controlled_accounts

#### Net
 - [ ] connect
 - [ ] disconnect
 - [ ] connections
 - [ ] status

#### Producer
 - [ ] pause
 - [ ] resume
 - [ ] paused
 - [ ] get_greylist
 - [ ] add_greylist_accounts
 - [ ] remove_grelist_accounts
 
### Yxj APIs
[API references](https://github.com/YosemiteLabs/yosemite-j) - Not available yet
 
## License

    Copyright (c) 2018 Yosemite X.

    The MIT License

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.