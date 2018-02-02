package py.org.fundacionparaguaya.pspserver.surveys.services;

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
}
