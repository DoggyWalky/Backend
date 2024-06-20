package com.doggyWalky.doggyWalky.jobpost.repository;

import com.doggyWalky.doggyWalky.dog.entity.Dog;
import com.doggyWalky.doggyWalky.dog.entity.DogSize;
import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import com.doggyWalky.doggyWalky.jobpost.entity.Status;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class JobPostSpecifications {

    public static Specification<JobPost> withDynamicQuery(
            String title,
            List<Status> statuses,
            List<DogSize> dogSizes,
            String bcode,
            String sortOption) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
            }

            if (statuses != null && !statuses.isEmpty()) {
                Predicate statusPredicate = criteriaBuilder.disjunction();
                for (Status status : statuses) {
                    statusPredicate = criteriaBuilder.or(statusPredicate, criteriaBuilder.equal(root.get("status"), status));
                }
                predicates.add(statusPredicate);
            }

            Join<JobPost, Dog> dogJoin = root.join("dog", JoinType.LEFT);
            root.fetch("dog", JoinType.LEFT); // Fetch join to avoid N+1 problem

            if (dogSizes != null && !dogSizes.isEmpty()) {
                Predicate dogSizePredicate = criteriaBuilder.disjunction();
                for (DogSize dogSize : dogSizes) {
                    dogSizePredicate = criteriaBuilder.or(dogSizePredicate, criteriaBuilder.equal(dogJoin.get("dogSize"), dogSize));
                }
                predicates.add(dogSizePredicate);
            }
            if (bcode != null && !bcode.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("bcode"), bcode));
            }

            Predicate finalPredicate = criteriaBuilder.and(predicates.toArray(new Predicate[0]));

            if (sortOption != null && !sortOption.isEmpty()) {
                if ("asc".equalsIgnoreCase(sortOption)) {
                    query.orderBy(criteriaBuilder.asc(root.get("createdDate")));
                } else if ("desc".equalsIgnoreCase(sortOption)) {
                    query.orderBy(criteriaBuilder.desc(root.get("createdDate")));
                }
            }

            return finalPredicate;
        };
    }
}