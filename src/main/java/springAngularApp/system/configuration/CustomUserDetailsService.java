package springAngularApp.system.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springAngularApp.system.domain.model.SystemUser;
import springAngularApp.users.domain.entities.User;
import springAngularApp.users.domain.entities.UserAuthority;
import springAngularApp.users.domain.repositories.UserAuthorityRepository;
import springAngularApp.users.domain.repositories.UserRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired private UserRepository userRepository;
    @Autowired private UserAuthorityRepository userAuthorityRepository;

    @Override
    @Transactional
    public SystemUser loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username);
        if (user == null) {
            return null;
        }
        List<UserAuthority> authorities = getAuthorities(user);
        List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
                .map(this::mapToSimpleGrantedAuthority)
                .collect(toList());
        return new SystemUser(user.getName(), user.getPassword(), grantedAuthorities, user.getFirstName(), user.getLastName());
    }

    private List<UserAuthority> getAuthorities(User user) {
        if (user.getGroup().isSuperUserGroup()) {
            return userAuthorityRepository.findAll();
        }
        return user.getGroup().getAuthorities();
    }

    private SimpleGrantedAuthority mapToSimpleGrantedAuthority(UserAuthority userAuthority) {
        return new SimpleGrantedAuthority(userAuthority.getName());
    }

}
