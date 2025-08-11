package com.daon.be.child.repository;

import com.daon.be.child.entity.ChildInterest;
import com.daon.be.child.entity.InterestAuthor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildInterestRepository extends JpaRepository<ChildInterest, Long> {

    List<ChildInterest> findByChildProfileId(Long childProfileId);

    // 관심사 동기화 시, name 컬럼 기준으로 삭제
    void deleteByChildProfileIdAndNameIn(Long childProfileId, List<String> names);

    // 저자 기준 조회(새로 추가)
    List<ChildInterest> findByChildProfileIdAndAuthor(Long childProfileId, InterestAuthor author);

}
