package py.org.fundacionparaguaya.pspserver.config;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.benetech.security.client.digest.DigestRestTemplateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import py.org.fundacionparaguaya.pspserver.odkclient.GeneralConsts;
import py.org.fundacionparaguaya.pspserver.odkclient.OdkClient;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class WebServiceDelegatingAuthenticationProvider implements AuthenticationProvider {

    private static Logger LOG = LoggerFactory.getLogger(WebServiceDelegatingAuthenticationProvider.class);

    Properties webServicesProperties;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String odkUrlString = webServicesProperties.getProperty("odk.url");
        String odkRealmName = webServicesProperties.getProperty("odk.realm");
        Map<String, Object> userDetails = new HashMap<String, Object>();

        URL odkUrl = null;
        URI odkUri = null;
        if (odkUrlString == null) {
            throw new InternalAuthenticationServiceException(
                    "Host address is blank.  Did you configure the web service host?");
        }
        try {
            odkUrl = new URL(odkUrlString);
            odkUri = odkUrl.toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new InternalAuthenticationServiceException(
                    "Bad host syntax.  Did you configure the web service host?");
        }

        String username = (String) authentication.getPrincipal();
        String password = authentication.getCredentials().toString();

        RestTemplate restTemplate = DigestRestTemplateFactory.getRestTemplate(odkUri.getHost(),
                odkUri.getPort(), odkUri.getScheme(), odkRealmName, username, password);
        String getRolesGrantedUrl = odkUrl.toExternalForm() + OdkClient.ROLES_GRANTED_ENDPOINT;
        ResponseEntity<List<String>> getResponse = null;
        try {
            LOG.info("Logging in with " + getRolesGrantedUrl);

            getResponse = restTemplate.exchange(getRolesGrantedUrl, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<String>>() {});
        } catch (HttpClientErrorException e) {
            LOG.info("Received an exception when getting granted roles");
            LOG.info("Received " + e.getRawStatusCode());
            LOG.info("Received " + e.getResponseBodyAsString());
            if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                throw new BadCredentialsException("Unable to log in to remote web service.");
            }
            else {
                throw new AuthenticationServiceException(e.getMessage());
            }
        }


        userDetails.put(GeneralConsts.ODK_REST_CLIENT, restTemplate);

        // Cached credentials for file upload form / pre-emptive digest authentication
        UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(username,
                password);

        userDetails.put(GeneralConsts.PREEMPTIVE_CREDENTIALS, usernamePasswordCredentials);



        if (getResponse.getStatusCode().equals(HttpStatus.OK)) {
            Set<GrantedAuthority> authorized = new HashSet<GrantedAuthority>();
            for (String role : getResponse.getBody()) {
                authorized.add(new SimpleGrantedAuthority((String) role));
            }
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(username, password, authorized);
            token.setDetails(userDetails);
            return token;
        } else {
            LOG.info("Received a non-200 error code when getting granted roles: " + getResponse.getStatusCodeValue());
            LOG.info("Response {}", getResponse.getBody());
            // Add more error cases here, or research how it is handled by default.
            // "Bad Credentials" is only one potential cause.
            if (getResponse.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                throw new BadCredentialsException("Unable to log in to remote web service.");
            }
            else {
                throw new AuthenticationServiceException(getResponse.getStatusCode().getReasonPhrase());
            }
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    public Properties getWebServicesProperties() {
        return webServicesProperties;
    }

    public void setWebServicesProperties(Properties webServicesProperties) {
        this.webServicesProperties = webServicesProperties;
    }


}
