package py.org.fundacionparaguaya.pspserver.odkclient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opendatakit.api.offices.entity.RegionalOffice;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.web.client.RestTemplate;

public class OdkClientUtils {

    private static Log logger = LogFactory.getLog(OdkClientUtils.class);

    public static RestTemplate getRestTemplate(Authentication authentication) {
        RestTemplate restTemplate = null;
        try {
            Map<String, Object> userDetails = (Map<String, Object>) authentication.getDetails();

            if (userDetails == null || userDetails.get(GeneralConsts.ODK_REST_CLIENT) == null) {
                // TODO: Get reauthenticated. Easy workaround may be to force logout for now.
                SecurityUtils.logout();
                throw new PreAuthenticatedCredentialsNotFoundException("Cannot find.  Please logout and log in again.");

            } else {
                restTemplate = (RestTemplate) userDetails.get(GeneralConsts.ODK_REST_CLIENT);
            }
        } catch (ClassCastException e) {
            logger.debug("Unable to get REST template for this user.");
        }
        return restTemplate;
    }

    public static RestTemplate getRestTemplate() {
        return getRestTemplate(SecurityContextHolder.getContext().getAuthentication());
    }

    public static RestTemplate getRestTemplate(SecurityContext context) {
        return getRestTemplate(context.getAuthentication());
    }


    public static Map<String, RegionalOffice> getOfficeMap(List<RegionalOffice> offices) {
        Map<String, RegionalOffice> result = new HashMap<String, RegionalOffice>();
        for (RegionalOffice office : offices) {
            result.put(office.getOfficeId(), office);
        }
        return result;
    }

}
