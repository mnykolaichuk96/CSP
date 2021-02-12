package mnykolaichuk.prz.pracaDyplomowa.model.entity;

import mnykolaichuk.prz.pracaDyplomowa.model.enums.AuthorityEnum;

import javax.persistence.*;

@Entity
@Table(name = "authority")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "authority")
    private AuthorityEnum authority;

    public Authority() { }

    public Integer getId() {
        return id;
    }

    public AuthorityEnum getAuthority() {
        return authority;
    }

    public void setAuthority(AuthorityEnum authority) {
        this.authority = authority;
    }
}
