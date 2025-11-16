package com.example.Job_Post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Job_Post.entity.SalaryNegotiation;
import com.example.Job_Post.enumerator.NegotiationStatus;

public interface SalaryNegotiationRepository extends JpaRepository<SalaryNegotiation, Integer> {

    List<SalaryNegotiation> findByJobApplicationId(Integer jobApplicationId);

    SalaryNegotiation findByJobApplicationIdAndStatus(Integer jobApplicationId, NegotiationStatus status);


}
