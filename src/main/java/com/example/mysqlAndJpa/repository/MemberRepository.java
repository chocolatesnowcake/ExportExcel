package com.example.mysqlAndJpa.repository;

import com.example.mysqlAndJpa.model.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

    public List<Member> findAll();
}
