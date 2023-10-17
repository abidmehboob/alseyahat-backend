package com.alseyahat.app.feature.hotel.repository.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Getter
@Setter
@Entity
@Table(name = "orderedMenu")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderedMenu implements Serializable {

	static final long serialVersionUID = -798991492884005022L;

	@Id
	@Column(name = "ordered_id", nullable = false)
	String orderedId = UUID.randomUUID().toString().replace("-", "");

	@Column
	String menuId;
	
	@Column(name = "hotel_booking_id")
	String hotelBookingId;
	
		  
	@Column(name = "item_count")
	Integer itemCount;
	
	@Column
	boolean isEnabled = true;
	
	@Column
	boolean isCanceled = false;

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
