package org.tron.core.db.export;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tron.common.utils.StringUtil;
import org.tron.core.capsule.AccountCapsule;
import org.tron.core.db.Manager;
import org.tron.core.services.interfaceOnSolidity.WalletOnSolidity;
import org.tron.core.store.AccountStore;
import org.tron.protos.Protocol.AccountType;

@Slf4j(topic = "exporter")
@Component
public class AccountExporter {

  public static final String FILE_NAME = "accounts.csv";

  private static WalletOnSolidity walletOnSolidity;


  @Autowired
  public void setWalletOnSolidity(WalletOnSolidity walletOnSolidity) {
    AccountExporter.walletOnSolidity = walletOnSolidity;
  }

  @Autowired
  public Manager dbManager;

  public void export(long height, AccountStore accountStore) {

    logger.info("height: {} , export account data", height);
    exportAll(accountStore, height);
    exportNormal(accountStore, height);
    exportContract(accountStore, height);
    exportAssetIssue(accountStore, height);
  }


//  private Map<String, Long> readFromJustContract() {
//    Map<String, Long> accounts = justContractTool.readFromJustContract();
//    return accounts;
//  }

  private void merge(Map<String, Long> from, Map<String, Long> to) {
    Iterator<Entry<String, Long>> iterator = from.entrySet().iterator();
    while (iterator.hasNext()) {
      Entry<String, Long> entry = iterator.next();
      if (to.containsKey(entry.getKey())) {
        to.put(entry.getKey(), entry.getValue() + to.get(entry.getKey()));
      } else {
        to.put(entry.getKey(), entry.getValue());
      }

    }
  }

  private void exportAll(AccountStore accountStore,
                         long fingerprint) {
    AtomicLong total = new AtomicLong(0);

    Map<String, Long> accounts = loopAccountStore(accountStore, null, total);
    logger.info("export {} All accounts, total {} trx(SUN)", accounts.size(), total);

    String filename = "block_" + fingerprint + "_all_" + FILE_NAME;
    writeCSVFile(total, accounts, filename);
  }

  private void exportNormal(AccountStore accountStore, long fingerprint) {
    AtomicLong total = new AtomicLong(0);
    AtomicLong totalFromJust = new AtomicLong(0);

    Map<String, Long> accounts = loopAccountStore(accountStore, AccountType.Normal, total);
    logger.info("export {} Normal accounts, total {} trx(SUN)", accounts.size(), total);

    String filename = "block_" + fingerprint + "_normal_" + FILE_NAME;
    writeCSVFile(total, accounts, filename);
  }

  private void exportContract(AccountStore accountStore, long fingerprint) {
    AtomicLong total = new AtomicLong(0);
    Map<String, Long> accounts = loopAccountStore(accountStore, AccountType.Contract, total);
    logger.info("export {} Contract accounts, total {} trx(SUN)", accounts.size(), total);

    String filename = "block_" + fingerprint + "_contract_" + FILE_NAME;
    writeCSVFile(total, accounts, filename);
  }

  private void exportAssetIssue(AccountStore accountStore, long fingerprint) {
    AtomicLong total = new AtomicLong(0);
    Map<String, Long> accounts = loopAccountStore(accountStore, AccountType.AssetIssue, total);
    logger.info("export {} AssetIssue accounts, total {} trx(SUN)", accounts.size(), total);

    String filename = "block_" + fingerprint + "_assetissue_" + FILE_NAME;
    writeCSVFile(total, accounts, filename);
  }

  /**
   * This is used for iterate AccountStore. Support simple filter for different type of Account.
   *
   * @param: accountStore  Iterable database of account.
   * @param: type          AccountType for filter.
   * @param: total         Output param, used for recording the total trx amount.
   * @return: accounts     Map<String, long> Key is account address of Base58Check format; Value is
   * Trx amount(unit SUN) this address holds, including balance, frozen balance for self's bandwidth
   * and energy, frozen balance for others' bandwidth and energy.
   */
  private Map<String, Long> loopAccountStore(AccountStore accountStore, AccountType type,
                                             AtomicLong total) {
    Map<String, Long> accounts = new HashMap<>();

    Iterator<Entry<byte[], AccountCapsule>> iterator = accountStore.iterator();
    while (iterator.hasNext()) {
      Entry<byte[], AccountCapsule> entry = iterator.next();
      AccountCapsule accountCapsule = entry.getValue();

      if (type != null && accountCapsule.getType() != type) {
        continue;
      }

      String address = StringUtil.encode58Check(entry.getKey());
      long trx = accountCapsule.getBalance() + accountCapsule.getFrozenBalance() + accountCapsule
              .getEnergyFrozenBalance() + accountCapsule.getDelegatedFrozenBalanceForBandwidth()
              + accountCapsule.getDelegatedFrozenBalanceForEnergy();

      if (trx > 0) {
        total.getAndAdd(trx);
      }

      accounts.put(address, trx);
    }

    return accounts;
  }

  /**
   * This is used for writing the account data to csv file.
   *
   * @param: accounts      Map<String, long> Key is account address of Base58Check format; Value is
   * Trx amount(unit SUN).
   * @param: total         The total trx amount.
   * @param: filename      csv file name.
   */
  private void writeCSVFile(AtomicLong total, Map<String, Long> accounts, String filename) {
    try (CSVPrinter printer = new CSVPrinter(new FileWriter(filename),
            CSVFormat.EXCEL.withHeader("address", "balance"))) {
      printer.printRecord("total", total.get());
      accounts.forEach((k, v) -> {
        String address = k;
        long balance = v;
        try {
          printer.printRecord(address, balance);
        } catch (Exception e1) {
          logger.error("address {} write error.", address);
        }
      });
    } catch (Exception e) {
      logger.error("export error", e);
    }
  }
}
