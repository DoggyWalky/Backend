package com.doggyWalky.doggyWalky.jobpost.repository;

import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import com.doggyWalky.doggyWalky.jobpost.entity.Status;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class JobPostSpecifications {

    public static Specification<JobPost> withDynamicQuery(String title, Status status, String startPoint) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(title)) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (StringUtils.hasText(startPoint)) {
                predicates.add(criteriaBuilder.equal(root.get("startPoint"), startPoint));
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}