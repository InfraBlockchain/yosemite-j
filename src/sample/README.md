# Sample

It assumes that the sample programs are run on the Yosemite Sentinel Testnet.


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

### prepare private keys for the sample accounts
* For DigitalContractJSample
```
clyos wallet import --private-key YPV_5JPknPMKNadXMaixB5zo6zAss7ZMtbJcyETxhVV19cP115RCKBi
clyos wallet import --private-key YPV_5K31Vufu6KibpfUjrmSYQm3BHfG7XYPYWMvQG4CiSJUGNv5WJHq
clyos wallet import --private-key YPV_5JdwhSpBSoZVysfhGtXPrVdNSbGBCr7hUoZRAaxWN6HfX7tKQbF
```
* For StandardTokenSample1
```
clyos wallet import --private-key YPV_5HwSdWSAxu5QSVPN6dB9gtTKXFLKSnQTYGa7eLRN91aiw98kCwL
```

## Build
```
./gradlew buildSample
```

## Execute
* prepare logic (if exists)
```shell
./gradlew -PmainClass=DigitalContractJSample runSample -Ppargs='-prepare'
```

* main logic
```shell
./gradlew -PmainClass=DigitalContractJSample runSample
./gradlew -PmainClass=StandardTokenSample runSample
```
