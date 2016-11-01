package dv.dao;

import dv.model.TempLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TempLogRepository extends CrudRepository<TempLog, Long> {

    List<TempLog> findByTakenAtBetweenOrderByTakenAtAsc(Date fromDate, Date toDate);
}
