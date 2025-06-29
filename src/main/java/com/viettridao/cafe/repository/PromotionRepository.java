package com.viettridao.cafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viettridao.cafe.model.PromotionEntity;

@Repository
public interface PromotionRepository extends JpaRepository<PromotionEntity, Integer> {

}
