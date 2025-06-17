package org.example.online_ticketing_system.controller;

import java.util.List;

import org.example.online_ticketing_system.model.TicketType;
import org.example.online_ticketing_system.repository.TicketTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ticket-types")
public class TicketTypeController {

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @GetMapping
    public List<TicketType> getAllTicketTypes() {
        return ticketTypeRepository.findAll();
    }

    @PostMapping
    public String addTicketType(@RequestParam String name, @RequestParam int price) {
        if (ticketTypeRepository.existsById(name)) {
            return "票种已存在";
        }
        ticketTypeRepository.save(new TicketType(name, price));
        return "票种添加成功";
    }

    @DeleteMapping
    public String deleteTicketType(@RequestParam String name) {
        if (!ticketTypeRepository.existsById(name)) {
            return "票种不存在";
        }
        ticketTypeRepository.deleteById(name);
        return "票种删除成功";
    }

    @PutMapping
    public String updateTicketType(@RequestParam String oldName, 
                                 @RequestParam(required = false) String newName,
                                 @RequestParam(required = false) Integer price) {
        if (!ticketTypeRepository.existsById(oldName)) {
            return "票种不存在";
        }
        
        TicketType ticketType = ticketTypeRepository.findById(oldName).get();
        
        if (newName != null && !newName.isEmpty()) {
            // 如果要修改名称，需要检查新名称是否已存在
            if (!newName.equals(oldName) && ticketTypeRepository.existsById(newName)) {
                return "新票种名称已存在";
            }
            ticketType.setName(newName);
        }
        
        if (price != null) {
            ticketType.setPrice(price);
        }
        
        ticketTypeRepository.save(ticketType);
        
        // 如果修改了名称，需要删除旧记录
        if (newName != null && !newName.equals(oldName)) {
            ticketTypeRepository.deleteById(oldName);
        }
        
        return "票种修改成功";
    }
}
