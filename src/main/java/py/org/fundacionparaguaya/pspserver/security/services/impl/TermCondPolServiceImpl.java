package py.org.fundacionparaguaya.pspserver.security.services.impl;

import org.springframework.stereotype.Service;
import py.org.fundacionparaguaya.pspserver.common.exceptions.CustomParameterizedException;
import py.org.fundacionparaguaya.pspserver.common.exceptions.UnknownResourceException;
import py.org.fundacionparaguaya.pspserver.security.constants.TermCondPolLocale;
import py.org.fundacionparaguaya.pspserver.security.constants.TermCondPolType;
import py.org.fundacionparaguaya.pspserver.security.dtos.TermCondPolDTO;
import py.org.fundacionparaguaya.pspserver.security.entities.TermCondPolEntity;
import py.org.fundacionparaguaya.pspserver.security.mapper.TermCondPolMapper;
import py.org.fundacionparaguaya.pspserver.security.repositories.TermCondPolRepository;
import py.org.fundacionparaguaya.pspserver.security.services.TermCondPolService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public class TermCondPolServiceImpl implements TermCondPolService {

    private TermCondPolRepository repository;

    private final TermCondPolMapper mapper;

    public TermCondPolServiceImpl(TermCondPolRepository repository,
                                  TermCondPolMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public TermCondPolDTO getLastTermCondPol(TermCondPolType type, Long applicationId, TermCondPolLocale locale){
        checkArgument(type != null,
                "Argument was %s but expected not null", type);
        checkArgument(applicationId != null,
                "Argument was %s but expected not null", applicationId);
        checkArgument(locale != null,
                "Argument was %s but expected not null", applicationId);

        return Optional.ofNullable(repository
                .findFirstByTypeCodAndApplicationIdAndLocaleOrderByIdDesc(type, applicationId, locale))
                .map(mapper::entityToDto)
                .orElseThrow(() -> new CustomParameterizedException("Terms and Conditions or Privacy"
                + " Policy does not exist"));
    }

    public List<TermCondPolDTO> getAllTermLanguagePairs(Long applicationId){
        checkArgument(applicationId != null,
                "Argument was %s but expected not null", applicationId);

        return repository.findDistinctLocaleAndTypeCodByApplicationId(applicationId).stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TermCondPolDTO updateTerms(String htmlFile, Long termCondPolId) {
        checkArgument(termCondPolId != null,
                "Argument was %s but expected not null", termCondPolId);

        return Optional.ofNullable(repository
                .findOne(termCondPolId))
                .map(entity -> {
                    entity.setHtml(htmlFile);
                    return repository.save(entity);
                })
                .map(mapper::entityToDto)
                .orElseThrow(() -> new UnknownResourceException(
                        "Terms and Conditions or Privacy Policy"
                                + " does not exist"));

    }

    @Override
    public TermCondPolDTO saveTerms(TermCondPolDTO termCondPolDTO) {
        TermCondPolEntity entity = mapper.dtoToEntity(termCondPolDTO);
        entity.setCreatedDate(LocalDate.now());
        return mapper.entityToDto(repository.save(entity));
    }

    @Override
    public List<TermCondPolDTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

}
