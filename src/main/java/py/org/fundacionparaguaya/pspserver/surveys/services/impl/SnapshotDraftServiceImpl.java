package py.org.fundacionparaguaya.pspserver.surveys.services.impl;

import static com.google.common.base.Preconditions.checkArgument;

import static org.springframework.data.jpa.domain.Specifications.where;
import static py.org.fundacionparaguaya.pspserver.surveys.specifications.SnapshotDraftSpecification.userEquals;
import static py.org.fundacionparaguaya.pspserver.surveys.specifications.SnapshotDraftSpecification.likeFamilyName;
import static py.org.fundacionparaguaya.pspserver.surveys.specifications.SnapshotDraftSpecification.createdAtLess8Days;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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

    private final SnapshotDraftMapper mapper;

    private final SnapshotDraftRepository repository;

    private final UserRepository userRepo;

    public SnapshotDraftServiceImpl(SnapshotDraftMapper mapper,
                    SnapshotDraftRepository repository,
                    UserRepository userRepo) {
        this.mapper = mapper;
        this.repository = repository;
        this.userRepo = userRepo;
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
    public List<SnapshotDraft> getSnapshotDraftByUser(UserDetailsDTO details,
                    String familyName) {

        List<SnapshotDraftEntity> ret = new ArrayList<SnapshotDraftEntity>();

        UserEntity user = userRepo.findOneByUsername(
                details.getUsername()).orElse(null);

        if (user == null) {
            return Collections.emptyList();
        }

        ret = repository
                  .findAll(where(userEquals(user.getId()))
                  .and(likeFamilyName(familyName))
                  .and(createdAtLess8Days()));

        return mapper.entityListToDtoList(ret);

    }

}
