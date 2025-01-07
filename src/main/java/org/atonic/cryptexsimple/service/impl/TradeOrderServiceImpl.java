package org.atonic.cryptexsimple.service.impl;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.model.entity.jpa.*;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.enums.OrderStatus;
import org.atonic.cryptexsimple.model.enums.OrderType;
import org.atonic.cryptexsimple.model.repository.jpa.CryptocurrencyRepository;
import org.atonic.cryptexsimple.model.repository.jpa.TradeOrderRepository;
import org.atonic.cryptexsimple.model.repository.jpa.TradeRepository;
import org.atonic.cryptexsimple.service.CryptoWalletService;
import org.atonic.cryptexsimple.service.FIATWalletService;
import org.atonic.cryptexsimple.service.TradeOrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TradeOrderServiceImpl implements TradeOrderService {
    private final CryptoWalletService cryptoWalletService;
    private final FIATWalletService fiatWalletService;

    private final TradeOrderRepository tradeOrderRepository;
    private final TradeRepository tradeRepository;
    private final CryptocurrencyRepository cryptocurrencyRepository;

    @Override
    public Optional<TradeOrder> placeOrder(TradeOrder tradeOrder) {
        if (!tradeOrder.getFiatCurrency().getSymbol().equals(FIATSymbol.USD)) {
            return Optional.empty();
        }
        tradeOrder.setStatus(OrderStatus.OPEN);
        tradeOrder.setTimestamp(LocalDateTime.now());
        if (OrderType.BUY.equals(tradeOrder.getType())) {
            FIATWallet wallet = fiatWalletService.getUserFIATWallets(tradeOrder.getUser()).getFirst();
            if (wallet.getBalances().getFirst().getBalance().compareTo(tradeOrder.getAmount().multiply(tradeOrder.getPrice())) < 0) {
                return Optional.empty();
            }
        } else if (OrderType.SELL.equals(tradeOrder.getType())) {
            CryptoWalletBalance balance = cryptoWalletService.getBalance(
                tradeOrder.getCryptoWallet().getId(),
                tradeOrder.getCryptocurrency().getSymbol());
            if (balance.getBalance().compareTo(tradeOrder.getAmount()) < 0) {
                return Optional.empty();
            }
        }

        tradeOrderRepository.save(tradeOrder);
        matchOrders(tradeOrder.getCryptocurrency(), tradeOrder.getUser());
        return Optional.of(tradeOrder);
    }

    @Override
    public void matchOrders(Cryptocurrency crypto, User user) {
        List<TradeOrder> buyOrders = tradeOrderRepository.findByStatusAndCryptocurrency(OrderStatus.OPEN, crypto).stream()
            .filter(o -> o.getType().equals(OrderType.BUY))
            .sorted(Comparator.comparing(TradeOrder::getPrice).reversed())
            .toList();

        List<TradeOrder> sellOrders = tradeOrderRepository.findByStatusAndCryptocurrency(OrderStatus.OPEN, crypto).stream()
            .filter(o -> o.getType().equals(OrderType.SELL))
            .sorted(Comparator.comparing(TradeOrder::getPrice))
            .toList();

        for (TradeOrder buyOrder : buyOrders) {
            for (TradeOrder sellOrder : sellOrders) {
                if (buyOrder.getPrice().compareTo(sellOrder.getPrice()) >= 0 && !Objects.equals(buyOrder.getUser().getId(), sellOrder.getId())) {
                    executeTrade(buyOrder, sellOrder, crypto);
                }
            }
        }
    }

    @Override
    public List<TradeOrder> getOpenTradeOrders(CryptoSymbol symbol) {
        Cryptocurrency crypto = cryptocurrencyRepository.findBySymbol(symbol)
            .orElseThrow(() -> new RuntimeException("Cryptocurrency not found"));

        return tradeOrderRepository.findByStatusAndCryptocurrency(OrderStatus.OPEN, crypto);
    }

    private void executeTrade(TradeOrder buyOrder, TradeOrder sellOrder, Cryptocurrency crypto) {
        BigDecimal tradeAmount = buyOrder.getAmount().min(sellOrder.getAmount());

        buyOrder.setAmount(buyOrder.getAmount().subtract(tradeAmount));
        sellOrder.setAmount(sellOrder.getAmount().subtract(tradeAmount));

        if (buyOrder.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            buyOrder.setStatus(OrderStatus.FILLED);
        }
        if (sellOrder.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            sellOrder.setStatus(OrderStatus.FILLED);
        }

        tradeOrderRepository.save(buyOrder);
        tradeOrderRepository.save(sellOrder);

        Trade trade = new Trade();
        trade.setAmount(tradeAmount);
        trade.setPrice(sellOrder.getPrice());
        trade.setTimestamp(LocalDateTime.now());
        trade.setCryptocurrency(crypto);
        tradeRepository.save(trade);

        BigDecimal totalCost = tradeAmount.multiply(sellOrder.getPrice());

        // Update wallets
        fiatWalletService.updateBalance(buyOrder.getFiatWallet().getId(), buyOrder.getFiatCurrency().getSymbol(), totalCost.negate());
        cryptoWalletService.updateBalance(buyOrder.getCryptoWallet().getId(), crypto.getSymbol(), tradeAmount);

        fiatWalletService.updateBalance(sellOrder.getFiatWallet().getId(), buyOrder.getFiatCurrency().getSymbol(), totalCost);
        cryptoWalletService.updateBalance(sellOrder.getCryptoWallet().getId(), crypto.getSymbol(), tradeAmount.negate());
    }

}
