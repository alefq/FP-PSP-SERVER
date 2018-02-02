package py.org.fundacionparaguaya.pspserver.surveys.services.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.data.jpa.domain.Specifications.where;
import static py.org.fundacionparaguaya.pspserver.surveys.specifications.SnapshotDraftSpecification.byFilter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import py.org.fundacionparaguaya.pspserver.common.exceptions.CustomParameterizedException;
import py.org.fundacionparaguaya.pspserver.common.exceptions.UnknownResourceException;

import py.org.fundacionparaguaya.pspserver.security.dtos.UserDetailsDTO;
import py.org.fundacionparaguaya.pspserver.security.entities.UserEntity;

import py.org.fundacionparaguaya.pspserver.security.repositories.UserRepository;
import py.org.fundacionparaguaya.pspserver.surveys.dtos.SnapshotDraft;
import py.org.fundacionparaguaya.pspserver.surveys.entities.SnapshotDraftEntity;
import py.org.fundacionparaguaya.pspserver.surveys.mapper.SnapshotDraftMapper;
import py.org.fundacionparaguaya.pspserver.surveys.repositories.SnapshotDraftRepository;
import py.org.fundacionparaguaya.pspserver.surveys.services.SnapshotDraftService;

/**
 *
 * @author mgonzalez
 *
 */
@Service
public class SnapshotDraftServiceImpl implements SnapshotDraftService {

    private static final long SNAPSHOT_DRAFT_MAX_DAY = 8;

    private final SnapshotDraftMapper mapper;

    private final SnapshotDraftRepository repository;

    private final UserRepository userRepository;

    public SnapshotDraftServiceImpl(SnapshotDraftMapper mapper,
            SnapshotDraftRepository repository,
            UserRepository userRepository) {
        this.mapper = mapper;
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public SnapshotDraft addSnapshotDraft(SnapshotDraft snapshot) {
        SnapshotDraftEntity snapshotEntity = mapper.dtoToEntity(snapshot);
        snapshotEntity = repository.save(snapshotEntity);
        return mapper.entityToDto(snapshotEntity);
    }

    @Override
    public SnapshotDraft getSnapshotDraft(Long id) {

       checkArgument(id!=null && id > 0, "Argument"
               + " was %s but expected nonnegative", id);
       return Optional.ofNullable(repository
               .findOne(id))
               .map(mapper::entityToDto)
               .orElseThrow(() ->
               new UnknownResourceException(
                       "Temporal snapshot does not exist"));
    }

    @Override
    public void deleteSnapshotDraft(Long id) {

        checkArgument(id!=null && id > 0, "Argument"
                + " was %s but expected nonnegative", id);

        Optional.ofNullable(repository.findOne(id)).ifPresent(snapshot -> {
            repository.delete(snapshot);
        });
    }

    @Override

    public SnapshotDraft updateSnapshotDraft(Long id,
            SnapshotDraft snapshotDraft) {
        checkArgument(id!=null && id > 0, "Argument"
                + " was %s but expected nonnegative", id);
        checkArgument(snapshotDraft!=null, "Argument"
                + " was %s but expected non null", snapshotDraft);

        SnapshotDraftEntity snapshotEntity = Optional.ofNullable(repository
                .findOne(id))
                .orElseThrow(() ->
                new CustomParameterizedException(
                        "Snapshot draft does not exist"));
        
        snapshotEntity.setStateDraft(snapshotDraft.getStateDraft());

        if (snapshotDraft.getUserName()!=null) {
            snapshotEntity.setUser(userRepository
                    .findOneByUsername(snapshotDraft.getUserName())
                    .orElseThrow(() ->
                    new CustomParameterizedException(
                            "User does not exist")));
        }

        snapshotEntity = repository.save(snapshotEntity);
        return mapper.entityToDto(snapshotEntity);
    }

    public List<SnapshotDraft> getSnapshotDraftByUser(UserDetailsDTO details,
                    String description) {

        List<SnapshotDraftEntity> ret = new ArrayList<SnapshotDraftEntity>();
        
        Optional<UserEntity> user = userRepository.findOneByUsername(details.getUsername());

        LocalDateTime now = LocalDateTime.now();
        
        for (SnapshotDraftEntity snapshotDraft : repository
                        .findAll(where(byFilter(user.get().getId(), description)))) {

            if (snapshotDraft.getCreatedAt().until(now,
                            ChronoUnit.DAYS) < SNAPSHOT_DRAFT_MAX_DAY) {
                ret.add(snapshotDraft);
            }
        }

        return mapper.entityListToDtoList(ret);

    }

}
