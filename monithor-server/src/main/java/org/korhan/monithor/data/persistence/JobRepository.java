package org.korhan.monithor.data.persistence;

import org.korhan.monithor.data.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

  public List<Job> findAllByOrderByName();

  public List<Job> findAllByNameIgnoreCaseContainingOrderByName(String name);

  @Query("SELECT j FROM Job j WHERE :tag MEMBER OF j.tags ORDER BY j.name")
  public List<Job> findByTag(@Param("tag") String tag);

  @Query("SELECT j FROM Job j WHERE UPPER(j.name) LIKE UPPER(CONCAT('%',:value,'%')) OR UPPER(:value) MEMBER OF j.tags ORDER BY j.name")
  public List<Job> findByTagOrName(@Param("value") String value);

  @Query(value = "SELECT DISTINCT(tag) FROM TAGS ORDER BY tag", nativeQuery = true)
  public List<String> findAllTags();
}
