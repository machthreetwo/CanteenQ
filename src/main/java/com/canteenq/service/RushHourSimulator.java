package com.canteenq.service;

import com.canteenq.dto.RushReport;
import com.canteenq.model.Member;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RushHourSimulator {

    private final MemberService members;

    public RushHourSimulator(MemberService members) {
        this.members = members;
    }

    public RushReport run(String dish, int plates) {
        if (plates < 1 || plates > 50) {
            throw new IllegalArgumentException("Plates should be between 1 and 50");
        }
        List<Member> crowd = members.all();
        PlateCounter counter = new PlateCounter(plates);
        List<String> log = Collections.synchronizedList(new ArrayList<>());
        List<String> missed = Collections.synchronizedList(new ArrayList<>());
        List<Thread> threads = new ArrayList<>();

        for (Member m : crowd) {
            Thread t = new Thread(() -> {
                log.add(Thread.currentThread().getName() + " is " + Thread.currentThread().getState() + " towards the counter");
                if (counter.grab(m.getName(), m.getPriority())) {
                    log.add(m.getName() + " got a plate of " + dish);
                } else {
                    missed.add(m.getName());
                    log.add(m.getName() + " reached late, " + dish + " finished");
                }
            }, m.getName());
            t.setPriority(m.getPriority());
            counter.standInLine(m.getPriority());
            threads.add(t);
            log.add(t.getName() + " (" + m.getRole() + ", priority " + t.getPriority() + ") is " + t.getState());
        }

        threads.forEach(Thread::start);
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        threads.forEach(t -> log.add(t.getName() + " is " + t.getState()));

        return new RushReport(dish, plates, counter.servedTo(), new ArrayList<>(missed), new ArrayList<>(log));
    }
}
