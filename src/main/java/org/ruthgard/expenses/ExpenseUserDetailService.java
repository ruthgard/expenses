package org.ruthgard.expenses;

import org.ruthgard.expenses.model.User;
import org.ruthgard.expenses.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class ExpenseUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User u = userRepository.findByName(s).stream().findFirst().orElseThrow(() -> new UsernameNotFoundException("No such user: " + s));
        GrantedAuthority gaUser = new SimpleGrantedAuthority("USER");
        GrantedAuthority gaAdmin = new SimpleGrantedAuthority("ADMIN");
        Collection<GrantedAuthority> gac = new ArrayList<>();
        gac.add(gaUser);
        if (u.isAdmin())
            gac.add(gaAdmin);
        UserDetails ud = new org.springframework.security.core.userdetails.User(u.getName(), u.getPassword(), gac);
        return ud;
    }
}
