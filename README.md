<h1 align="center">
  <br>
  <img width=20% src="https://github.com/tronprotocol/wiki/blob/master/images/java-tron.jpg?raw=true">
  <br>
  java-tron 
  <br>
</h1>

<h4 align="center">
  Java implementation of the <a href="https://tron.network">Tron Protocol</a>
</h4>


<p align="center">
  <a href="https://gitter.im/tronprotocol/allcoredev">
    <img src="https://camo.githubusercontent.com/da2edb525cde1455a622c58c0effc3a90b9a181c/68747470733a2f2f6261646765732e6769747465722e696d2f4a6f696e253230436861742e737667">
  </a>

  <a href="https://travis-ci.org/tronprotocol/java-tron">
    <img src="https://travis-ci.org/tronprotocol/java-tron.svg?branch=develop">
  </a>

  <a href="https://codecov.io/gh/tronprotocol/java-tron">
    <img src="https://codecov.io/gh/tronprotocol/java-tron/branch/develop/graph/badge.svg" />
  </a>

  <a href="https://github.com/tronprotocol/java-tron/issues">
    <img src="https://img.shields.io/github/issues/tronprotocol/java-tron.svg">
  </a>

  <a href="https://github.com/tronprotocol/java-tron/pulls">
    <img src="https://img.shields.io/github/issues-pr/tronprotocol/java-tron.svg">
  </a>

  <a href="https://github.com/tronprotocol/java-tron/graphs/contributors">
    <img src="https://img.shields.io/github/contributors/tronprotocol/java-tron.svg">
  </a>

  <a href="LICENSE">
    <img src="https://img.shields.io/github/license/tronprotocol/java-tron.svg">
  </a>
</p>

<p align="center">
  <a href="#quick-start">Quick Start</a> •
  <a href="#deploy">Deploy</a> •
  <a href="#Deployment">Deployment</a> •
  <a href="#Channel">Channel</a> •
  <a href="#Contribution">Contribution</a> •
  <a href="#Resources">Resources</a>
</p>

## Introduction
This is a demo project for exporting data when node is running. This project is based on java-tron GreatVoyage-v4.4.3(Pythagoras). And added exporting data related codes.


## What new code have been added
1. Add a new http api to set the block height and block timestamp
   
   https://github.com/vivian-kang/java-tron-airdrop/blob/main/framework/src/main/java/org/tron/core/services/http/FullNodeHttpApiService.java#L293 
   https://github.com/vivian-kang/java-tron-airdrop/blob/main/framework/src/main/java/org/tron/core/services/http/ExportAccountServlet.java 


2. Add account exporting code in /framework/src/main/java/org/tron/core/db/export/



## How to do data exporting before airdrop
1. Set block height and block timestamp through http api: /wallet/exportaccount  
   For example: http://127.0.0.1:16887/wallet/exportaccount?block_number=950&timestamp=1642153902000
   

2. After the specified block arrived, or the time is up, the node will export the account data, you can find the exported files on the node directory:

   * block_950_all_accounts.csv  : includes all accounts which hold TRX
   * block_950_normal_accounts.csv : includes all normal accounts which hold TRX
   * block_950_contract_accounts.csv : includes all contract accounts which hold TRX
   * block_950_assetissue_accounts.csv : includes all assetIssue accounts which hold TRX
 

3. After exporting the data, the node continues to synchronize data from other node 
