package org.atonic.cryptexsimple.validators;

import lombok.extern.slf4j.Slf4j;
import org.atonic.cryptexsimple.model.pojo.TradePOJO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

@Slf4j
@Component
public class TradePOJOValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return TradePOJO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        TradePOJO trade = (TradePOJO) target;
        if (trade.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            errors.rejectValue("amount", "trade.amount.invalid", "Trade amount must be greater than 0");
            log.error("Trade amount must be greater than 0: amount={}", trade.getAmount());
        }
        if (trade.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.rejectValue("price", "trade.price.invalid", "Trade price must be greater than 0");
            log.error("Trade price must be greater than 0: price={}", trade.getPrice());
        }
        if (trade.getSellerId().equals(trade.getBuyerId())) {
            errors.rejectValue("sellerId", "trade.seller.invalid", "Seller and buyer cannot be the same");
            log.error("Seller and buyer cannot be the same: seller={}, buyer={}", trade.getSellerId(), trade.getBuyerId());
        }
        if (trade.getSellerFIATWalletId().equals(trade.getBuyerFIATWalletId())) {
            errors.rejectValue("sellerFIATWalletId", "trade.sellerFIATWallet.invalid", "Seller and buyer cannot use the same FIAT wallet");
            log.error("Seller and buyer cannot use the same FIAT wallet: sellerFIATWallet={}, buyerFIATWallet={}", trade.getSellerFIATWalletId(), trade.getBuyerFIATWalletId());
        }
        if (trade.getSellerCryptoWalletId().equals(trade.getBuyerCryptoWalletId())) {
            errors.rejectValue("sellerCryptoWalletId", "trade.sellerCryptoWallet.invalid", "Seller and buyer cannot use the same Crypto wallet");
            log.error("Seller and buyer cannot use the same Crypto wallet: sellerCryptoWallet={}, buyerCryptoWallet={}", trade.getSellerCryptoWalletId(), trade.getBuyerCryptoWalletId());
        }
    }
}
