package com.canteenq.repository;

import com.canteenq.model.FoodOrder;
import com.canteenq.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {

    List<FoodOrder> findByMemberIdOrderByPlacedAtDesc(Long memberId);

    List<FoodOrder> findByOrderDateBetween(LocalDate from, LocalDate to);

    List<FoodOrder> findByStatusOrderByToken(OrderStatus status);
}
