package com.canteenq.service;

import com.canteenq.dto.LineRequest;
import com.canteenq.dto.OrderRequest;
import com.canteenq.dto.Receipt;
import com.canteenq.exception.NotFoundException;
import com.canteenq.model.*;
import com.canteenq.pattern.OfferPolicy;
import com.canteenq.pattern.TokenMachine;
import com.canteenq.repository.FoodOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final FoodOrderRepository orders;
    private final MemberService members;
    private final MenuService menu;
    private final OfferService offers;
    private final KitchenService kitchen;
    private final TokenMachine tokenMachine;

    public OrderService(FoodOrderRepository orders, MemberService members, MenuService menu,
                        OfferService offers, KitchenService kitchen, TokenMachine tokenMachine) {
        this.orders = orders;
        this.members = members;
        this.menu = menu;
        this.offers = offers;
        this.kitchen = kitchen;
        this.tokenMachine = tokenMachine;
    }

    @Transactional
    public Receipt place(OrderRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Add at least one dish to the order");
        }
        Member member = members.find(request.memberId());
        OfferPolicy offer = offers.pick(request.offer());

        FoodOrder order = new FoodOrder(member, offer.label());
        for (LineRequest line : request.items()) {
            MenuItem item = menu.find(line.itemId());
            item.takeFromStock(line.quantity());
            order.addLine(new OrderLine(item, line.quantity()));
        }
        order.applyDiscount(offer.discountFor(member, order.getSubtotal()));
        member.withdraw(order.getTotal());
        order.setToken(tokenMachine.next());

        FoodOrder saved = orders.save(order);
        kitchen.enqueue(saved.getId());
        return Receipt.from(saved);
    }

    public Receipt find(Long id) {
        return Receipt.from(load(id));
    }

    public List<Receipt> historyOf(Long memberId) {
        members.find(memberId);
        return orders.findByMemberIdOrderByPlacedAtDesc(memberId).stream()
                .map(Receipt::from)
                .toList();
    }

    @Transactional
    public Receipt collect(Long id) {
        FoodOrder order = load(id);
        if (order.getStatus() != OrderStatus.READY) {
            throw new IllegalStateException("Token " + order.getToken() + " is " + order.getStatus() + ", it is not ready to collect");
        }
        order.setStatus(OrderStatus.COLLECTED);
        return Receipt.from(order);
    }

    private FoodOrder load(Long id) {
        return orders.findById(id).orElseThrow(() -> new NotFoundException("Order", id));
    }
}
