package py.org.fundacionparaguaya.pspserver.security.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import py.org.fundacionparaguaya.pspserver.security.constants.TermCondPolLocale;
import py.org.fundacionparaguaya.pspserver.security.constants.TermCondPolType;
import py.org.fundacionparaguaya.pspserver.security.entities.TermCondPolEntity;

public interface TermCondPolRepository extends
    JpaRepository<TermCondPolEntity, Long> {
    TermCondPolEntity findFirstByTypeCodAndApplicationIdAndLocaleOrderByIdDesc(
            TermCondPolType type, Long applicationId, TermCondPolLocale locale);

    @Query(value = "SELECT DISTINCT ON (t.locale, t.type_cod) * FROM security.termcondpol t " +
            "WHERE t.id_application = :appId " +
            "ORDER BY t.locale, t.type_cod, t.created_date DESC",
           nativeQuery = true)
    List<TermCondPolEntity> findDistinctOnLocaleAndTypeCodByApplicationId(@Param("appId") Long applicationId);
}
