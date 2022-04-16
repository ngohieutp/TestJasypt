package com.mt.data.repository;


import com.mt.data.Sortable;
import com.mt.data.repository.support.CustomizeSpecification;
import com.mt.data.repository.support.EntityGraphOption;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface GenericRepository<E extends Serializable, K extends Serializable> extends PagingAndSortingRepository<E> {

    EntityManager getEntityManager();

    E find(K key);

    E find(CustomizeSpecification<E> spec) throws IllegalAccessException;

    E find(K key, EntityGraphOption entityGraphOption);

    E findOneByField(String field, Object value);

    E findOneByField(String field, Object value, boolean ignoreCase);

    E findOneByField(String field, Object value, boolean ignoreCase, EntityGraphOption entityGraphOption);

    List<E> findManyByField(String field, Collection<?> list);

    List<E> findInList(List<K> keyList);

    void create(E entity);

    void createAll(List<E> entityList);

    void create(E entity, boolean flush);

    E update(E entity);

    E update(E entity, boolean flush);

    void deleteById(K id);

    void delete(E entity);

    void deleteAll(List<E> entityList);

    int erase();

    void flush();

    long count(CustomizeSpecification<E> spec) throws IllegalAccessException;

    long count(CustomizeSpecification<E> spec, boolean distinct) throws IllegalAccessException;

    List<E> findAll();

    List<E> findAll(Sortable sortable) throws IllegalAccessException;

    List<E> findAll(CustomizeSpecification<E> spec) throws IllegalAccessException;

    List<E> findAll(Sortable sortable, CustomizeSpecification<E> spec) throws IllegalAccessException;

    E findFirst(Sortable sortable, CustomizeSpecification<E> spec) throws IllegalAccessException;

    List<E> findAll(Sortable sortable, CustomizeSpecification<E> spec, Integer top) throws IllegalAccessException;

    List<E> findAll(Sortable sortable, CustomizeSpecification<E> spec, Integer top, EntityGraphOption entityGraphOption) throws IllegalAccessException;

    List<E> findAll(Sortable sortable, CustomizeSpecification<E> spec, EntityGraphOption entityGraphOption) throws IllegalAccessException;

    List<E> findAll(Sortable sortable, CustomizeSpecification<E> spec, EntityGraphOption entityGraphOption, boolean distinct) throws IllegalAccessException;

    List<E> getAll(List<Sortable> sortables, CustomizeSpecification<E> spec) throws IllegalAccessException;

    List<E> getAll(List<Sortable> sortables, CustomizeSpecification<E> spec, EntityGraphOption entityGraphOption) throws IllegalAccessException;

    <T> List<T> getOneColumn(String field, Class<T> columnDataType) throws IllegalAccessException;

    <T> List<T> getOneColumn(String field, Class<T> columnDataType, CustomizeSpecification<E> spec) throws IllegalAccessException;

    List findAllNative(String queryStr, List<Object> parameters, String resultSetMapping);

    List getResultListFromQuery(String jpaQuery);

    List getResultListFromQuery(String jpaQuery, Map<String, Object> map);

    List getResultListFromQuery(String jpaQuery, String key, Object value);

    List getResultListFromQuery(String jpaQuery, Object... params);

    E getSingleResultFromQuery(String jpaQuery);

    E getSingleResultFromQuery(String jpaQuery, Map<String, Object> map);

    E getSingleResultFromQuery(String jpaQuery, Object... params);

    E getSingleResultFromQuery(String jpaQuery, String key, Object value);

    E getSingleResultFromNamedQuery(String namedQuery, String key, Object value);

    int executeUpdate(String jpaQuery);

    int executeUpdate(String jpaQuery, Map<String, Object> map);

    int executeUpdate(String jpaQuery, Object... params);

    int executeUpdate(String jpaQuery, String key, Object value);

    boolean checkExists(String jpaQuery, Object... params);

    boolean checkExistField(String field, Object value);

    boolean checkExistField(String field, Object value, boolean ignoreCase);

    <T> boolean isDuplicate(String field, T value);

    <T> boolean isDuplicate(String field, T value, K key);

    <T> boolean isDuplicate(String field, T value, K key, boolean ignoreCase);

    Class<E> getEntityClass();
}
