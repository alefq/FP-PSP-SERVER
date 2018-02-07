package py.org.fundacionparaguaya.pspserver.network.services;

import java.util.List;

import py.org.fundacionparaguaya.pspserver.common.pagination.PaginableList;
import py.org.fundacionparaguaya.pspserver.network.dtos.OrganizationDTO;
import py.org.fundacionparaguaya.pspserver.security.dtos.UserDetailsDTO;

public interface OrganizationService {

  OrganizationDTO updateOrganization(Long organizationId,
            OrganizationDTO organizationDto);

  OrganizationDTO addOrganization(OrganizationDTO organizationDto);

  OrganizationDTO getOrganizationById(Long organizationId);

  List<OrganizationDTO> getAllOrganizations();

  void deleteOrganization(Long organizationId);

  PaginableList<OrganizationDTO> listOrganizations(Long applicationId,
            Long organizationId, int page, int perPage, String orderBy,
            String sortBy);

  OrganizationDTO getOrganizationDashboard(Long organizationId,
            UserDetailsDTO details);

  OrganizationDTO getUserOrganization(UserDetailsDTO details,
            Long organizationId);

}
