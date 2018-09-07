# Sample

It assumes that the single node testnet on the local enviroment is prepared.
So we are going to provide quick command guideline for preparation.

## Single node testnet preparation
* It assumes that PATH environment is set to `yosemite`, `clyos`, and `keyos` executables.
* https://github.com/YosemiteLabs/yosemite-public-blockchain#local-single-node-testnet
   * execute yosemite like below;
```
yosemite --filter-on yx.dcontract:create: --filter-on yx.dcontract:sign:
```

### prepare wallet and its management daemon(keyos)
* create default wallet (keyos is executed implicitly by clyos)
```shell
clyos wallet create --to-console
```
* save its password somewhere
* If the wallet is locked or you should need to execute keyos again, just unlock it like below;
```
clyos wallet unlock --password your_password
```
* You can check keyos daemon like below;
```shell
$ ps -ef | grep keyos
eugene    22242   1361  0 12:26 pts/2    00:00:00 ..../yosemite-public-blockchain/build/programs/keyos/keyos --http-server-address=127.0.0.1:8900
```

### prepare others
* It assumes that the current directory is `yosemite-public-blockchain` local git repository.
* We just want this process be simple as possible. If you need more, you should check https://github.com/YosemiteLabs/yosemite-public-blockchain
```
# prepare yosemite contract accounts
clyos wallet import --private-key 5JwvMHnfQC5TjJ6RshcuFQbK2ydy9vEdAugE1HYZBYWThwD27LZ
clyos create account yosemite d1 EOS7WXCxsMuSChfEX5UPe19o6GZaELji2yQBgwgmXtbvytxkSGjKz
clyos create account yosemite yx.ntoken EOS7WXCxsMuSChfEX5UPe19o6GZaELji2yQBgwgmXtbvytxkSGjKz
clyos create account yosemite yx.token EOS7WXCxsMuSChfEX5UPe19o6GZaELji2yQBgwgmXtbvytxkSGjKz
clyos create account yosemite yx.identity EOS7WXCxsMuSChfEX5UPe19o6GZaELji2yQBgwgmXtbvytxkSGjKz
clyos create account yosemite yx.txfee EOS7WXCxsMuSChfEX5UPe19o6GZaELji2yQBgwgmXtbvytxkSGjKz
clyos create account yosemite yx.dcontract EOS7WXCxsMuSChfEX5UPe19o6GZaELji2yQBgwgmXtbvytxkSGjKz

# install yosemite contracts
clyos set contract yx.identity build/contracts/yx.identity -p yx.identity
clyos set contract yx.ntoken build/contracts/yx.ntoken -p yx.ntoken
clyos set contract yx.token build/contracts/yx.token -p yx.token
clyos set contract yx.txfee build/contracts/yx.txfee -p yx.txfee
clyos set contract yx.dcontract build/contracts/yx.dcontract -p yx.dcontract
clyos set contract yosemite build/contracts/yx.system/ -p yosemite

# set some yosemite contract accounts as privileged
clyos push action yosemite setpriv '["yx.ntoken",1]' -p yosemite@active
clyos push action yosemite setpriv '["yx.token",1]' -p yosemite@active
clyos push action yosemite setpriv '["yx.dcontract",1]' -p yosemite@active

# prepare system depository and identity authority service
clyos push action yosemite regsysdepo '["d1","http://d1.org",1]' -p d1@active -p yosemite@active
clyos push action yosemite authsysdepo '["d1"]' -p yosemite@active
clyos push action yosemite regidauth '["d1","http://d1.org",1]' -p d1@active -p yosemite@active
clyos push action yosemite authidauth '["d1"]' -p yosemite@active

# set transaction fee to yx.system service (for newaccount)
clyos push action yx.txfee settxfee '[ "tf.newacc", "1000.0000 DKRW" ]' -p yosemite@active

# set transaction fee to yx.dcontract service (for DigitalContractJSample)
clyos push action yx.txfee settxfee '{"operation":"tf.dccreate", "fee":"50.0000 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.dcaddsign", "fee":"10.0000 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.dcsign", "fee":"30.0000 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.dcupadd", "fee":"5.0000 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.dcremove", "fee":"0.0000 DKRW"}}' -p yosemite

# set transaction fee to yx.token service (for TokenContractJSample)
clyos push action yx.txfee settxfee '{"operation":"tf.tcreate", "fee":"10000.0000 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.tissue", "fee":"100.0000 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.tredeem", "fee":"100.0000 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.ttransfer", "fee":"10.0000 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.tsetkyc", "fee":"5.0000 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.tsetopts", "fee":"5.0000 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.tfreezeac", "fee":"5.0000 DKRW"}}' -p yosemite
```

## Build
It assumes that yosemite-j library is prepared with `./gradlew shadowJar`.

```shell
javac -classpath ../build/libs/yosemitej-${latest-version}-all.jar io/yosemite/sample/DigitalContractJSample.java
javac -classpath ../build/libs/yosemitej-${latest-version}-all.jar io/yosemite/sample/TokenContractJSample.java
```
## Execute
```shell
java -classpath ../build/libs/yosemitej-${latest-version}-all.jar:. io.yosemite.sample.DigitalContractJSample
java -classpath ../build/libs/yosemitej-${latest-version}-all.jar:. io.yosemite.sample.TokenContractJSample
```
