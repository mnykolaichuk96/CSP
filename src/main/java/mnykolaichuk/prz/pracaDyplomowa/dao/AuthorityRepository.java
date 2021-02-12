package mnykolaichuk.prz.pracaDyplomowa.dao;

import mnykolaichuk.prz.pracaDyplomowa.model.entity.Authority;
import mnykolaichuk.prz.pracaDyplomowa.model.enums.AuthorityEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {

    Authority findByAuthority(AuthorityEnum authorityEnum);
}
