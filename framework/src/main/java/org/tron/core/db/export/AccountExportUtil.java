package org.tron.core.db.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tron.core.db.Manager;


@Component
public class AccountExportUtil {

  private Manager manager;

  @Autowired
  public AccountExportUtil(Manager manager) {
    this.manager = manager;
  }

  @Autowired
  private AccountExporter exporter;

  public void doExport(long height){
    exporter.export(height, this.manager.getAccountStore());
  }
}