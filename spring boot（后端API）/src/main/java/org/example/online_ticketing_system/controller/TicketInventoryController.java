package org.example.online_ticketing_system.controller;

import java.util.List;
import java.util.Optional;

import org.example.online_ticketing_system.config.RequiresAuth;
import org.example.online_ticketing_system.model.TicketInventory;
import org.example.online_ticketing_system.repository.TicketInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 票务库存管理控制器
 * 提供对票务库存的CRUD操作接口
 * 需要景区管理员权限(scenic)才能访问
 */
@RestController
@RequestMapping("/api/ticket-inventory")
public class TicketInventoryController {
    @Autowired
    private TicketInventoryRepository ticketInventoryRepository;

    /**
     * 获取所有票库存信息
     * @return 包含所有票库存的列表
     */
    @GetMapping("")
    @RequiresAuth(roles = {"scenic"})
    public List<TicketInventory> getAllTicketInventory() {
        return ticketInventoryRepository.findAll();
    }

    // 获取特定票库存
    /**
     * 根据ID获取特定票库存
     * @param id 票库存ID
     * @return 包含票库存的ResponseEntity，如果不存在则返回404
     */
    @GetMapping("/{id}")
    @RequiresAuth(roles = {"scenic"})
    public ResponseEntity<TicketInventory> getTicketInventoryById(@PathVariable Long id) {
        Optional<TicketInventory> ticketInventory = ticketInventoryRepository.findById(id);
        return ticketInventory.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 创建新的票库存
     * @param ticketInventory 要创建的票库存对象
     * @return 创建成功的票库存对象
     */
    @PostMapping("")
    @RequiresAuth(roles = {"scenic"})
    public TicketInventory createTicketInventory(@RequestBody TicketInventory ticketInventory) {
        return ticketInventoryRepository.save(ticketInventory);
    }

    /**
     * 更新票库存信息
     * @param id 要更新的票库存ID
     * @param ticketInventoryDetails 包含更新信息的票库存对象
     * @return 更新后的票库存ResponseEntity，如果不存在则返回404
     */
    @PutMapping("/{id}")
    @RequiresAuth(roles = {"scenic"})
    public ResponseEntity<TicketInventory> updateTicketInventory(@PathVariable Long id, @RequestBody TicketInventory ticketInventoryDetails) {
        Optional<TicketInventory> ticketInventory = ticketInventoryRepository.findById(id);
        if (ticketInventory.isPresent()) {
            TicketInventory updatedTicketInventory = ticketInventory.get();
            updatedTicketInventory.setDate(ticketInventoryDetails.getDate());
            updatedTicketInventory.setTime(ticketInventoryDetails.getTime());
            updatedTicketInventory.setTotalTickets(ticketInventoryDetails.getTotalTickets());
            updatedTicketInventory.setSoldTickets(ticketInventoryDetails.getSoldTickets());
            updatedTicketInventory.setStatus(ticketInventoryDetails.getStatus());
            updatedTicketInventory.setDayStatus(ticketInventoryDetails.getDayStatus());
            return ResponseEntity.ok(ticketInventoryRepository.save(updatedTicketInventory));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 删除票库存
     * @param id 要删除的票库存ID
     * @return 成功返回200，如果不存在则返回404
     */
    @DeleteMapping("/{id}")
    @RequiresAuth(roles = {"scenic"})
    public ResponseEntity<?> deleteTicketInventory(@PathVariable Long id) {
        Optional<TicketInventory> ticketInventory = ticketInventoryRepository.findById(id);
        if (ticketInventory.isPresent()) {
            ticketInventoryRepository.delete(ticketInventory.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
