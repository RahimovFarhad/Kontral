package com.example.Job_Post.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Job_Post.dto.SkillDTO;
import com.example.Job_Post.dto.SkillMapper;
import com.example.Job_Post.entity.Skill;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.repository.SkillRepository;
import com.example.Job_Post.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/skill")
public class SkillController {
    private final SkillRepository skillRepository;
    private final UserService userService;
    private final SkillMapper skillMapper;

    @PostMapping("/addSkill")
    public ResponseEntity<?> addSkill(@RequestBody SkillDTO skillDTO, Principal principal) {
        try {
            User user = userService.getUserByEmail(principal.getName());
            Skill skill = skillMapper.toEntity(skillDTO);
            if (skill == null) {
                return ResponseEntity.badRequest().body("Invalid skill data");
            }

            skill.setUser(user);
            Skill savedSkill = skillRepository.save(skill);

            return ResponseEntity.ok(skillMapper.toDTO(savedSkill));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to add skill: " + e.getMessage());
        }
    }

    @DeleteMapping("/removeSkill/{id}")
    public ResponseEntity<?> removeSkill(@PathVariable Integer id, Principal principal) {
        try {
            User user = userService.getUserByEmail(principal.getName());
            Skill skill = skillRepository.findById(id).orElse(null);
            if (skill != null && skill.getUser().equals(user)) {
                skillRepository.delete(skill);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().body("Failed to remove skill: Skill not found or does not belong to user");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to remove skill: " + e.getMessage());

        }
    }

}
    

