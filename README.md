# Yosemite Chain Java API

This version is currently working with EOS mainnet `v1.0.7` and will be converted to the Yosemite Chain Java API.
The project's goal would be something like Java version of `cloes` in `EOS` which provides convenient interfaces for the useful commands such as pushing action / multisig.

The first step would be to implement a wrapper for basic HTTP APIs and then the other convenient interface would be built on top of it.

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
 - [ ] get_public_keys
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