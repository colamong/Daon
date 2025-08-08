package com.daon.be.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "community")
@Getter
@Setter
@NoArgsConstructor
public class Community {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "current_participants")
    private Integer currentParticipants;
    
    public Community(String title, Integer currentParticipants) {
        this.title = title;
        this.currentParticipants = currentParticipants;
    }
}