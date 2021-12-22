package it.perigea.formazione.extractor.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import it.perigea.formazione.extractor.entity.ExecutionEntity;

@Repository
public interface ExecutionRepository extends JpaRepository<ExecutionEntity,Long> { 

	public List<ExecutionEntity> findByType(String type);

}
