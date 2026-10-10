package com.parking.dto;

import com.parking.entity.BookingEntity;
import com.parking.entity.BookingStatus;

import java.time.LocalDateTime;

public class BookingResponse {
    private Long id;
    private Long slotId;
    private String slotNumber;
    private Integer floor;
    private Long userId;
    private String userName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String vehicleNumber;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private String qrCode;

    public static BookingResponse from(BookingEntity b) {
        BookingResponse r = new BookingResponse();
        r.id            = b.getId();
        r.slotId        = b.getSlot().getId();
        r.slotNumber    = b.getSlot().getSlotNumber();
        r.floor         = b.getSlot().getFloor();
        r.userId        = b.getUser().getId();
        r.userName      = b.getUser().getName();
        r.startTime     = b.getStartTime();
        r.endTime       = b.getEndTime();
        r.vehicleNumber = b.getVehicleNumber();
        r.status        = b.getStatus();
        r.createdAt     = b.getCreatedAt();
        r.qrCode        = b.getQrCode();
        return r;
    }

    public Long getId()              { return id; }
    public Long getSlotId()          { return slotId; }
    public String getSlotNumber()    { return slotNumber; }
    public Integer getFloor()        { return floor; }
    public Long getUserId()          { return userId; }
    public String getUserName()      { return userName; }
    public LocalDateTime getStartTime()  { return startTime; }
    public LocalDateTime getEndTime()    { return endTime; }
    public String getVehicleNumber() { return vehicleNumber; }
    public BookingStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public String getQrCode()        { return qrCode; }
}
