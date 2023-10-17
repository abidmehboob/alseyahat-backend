package com.alseyahat.app.feature.driver.repository.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import com.alseyahat.app.feature.employee.repository.entity.Employee;
import com.alseyahat.app.feature.ride.repository.entity.Booking;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@Entity
@Table(name = "driverbooking")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverBooking implements Serializable  {
	
	   static final long serialVersionUID = -798991492884005022L;
	    @Id
	    @Column(name = "driver_booking_id")
	    String driverBookingId= UUID.randomUUID().toString().replace("-", "");

	    
	    @ManyToOne
	    @JoinColumn(name = "employee_id")
	    Employee employee;
	    
	    @ManyToOne
	    @JoinColumn(name = "booking_id")
	    Booking booking;
	    	
	    @Column
	    String bookingStatus;
	    
	    @Column
	    double totalRent;
	    
	    @Column
	    boolean isEnabled = true;

	    @Column
	    Date dateCreated;

	    @Column
	    Date lastUpdated;

	    @PrePersist
	    protected void prePersist() {
	        lastUpdated = new Date();
	        if (dateCreated == null) {
	            dateCreated = new Date();
	        }
	    }

}