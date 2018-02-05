package py.org.fundacionparaguaya.pspserver.surveys.services;

import java.util.List;

import py.org.fundacionparaguaya.pspserver.security.dtos.UserDetailsDTO;
import py.org.fundacionparaguaya.pspserver.surveys.dtos.SnapshotDraft;
/**
 *
 * @author mgonzalez
 *
 */
public interface SnapshotDraftService {

    SnapshotDraft addSnapshotDraft(SnapshotDraft snapshot);

    SnapshotDraft getSnapshotDraft(Long id);

    void deleteSnapshotDraft(Long id);

    List<SnapshotDraft> getSnapshotDraftByUser(UserDetailsDTO details,
            String description);
}
