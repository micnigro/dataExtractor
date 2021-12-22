package it.perigea.formazione.extractor.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import it.perigea.formazione.extractor.entity.ProcessEntity;

@Repository
public interface ProcessRepository extends JpaRepository<ProcessEntity,Long> { 
	
	public List<ProcessEntity> findByDateTime(Date date);
	
	public List<ProcessEntity> findByType(String type);

	public List<ProcessEntity> findAllByDateTimeLessThanEqualAndDateTimeGreaterThanEqual(Date endDate,
			Date startDate);

}
