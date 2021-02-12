package mnykolaichuk.prz.pracaDyplomowa.model.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "secure_token")
public class SecureToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "token", unique = true)
    private String token;

    @Column(name = "verification_car_id")
    private Integer verificationCarId;

    @Column(name = "verification_car_customer_id")
    private Integer verificationCarCustomerId;

    @CreationTimestamp
    @Column(name = "time_stamp")
    private Timestamp timeStamp;

    @Column(name = "expire_at")
    private LocalDateTime expireAt;

    @Column(name = "not_new_customer_detail")
    private Boolean notNewCustomerDetail;

   @ManyToOne
   @JoinColumn(name = "customer_detail_id")
   private CustomerDetail customerDetail;

    @ManyToOne
    @JoinColumn(name = "workshop_id")
    private Workshop workshop;

   @Transient
   private boolean isExpired;

    public SecureToken() {
    }

    public Integer getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getVerificationCarId() {
        return verificationCarId;
    }

    public void setVerificationCarId(Integer verificationCarId) {
        this.verificationCarId = verificationCarId;
    }

    public Integer getVerificationCarCustomerId() {
        return verificationCarCustomerId;
    }

    public void setVerificationCarCustomerId(Integer verificationCarCustomerId) {
        this.verificationCarCustomerId = verificationCarCustomerId;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }


    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public Boolean isNotNewCustomerDetail() {
        return notNewCustomerDetail;
    }

    public void setNotNewCustomerDetail(Boolean newCustomerDetail) {
        this.notNewCustomerDetail = newCustomerDetail;
    }

    public CustomerDetail getCustomerDetail() {
        return customerDetail;
    }

    public void setCustomerDetail(CustomerDetail customerDetail) {
        this.customerDetail = customerDetail;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }
}
