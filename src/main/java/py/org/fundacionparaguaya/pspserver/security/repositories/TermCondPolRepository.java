package py.org.fundacionparaguaya.pspserver.security.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import py.org.fundacionparaguaya.pspserver.security.constants.TermCondPolLocale;
import py.org.fundacionparaguaya.pspserver.security.constants.TermCondPolType;
import py.org.fundacionparaguaya.pspserver.security.entities.TermCondPolEntity;

public interface TermCondPolRepository extends
    JpaRepository<TermCondPolEntity, Long> {
    TermCondPolEntity findFirstByTypeCodAndApplicationIdAndLocaleOrderByIdDesc(
            TermCondPolType type, Long applicationId, TermCondPolLocale locale);
    List<TermCondPolEntity> findDistinctLocaleAndTypeCodByApplicationId(Long applicationId);
}
