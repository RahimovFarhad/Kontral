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

            boolean hasMin = (minPrice != null && minPrice >= 0);
            boolean hasMax = (maxPrice != null && maxPrice >= 0);

            // No filtering requested
            if (!hasMin && !hasMax) {
                return cb.conjunction();
            }

            // Invalid range requested
            if (hasMin && hasMax && minPrice > maxPrice) {
                return cb.conjunction();
            }

            Double salary = root.get("salary").toString().isEmpty() ? null : Double.valueOf(root.get("salary").toString());
            Double salaryRangeLower = root.get("salaryRangeLower").toString().isEmpty() ? null : Double.valueOf(root.get("salaryRangeLower").toString());
            Double salaryRangeUpper = root.get("salaryRangeUpper").toString().isEmpty() ? null : Double.valueOf(root.get("salaryRangeUpper").toString());

            if (salary == null && (salaryRangeLower == null || salaryRangeUpper == null)) {
                return cb.conjunction();
            }
            Boolean isFixedSalary = (salary != null);

            // Predicate list
            if (isFixedSalary) {
                // Fixed salary filtering
                if (hasMin) {
                    predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("salary"), minPrice.doubleValue()));
                }
                if (hasMax) {
                    predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("salary"), maxPrice.doubleValue()));
                }
            } else {
                // Salary range filtering
                if (hasMin) {
                    predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("salaryRangeLower"), minPrice.doubleValue()));
                }
                if (hasMax) {
                    predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("salaryRangeUpper"), maxPrice.doubleValue()));
                }
            }

            return predicate;
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