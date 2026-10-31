package com.canteenq.service;

import com.canteenq.dto.MemberRequest;
import com.canteenq.exception.NotFoundException;
import com.canteenq.model.Member;
import com.canteenq.pattern.MemberFactory;
import com.canteenq.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository repo;
    private final MenuService menuService;

    public MemberService(MemberRepository repo, MenuService menuService) {
        this.repo = repo;
        this.menuService = menuService;
    }

    public List<Member> all() {
        return repo.findAll();
    }

    public Member find(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Member id is required");
        }
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Member", id));
    }

    @Transactional
    public Member register(MemberRequest request) {
        return repo.save(MemberFactory.create(request));
    }

    @Transactional
    public Member recharge(Long id, double amount) {
        Member member = find(id);
        member.deposit(amount);
        return member;
    }

    @Transactional
    public Member addFavourite(Long memberId, Long itemId) {
        Member member = find(memberId);
        member.getFavourites().add(menuService.find(itemId));
        return member;
    }
}
