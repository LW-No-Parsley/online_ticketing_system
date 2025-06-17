package org.example.online_ticketing_system.controller;

import java.util.HashMap;
import java.util.Map;

import org.example.online_ticketing_system.model.Ticket;
import org.example.online_ticketing_system.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @PostMapping("/purchase")
    public Map<String, Object> purchaseTicket(@RequestBody Map<String, String> ticketData) {
        String date = ticketData.get("date");
        String time = ticketData.get("time");

        Map<String, Object> result = new HashMap<>();

        // 模拟票务管理逻辑
        boolean isAvailable = checkAvailability(date, time);

        if (isAvailable) {
            result.put("success", true);
            result.put("message", "购票成功");
        } else {
            result.put("success", false);
            result.put("message", "所选日期和时间的票已售罄");
        }

        return result;
    }

    private boolean checkAvailability(String date, String time) {
        // 查询数据库获取指定日期和时间的票务信息
        return ticketRepository.findByDateAndTime(date, time)
                .map(ticket -> ticket.getSoldTickets() < 500) // 假设总票数为100
                .orElse(true); // 如果没有记录，表示还有余票
    }

    private int getSoldTickets(String date, String time) {
        // 查询数据库获取已售票数
        return ticketRepository.findByDateAndTime(date, time)
                .map(Ticket::getSoldTickets)
                .orElse(0); // 如果没有记录，返回0
    }
}
