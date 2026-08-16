package com.admissionconsultancy.repository.specification;

import com.admissionconsultancy.entity.University;
import org.springframework.data.jpa.domain.Specification;

public class UniversitySpecification {

    public static Specification<University> hasCountrySlug(String countrySlug) {
        return (root, query, cb) ->
                countrySlug == null ? null : cb.equal(root.get("country").get("slug"), countrySlug);
    }

    public static Specification<University> maxTuitionUsdLessThanOrEqual(Double maxBudget) {
        return (root, query, cb) ->
                maxBudget == null ? null : cb.lessThanOrEqualTo(root.get("minTuitionUsd"), maxBudget);
    }

    public static Specification<University> hasRankingLessThanOrEqual(Integer maxRanking) {
        return (root, query, cb) ->
                maxRanking == null ? null : cb.lessThanOrEqualTo(root.get("ranking"), maxRanking);
    }

    public static Specification<University> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("isActive"));
    }
}