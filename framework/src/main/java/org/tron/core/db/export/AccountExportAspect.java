package org.tron.core.db.export;

import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tron.core.capsule.BlockCapsule;


@Slf4j(topic = "exporter")
@Component
@Aspect
public class AccountExportAspect {

  public static final AtomicLong EXPORT_NUM = new AtomicLong(0);
  public static final AtomicLong EXPORT_TIME = new AtomicLong(0);

  @Autowired
  private AccountExportUtil util;

  @Pointcut("execution(** org.tron.core.db.Manager.pushBlock(..)) && args(block)")
  public void pointPushBlock(BlockCapsule block) {

  }

  @After("pointPushBlock(block)")
  public void exportAccount(BlockCapsule block) {

    if (block.getNum() == EXPORT_NUM.get()
            || Math.abs(block.getTimeStamp() - EXPORT_TIME.get()) <= 6000) {
      try {
        util.doExport(block.getNum());
      } catch (Exception ex) {
        logger.error("export account failure: {}", ex.getMessage());
      }
    }
  }

  @AfterThrowing("execution(** org.tron.core.db.Manager.pushBlock(..)) && args(block)")
  public void logException(BlockCapsule block) {

  }

}

