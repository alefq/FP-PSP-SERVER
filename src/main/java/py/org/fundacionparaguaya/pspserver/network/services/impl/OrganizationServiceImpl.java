package py.org.fundacionparaguaya.pspserver.network.services.impl;

import com.google.common.collect.ImmutableMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import py.org.fundacionparaguaya.pspserver.common.exceptions.CustomParameterizedException;
import py.org.fundacionparaguaya.pspserver.common.exceptions.UnknownResourceException;
import py.org.fundacionparaguaya.pspserver.common.pagination.PaginableList;
import py.org.fundacionparaguaya.pspserver.common.pagination.PspPageRequest;
import py.org.fundacionparaguaya.pspserver.config.ApplicationProperties;
import py.org.fundacionparaguaya.pspserver.families.dtos.FamilyFilterDTO;
import py.org.fundacionparaguaya.pspserver.families.services.FamilyService;
import py.org.fundacionparaguaya.pspserver.network.dtos.ApplicationDTO;
import py.org.fundacionparaguaya.pspserver.network.dtos.DashboardDTO;
import py.org.fundacionparaguaya.pspserver.network.dtos.OrganizationDTO;
import py.org.fundacionparaguaya.pspserver.network.entities.ApplicationEntity;
import py.org.fundacionparaguaya.pspserver.network.entities.OrganizationEntity;
import py.org.fundacionparaguaya.pspserver.network.mapper.OrganizationMapper;
import py.org.fundacionparaguaya.pspserver.network.repositories.ApplicationRepository;
import py.org.fundacionparaguaya.pspserver.network.repositories.OrganizationRepository;
import py.org.fundacionparaguaya.pspserver.network.services.OrganizationService;
import py.org.fundacionparaguaya.pspserver.security.dtos.UserDetailsDTO;
import py.org.fundacionparaguaya.pspserver.system.dtos.ImageDTO;
import py.org.fundacionparaguaya.pspserver.system.dtos.ImageParser;
import py.org.fundacionparaguaya.pspserver.system.services.ImageUploadService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.data.jpa.domain.Specifications.where;
import static py.org.fundacionparaguaya.pspserver.network.specifications.OrganizationSpecification.byFilter;


@Service
public class OrganizationServiceImpl implements OrganizationService {

    private static final Logger LOG =
            LoggerFactory.getLogger(OrganizationServiceImpl.class);

    private final OrganizationRepository organizationRepository;

    private final ApplicationRepository applicationRepository;

    private final OrganizationMapper organizationMapper;

    private final FamilyService familyService;

    private final ImageUploadService imageUploadService;

    private final ApplicationProperties applicationProperties;

    public OrganizationServiceImpl(
            OrganizationRepository organizationRepository,
            ApplicationRepository applicationRepository,
            OrganizationMapper organizationMapper,
            FamilyService familyService,
            ImageUploadService imageUploadService,
            ApplicationProperties applicationProperties) {
        this.organizationRepository = organizationRepository;
        this.applicationRepository = applicationRepository;
        this.organizationMapper = organizationMapper;
        this.familyService = familyService;
        this.imageUploadService = imageUploadService;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public OrganizationDTO updateOrganization(Long organizationId,
                                              OrganizationDTO organizationDTO) {
        checkArgument(organizationId > 0,
                "Argument was %s but expected nonnegative", organizationId);

        return Optional.ofNullable(
                organizationRepository.findOne(organizationId)).map(
                organization -> {
                    organization.setName(organizationDTO.getName());
                    organization.setDescription(
                            organizationDTO.getDescription());
                    LOG.debug(
                            "Changed Information for Organization: {}",
                            organization);
                    return organizationRepository.save(organization);
                }
        ).map(organizationMapper::entityToDto).orElseThrow(() -> new
                UnknownResourceException("Organization does not exist")
        );
    }

    @Override
    public OrganizationDTO addOrganization(OrganizationDTO organizationDTO)
            throws IOException {
        organizationRepository.findOneByName(organizationDTO.getName())
                .ifPresent(organization -> {
                        throw new CustomParameterizedException(
                                "Organisation already exists",
                                new ImmutableMultimap.Builder<String, String>()
                                        .put("name", organization.getName())
                                        .build()
                                        .asMap()
                        );
                        }
                );

        // Save Organization entity
        OrganizationEntity organization = new OrganizationEntity();
        BeanUtils.copyProperties(organizationDTO, organization);
        ApplicationEntity application = applicationRepository
                .findById(organizationDTO.getApplication().getId());
        organization.setApplication(application); organization.setActive(true);
        OrganizationEntity newOrganization = organizationRepository
                .save(organization);

        // Upload image to AWS S3 service
        ImageDTO imageDTO = ImageParser.parse(organizationDTO.getFile());
        imageDTO.setImageDirectory(
                    applicationProperties.getAws().getOrgsImageDirectory());
        imageDTO.setImageNamePrefix(
                    applicationProperties.getAws().getOrgsImageNamePrefix());

        String logoURL =
            imageUploadService.uploadImage(imageDTO, newOrganization.getId());

        if (logoURL != null) {
            // Update Organization entity with image URL
            newOrganization.setLogoUrl(logoURL);
            organizationRepository.save(newOrganization);
        }

        return organizationMapper.entityToDto(newOrganization);
    }

    @Override
    public OrganizationDTO getOrganizationById(Long organizationId) {
        checkArgument(organizationId > 0,
                "Argument was %s but expected nonnegative", organizationId);

        return Optional.ofNullable(organizationRepository
                .findOne(organizationId)).map(organizationMapper::entityToDto)
                .orElseThrow(() -> new UnknownResourceException(
                        "Organization does not exist"));
    }

    @Override
    public List<OrganizationDTO> getAllOrganizations() {
        List<OrganizationEntity> organizations = organizationRepository
                .findAll();
        return organizationMapper.entityListToDtoList(organizations);
    }

    @Override
    public void deleteOrganization(Long organizationId) {
        checkArgument(organizationId > 0,
                "Argument was %s but expected nonnegative", organizationId);

        Optional.ofNullable(organizationRepository.findOne(organizationId))
                .ifPresent(organization -> {
                    organizationRepository.delete(organization);
                    LOG.debug("Deleted Organization: {}", organization);
                });
    }

    @Override
    public Page<OrganizationDTO> listOrganizations(PageRequest pageRequest,
                                                   UserDetailsDTO userDetails) {
        Long applicationId = Optional.ofNullable(userDetails.getApplication())
                .orElse(new ApplicationDTO()).getId();

        Long organizationId = Optional.ofNullable(userDetails.getOrganization())
                .orElse(new OrganizationDTO()).getId();

        Page<OrganizationEntity> pageResponse = organizationRepository
                .findAll(
                        where(byFilter(applicationId, organizationId)),
                        pageRequest
                );

        if (pageResponse != null) {
            return pageResponse.map(organizationMapper::entityToDto);
        }

        return null;
    }

    @Override
    public PaginableList<OrganizationDTO> listOrganizations(Long applicationId,
                    Long organizationId, int page, int perPage, String orderBy,
                                                                String sortBy) {

        PaginableList<OrganizationDTO> response;

        PageRequest pageRequest = new PspPageRequest(page, perPage, orderBy,
                sortBy);

        Page<OrganizationEntity> pageResponse = organizationRepository
                .findAll(where(byFilter(applicationId, organizationId)),
                        pageRequest);

        if (pageResponse == null) {
            return new PaginableList<>(Collections.emptyList());
        } else {
            Page<OrganizationDTO> organizationPage = pageResponse
                .map(new Converter<OrganizationEntity, OrganizationDTO>() {
                    @Override
                    public OrganizationDTO convert(OrganizationEntity source) {
                        return organizationMapper.entityToDto(source);
                    }
                });

            response = new PaginableList<OrganizationDTO>(organizationPage,
                    organizationPage.getContent());
        }

        return response;
    }

    @Override
    public OrganizationDTO getOrganizationDashboard(Long organizationId,
                                                    UserDetailsDTO details) {
        OrganizationDTO dto = getUserOrganization(details, organizationId);

        Long applicationId = Optional.ofNullable(details.getApplication())
                .orElse(new ApplicationDTO()).getId();

        FamilyFilterDTO filter =
                new FamilyFilterDTO(applicationId, dto.getId());
        dto.setDashboard(
                DashboardDTO.of(familyService.countFamiliesByFilter(filter)));

        return dto;
    }

    @Override
    public OrganizationDTO getUserOrganization(UserDetailsDTO details,
                                               Long organizationId) {
        if (details.getOrganization() != null
                && details.getOrganization().getId() != null) {
            return getOrganizationById(details.getOrganization().getId());
        } else if (organizationId != null) {
            return getOrganizationById(organizationId);
        }
        return null;
    }
}
