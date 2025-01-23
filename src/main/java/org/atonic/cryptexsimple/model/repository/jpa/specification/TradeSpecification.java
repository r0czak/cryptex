package org.atonic.cryptexsimple.model.repository.jpa.specification;

import org.atonic.cryptexsimple.model.entity.jpa.Cryptocurrency;
import org.atonic.cryptexsimple.model.entity.jpa.FIATCurrency;
import org.atonic.cryptexsimple.model.entity.jpa.Trade;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.atonic.cryptexsimple.model.enums.OrderType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TradeSpecification {
    private TradeSpecification() {
    }

    public static Specification<Trade> defaultSpecification(User user, LocalDateTime from, LocalDateTime to) {
        return ((root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.and(
                criteriaBuilder.between(root.get("timestamp"), from, to),
                criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("seller"), user),
                    criteriaBuilder.equal(root.get("buyer"), user)
                )
            );
        });
    }

    public static Specification<Trade> whereCryptocurrency(Cryptocurrency cryptocurrency) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("cryptocurrency"), cryptocurrency);
    }

    public static Specification<Trade> whereFiatCurrency(FIATCurrency fiatCurrency) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("fiatCurrency"), fiatCurrency);
    }

    public static Specification<Trade> whereOrderType(User user, OrderType orderType) {
        return ((root, query, criteriaBuilder) -> {
            if (orderType == OrderType.BUY) {
                return criteriaBuilder.equal(root.get("buyer"), user);
            } else {
                return criteriaBuilder.equal(root.get("seller"), user);
            }
        });
    }
}
