package py.org.fundacionparaguaya.pspserver.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import py.org.fundacionparaguaya.pspserver.network.dtos.ApplicationDTO;
import py.org.fundacionparaguaya.pspserver.network.dtos.OrganizationDTO;
import py.org.fundacionparaguaya.pspserver.security.dtos.UserDetailsDTO;
import py.org.fundacionparaguaya.pspserver.security.dtos.UserDetailsDTOBuilder;
import py.org.fundacionparaguaya.pspserver.surveys.dtos.NewSnapshot;
import py.org.fundacionparaguaya.pspserver.surveys.dtos.NewSurveyDefinition;
import py.org.fundacionparaguaya.pspserver.surveys.dtos.Snapshot;
import py.org.fundacionparaguaya.pspserver.surveys.dtos.SurveyDefinition;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rodrigovillalba on 8/28/17.
 */
public class TestHelper {

    public static final Long SURVEY_ID = 1L;
    public static final String SURVEY_DEFAULTS = "/survey_defaults.json";
    public static final Long USER_ID = 3L;
    public static final Long TERM_COND_ID = 4L;
    public static final Long PRIV_POL_ID = 5L;
    public static final String SNAPSHOT_JSON_FILE = "/snapshot.json";
    private final static ObjectMapper mapper = new ObjectMapper();
    private static final Long SNAPSHOT_ID = 2L;

    public static String mapToJson(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    public static Object mapToObject(String contentAsString, Class<?> clazz) throws JsonParseException, JsonMappingException, IOException {
        return mapper.readValue(contentAsString, clazz);
    }

    public static Object mapToObjectFromFile(String fileName, Class<?> clazz) {
        try {
            InputStream is = TestHelper.class.getResourceAsStream(fileName);
            return mapper.readValue(is, clazz);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static SurveyDefinition getDefinition() {
        SurveyDefinition def = (SurveyDefinition) mapToObjectFromFile(SURVEY_DEFAULTS, SurveyDefinition.class);

        return new SurveyDefinition().id(SURVEY_ID).title(def.getTitle())
                .description(def.getDescription())
                .surveySchema(def.getSurveySchema())
                .surveyUISchema(def.getSurveyUISchema())
                .organizations(def.getOrganizations())
                .applications(def.getApplications());
    }

    public static String getSurveyDefinitionAsJsonString() throws JsonProcessingException {
        SurveyDefinition definition = getDefinition();
        return mapToJson(definition);
    }

    public static Snapshot getSnapshotWithEconomicId(Long surveyId) {
        Snapshot snapshot = (Snapshot) mapToObjectFromFile(SNAPSHOT_JSON_FILE, Snapshot.class);
        return new Snapshot()
                .surveyId(surveyId)
                .snapshotEconomicId(SNAPSHOT_ID)
                .userId(USER_ID)
                .user(snapshot.getUser())
                .family(snapshot.getFamily())
                .termCondId(TERM_COND_ID)
                .privPolId(PRIV_POL_ID)
                .personalSurveyData(snapshot.getPersonalSurveyData())
                .economicSurveyData(snapshot.getEconomicSurveyData())
                .indicatorSurveyData(snapshot.getIndicatorSurveyData())
                .snapshotIndicatorId(snapshot.getSnapshotIndicatorId());
    }

    public static NewSnapshot getNewSnapshot(Long surveyId) {
        NewSnapshot snapshot = (NewSnapshot) mapToObjectFromFile(SNAPSHOT_JSON_FILE, NewSnapshot.class);
        snapshot.setSurveyId(surveyId);
        return snapshot;
    }

    public static String getNewSnapshotAsJsonString(Long id) throws JsonProcessingException {
        NewSnapshot newSnapshot = getNewSnapshot(id);
        return mapToJson(newSnapshot);
    }

    public static NewSurveyDefinition getNewDefinition() {
        NewSurveyDefinition def = (NewSurveyDefinition) mapToObjectFromFile(SURVEY_DEFAULTS, NewSurveyDefinition.class);
        return def;
    }

    public static UserDetailsDTO getUser() {
        return new UserDetailsDTOBuilder().username("admin").build();
    }

    public static UserDetailsDTO getHubAdminUser(OrganizationDTO org) {
        return new UserDetailsDTOBuilder().username("hub_admin").organization(org).build();
    }

    public static ApplicationDTO newApp() {
        ApplicationDTO app = new ApplicationDTO();
        app.setName("app_it");
        app.setCode("code");
        app.setDescription("description");
        app.setActive(true);
        return app;
    }

    public static OrganizationDTO newOrg(ApplicationDTO app) {
        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setName("it_org");
        organizationDTO.setCode("code");
        organizationDTO.setDescription("desc");
        organizationDTO.setActive(true);
        organizationDTO.setApplication(app);
        return organizationDTO;
    }
}
