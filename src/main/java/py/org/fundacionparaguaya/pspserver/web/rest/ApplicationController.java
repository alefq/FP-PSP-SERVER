package py.org.fundacionparaguaya.pspserver.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.org.fundacionparaguaya.pspserver.common.pagination.PaginableList;
import py.org.fundacionparaguaya.pspserver.network.dtos.ApplicationDTO;
import py.org.fundacionparaguaya.pspserver.network.services.ApplicationService;
import py.org.fundacionparaguaya.pspserver.security.dtos.UserDetailsDTO;

@RestController
@RequestMapping(value = "/api/v1/applications")
public class ApplicationController {

  private static final Logger LOG = LoggerFactory
      .getLogger(ApplicationController.class);

  private ApplicationService applicationService;

  public ApplicationController(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @PostMapping()
  public ResponseEntity<ApplicationDTO> addApplication(
      @Valid @RequestBody ApplicationDTO applicationDto)
      throws URISyntaxException {
    ApplicationDTO result = applicationService.addApplication(applicationDto);
    return ResponseEntity
        .created(new URI("/api/v1/applications/" + result.getId()))
        .body(result);
  }

  @PutMapping("/{applicationId}")
  public ResponseEntity<ApplicationDTO> updateApplication(
      @PathVariable("applicationId") long applicationId,
      @RequestBody ApplicationDTO applicationDto) {
    ApplicationDTO result = applicationService.updateApplication(applicationId,
        applicationDto);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/{applicationId}")
  public ResponseEntity<ApplicationDTO> getApplicationById(
      @PathVariable("applicationId") Long applicationId) {
    ApplicationDTO dto = applicationService.getApplicationById(applicationId);
    return ResponseEntity.ok(dto);
  }

  @GetMapping()
  public ResponseEntity<List<ApplicationDTO>> getAllApplications() {
    List<ApplicationDTO> applications = applicationService.getAllApplications();
    return ResponseEntity.ok(applications);
  }

  @DeleteMapping("/{applicationId}")
  public ResponseEntity<Void> deleteApplication(
      @PathVariable("applicationId") Long applicationId) {
    LOG.debug("REST request to delete Application: {}", applicationId);
    applicationService.deleteApplication(applicationId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/dashboard")
  public ResponseEntity<ApplicationDTO> getApplicationDashboard(
      @RequestParam(value = "applicationId",
          required = false) Long applicationId,
      @AuthenticationPrincipal UserDetailsDTO details) {
    ApplicationDTO dto = applicationService
        .getApplicationDashboard(applicationId, details);
    return ResponseEntity.ok(dto);
  }

  @GetMapping("/hubs")
  public ResponseEntity<PaginableList<ApplicationDTO>> getAllApplicationsHubs(
      @RequestParam(value = "page", required = false,
                         defaultValue = "1") int page,
      @RequestParam(value = "per_page", required = false,
                         defaultValue = "12") int perPage,
      @RequestParam(value = "sort_by", required = false,
                         defaultValue = "name") String sortBy,
      @RequestParam(value = "order", required = false,
                         defaultValue = "asc") String orderBy) {

    return ResponseEntity.ok(applicationService.listApplicationsHubs(page,
        perPage, orderBy, sortBy));

  }

}