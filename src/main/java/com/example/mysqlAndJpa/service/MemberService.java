package com.example.mysqlAndJpa.service;

import com.example.mysqlAndJpa.model.Member;
import com.example.mysqlAndJpa.repository.MemberRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    public List<Member> findAll(){
        return memberRepository.findAll();
    }
}
