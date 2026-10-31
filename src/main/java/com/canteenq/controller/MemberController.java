package com.canteenq.controller;

import com.canteenq.dto.MemberRequest;
import com.canteenq.dto.Receipt;
import com.canteenq.dto.RechargeRequest;
import com.canteenq.model.Member;
import com.canteenq.service.MemberService;
import com.canteenq.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final OrderService orderService;

    public MemberController(MemberService memberService, OrderService orderService) {
        this.memberService = memberService;
        this.orderService = orderService;
    }

    @GetMapping
    public List<Member> list() {
        return memberService.all();
    }

    @GetMapping("/{id}")
    public Member get(@PathVariable Long id) {
        return memberService.find(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Member register(@RequestBody MemberRequest request) {
        return memberService.register(request);
    }

    @PostMapping("/{id}/recharge")
    public Member recharge(@PathVariable Long id, @RequestBody RechargeRequest request) {
        return memberService.recharge(id, request.amount());
    }

    @PostMapping("/{id}/favourites/{itemId}")
    public Member favourite(@PathVariable Long id, @PathVariable Long itemId) {
        return memberService.addFavourite(id, itemId);
    }

    @GetMapping("/{id}/orders")
    public List<Receipt> orders(@PathVariable Long id) {
        return orderService.historyOf(id);
    }
}
