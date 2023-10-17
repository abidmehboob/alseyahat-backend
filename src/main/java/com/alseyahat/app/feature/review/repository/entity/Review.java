package com.alseyahat.app.feature.review.repository.entity;

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

import com.alseyahat.app.feature.customer.repository.entity.Customer;
import com.alseyahat.app.feature.deal.repository.entity.Deal;
import com.alseyahat.app.feature.hotel.repository.entity.Hotel;
import com.alseyahat.app.feature.sightSeeing.repository.entity.SightSeeing;
import com.alseyahat.app.feature.vehicle.hired.repository.entity.PrivateHired;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Table(name = "review")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review implements Serializable  {
	
	   static final long serialVersionUID = -798991492884005022L;
	    @Id
	    String reviewRatingId= UUID.randomUUID().toString().replace("-", "");


	    @ManyToOne
	    @JoinColumn(name = "customer_id")
	    Customer customer;
	    
	    @ManyToOne
	    @JoinColumn(name = "hotel_id")
	    Hotel hotel;

	    @ManyToOne
	    @JoinColumn(name = "sight_seeing_id")
	    SightSeeing sightSeeing;

	    @ManyToOne
	    @JoinColumn(name = "deal_id")
	    Deal deal;
	    
	    @ManyToOne
	    @JoinColumn(name = "private_hired_id")
	    PrivateHired privateHired;

	    @Column(nullable = false)
	    String reviewFor;

	    @Column(nullable = false)
	    Integer rating;

	    @Column
	    String review;

	    @Column
	    boolean isEnabled = Boolean.TRUE;

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
