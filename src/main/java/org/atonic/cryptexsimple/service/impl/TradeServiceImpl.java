package org.atonic.cryptexsimple.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atonic.cryptexsimple.model.dto.TradeDetails;
import org.atonic.cryptexsimple.model.entity.jpa.*;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.pojo.TradePOJO;
import org.atonic.cryptexsimple.model.repository.jpa.CryptocurrencyRepository;
import org.atonic.cryptexsimple.model.repository.jpa.FIATCurrencyRepository;
import org.atonic.cryptexsimple.model.repository.jpa.TradeRepository;
import org.atonic.cryptexsimple.service.CryptoWalletService;
import org.atonic.cryptexsimple.service.FIATWalletService;
import org.atonic.cryptexsimple.service.TradeService;
import org.atonic.cryptexsimple.service.UserService;
import org.atonic.cryptexsimple.validators.TradePOJOValidator;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TradeServiceImpl implements TradeService {
    private final CryptoWalletService cryptoWalletService;
    private final FIATWalletService fiatWalletService;
    private final UserService userService;

    private final CryptocurrencyRepository cryptocurrencyRepository;
    private final FIATCurrencyRepository fiatCurrencyRepository;
    private final TradeRepository tradeRepository;

    private final TradePOJOValidator tradePOJOValidator;

    @Transactional
    @Override
    public void executeTrade(TradePOJO tradePOJO) {
        validateTradePOJO(tradePOJO);
        TradeDetails tradeDetails = prepareTradeDetails(tradePOJO);
        Trade trade = prepareTrade(tradeDetails);

        updateCryptoWalletsBalances(tradePOJO.getSellerCryptoWalletId(), tradePOJO.getBuyerCryptoWalletId(), tradePOJO.getCryptoSymbol(), tradePOJO.getAmount());
        updateFIATWalletsBalances(tradePOJO.getSellerFIATWalletId(), tradePOJO.getBuyerFIATWalletId(), tradePOJO.getFiatSymbol(), tradePOJO.getAmount(), tradePOJO.getPrice());
        tradeRepository.save(trade);
        log.info("Trade executed between seller={} and buyer={}. Trade params={} {} for {} {}",
            tradePOJO.getSellerId(),
            tradePOJO.getBuyerId(),
            tradePOJO.getAmount(),
            tradePOJO.getCryptoSymbol(),
            tradePOJO.getAmount().multiply(tradePOJO.getPrice()),
            tradePOJO.getFiatSymbol());
    }

    @Override
    public List<Trade> getTradesInGivenTimeframe(CryptoSymbol symbol, LocalDateTime from, LocalDateTime to) {
        Optional<Cryptocurrency> crypto = cryptocurrencyRepository.findBySymbol(symbol);
        if (crypto.isEmpty()) {
            log.error("Cryptocurrency not found for trades in given timeframe: symbol={}", symbol);
            throw new IllegalArgumentException("Cryptocurrency with symbol " + symbol + " does not exist");
        }

        return tradeRepository.findAllByCryptocurrencyAndTimestampBetween(crypto.get(), from, to);
    }

    @Override
    public List<Trade> getTop3Trades(CryptoSymbol symbol) {
        Optional<Cryptocurrency> crypto = cryptocurrencyRepository.findBySymbol(symbol);
        if (crypto.isEmpty()) {
            log.error("Cryptocurrency not found for Top 3 trades: symbol={}", symbol);
            throw new IllegalArgumentException("Cryptocurrency with symbol " + symbol + " does not exist");
        }

        return tradeRepository.findTop3ByCryptocurrencyOrderByTimestamp(crypto.get());
    }

    private TradeDetails prepareTradeDetails(TradePOJO tradePOJO) {
        Optional<User> seller = userService.getUser(tradePOJO.getSellerId());
        Optional<User> buyer = userService.getUser(tradePOJO.getBuyerId());

        if (seller.isEmpty() || buyer.isEmpty()) {
            log.error("User not found: seller={}, buyer={}", seller, buyer);
            throw new IllegalArgumentException("User with id " + tradePOJO.getSellerId() + " or " + tradePOJO.getBuyerId() + " does not exist");
        } else if (seller.get().getId().equals(buyer.get().getId())) {
            log.error("Seller and buyer are the same: seller={}, buyer={}", seller, buyer);
            throw new IllegalArgumentException("Seller and buyer cannot be the same");
        }

        Optional<FIATWallet> sellerFIATWallet = fiatWalletService.getFIATWallet(tradePOJO.getSellerFIATWalletId());
        Optional<CryptoWallet> sellerCryptoWallet = cryptoWalletService.getCryptoWallet(tradePOJO.getSellerCryptoWalletId());

        Optional<FIATWallet> buyerFIATWallet = fiatWalletService.getFIATWallet(tradePOJO.getBuyerFIATWalletId());
        Optional<CryptoWallet> buyerCryptoWallet = cryptoWalletService.getCryptoWallet(tradePOJO.getBuyerCryptoWalletId());


        if (sellerFIATWallet.isEmpty() || sellerCryptoWallet.isEmpty() || buyerFIATWallet.isEmpty() || buyerCryptoWallet.isEmpty()) {
            log.error("Wallets not found: sellerFIATWallet={}, sellerCryptoWallet={}, buyerFIATWallet={}, buyerCryptoWallet={}",
                sellerFIATWallet, sellerCryptoWallet, buyerFIATWallet, buyerCryptoWallet);
            throw new IllegalArgumentException("Wallets not found");
        }

        Optional<FIATCurrency> tradeFIATCurrency = fiatCurrencyRepository.findBySymbol(tradePOJO.getFiatSymbol());

        if (tradeFIATCurrency.isEmpty()) {
            log.error("FIAT currency not found: tradeFIATCurrency={}", tradeFIATCurrency);
            throw new IllegalArgumentException("FIAT currency not found");
        }

        Optional<Cryptocurrency> tradeCryptocurrency = cryptocurrencyRepository.findBySymbol(tradePOJO.getCryptoSymbol());

        if (tradeCryptocurrency.isEmpty()) {
            log.error("Cryptocurrency not found: tradeCryptocurrency={}", tradeCryptocurrency);
            throw new IllegalArgumentException("Cryptocurrency not found");
        }

        return TradeDetails.builder()
            .tradePOJO(tradePOJO)
            .seller(seller.get())
            .buyer(buyer.get())
            .sellerFIATWallet(sellerFIATWallet.get())
            .sellerCryptoWallet(sellerCryptoWallet.get())
            .buyerFIATWallet(buyerFIATWallet.get())
            .buyerCryptoWallet(buyerCryptoWallet.get())
            .tradeFIATCurrency(tradeFIATCurrency.get())
            .tradeCryptocurrency(tradeCryptocurrency.get())
            .build();
    }

    private Trade prepareTrade(TradeDetails tradeDetails) {
        Trade trade = new Trade();
        trade.setAmount(tradeDetails.getTradePOJO().getAmount());
        trade.setPrice(tradeDetails.getTradePOJO().getPrice());
        trade.setTimestamp(LocalDateTime.now());

        trade.setSeller(tradeDetails.getSeller());
        trade.setBuyer(tradeDetails.getBuyer());

        trade.setSellerFIATWallet(tradeDetails.getSellerFIATWallet());
        trade.setSellerCryptoWallet(tradeDetails.getSellerCryptoWallet());
        trade.setBuyerFIATWallet(tradeDetails.getBuyerFIATWallet());
        trade.setBuyerCryptoWallet(tradeDetails.getBuyerCryptoWallet());

        trade.setFiatCurrency(tradeDetails.getTradeFIATCurrency());
        trade.setCryptocurrency(tradeDetails.getTradeCryptocurrency());

        return trade;
    }

    private void validateTradePOJO(TradePOJO tradePOJO) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(tradePOJO, "tradePOJO");
        tradePOJOValidator.validate(tradePOJO, errors);
        if (errors.hasErrors()) {
            throw new IllegalArgumentException(
                errors.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "))
            );
        }
    }

    private void updateCryptoWalletsBalances(Long sellerCryptoWalletId, Long buyerCryptoWalletId, CryptoSymbol symbol, BigDecimal amount) {
        cryptoWalletService.updateBalance(sellerCryptoWalletId, symbol, amount.negate());
        cryptoWalletService.updateBalance(buyerCryptoWalletId, symbol, amount);
    }

    private void updateFIATWalletsBalances(Long sellerFIATWalletId, Long buyerFIATWalletId, FIATSymbol symbol, BigDecimal amount, BigDecimal price) {
        fiatWalletService.updateBalance(sellerFIATWalletId, symbol, amount.multiply(price));
        fiatWalletService.updateBalance(buyerFIATWalletId, symbol, amount.multiply(price).negate());
    }
}
