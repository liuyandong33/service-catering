package build.dream.catering.auth;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CustomClientDetailsService implements ClientDetailsService {
    public static final int ACCESS_TOKEN_VALIDITY_SECONDS = 7200;
    public static final int REFRESH_TOKEN_VALIDITY_SECONDS = 7200;
    private static final String GRANT_TYPES = "password,refresh_token";
    private static final String ALL = "all";

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        BaseClientDetails baseClientDetails = new BaseClientDetails();
        baseClientDetails.setClientId(clientId);
        baseClientDetails.setClientSecret("{MD5}" + DigestUtils.md5Hex("123456"));
        baseClientDetails.setScope(StringUtils.commaDelimitedListToSet(ALL));
        baseClientDetails.setAuthorizedGrantTypes(StringUtils.commaDelimitedListToSet(GRANT_TYPES));
        baseClientDetails.setAccessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS);
        baseClientDetails.setRefreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS);
        return baseClientDetails;
    }
}
