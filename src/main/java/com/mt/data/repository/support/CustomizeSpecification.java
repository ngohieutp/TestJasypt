package com.mt.data.repository.support;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

public interface CustomizeSpecification<T extends Serializable> {

    List<Predicate> toPredicates(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder)
            throws IllegalAccessException;
}
