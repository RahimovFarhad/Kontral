package com.example.Job_Post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Job_Post.entity.SalaryOffer;

public interface SalaryOfferRepository extends JpaRepository<SalaryOffer, Integer> {
    SalaryOffer findFirstByNegotiationJobApplicationIdOrderByIdDesc(Integer jobApplicationId);

}
