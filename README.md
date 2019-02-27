# Yosemite J - Yosemite Chain Java API (BETA)

The project's goal is to provide convenient interfaces for the useful commands such as pushing action including some native actions of Yostemite Public Blockchain.

## NOTE
This is on BETA phase. The API could be changed without considering compatibility.

## Requirements
* Java 8
* Gradle

# Getting Started

## Blockchain Setup
Please refer to the [Yosemite Blockchain Guide](https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/yosemite_bios/yosemite_bios_testnet_permissioned.md) for getting prepared.
You should be aware of HTTP endpoints for blockchain node and `keyos` for your wallet access.

Public testnet(http://testnet-sentinel.yosemitelabs.org:8888) is already given for developers and it is recommended to run `keyos` in secure environment. 

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
* chain HTTP endpoint: http://testnet-sentinel.yosemitelabs.org:8888
* wallet HTTP endpoint: http://127.0.0.1:8900

_Please make sure that you have opened and unlocked your wallet_

First create the http service.
```java
YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://testnet-sentinel.yosemitelabs.org:8888", "http://127.0.0.1:8900");
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
* StandardToken
* YosemiteDigitalContractJ

```java
import io.yosemite.services.yxcontracts.*;

YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://testnet-sentinel.yosemitelabs.org:8888", "http://127.0.0.1:8900");
YosemiteSystemJ yxj = new YosemiteSystemJ(apiClient);
```

### Transaction and its actions
In YosemiteChain, a transaction includes one or more actions. As the same as general transaction concept, the transaction is rolled back if at least one of the actions is failed.

### Synching or Polling the Result of Transaction
Even if a transaction is successfully accepted by the YosemiteChain, it's not that the transaction becomes irreversible immediately, which means it is the part of a block. The DApps should wait for or poll the transaction becomes irreversible.

### Setting Transaction-as-a-Vote(TaaV) Account for Proof-of-Transaction(PoT)
TaaV is the most important feature of the YosemiteChain, which is required to elect the PoT block producers. The accounts which get more transaction votes than others become PoT block producers.

<b>
It's highly recommended for the service providers or DApps implementors to determine carefully which account to vote to for making the YosemiteChain even healthier. We think the first candidate for the TaaV account is the block producer account advertised by their native token issuer.
</b>

```java
YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://testnet-sentinel.yosemitelabs.org:8888", "http://127.0.0.1:8900");
apiClient.setTransactionVoteTarget("<account-name>");
```

### Setting transaction expiration time
Even if a transaction is successfully accepted by the YosemiteChain, there is a possiblility that the transaction is failed to be in the irreversible block.
For such pending transaction, it can be expired. The DApps can set the expiration time of transaction in milliseconds.
```java
// set transaction expiration time as 30 seconds
YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://testnet-sentinel.yosemitelabs.org:8888", "http://127.0.0.1:8900", 30000);
```

### Setting transaction parameters for each transaction
Setting of Transaction-as-a-Vote(TaaV) Account, transaction expiration time, and transaction fee payer including required permissions and public keys can be set by TransactionParameters class for each action or transaction.
You can see the sample usages of TransactionParameters and TransactionParametersBuilder from samples.

### Pushing action
If you want to push an action as a transaction to a deployed contract on the blockchain, you can use `pushAction` method.
API calls are asynchronously composed using `CompletableFuture` in each method. The returned `PushedTransaction` instance provides the transaction id.

```java
// contract and authorization information
String contract = "yx.ntoken";
String action = "transfer";
String data = "{\"from\":\"user1\",\"to\":\"user2\",\"quantity\":\"1000.0000 DKRW\",\"memo\":\"test\"}";
TransactionParameters txParameters = TransactionParameters.Builder().addPermission("user1").build();

PushedTransaction pushedTransaction = yxj.pushAction(contract, action, data, txParameters).join();

String txId = pushedTransaction.getTransactionId();
```

### Getting the transaction information
```java
import io.yosemite.data.remote.history.transaction.Transaction;

YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
        "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://testnet-explorer-api.yosemitelabs.org");
Transaction tx = apiClient.getTransaction("312ad1eb7e6c797ed5a19e09da0c0f8bc3c67b3b8ee4ef93a49a76c3cb0c394b").execute();

if (tx.getIrreversibleAt() != null) {
    // transaction is irreversible now
    long timestamp = tx.getIrreversibleAt().getTimestamp(); // getting the timestamp when this transaction became irreversible
} else {
    // transaction is not yet irreverisble
}
```

### Getting the list of actions for checking the irreversibility of the transaction
```java
YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
        "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://testnet-explorer-api.yosemitelabs.org");
Actions result = apiClient.getActions(Consts.YOSEMITE_DIGITAL_CONTRACT_CONTRACT, -1, -20).execute();
System.out.println("LastIrreversibleBlock : " + result.getLastIrreversibleBlock());
for (Action action : result.getActions()) {
    System.out.println(action.getBlockNum());
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

### Delegating fee to another account
If you don't want to worry about handling DKRW yourself and there is a 3rd party service(ex: depositories may expose such an API as a service) that handles DKRW directly, you can delegate your transaction fee payment by just signing your transaction first and passing it to the 3rd party service. Then, it adds its signature to the received transaction and finally pushes it to the network. The code snippet below describes how it works.

```java
// Set the data below for your needs
final String chainId = "...";

final String payerAccountName = "...";
final String payerAccountPublicKey = "...;

final String contract = "...";
final String action = "...";
final String data = "...";
final String[] permissions = "...";
final String[] requiredPublicKeys = "...";

YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://testnet-sentinel.yosemitelabs.org:8888", "http://127.0.0.1:8900");
//apiClient.setTransactionFeePayer(payerAccountName); // globally set but not recommended

// Here, we use YosemiteNativeTokenJ as an example
YosemiteJ yxj = new YosemiteNativeTokenJ(apiClient);

TransactionParameters txParameters = TransactionParameters.Builder().
        addPermission(...).addPermission(...).
        addPublicKey(...).addPublicKey(...).
        setTransactionFeePayer(payerAccountName).
        build();
final SignedTransaction signedTransactionByService = yxj.signTransaction(
    contract, action , data, txParameters).join();

// Send `signedTransactionByService` to the payer. Here we assume that `unmarshalledTransaction` is what the payer side receives and unmarshalls after that.
final SignedTransaction unmarshalledReceivedTransaction = "...";

// Lastly, sign the transaction with the payer's account. Remember how the method signature is different from the one above. The former `signTransaction()` is only used when the signed transaction is initiated and all subsequent signing operations should be performed by the one below.
final SignedTransaction finalSignedTransaction = yxj.signTransaction(
    unmarshalledReceivedTransaction, chainId, Collections.singletonList(payerAccountPublicKey)).join();

// Push the transaction to the network
PushedTransaction pushedTransaction = apiClient.pushTransaction(new PackedTransaction(finalSignedTransaction)).execute();
```

# Yosemite Actions

Note that most methods below are just the wrapper of HTTP JSON request using `pushAction` method.

## Passing Permissions and its Public Keys To Yosemite API
For transaction integrity, authentication and transaction fee, all transactions are signed by proper accounts before pushing them.
For generating transaction signature by the wallet daemon, keyos, the account permission and its matching public key are required.

In most cases, a permission is denoted by the account name following its permistion name with the denominator '@'. e.g. "serviceuser1@active"

With the permission, the matching public keys are required to sign the transaction by the wallet daemon.
In most cases, there is one public key to match its active permission.

We assume that the public keys of the accounts are already known to the service provider.
Before creating an account, the service provider must create the key pair and must save the public key somewhere like RDB for the account creation.
It can continuously use the saved public key to push the transaction to the Yosemite chain.

If the permission is not provided, the API use the default ones.
But the public key is not provided, DApps would undergo performance problem to search the requied public keys.
FYI : https://github.com/YosemiteLabs/yosemite-j/issues/27

## System Actions

### Create a new account
```java
import io.yosemite.services.yxcontracts.*;

YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://testnet-sentinel.yosemitelabs.org:8888", "http://127.0.0.1:8900");
YosemiteSystemJ yxj = new YosemiteSystemJ(apiClient);

String identityPublicKey = "YOS8fCYDtA6FRYtnDpJ4qkoHq3riQUDyTebdsTAR5SDYUkaefNHMR"; // get it from your service location

TransactionParameters txParameters = TransactionParameters.Builder().
        addPermission("identity").
        addPublicKey(identityPublicKey).
        build();
PushedTransaction pushedTransaction = yxj.createAccount("identity", "user1account",
                "YOS8Ledj...vr9gj",
                "YOS8Ledj...vr9gj",
                txParameters).join();
```

## Standard Token Actions

* All Yosemite accounts embed standard token actions, issue, redeem, transfer and settokenmeta. It means all accounts can be token issuer.
* For example, if you makes the account 'mypersonaltk', the token issuer is 'mypersonaltk'. You can issue your token after setting token meta information including token symbol and precision.

### Set Token Meta
```java
YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://testnet-sentinel.yosemitelabs.org:8888", "http://127.0.0.1:8900");
StandardToken standardToken = new StandardToken(apiClient);

TransactionParameters txParameters = TransactionParameters.Builder().
        addPermission("mypersonaltk").
        setTransactionFeePayer("mypersonaltk").
        build();
PushedTransaction pushedTransaction = standardToken.setTokenMeta("DUSD", 4, "mypersonaltk", "url", "description", txParameters).join();
```

### Issueing Token
```java
PushedTransaction pushedTransaction = standardToken.issueToken("user1", "100000.0000 DUSD", "mypersonaltk", "my memo", txParameters).join();
```

### Redeeming Token
```java
PushedTransaction pushedTransaction = standardToken.redeemToken("20000.0000 DUSD", "mypersonaltk", "my memo", txParameters).join();
```

### Transferring Token
```java
PushedTransaction pushedTransaction = standardToken.transferToken("user1", "user2", "100.0000 DUSD", "mypersonaltk", "my memo", txParameters).join();
```

### Getting Token Statistics of Issuer
```java
import io.yosemite.data.remote.chain.TokenInfo;

TokenInfo tokenInfo = standardToken.getTokenInfo("mypersonaltk").join();
```

### Getting Token Balance of Account
* Balance for user1
```java
import io.yosemite.data.types.TypeAsset;

TypeAsset user1AccountBalance = standardToken.getAccountBalance("mypersonaltk", "user1").join();
TypeAsset user2AccountBalance = standardToken.getAccountBalance("mypersonaltk", "user2").join();
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
        signers, expirationTime, 0, EnumSet.noneOf(KYCStatusType.class), (short)0, null).join();
```
* Even if createDigitalContract method returns successfully, the digital contract you have created is not actually created on the YosemiteChain.
* The create action must be included in the block and finally confirmed by other block producers, which it is called the action is irreversible.
* You must check the finality or irreversibility of the action so that the digital contract is actually created.
* The program must poll(check regurarly) to confirm the irreversibility with [getTransaction method](#getting-the-transaction-information) or [getActions method](#getting-the-list-of-actions).

### Adding Additional Signers
```java
List<String> newSigners = Collections.singletonList("user3");
pushedTransaction = yxj.addSigners("servprovider", 11, newSigners, null).join();
```

### Signing Digital Contract
```java
pushedTransaction = yxj.signDigitalDocument("servprovider", 11, "user2", "", null).join();
pushedTransaction = yxj.signDigitalDocument("servprovider", 11, "user3", "I am user3", null).join();
```
### Updating Additional Document Hash of Digital Contract
```java
pushedTransaction = yxj.updateAdditionalDocumentHash("servprovider", 11, "added after signing", null).join();
```

### Removing Digital Contract
```java
pushedTransaction = yxj.removeDigitalContract("servprovider", 11, null).join();
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

