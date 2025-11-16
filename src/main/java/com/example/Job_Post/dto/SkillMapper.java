package com.example.Job_Post.dto;

import org.springframework.stereotype.Component;

import com.example.Job_Post.entity.Skill;

@Component
public class SkillMapper {

    public SkillDTO toDTO(Skill skill) {
        if (skill == null) {
            return null;
        }
        return SkillDTO.builder()
                .id(skill.getId())
                .skillType(skill.getSkillType())
                .name(skill.getName())
                .level(skill.getLevel())
                .experience(skill.getExperience())
                .build();
    }

    public Skill toEntity(SkillDTO skillDTO) {
        if (skillDTO == null) {
            return null;
        }
        Skill skill = new Skill();
        skill.setId(skillDTO.getId());
        skill.setSkillType(skillDTO.getSkillType());
        skill.setName(skillDTO.getName());
        skill.setLevel(skillDTO.getLevel());
        skill.setExperience(skillDTO.getExperience());
        return skill;
    }
}
