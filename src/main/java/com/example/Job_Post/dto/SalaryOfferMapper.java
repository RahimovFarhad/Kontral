package com.example.Job_Post.dto;

import org.springframework.stereotype.Component;

import com.example.Job_Post.entity.SalaryNegotiation;
import com.example.Job_Post.entity.SalaryOffer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SalaryOfferMapper {
    private final UserMapper userMapper;

    public SalaryOfferDTO toDTO(SalaryOffer offer) {
        if (offer == null) {
            return null;
        }

        return SalaryOfferDTO.builder()
                .id(offer.getId())
                .negotiationId(offer.getNegotiation().getId())
                .sender(userMapper.toDTO(offer.getSender()))
                .proposedSalary(offer.getProposedSalary())
                .message(offer.getMessage())
                .accepted(offer.isAccepted())
                .isResponded(offer.isResponded())
                .createdAt(offer.getCreatedAt())
                .build();
    }

    public SalaryOffer toEntity(SalaryOfferDTO offerDTO) {
        if (offerDTO == null) {
            return null;
        }

        return SalaryOffer.builder()
                .id(offerDTO.getId())
                .negotiation(SalaryNegotiation.builder().id(offerDTO.getNegotiationId()).build()) // Minimal reference to avoid circular dependency
                .sender(userMapper.toEntity(offerDTO.getSender()))
                .proposedSalary(offerDTO.getProposedSalary())
                .message(offerDTO.getMessage())
                .accepted(offerDTO.isAccepted())
                .isResponded(offerDTO.isResponded())
                .createdAt(offerDTO.getCreatedAt())
                .build();
    }
    
}
