package com.example.Job_Post.specification;

import com.example.Job_Post.entity.Post;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class PostSpecification {

    public static Specification<Post> filterBySearchQuery(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("title")), pattern),
                cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Post> filterByCategory(String category) {
        return (root, query, cb) -> {
            if (category == null || category.isEmpty() || category.equals("all")) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("jobCategory")), category.toLowerCase());
        };
    }

    public static Specification<Post> filterByPriceRange(Integer minPrice, Integer maxPrice) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Skip if both filters are null
            if ( ((minPrice == null || minPrice < 0) && (maxPrice == null || maxPrice < 0))
                || (minPrice != null && maxPrice != null && minPrice > maxPrice) ){
                return predicate;
            }

            // Predicate list
            Predicate numericPredicate = cb.conjunction();

            // --- Case 1: Salary (numeric field) ---
            if (minPrice != null && minPrice >= 0) {
                numericPredicate = cb.and(numericPredicate,
                    cb.greaterThanOrEqualTo(root.get("salary"), minPrice.doubleValue()));
            }
            if (maxPrice != null && maxPrice >= 0) {
                numericPredicate = cb.and(numericPredicate,
                    cb.lessThanOrEqualTo(root.get("salary"), maxPrice.doubleValue()));
            }

            // --- Case 2: Salary range (string like "2000-3000") ---
            Predicate rangePredicate = cb.conjunction();

            if (minPrice != null && minPrice >= 0 || maxPrice != null && maxPrice >= 0) {
                // handle null or malformed ranges gracefully
                Predicate lowerBound = cb.conjunction();
                Predicate upperBound = cb.conjunction();

                if (minPrice != null && minPrice >= 0) {
                    lowerBound = cb.greaterThanOrEqualTo(root.get("salaryMin"), minPrice.doubleValue());
                }
                if (maxPrice != null && maxPrice >= 0) {
                    upperBound = cb.lessThanOrEqualTo(root.get("salaryMax"), maxPrice.doubleValue());
                }


                rangePredicate = cb.and(lowerBound, upperBound);
            }

            // Combine both salary or salaryRange
            return cb.or(numericPredicate, rangePredicate);


        };
    }


    public static Specification<Post> filterByEmploymentType(String employmentType) {
        return (root, query, cb) -> {
            if (employmentType == null || employmentType.isEmpty()) {
                return cb.conjunction();
            }
            
            // Handle multiple employment types (comma-separated)
            String[] types = employmentType.split(",");
            if (types.length == 1) {
                return cb.equal(cb.lower(root.get("employmentType")), types[0].trim().toLowerCase());
            }
            
            Predicate[] predicates = new Predicate[types.length];
            for (int i = 0; i < types.length; i++) {
                predicates[i] = cb.equal(cb.lower(root.get("employmentType")), types[i].trim().toLowerCase());
            }
            return cb.or(predicates);
        };
    }

    public static Specification<Post> combineFilters(String search, String category, 
                                                      Integer minPrice, Integer maxPrice, 
                                                      String employmentType) {
        return Specification
            .where(filterBySearchQuery(search))
            .and(filterByCategory(category))
            .and(filterByPriceRange(minPrice, maxPrice))
            .and(filterByEmploymentType(employmentType));
    }
}