package py.org.fundacionparaguaya.pspserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import py.org.fundacionparaguaya.pspserver.families.dtos.FamilyDTO;
import py.org.fundacionparaguaya.pspserver.families.services.FamilyService;
import py.org.fundacionparaguaya.pspserver.network.dtos.ApplicationDTO;
import py.org.fundacionparaguaya.pspserver.network.dtos.OrganizationDTO;
import py.org.fundacionparaguaya.pspserver.network.services.ApplicationService;
import py.org.fundacionparaguaya.pspserver.network.services.OrganizationService;
import py.org.fundacionparaguaya.pspserver.security.dtos.UserDetailsDTO;
import py.org.fundacionparaguaya.pspserver.surveys.dtos.NewSnapshot;
import py.org.fundacionparaguaya.pspserver.surveys.dtos.NewSurveyDefinition;
import py.org.fundacionparaguaya.pspserver.surveys.dtos.Snapshot;
import py.org.fundacionparaguaya.pspserver.surveys.dtos.SurveyDefinition;
import py.org.fundacionparaguaya.pspserver.surveys.services.SnapshotService;
import py.org.fundacionparaguaya.pspserver.surveys.services.SurveyService;
import py.org.fundacionparaguaya.pspserver.util.TestHelper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by rodrigovillalba on 7/26/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
@Transactional
public class ApplicationIT {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private SnapshotService snapshotService;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ApplicationService applicationService;


    @Test
    public void itShouldStart() {
        assertThat(true).isTrue();
    }


    @Test
    public void itShouldVerifyCoreSurveySnapshotFamilyLifeCycleInServiceSlice() throws JsonProcessingException {

        // 1. Create survey
        NewSurveyDefinition definition = TestHelper.getNewDefinition();
        SurveyDefinition addSurveyDefinition = surveyService.addSurveyDefinition(definition);
        assertSurveyCreation(addSurveyDefinition);

        // 2. Create snapshot
        NewSnapshot newSnapshot = TestHelper.getNewSnapshot(addSurveyDefinition.getId());
        Snapshot snapshot = snapshotService.addSurveySnapshot(userWithOrganization(), newSnapshot);
        assertSnapshotCreation(snapshot);

        // 3. Verify family creation
        FamilyDTO familyById = familyService.getFamilyById(snapshot.getFamily().getFamilyId());
        assertFamilyCreation(familyById);

    }
    private UserDetailsDTO userWithOrganization() {
        try {
            ApplicationDTO app = newTestApplication();
            OrganizationDTO savedOrg = newTestOrganization(app);
            return TestHelper.getHubAdminUser(savedOrg);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    private ApplicationDTO newTestApplication() {
        ApplicationDTO app = TestHelper.newApp();
        return applicationService.addApplication(app);
    }

    private OrganizationDTO newTestOrganization(ApplicationDTO app) {
        OrganizationDTO organizationDTO = TestHelper.newOrg(app);
        return organizationService.addOrganization(organizationDTO);
    }

    private void assertFamilyCreation(FamilyDTO familyDTO) {
        assertThat(familyDTO).isNotNull();
        assertThat(familyDTO.getOrganization()).isNotNull();
        assertThat(familyDTO.getUser()).isNotNull();
        assertThat(familyDTO.getPerson()).isNotNull();
        assertThat(familyDTO.getCode()).isNotNull();
    }

    private void assertSnapshotCreation(Snapshot snapshot) {
        assertThat(snapshot).isNotNull();
        assertThat(snapshot.getSnapshotEconomicId()).isNotNull();
        assertThat(snapshot.getSnapshotIndicatorId()).isNotNull();
        assertThat(snapshot.getFamily()).isNotNull();
    }

    private void assertSurveyCreation(SurveyDefinition addSurveyDefinition) {
        assertThat(addSurveyDefinition).isNotNull();
        assertThat(addSurveyDefinition.getId()).isNotNull();
    }
}
