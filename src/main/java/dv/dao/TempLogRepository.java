package dv.dao;

import dv.dto.TempLogDTO;
import dv.dto.TempLogProjection;
import dv.model.TempLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Repository component to load temperature logs within specified date interval. <br>
 * All three methods provide the same business function, though via different techniques: <br>
 * - standard entity loading; <br>
 * - projection loading (Spring Data projections); <br>
 * - DTO object loading (JPA constructor expressions).
 */
@Repository
public interface TempLogRepository extends CrudRepository<TempLog, Long> {

    List<TempLog> findByTakenAtBetweenOrderByTakenAtAsc(Date fromDate, Date toDate);

    List<TempLogProjection> findProjectionByTakenAtBetweenOrderByTakenAtAsc(Date fromDate, Date toDate);

    @Query("select new dv.dto.TempLogDTO(tl.temperature, tl.takenAt)"
            + " from TempLog tl where tl.takenAt between ?1 and ?2")
    List<TempLogDTO> findDtoByTakenAtBetweenOrderByTakenAtAsc(Date fromDate, Date toDate);
}
