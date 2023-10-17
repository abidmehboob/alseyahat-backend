package com.alseyahat.app.feature.hotel.repository.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import com.alseyahat.app.feature.review.repository.entity.Review;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Getter
@Setter
@Entity
@Table(name = "hotel")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Hotel implements Serializable {

    static final long serialVersionUID = -797991492884005122L;

    @Id
    @Column(name = "hotel_id", nullable = false)
    String hotelId = UUID.randomUUID().toString().replace("-", "");
    
    @Column(nullable = false)
    String name;

    @Column(length = 1000)
    String description;

    @Column
    String email;

    @Column
    String phone;

    @Column
    String registerFrom;

    @Column(length = 1000)
    String images;
    
    @Column
    String accountNumber;

    @Column
    boolean isEnabled = true;
    
    @Column
    @Lob
    String termAndCondition;
    
    @Column
    boolean isTermCondition = true;
    
    @Column
    String addressLine;
    
    @Column
    String businessType;
    
    @Column
    String city;
    
    @Column
    String district;

    @Column
    String town;

    @Column
    String postcode;

    @Column
    Double latitude;

    @Column
    Double longitude;
    
    @Column
    Double itemAverageRating=3.0;
    
    @Column
    boolean hotHotel =  Boolean.FALSE;

    @Column
    boolean newArrival =  Boolean.FALSE;

    @Column
    boolean advertised = Boolean.FALSE;
    
    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = Review.class)
    List<Review> review;
    
    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = HotelFacility.class)
    List<HotelFacility> hotelFacility;
    
    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = HotelBooking.class)
    List<HotelBooking> hotelBooking;
    
    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = RoomType.class)
    List<RoomType> roomType;

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
