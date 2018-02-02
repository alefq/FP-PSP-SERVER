package py.org.fundacionparaguaya.pspserver.surveys.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import py.org.fundacionparaguaya.pspserver.common.mapper.BaseMapper;
import py.org.fundacionparaguaya.pspserver.security.repositories.TermCondPolRepository;
import py.org.fundacionparaguaya.pspserver.security.repositories.UserRepository;
import py.org.fundacionparaguaya.pspserver.surveys.dtos.SnapshotDraft;
import py.org.fundacionparaguaya.pspserver.surveys.entities.SnapshotDraftEntity;
import py.org.fundacionparaguaya.pspserver.surveys.repositories.SurveyRepository;

/**
 *
 * @author mgonzalez
 *
 */
@Component
public class SnapshotDraftMapper implements
    BaseMapper<SnapshotDraftEntity, SnapshotDraft> {

    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    private final TermCondPolRepository termCondPolRepository;

    private final SurveyRepository surveyRepository;

    public SnapshotDraftMapper(ModelMapper modelMapper,
            UserRepository userRepository,
            TermCondPolRepository termCondPolRepository,
            SurveyRepository surveyRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.termCondPolRepository = termCondPolRepository;
        this.surveyRepository = surveyRepository;
    }

    @Override
    public List<SnapshotDraft> entityListToDtoList(
            List<SnapshotDraftEntity> entityList) {
        return entityList.stream()
                .filter(Objects::nonNull)
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SnapshotDraft entityToDto(SnapshotDraftEntity entity) {
        SnapshotDraft dto = modelMapper.map(entity, SnapshotDraft.class);
        dto = dto.createdAt(entity.getCreatedAtAsISOString()).
                surveyId(entity.getSurveyDefinition().getId());
        return dto;
    }

    @Override
    public SnapshotDraftEntity dtoToEntity(SnapshotDraft dto) {
        SnapshotDraftEntity entity = modelMapper.map(dto,
                SnapshotDraftEntity.class);
        if (dto.getUserName()!=null) {
            entity.setUser(userRepository.findOneByUsername(
                    dto.getUserName()).orElse(null));
        }

        if (dto.getTermCondId()!=null) {
            entity.setTermCond(termCondPolRepository
                .findOne(dto.getTermCondId()));
        }

        if (dto.getPrivPolId()!=null) {
            entity.setPrivPol(termCondPolRepository
                .findOne(dto.getPrivPolId()));
        }

        if (dto.getSurveyId()!=null) {
            entity.setSurveyDefinition(surveyRepository
                .findOne(dto.getSurveyId()));
        }

        return entity;
    }

}
