package mnykolaichuk.prz.pracaDyplomowa.model.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "customer_detail")
public class CustomerDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "account_verified")
    private boolean accountVerified;

    @OneToOne(mappedBy = "customerDetail",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Customer customer;

    @OneToMany(mappedBy = "customerDetail",
        fetch = FetchType.EAGER,
        cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<SecureToken> tokens;


    @OneToMany(mappedBy = "customerDetail",
            fetch = FetchType.EAGER,
            cascade = CascadeType.PERSIST)
    private List<Order> orders;

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isAccountVerified() {
        return accountVerified;
    }

    public void setAccountVerified(boolean accountVerified) {
        this.accountVerified = accountVerified;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Set<SecureToken> getTokens() {
        return tokens;
    }

    public void setTokens(Set<SecureToken> tokens) {
        this.tokens = tokens;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

}
