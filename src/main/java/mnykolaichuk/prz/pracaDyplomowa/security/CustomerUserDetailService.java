package mnykolaichuk.prz.pracaDyplomowa.security;

import mnykolaichuk.prz.pracaDyplomowa.dao.CustomerRepository;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Authority;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

//поки що я розумію що це метод для того щоб спрінг розумів як логувати юзера, що є його юзернеймом
@Service("customerUserDetailService")
public class CustomerUserDetailService implements UserDetailsService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Customer customer = customerRepository.findCustomerByUsername(username);
        if(customer == null) {
            throw new UsernameNotFoundException(username);
        }
        boolean enabled = !customer.getCustomerDetail().isAccountVerified();
        UserDetails user = User.withUsername(customer.getUsername())
                .password(customer.getPassword())
                .disabled(enabled)
                .authorities(mapRolesToAuthorities(customer.getAuthorities())).build();

        return user;
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Authority> authorities) {
        return authorities.stream().map(authority -> new SimpleGrantedAuthority(authority.getAuthority().toString())).collect(Collectors.toList());
    }
}
