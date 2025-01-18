package org.atonic.cryptexsimple.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.service.TaskScheduler;
import org.atonic.cryptexsimple.service.VWAPService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
@AllArgsConstructor
public class TaskSchedulerImpl implements TaskScheduler {
    private final VWAPService vwapService;

    @Override
    @Scheduled(cron = "0 * * * * *")
    public void executeMinutelyTasks() {
        try {
            log.debug("Starting minutely tasks");

            CompletableFuture.allOf(
                CompletableFuture.runAsync(this::snapshotVWAP)
            ).get(55, TimeUnit.SECONDS);

            log.debug("Minutely tasks completed");
        } catch (TimeoutException e) {
            log.error("Minutely tasks timed out");
        } catch (Exception e) {
            log.error("Error while executing minutely tasks: {}", e.getMessage());
        }
    }

    private void snapshotVWAP() {
        for (CryptoSymbol cryptoSymbol : CryptoSymbol.values()) {
            for (FIATSymbol fiatSymbol : FIATSymbol.values()) {
                vwapService.snapshotVWAP(cryptoSymbol, fiatSymbol);
            }
        }
    }
}
