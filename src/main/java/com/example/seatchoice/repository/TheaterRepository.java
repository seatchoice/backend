package com.example.seatchoice.repository;

import com.example.seatchoice.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long> {

	Theater findByNameAndFacility_Name(String name, String facilityName);

}
