package org.example.online_ticketing_system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ticket_inventory")
public class TicketInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 日期，格式为 yyyy-MM-dd
    private String date;

    // 时间段，如“上午”或“下午”
    private String time;

    // 总票数
    private int totalTickets;

    // 已售票数
    private int soldTickets;

    // 状态：1 = 可预订，0 = 不可预订
    private int status;

    // 表示一天的状态（如：0 = 有票，2 = 售完）
    private int dayStatus;

    // ====== Getters and Setters ======

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public int getSoldTickets() {
        return soldTickets;
    }

    public void setSoldTickets(int soldTickets) {
        this.soldTickets = soldTickets;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDayStatus() {
        return dayStatus;
    }

    public void setDayStatus(int dayStatus) {
        this.dayStatus = dayStatus;
    }
}
