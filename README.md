# Yosemite J - Yosemite Chain Java API

The project's goal would be something like Java version of `clyos` which provides convenient interfaces for the useful commands such as pushing action including some native actions of Yostemite Public Blockchain.

The first step would be to implement a wrapper for basic HTTP APIs and then the other convenient interface would be built on top of it.

## Requirements
* Java 8
* Gradle

# Getting Started

## Blockchain Setup
Please refer to the [Yosemite Blockchain Guide](https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/yosemite_bios/yosemite_bios_testnet_permissioned.md) for getting prepared.
You should be aware of HTTP endpoints for blockchain node and `keyos` for your wallet access.

Public testnet(http://testnet.yosemitelabs.org:8888) is already given for developers and it is recommended to run `keyos` in secure environment. 

## Using Library

### Build and add archive
#### 1. Build the library project
```
git clone git@github.com:YosemiteLabs/yosemite-j.git
cd yosemite-j 
./gradlew build
```

#### 2. Get your archive
```
build/libs/yosemitej-${version}-SNAPSHOT.jar
```

#### Library with all dependencies
```
./gradlew shadowJar

build/libs/yosemitej-${version}-SNAPSHOT-all.jar
```


### Using dependency management tool
Currently, we do not support public repository. Instead, you can publish to a maven local repository.

#### 1. Build & publish to a local maven repository
```
./gradlew build publishToMavenLocal
```   

#### 2. Specify the dependency from your project
* Gradle
```
compile ('io.yosemite:yosemitej:${version}-SNAPSHOT')
```

* Maven
```
<dependency>
  <groupId>io.yosemite</groupId>
  <artifactId>yosemitej</artifactId>
  <version>${version}-SNAPSHOT</version>
</dependency>
```

## Using HTTP APIs
Let's assume we have the following setups
* chain HTTP endpoint: http://testnet.yosemitelabs.org:8888
* wallet HTTP endpoint: http://127.0.0.1:8900

_Please make sure that you have opened and unlocked your wallet_

First create the http service.
```java
YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900");
```

### To Send Synchronous Requests
```java
Info info = apiClient.getInfo().execute();
```

### To Send Asynchronous Requests
Asynchronous calls use `Java 8 ForkJoinPool` to handle requests.
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
import io.yosemite.services.yxcontracts.*;

YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900");
YosemiteSystemJ yxj = new YosemiteSystemJ(apiClient);
```

### Transaction and its actions
In YosemiteChain, a transaction includes one or more actions. As the same as general transaction concept, the transaction is rolled back if at least one of the actions is failed.

### Synching or Polling the Result of Transaction
Even if a transaction is successfully accepted by the YosemiteChain, it's not that the transaction becomes irreversible immediately, which means it is the part of a block. The DApps should wait for or poll the transaction becomes irreversible.

### Setting Transaction-as-a-Vote(TaaV) Account for Proof-of-Transaction(PoT)
TaaV is the most important feature of the YosemiteChain, which is required to elect the PoT block producers. The accounts which get more transaction votes than others become PoT block producers.

<b>
It's highly recommended for the service providers or DApps implementors to determine carefully which account to vote to for making the YosemiteChain even healthier. We think the first candidate for the TaaV account is the system depository selected as their native token issuer.
</b>

```java
YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900");
apiClient.setTransactionVoteTarget("<account-name>");
```

### Setting transaction expiration time
Even if a transaction is successfully accepted by the YosemiteChain, there is a possiblility that the transaction is failed to be in the irreversible block.
For such pending transaction, it can be expired. The DApps can set the expiration time of transaction in milliseconds.
```java
// set transaction expiration time as 30 seconds
YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900", 30000);
```

### Pushing action
If you want to push an action as a transaction to a deployed contract on the blockchain, you can use `pushAction` method.
API calls are asynchronously composed using `CompletableFuture` in each method. The returned `PushedTransaction` instance provides the transaction id.

```java
// contract and authorization information
String contract = "yx.ntoken";
String action = "transfer";
String data = "{\"from\":\"user1\",\"to\":\"user2\",\"quantity\":\"1000.0000 DKRW\",\"memo\":\"test\"}";
String[] permissions = new String[]{"user1@active"};

PushedTransaction pushedTransaction = yxj.pushAction(contract, action, data, permissions).join();

String txId = pushedTransaction.getTransactionId();
```

### Getting the transaction information
```java
import io.yosemite.data.remote.history.transaction.Transaction;

YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
        "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");
Transaction tx = apiClient.getTransaction("312ad1eb7e6c797ed5a19e09da0c0f8bc3c67b3b8ee4ef93a49a76c3cb0c394b").execute();
if (tx.getBlockNum() <= tx.getLastIrreversibleBlock()) {
    //transaction is irreversible now
    //
}
```

### Getting the list of actions for checking the irreversibility of the transaction
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

### Sign arbitrary data & verify signature
```java
import io.yosemite.crypto.ec.EcDsa;

String strData = "hello";
byte[] data = strData.getBytes(StandardCharsets.UTF_8);
String pubKey = "YOS6pR7dfCkMkuEePpLs3bJxt39eE8qb2hVNWmv93jFHEMQbTRRsJ";

String signature = yxj.sign(data, pubKey).join();

boolean isVerified = EcDsa.verifySignature(data, signature, pubKey);
```

# Yosemite Actions

Note that most methods below are just the wrapper of HTTP JSON request using `pushAction` method.

## System Actions

### Create a new account
```java
import io.yosemite.services.yxcontracts.*;

YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900");
YosemiteSystemJ yxj = new YosemiteSystemJ(apiClient);

PushedTransaction pushedTransaction = yxj.createAccount("identity", "user1account",
                "YOS8Ledj...vr9gj",
                "YOS8Ledj...vr9gj",
                new String[]{"identity@active"}).join();
``` 

## Native Token Actions

### Issuing Native Token
```java
import io.yosemite.services.yxcontracts.*;

YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900");
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
import io.yosemite.services.yxcontracts.*;

YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
        "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");

YosemiteDigitalContractJ yxj = new YosemiteDigitalContractJ(apiClient);
```
* [yx.dcontract](https://github.com/YosemiteLabs/yosemite-public-blockchain/tree/yosemite-master/contracts/yx.dcontract) would help you to understand the chain-side.

### Creating Digital Contract
import io.yosemite.services.yxcontracts.*;

```java
List<String> signers = Arrays.asList("user1", "user2");
// prepare expiration time based on UTC time-zone
Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
calendar.add(Calendar.HOUR, 48); // contract will be expired after 2 days
Date expirationTime = calendar.getTime();

PushedTransaction pushedTransaction = yxj.createDigitalContract("servprovider", 11, "test1234", "",
        signers, expirationTime, 0, EnumSet.noneOf(KYCStatusType.class), (short)0, new String[]{"servprovider@active"}).join();
```
* Even if createDigitalContract method returns successfully, the digital contract you have created is not actually created on the YosemiteChain.
* The create action must be included in the block and finally confirmed by other block producers, which it is called the action is irreversible.
* You must check the finality or irreversibility of the action so that the digital contract is actually created.
* The program must poll(check regurarly) to confirm the irreversibility with [getTransaction method](#getting-the-transaction-information) or [getActions method](#getting-the-list-of-actions).

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
    logger.debug((String) row.get("signerinfo"));

    // There must be only one row.
    break;
}
```


## References 
```
./gradlew javadoc
```
Please open `build/docs/javadoc/index.html` with the web browser.

