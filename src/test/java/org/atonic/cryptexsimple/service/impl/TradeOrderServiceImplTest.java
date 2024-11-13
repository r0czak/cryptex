package org.atonic.cryptexsimple.service.impl;

import org.atonic.cryptexsimple.model.entity.*;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.OrderStatus;
import org.atonic.cryptexsimple.model.enums.OrderType;
import org.atonic.cryptexsimple.model.repository.CryptocurrencyRepository;
import org.atonic.cryptexsimple.model.repository.TradeOrderRepository;
import org.atonic.cryptexsimple.model.repository.TradeRepository;
import org.atonic.cryptexsimple.service.CryptoWalletService;
import org.atonic.cryptexsimple.service.FIATWalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeOrderServiceImplTest {

    @Mock
    private CryptoWalletService cryptoWalletService;

    @Mock
    private FIATWalletService fiatWalletService;

    @Mock
    private TradeOrderRepository tradeOrderRepository;

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private CryptocurrencyRepository cryptocurrencyRepository;

    @InjectMocks
    private TradeOrderServiceImpl tradeOrderService;

    private User buyer;
    private User seller;
    private Cryptocurrency crypto;
    private FIATWallet buyerFiatWallet;
    private FIATWallet sellerFiatWallet;
    private CryptoWalletBalance buyerCryptoBalance;
    private CryptoWalletBalance sellerCryptoBalance;

    @BeforeEach
    public void setUp() {
        buyer = new User("buyer", "buyer@example.com", "password");
        buyer.setId(1L);

        seller = new User("seller", "seller@example.com", "password");
        seller.setId(2L);

        crypto = new Cryptocurrency();
        crypto.setId(1L);
        crypto.setSymbol(CryptoSymbol.BTC);

        buyerFiatWallet = new FIATWallet();
        buyerFiatWallet.setUser(buyer);
        buyerFiatWallet.setBalance(new BigDecimal("10000"));

        sellerFiatWallet = new FIATWallet();
        sellerFiatWallet.setUser(seller);
        sellerFiatWallet.setBalance(new BigDecimal("5000"));

        buyerCryptoBalance = new CryptoWalletBalance();
        buyerCryptoBalance.setBalance(new BigDecimal("1"));
        buyerCryptoBalance.setCryptocurrency(crypto);

        sellerCryptoBalance = new CryptoWalletBalance();
        sellerCryptoBalance.setBalance(new BigDecimal("10"));
        sellerCryptoBalance.setCryptocurrency(crypto);
    }

    @Test
    void testPlaceOrder_BuyOrder_SufficientFunds() {
        // Arrange
        TradeOrder buyOrder = new TradeOrder();
        buyOrder.setUser(buyer);
        buyOrder.setType(OrderType.BUY);
        buyOrder.setPrice(new BigDecimal("5000"));
        buyOrder.setAmount(new BigDecimal("1"));
        buyOrder.setCryptocurrency(crypto);

        when(fiatWalletService.getFIATWallet(buyer)).thenReturn(buyerFiatWallet);
        when(tradeOrderRepository.save(any(TradeOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tradeOrderRepository.findByStatusAndCryptocurrency(OrderStatus.OPEN, crypto)).thenReturn(Collections.emptyList());

        // Act
        Optional<TradeOrder> result = tradeOrderService.placeOrder(buyOrder);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(OrderStatus.OPEN, result.get().getStatus());
        verify(tradeOrderRepository, times(1)).save(buyOrder);
    }

    @Test
    void testPlaceOrder_SellOrder_SufficientCrypto() {
        // Arrange
        TradeOrder sellOrder = new TradeOrder();
        sellOrder.setUser(seller);
        sellOrder.setType(OrderType.SELL);
        sellOrder.setPrice(new BigDecimal("5000"));
        sellOrder.setAmount(new BigDecimal("1"));
        sellOrder.setCryptocurrency(crypto);

        when(cryptoWalletService.getBalance(seller, crypto.getSymbol())).thenReturn(sellerCryptoBalance);
        when(tradeOrderRepository.save(any(TradeOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tradeOrderRepository.findByStatusAndCryptocurrency(OrderStatus.OPEN, crypto)).thenReturn(Collections.emptyList());

        // Act
        Optional<TradeOrder> result = tradeOrderService.placeOrder(sellOrder);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(OrderStatus.OPEN, result.get().getStatus());
        verify(tradeOrderRepository, times(1)).save(sellOrder);
    }

    @Test
    void testPlaceOrder_BuyOrder_InsufficientFunds() {
        // Arrange
        buyerFiatWallet.setBalance(new BigDecimal("1000")); // Not enough funds
        TradeOrder buyOrder = new TradeOrder();
        buyOrder.setUser(buyer);
        buyOrder.setType(OrderType.BUY);
        buyOrder.setPrice(new BigDecimal("5000"));
        buyOrder.setAmount(new BigDecimal("1"));
        buyOrder.setCryptocurrency(crypto);

        when(fiatWalletService.getFIATWallet(buyer)).thenReturn(buyerFiatWallet);

        // Act
        Optional<TradeOrder> result = tradeOrderService.placeOrder(buyOrder);

        // Assert
        assertFalse(result.isPresent());
        verify(tradeOrderRepository, never()).save(any(TradeOrder.class));
    }

    @Test
    void testPlaceOrder_SellOrder_InsufficientCrypto() {
        // Arrange
        sellerCryptoBalance.setBalance(new BigDecimal("0.5")); // Not enough crypto
        TradeOrder sellOrder = new TradeOrder();
        sellOrder.setUser(seller);
        sellOrder.setType(OrderType.SELL);
        sellOrder.setPrice(new BigDecimal("5000"));
        sellOrder.setAmount(new BigDecimal("1"));
        sellOrder.setCryptocurrency(crypto);

        when(cryptoWalletService.getBalance(seller, crypto.getSymbol())).thenReturn(sellerCryptoBalance);

        // Act
        Optional<TradeOrder> result = tradeOrderService.placeOrder(sellOrder);

        // Assert
        assertFalse(result.isPresent());
        verify(tradeOrderRepository, never()).save(any(TradeOrder.class));
    }

    @Test
    void testGetOpenTradeOrders() {
        // Arrange
        TradeOrder order1 = new TradeOrder();
        order1.setId(1L);
        order1.setStatus(OrderStatus.OPEN);
        order1.setCryptocurrency(crypto);

        TradeOrder order2 = new TradeOrder();
        order2.setId(2L);
        order2.setStatus(OrderStatus.OPEN);
        order2.setCryptocurrency(crypto);

        List<TradeOrder> openOrders = Arrays.asList(order1, order2);

        when(cryptocurrencyRepository.findBySymbol(CryptoSymbol.BTC)).thenReturn(Optional.of(crypto));
        when(tradeOrderRepository.findByStatusAndCryptocurrency(OrderStatus.OPEN, crypto)).thenReturn(openOrders);

        // Act
        List<TradeOrder> result = tradeOrderService.getOpenTradeOrders(CryptoSymbol.BTC);

        // Assert
        assertEquals(2, result.size());
        verify(tradeOrderRepository, times(1)).findByStatusAndCryptocurrency(OrderStatus.OPEN, crypto);
    }

    @Test
    void testPlaceOrder_MatchOrders_ExecuteTrade() {
        // Arrange
        TradeOrder buyOrder = new TradeOrder();
        buyOrder.setId(1L);
        buyOrder.setUser(buyer);
        buyOrder.setType(OrderType.BUY);
        buyOrder.setPrice(new BigDecimal("5000"));
        buyOrder.setAmount(new BigDecimal("1"));
        buyOrder.setCryptocurrency(crypto);
        buyOrder.setStatus(OrderStatus.OPEN);

        TradeOrder sellOrder = new TradeOrder();
        sellOrder.setId(2L);
        sellOrder.setUser(seller);
        sellOrder.setType(OrderType.SELL);
        sellOrder.setPrice(new BigDecimal("5000"));
        sellOrder.setAmount(new BigDecimal("1"));
        sellOrder.setCryptocurrency(crypto);
        sellOrder.setStatus(OrderStatus.OPEN);

        when(fiatWalletService.getFIATWallet(buyer)).thenReturn(buyerFiatWallet);

        when(tradeOrderRepository.save(any(TradeOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tradeOrderRepository.findByStatusAndCryptocurrency(OrderStatus.OPEN, crypto)).thenReturn(Arrays.asList(buyOrder, sellOrder));
        when(tradeRepository.save(any(Trade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        tradeOrderService.placeOrder(buyOrder);

        // Assert
        assertEquals(OrderStatus.FILLED, buyOrder.getStatus());
        assertEquals(OrderStatus.FILLED, sellOrder.getStatus());
        verify(tradeRepository, times(1)).save(any(Trade.class));
        verify(fiatWalletService, times(1)).updateBalance(buyer, new BigDecimal("-5000"));
        verify(cryptoWalletService, times(1)).updateBalance(buyer, crypto.getSymbol(), new BigDecimal("1"));
        verify(fiatWalletService, times(1)).updateBalance(seller, new BigDecimal("5000"));
        verify(cryptoWalletService, times(1)).updateBalance(seller, crypto.getSymbol(), new BigDecimal("-1"));
    }

    @Test
    void testPlaceOrder_NoMatchingOrders() {
        // Arrange
        TradeOrder buyOrder = new TradeOrder();
        buyOrder.setUser(buyer);
        buyOrder.setType(OrderType.BUY);
        buyOrder.setPrice(new BigDecimal("4000"));
        buyOrder.setAmount(new BigDecimal("1"));
        buyOrder.setCryptocurrency(crypto);
        buyOrder.setStatus(OrderStatus.OPEN);

        when(fiatWalletService.getFIATWallet(buyer)).thenReturn(buyerFiatWallet);
        when(tradeOrderRepository.save(any(TradeOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tradeOrderRepository.findByStatusAndCryptocurrency(OrderStatus.OPEN, crypto)).thenReturn(Collections.singletonList(buyOrder));

        // Act
        tradeOrderService.placeOrder(buyOrder);

        // Assert
        assertEquals(OrderStatus.OPEN, buyOrder.getStatus());
        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void testMatchOrders_NoOrdersToMatch() {
        // Arrange
        when(tradeOrderRepository.findByStatusAndCryptocurrency(OrderStatus.OPEN, crypto)).thenReturn(Collections.emptyList());

        // Act
        tradeOrderService.matchOrders(crypto, buyer);

        // Assert
        verify(tradeOrderRepository, times(2)).findByStatusAndCryptocurrency(OrderStatus.OPEN, crypto);
        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void testGetOpenTradeOrders_CryptoNotFound() {
        // Arrange
        when(cryptocurrencyRepository.findBySymbol(CryptoSymbol.BTC)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tradeOrderService.getOpenTradeOrders(CryptoSymbol.BTC);
        });
        assertEquals("Cryptocurrency not found", exception.getMessage());
    }
}
