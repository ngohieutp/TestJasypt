package com.mt.data.repository.impl;

import com.mt.data.Constants;
import com.mt.data.Pageable;
import com.mt.data.PagedResult;
import com.mt.data.Sortable;
import com.mt.data.enums.Direction;
import com.mt.data.repository.GenericRepository;
import com.mt.data.repository.support.CustomizeSpecification;
import com.mt.data.repository.support.EntityGraphOption;
import com.mt.data.repository.support.EntityGraphType;
import com.mt.data.repository.support.NormalFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class GenericRepositoryImpl<E extends Serializable, K extends Serializable> implements GenericRepository<E, K> {

    private final static Logger LOGGER = LoggerFactory.getLogger(GenericRepositoryImpl.class);

    private final Class<E> type;

    @PersistenceContext
    EntityManager em;

    public GenericRepositoryImpl(Class<E> type) {
        this.type = type;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public E find(K key) {
        return em.find(type, key);
    }

    @Override
    public E find(CustomizeSpecification<E> spec) throws IllegalAccessException {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(type);
        Root<E> root = applySpecificationToCriteria(spec, query);
        query.select(root);
        TypedQuery<E> selectQuery = em.createQuery(query);
        try {
            return selectQuery.getSingleResult();
        } catch (NoResultException ex) {
            LOGGER.debug("ERROR", ex);
            return null;
        }
    }

    @Override
    public E find(K key, EntityGraphOption entityGraphOption) {
        Map<String, Object> hints = new HashMap<>();
        EntityGraph graph = this.em.getEntityGraph(entityGraphOption.getName());
        if (entityGraphOption.getEntityGraphType() == EntityGraphType.FETCH)
            hints.put("javax.persistence.fetchgraph", graph);
        else hints.put("javax.persistence.loadgraph", graph);
        return em.find(type, key, hints);
    }

    @Override
    public void create(E entity) {
        this.create(entity, false);
    }

    @Override
    public void create(E entity, boolean flush) {
        em.persist(entity);
        if (flush) {
            em.flush();
        }
    }

    @Override
    public void createAll(List<E> entityList) {
        for (E entity : entityList) {
            this.create(entity);
        }
    }

    @Override
    public E update(E entity) {
        return em.merge(entity);
    }

    @Override
    public E update(E entity, boolean flush) {
        E e = em.merge(entity);
        if (flush) {
            em.flush();
        }
        return e;
    }

    @Override
    public void deleteById(K id) {
        delete(find(id));
    }

    @Override
    public void delete(E entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    @Override
    public void deleteAll(List<E> entityList) {
        for (E entity : entityList) {
            this.delete(entity);
        }
    }

    @Override
    public int erase() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaDelete<E> query = builder.createCriteriaDelete(type);
        query.from(type);
        return em.createQuery(query).executeUpdate();
    }

    @Override
    public void flush() {
        getEntityManager().flush();
        getEntityManager().clear();
    }

    @Override
    public long count(CustomizeSpecification<E> spec) throws IllegalAccessException {
        return count(spec, false);
    }

    @Override
    public long count(CustomizeSpecification<E> spec, boolean distinct) throws IllegalAccessException {
        TypedQuery<Long> countQuery = getCountQuery(spec, distinct);
        return countQuery.getSingleResult();
    }

    @Override
    public List<E> findAll() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(type);
        Root<E> root = query.from(type);
        query.select(root);
        TypedQuery<E> selectQuery = em.createQuery(query);
        return selectQuery.getResultList();
    }

    @Override
    public List<E> findAll(Sortable sortable) throws IllegalAccessException {
        return findAll(sortable, null);
    }

    @Override
    public List<E> findAll(CustomizeSpecification<E> spec) throws IllegalAccessException {
        return this.findAll(null, spec);
    }

    @Override
    public E findFirst(Sortable sortable, CustomizeSpecification<E> spec) throws IllegalAccessException {
        List<E> eList = this.findAll(sortable, spec, 1, null);
        return eList.isEmpty() ? null : eList.get(0);
    }

    @Override
    public List<E> findAll(Sortable sortable, CustomizeSpecification<E> spec, Integer top) throws IllegalAccessException {
        return this.findAll(sortable, spec, top, null);
    }

    @Override
    public List<E> findAll(Sortable sortable, CustomizeSpecification<E> spec, Integer top, EntityGraphOption entityGraphOption) throws IllegalAccessException {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(type);

        Root<E> root = applySpecificationToCriteria(spec, query);
        query.select(root);

        if (sortable != null && sortable.canSort()) {
            Order order;
            if (sortable.getDirection() == Direction.ASC) {
                order = builder.asc(root.get(sortable.getField()));
            } else {
                order = builder.desc(root.get(sortable.getField()));
            }
            query.orderBy(order);
        }

        TypedQuery<E> selectQuery = em.createQuery(query);
        if (top != null) {
            selectQuery.setMaxResults(top);
        }

        if (entityGraphOption != null) {
            EntityGraph graph = this.em.getEntityGraph(entityGraphOption.getName());
            if (entityGraphOption.getEntityGraphType() == EntityGraphType.FETCH)
                selectQuery.setHint("javax.persistence.fetchgraph", graph);
            else selectQuery.setHint("javax.persistence.loadgraph", graph);
        }
        return selectQuery.getResultList();

    }

    @Override
    public List<E> findAll(Sortable sortable, CustomizeSpecification<E> spec) throws IllegalAccessException {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(type);

        Root<E> root = applySpecificationToCriteria(spec, query);
        query.select(root);

        if (sortable != null && sortable.canSort()) {
            Order order;
            if (sortable.getDirection() == Direction.ASC) {
                order = builder.asc(root.get(sortable.getField()));
            } else {
                order = builder.desc(root.get(sortable.getField()));
            }
            query.orderBy(order);
        }

        TypedQuery<E> selectQuery = em.createQuery(query);
        return selectQuery.getResultList();
    }

    @Override
    public List<E> getAll(List<Sortable> sortables, CustomizeSpecification<E> spec, EntityGraphOption entityGraphOption) throws IllegalAccessException {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(type);

        Root<E> root = applySpecificationToCriteria(spec, query);
        query.select(root);

        if (sortables != null) {
            Map<String, Join> mapPath = new HashMap<>();
            if (spec instanceof NormalFilter && spec != null) {
                mapPath = ((NormalFilter) spec).getMapPathTempl();
            }
            List<Order> orderBies = new ArrayList<>();
            for (Sortable f : sortables) {
                Order order = null;
                Expression expression;
                String tableNames = f.getTableName();
                String field = f.getField();
                if (StringUtils.isBlank(tableNames)) {
                    expression = root.get(field);
                } else {
                    String[] paths = tableNames.split("\\.");
                    Join joinPath = null;
                    for (String p : paths) {
                        if (mapPath.get(p) == null) {
                            joinPath = joinPath == null ? root.join(p) : joinPath.join(p);
                            mapPath.put(p, joinPath);
                        } else {
                            joinPath = mapPath.get(p);
                        }
                    }
                    expression = joinPath.get(field);
                }
                if (StringUtils.isNotBlank(f.getField()) && (StringUtils.isNotBlank(tableNames) || FieldUtils.getField(type, f.getField(), true) != null)) {
                    if (f.getDirection() == Direction.ASC) {
                        order = builder.asc(expression);
                    } else {
                        order = builder.desc(expression);
                    }
                }
                orderBies.add(order);
            }
            query.orderBy(orderBies);
        }

        TypedQuery<E> selectQuery = em.createQuery(query);
        if (entityGraphOption != null) {
            EntityGraph graph = this.em.getEntityGraph(entityGraphOption.getName());
            if (entityGraphOption.getEntityGraphType() == EntityGraphType.FETCH)
                selectQuery.setHint("javax.persistence.fetchgraph", graph);
            else selectQuery.setHint("javax.persistence.loadgraph", graph);
        }

        return selectQuery.getResultList();
    }

    @Override
    public List<E> getAll(List<Sortable> sortables, CustomizeSpecification<E> spec) throws IllegalAccessException {
        return this.getAll(sortables, spec, null);
    }

    @Override
    public List<E> findAll(Sortable sortable, CustomizeSpecification<E> spec, EntityGraphOption entityGraphOption) throws IllegalAccessException {
        return findAll(sortable, spec, entityGraphOption, false);
    }

    @Override
    public List<E> findAll(Sortable sortable, CustomizeSpecification<E> spec, EntityGraphOption entityGraphOption, boolean distinct) throws IllegalAccessException {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(type);
        query.distinct(distinct);

        Root<E> root = applySpecificationToCriteria(spec, query);
        query.select(root);

        if (sortable != null && sortable.canSort()) {
            Order order;
            if (sortable.getDirection() == Direction.ASC) {
                order = builder.asc(root.get(sortable.getField()));
            } else {
                order = builder.desc(root.get(sortable.getField()));
            }
            query.orderBy(order);
        }

        TypedQuery<E> selectQuery = em.createQuery(query);
        return selectQuery.getResultList();
    }

    @Override
    public <T> List<T> getOneColumn(String field, Class<T> columnDataType) throws IllegalAccessException {
        return getOneColumn(field, columnDataType, null);
    }

    @Override
    public <T> List<T> getOneColumn(String field, Class<T> columnDataType, CustomizeSpecification<E> spec) throws IllegalAccessException {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(columnDataType);

        Root<E> root = applySpecificationToCriteria(spec, query);
        query.select(root.get(field));

        TypedQuery<T> selectQuery = em.createQuery(query);

        return selectQuery.getResultList();
    }

    @Override
    public E findOneByField(String field, Object value) {
        return this.findOneByField(field, value, false);
    }

    @Override
    public E findOneByField(String field, Object value, boolean ignoreCase) {
        return this.findOneByField(field, value, ignoreCase, null);
    }

    @Override
    public E findOneByField(String field, Object value, boolean ignoreCase, EntityGraphOption entityGraphOption) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(type);

        Root<E> root = query.from(type);

        if (root.get(field).getJavaType() == String.class && ignoreCase) {
            query.where(builder.equal(builder.lower(root.get(field)), value != null ? ((String) value).toLowerCase() : null));
        } else {
            query.where(builder.equal(root.get(field), value));
        }

        query.select(root);

        TypedQuery<E> selectQuery = em.createQuery(query);

        if (entityGraphOption != null) {
            EntityGraph graph = this.em.getEntityGraph(entityGraphOption.getName());
            if (entityGraphOption.getEntityGraphType() == EntityGraphType.FETCH)
                selectQuery.setHint("javax.persistence.fetchgraph", graph);
            else selectQuery.setHint("javax.persistence.loadgraph", graph);
        }

        try {
            return selectQuery.getSingleResult();
        } catch (NoResultException ex) {
            LOGGER.debug("ERROR", ex);
            return null;
        }
    }

    @Override
    public List<E> findManyByField(String field, Collection<?> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(type);
        Root<E> root = query.from(type);
        query.where(root.get(field).in(list));
        query.select(root);
        TypedQuery<E> selectQuery = em.createQuery(query);
        return selectQuery.getResultList();
    }

    @Override
    public List<E> findInList(List<K> keyList) {
        if (keyList == null || keyList.isEmpty()) {
            return new ArrayList<>();
        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(type);
        Root<E> root = query.from(type);
        query.where(root.get(getEntityId()).in(keyList));
        query.select(root);

        TypedQuery<E> selectQuery = em.createQuery(query);

        return selectQuery.getResultList();
    }

    @Override
    public List getResultListFromQuery(String jpaQuery) {
        return getResultListFromQuery(jpaQuery, new HashMap<>());
    }

    @Override
    public List getResultListFromQuery(String jpaQuery, Map<String, Object> map) {
        Query query = em.createQuery(jpaQuery);

        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }

        return query.getResultList();
    }

    @Override
    public List getResultListFromQuery(String jpaQuery, Object... params) {
        Query query = em.createQuery(jpaQuery);

        if (params.length > 0) {
            for (int i = 1; i <= params.length; i++) {
                query.setParameter(i, params[i - 1]);
            }
        }

        return query.getResultList();
    }

    @Override
    public List getResultListFromQuery(String jpaQuery, String key, Object value) {
        Query query = em.createQuery(jpaQuery);
        query.setParameter(key, value);
        return query.getResultList();
    }

    @Override
    public E getSingleResultFromQuery(String jpaQuery) {
        return getSingleResultFromQuery(jpaQuery, new HashMap<>());
    }

    @Override
    public E getSingleResultFromQuery(String jpaQuery, Map<String, Object> map) {
        TypedQuery<E> query = em.createQuery(jpaQuery, type);

        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }

        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            LOGGER.debug("ERROR", ex);
            return null;
        }
    }

    @Override
    public E getSingleResultFromQuery(String jpaQuery, Object... params) {
        TypedQuery<E> query = em.createQuery(jpaQuery, type);

        if (params.length > 0) {
            for (int i = 1; i <= params.length; i++) {
                query.setParameter(i, params[i - 1]);
            }
        }

        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            LOGGER.debug("ERROR", ex);
            return null;
        }
    }

    @Override
    public E getSingleResultFromQuery(String jpaQuery, String key, Object value) {
        TypedQuery<E> query = em.createQuery(jpaQuery, type);
        query.setParameter(key, value);
        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            ex.printStackTrace();
            LOGGER.debug("ERROR", ex);
            return null;
        }
    }

    @Override
    public E getSingleResultFromNamedQuery(String namedQuery, String key, Object value) {
        TypedQuery<E> query = em.createNamedQuery(namedQuery, type);
        query.setParameter(key, value);
        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            LOGGER.debug("ERROR", ex);
            return null;
        }
    }

    @Override
    public int executeUpdate(String jpaQuery) {
        return executeUpdate(jpaQuery, new HashMap<>());
    }

    @Override
    public int executeUpdate(String jpaQuery, Map<String, Object> map) {
        Query query = em.createQuery(jpaQuery);

        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }

        return query.executeUpdate();
    }

    @Override
    public int executeUpdate(String jpaQuery, Object... params) {
        Query query = em.createQuery(jpaQuery);

        if (params.length > 0) {
            for (int i = 1; i <= params.length; i++) {
                query.setParameter(i, params[i - 1]);
            }
        }

        return query.executeUpdate();
    }

    @Override
    public int executeUpdate(String jpaQuery, String key, Object value) {
        Query query = em.createQuery(jpaQuery);
        query.setParameter(key, value);
        return query.executeUpdate();
    }

    @Override
    public PagedResult page(Pageable pageable) throws IllegalAccessException {
        return page(pageable, null);
    }

    @Override
    public PagedResult<E> page(Pageable pageable, CustomizeSpecification<E> spec) throws IllegalAccessException {
        return this.page(pageable, spec, null);
    }

    @Override
    public PagedResult<E> page(Pageable pageable, CustomizeSpecification<E> spec, EntityGraphOption entityGraphOption) throws IllegalAccessException {
        return this.page(pageable, spec, entityGraphOption, false);
    }

    @Override
    public PagedResult<E> page(Pageable pageable, CustomizeSpecification<E> spec, EntityGraphOption entityGraphOption, boolean distinct) throws IllegalAccessException {
        if (pageable.getPageIndex() < Constants.PAGING_INDEX) pageable.setPageIndex(Constants.PAGING_INDEX);

        if (pageable.getPageSize() <= 0) pageable.setPageSize(Constants.PAGING_SIZE);

        PagedResult<E> pagedResult = new PagedResult<E>();
        pagedResult.setPageIndex(pageable.getPageIndex());
        pagedResult.setPageSize(pageable.getPageSize());

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(type);
        query.distinct(distinct);

        Root<E> root = applySpecificationToCriteria(spec, query);
        query.select(root);

        if (pageable.getSort() != null) {
            Sortable sortable = pageable.getSort();
            Order order;
            if (StringUtils.isNotBlank(sortable.getField()) && FieldUtils.getField(type, sortable.getField(), true) != null) {
                if (sortable.getDirection() == Direction.ASC) {
                    order = builder.asc(root.get(sortable.getField()));
                } else {
                    order = builder.desc(root.get(sortable.getField()));
                }
                query.orderBy(order);
            }
        }

        if (pageable.getSortables() != null) {
            Map<String, Join> mapPath = new HashMap<>();
            List<Order> orderBies = pageable.getSortables().stream().map(f -> {
                Order order = null;
                Expression expression;
                String tableNames = f.getTableName();
                String field = f.getField();
                if (StringUtils.isBlank(tableNames)) {
                    expression = root.get(field);
                } else {
                    String[] paths = tableNames.split("\\.");
                    Join joinPath = null;
                    for (String p : paths) {
                        if (mapPath.get(p) == null) {
                            joinPath = joinPath == null ? root.join(p) : joinPath.join(p);
                            mapPath.put(p, joinPath);
                        } else {
                            joinPath = mapPath.get(p);
                        }
                    }
                    expression = joinPath.get(field);
                }
                if (StringUtils.isNotBlank(f.getField()) && (StringUtils.isNotBlank(tableNames) || FieldUtils.getField(type, f.getField(), true) != null)) {
                    if (f.getDirection() == Direction.ASC) {
                        order = builder.asc(expression);
                    } else {
                        order = builder.desc(expression);
                    }
                }
                return order;
            }).collect(Collectors.toList());
            query.orderBy(orderBies);
        }

        TypedQuery<E> selectQuery = em.createQuery(query);

        if (entityGraphOption != null) {
            EntityGraph graph = this.em.getEntityGraph(entityGraphOption.getName());
            if (entityGraphOption.getEntityGraphType() == EntityGraphType.FETCH)
                selectQuery.setHint("javax.persistence.fetchgraph", graph);
            else selectQuery.setHint("javax.persistence.loadgraph", graph);
        }

        selectQuery.setFirstResult((pageable.getPageIndex() - 1) * pageable.getPageSize());
        selectQuery.setMaxResults(pageable.getPageSize());
        pagedResult.setData(selectQuery.getResultList());

        TypedQuery<Long> countQuery = getCountQuery(spec, distinct);
        pagedResult.setTotal(countQuery.getSingleResult());

        return pagedResult;
    }

    @Override
    public PagedResult<E> page(String selectQuery, String fromAndWhereQuery, String orderByQuery, Map<String, Object> parameters, Pageable pageable) {
        return page(selectQuery, fromAndWhereQuery, orderByQuery, "Select Count(*)", parameters, pageable, null);
    }

    @Override
    public PagedResult<E> page(String selectQuery, String fromAndWhereQuery, String orderByQuery, String countQuery, Map<String, Object> parameters, Pageable pageable) {
        return page(selectQuery, fromAndWhereQuery, orderByQuery, countQuery, parameters, pageable, null);
    }

    @Override
    public PagedResult<E> page(String selectQuery, String fromAndWhereQuery, String orderByQuery, String countQuery, Map<String, Object> parameters, Pageable pageable, EntityGraphOption entityGraphOption) {
        if (pageable.getPageIndex() < Constants.PAGING_INDEX) pageable.setPageIndex(Constants.PAGING_INDEX);

        if (pageable.getPageSize() <= 0) pageable.setPageSize(Constants.PAGING_SIZE);

        PagedResult<E> pagedResult = new PagedResult<>();
        pagedResult.setPageIndex(pageable.getPageIndex());
        pagedResult.setPageSize(pageable.getPageSize());

        TypedQuery<E> query = em.createQuery(selectQuery + fromAndWhereQuery + orderByQuery, type);
        if (entityGraphOption != null) {
            EntityGraph graph = this.em.getEntityGraph(entityGraphOption.getName());
            if (entityGraphOption.getEntityGraphType() == EntityGraphType.FETCH)
                query.setHint("javax.persistence.fetchgraph", graph);
            else query.setHint("javax.persistence.loadgraph", graph);
        }
        addParameters(query, parameters);

        query.setFirstResult((pageable.getPageIndex() - 1) * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());
        pagedResult.setData(query.getResultList());

        Query queryCount = em.createQuery(countQuery + fromAndWhereQuery);
        addParameters(queryCount, parameters);
        pagedResult.setTotal(((Number) queryCount.getSingleResult()).intValue());

        return pagedResult;
    }

    @Override
    public PagedResult<?> pageNative(String selectQuery, String fromAndWhereQuery, String orderByQuery, List<Object> parameters, Pageable pageable, String resultSetMapping) {
        if (pageable.getPageIndex() < Constants.PAGING_INDEX) pageable.setPageIndex(Constants.PAGING_INDEX);

        if (pageable.getPageSize() <= 0) pageable.setPageSize(Constants.PAGING_SIZE);

        PagedResult<?> pagedResult = new PagedResult<>();
        pagedResult.setPageIndex(pageable.getPageIndex());
        pagedResult.setPageSize(pageable.getPageSize());

        Query query = em.createNativeQuery(selectQuery + fromAndWhereQuery + orderByQuery, resultSetMapping);
        addParameters(query, parameters);

        query.setFirstResult((pageable.getPageIndex() - 1) * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());
        pagedResult.setData(query.getResultList());

        Query queryCount = em.createNativeQuery("Select Count(*) " + fromAndWhereQuery);
        addParameters(queryCount, parameters);
        pagedResult.setTotal(((Number) queryCount.getSingleResult()).intValue());

        return pagedResult;
    }

    @Override
    public List<?> getAllNative(String selectQuery, String fromAndWhereQuery, String orderByQuery, List<Object> parameters, String resultSetMapping) {
        Query query = em.createNativeQuery(selectQuery + fromAndWhereQuery + orderByQuery, resultSetMapping);
        addParameters(query, parameters);
        return query.getResultList();
    }

    @Override
    public List findAllNative(String queryStr, List<Object> parameters, String resultSetMapping) {
        Query query = em.createNativeQuery(queryStr, resultSetMapping);
        addParameters(query, parameters);
        return query.getResultList();
    }

    @Override
    public boolean checkExists(String jpaQuery, Object... params) {
        Query query = em.createQuery(jpaQuery);

        if (params.length > 0) {
            for (int i = 1; i <= params.length; i++) {
                query.setParameter(i, params[i - 1]);
            }
        }

        query.setMaxResults(1);

        return !query.getResultList().isEmpty();
    }

    @Override
    public boolean checkExistField(String field, Object value) {
        return this.checkExistField(field, value, false);
    }

    @Override
    public boolean checkExistField(String field, Object value, boolean ignoreCase) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<E> root = query.from(type);

        if (root.get(field).getJavaType() == String.class && ignoreCase) {
            query.where(builder.equal(builder.lower(root.get(field)), value != null ? ((String) value).toLowerCase() : null));
        } else {
            query.where(builder.equal(root.get(field), value));
        }

        query.select(builder.count(root));

        TypedQuery<Long> countQuery = em.createQuery(query);

        return countQuery.getSingleResult() > 0;
    }

    @Override
    public <T> boolean isDuplicate(String field, T value) {
        return isDuplicate(field, value, null, true);
    }

    @Override
    public <T> boolean isDuplicate(String field, T value, K key) {
        return isDuplicate(field, value, key, true);
    }

    @Override
    public <T> boolean isDuplicate(String field, T value, K key, boolean ignoreCase) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery(value.getClass());

        Root<E> root = query.from(type);

        Predicate[] predicateArr = new Predicate[key != null ? 2 : 1];

        if (root.get(field).getJavaType() == String.class && ignoreCase) {
            predicateArr[0] = builder.equal(builder.lower(root.get(field)), value != null ? ((String) value).toLowerCase() : null);
        } else {
            predicateArr[0] = builder.equal(root.get(field), value);
        }

        if (key != null) {
            predicateArr[1] = builder.notEqual(root.get(getEntityId()), key);
        }

        query.where(predicateArr);

        query.select(root.get(field));

        Query selectQuery = em.createQuery(query);

        selectQuery.setMaxResults(1);

        return !selectQuery.getResultList().isEmpty();
    }

    protected TypedQuery<Long> getCountQuery(CustomizeSpecification<E> spec, boolean distinct) throws IllegalAccessException {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<E> root = applySpecificationToCriteria(spec, query);

        if (distinct) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        // Remove all Orders the Specifications might have applied
        query.orderBy(Collections.emptyList());

        return em.createQuery(query);
    }

    public Root<E> applySpecificationToCriteria(CustomizeSpecification<E> spec, CriteriaQuery<?> query) throws IllegalAccessException {
        Root<E> root = query.from(type);

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        List<Predicate> predicates = spec.toPredicates(root, query, builder);
        if (predicates != null && predicates.size() > 0) {
            query.where(predicates.toArray(new Predicate[]{}));
        }

        return root;
    }

    public void addParameters(final Query query, final Map<String, Object> parameters) {
        if (null != parameters && !parameters.isEmpty()) {
            for (final Map.Entry<String, Object> entry : parameters.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
    }

    public void addParameters(final Query query, final List<Object> parameters) {
        if (parameters != null && !parameters.isEmpty()) {
            for (int i = 0; i < parameters.size(); i++) {
                query.setParameter(i + 1, parameters.get(i));
            }
        }
    }

    public List<E> insert(List<E> listEntities, int batchSize) {
        if (listEntities != null && !listEntities.isEmpty()) {
            for (int i = 0; i < listEntities.size(); i++) {
                create(listEntities.get(i));
                if (i % batchSize == 0) {
                    getEntityManager().flush();
                    getEntityManager().clear();
                }
            }
        }
        return listEntities;
    }

    public List<E> update(List<E> listEntities, int batchSize) {
        if (listEntities != null && !listEntities.isEmpty()) {
            for (int i = 0; i < listEntities.size(); i++) {
                update(listEntities.get(i));
                if (i % batchSize == 0) {
                    getEntityManager().flush();
                    getEntityManager().clear();
                }
            }
        }
        return listEntities;
    }

    private SingularAttribute getEntityId() {
        EntityType<E> entityType = em.getMetamodel().entity(type);
        if (!entityType.hasSingleIdAttribute()) {
            throw new RuntimeException("Entity " + type.getName() + " is not defined @Id attribute");
        }
        return entityType.getId(entityType.getIdType().getJavaType());
    }

    @Override
    public Class<E> getEntityClass() {
        return type;
    }
}
