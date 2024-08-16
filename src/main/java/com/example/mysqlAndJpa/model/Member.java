package com.example.mysqlAndJpa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "member")
@Entity
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer memberId;
    private String email;
    private String password;
    private String name;
    private Integer age;

}
