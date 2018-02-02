package py.org.fundacionparaguaya.pspserver.surveys.services.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Optional;

import org.springframework.stereotype.Service;

import py.org.fundacionparaguaya.pspserver.common.exceptions.UnknownResourceException;
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

    private final SnapshotDraftMapper mapper;

    private final SnapshotDraftRepository repository;

    public SnapshotDraftServiceImpl(SnapshotDraftMapper mapper,
            SnapshotDraftRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
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

}
