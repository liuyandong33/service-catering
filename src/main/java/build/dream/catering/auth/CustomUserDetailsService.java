package build.dream.catering.auth;

import build.dream.common.auth.SystemUserUserDetails;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUserUserDetails systemUserUserDetails = new SystemUserUserDetails();
        systemUserUserDetails.setUsername(username);
        systemUserUserDetails.setPassword("{MD5}" + DigestUtils.md5Hex("123456"));
        return systemUserUserDetails;
    }
}
