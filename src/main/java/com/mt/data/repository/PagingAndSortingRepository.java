package com.mt.data.repository;


import com.mt.data.Pageable;
import com.mt.data.PagedResult;
import com.mt.data.repository.support.CustomizeSpecification;
import com.mt.data.repository.support.EntityGraphOption;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface PagingAndSortingRepository<E extends Serializable> {

    PagedResult page(Pageable pageable) throws IllegalAccessException;

    PagedResult<E> page(Pageable pageable, CustomizeSpecification<E> spec) throws IllegalAccessException;

    PagedResult<E> page(Pageable pageable, CustomizeSpecification<E> spec, EntityGraphOption entityGraphOption) throws IllegalAccessException;

    PagedResult<E> page(Pageable pageable, CustomizeSpecification<E> spec, EntityGraphOption entityGraphOption, boolean distinct) throws IllegalAccessException;

    PagedResult<E> page(String selectQuery, String fromAndWhereQuery, String orderByQuery, Map<String, Object> parameters, Pageable pageable);

    PagedResult<E> page(String selectQuery, String fromAndWhereQuery, String orderByQuery, String countQuery, Map<String, Object> parameters, Pageable pageable);

    PagedResult<E> page(String selectQuery, String fromAndWhereQuery, String orderByQuery, String countQuery, Map<String, Object> parameters, Pageable pageable, EntityGraphOption entityGraphOption);

    PagedResult<?> pageNative(String selectQuery, String fromAndWhereQuery, String orderByQuery, List<Object> parameters, Pageable pageable, String resultSetMapping);

    List<?> getAllNative(String selectQuery, String fromAndWhereQuery, String orderByQuery, List<Object> parameters, String resultSetMapping);

}
