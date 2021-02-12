package mnykolaichuk.prz.pracaDyplomowa.dao;

import mnykolaichuk.prz.pracaDyplomowa.model.entity.CustomerDetail;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.SecureToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SecureTokenRepository extends JpaRepository<SecureToken, Integer> {

    @Transactional
    @Modifying
    @Query("delete from SecureToken st where st.id=:id")
    void deleteSecureTokenById(Integer id);

    SecureToken findByToken(final String token);
    SecureToken findByCustomerDetail(CustomerDetail customerDetail);
}
