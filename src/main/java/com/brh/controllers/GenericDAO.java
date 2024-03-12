package com.brh.controllers;

import com.brh.entities.bases.RootEntity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * This interface implements the Data Access Object (DAO) design pattern for JPA entities
 * @param <E> E is the type of an entity class as defined by Java Persistence API (JPA)
 * @param <ID> ID is the type of identifiers of entities defined by type E
 * @author Mohamed-B&eacute;cha Ka&acirc;niche &amp; Ibrahim Goddi
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("unused")
public interface GenericDAO<E extends RootEntity<ID>,ID extends java.io.Serializable> {
    /**
     * This method has to be implemented by a concrete DAO
     * and returns the unique instance of {@link EntityManager}4510
     * associated with the concrete DAO.
     * Each concrete DAO has to instantiate its own instance of {@link EntityManager}
     * @return An instance of {@link EntityManager} associated with the concrete DAO
     */
    EntityManager getEntityManager();

    /**
     * This method has to be implemented by a concrete DAO
     * and returns the instance of {@link Class <E>}
     * representing the type of the entities managed by the concrete DAO
     * @return an instance of {@link Class <E>} representing the
     * type of managed entities
     */
    Class<E> getEntityClass();

    /**
     * Check if an identifier exists within the database. This method builds and executes the following
     * SQL query with the Criteria API of the Java Persistence API (JPA):
     * <br/><code>select count(*) from {entity_table_name} where {column_id_name} = id;</code>
     * @param id The identifier of an entity to be checked
     * @return true if an entity associated with the given identifier exists within the database
     */
    default boolean existsById(ID id){
        if(id==null) return false;
        final CriteriaQuery<Long> cq = getEntityManager().getCriteriaBuilder().createQuery(Long.class);
        final Root<E> root = cq.from(getEntityClass());
        cq.select(getEntityManager().getCriteriaBuilder().count(root));
        cq.where(getEntityManager().getCriteriaBuilder().equal(root.get(idColumnName()),id));
        final TypedQuery<Long> query = getEntityManager().createQuery(cq);
        return query.getSingleResult() > 0;
    }

    /**
     * This method is used by {@link #existsById(ID)} to determine
     * the identifier column name of the entities' table.
     * @return the identifier column name.
     */
    default String idColumnName(){
        if(getEntityClass().isAnnotationPresent(AttributeOverrides.class)){
            for(AttributeOverride ao : getEntityClass().getAnnotation(AttributeOverrides.class).value()){
                if(ao.name().equals("id")){
                    return ao.column().name();
                }
            }
        }
        if (getEntityClass().isAnnotationPresent(AttributeOverride.class)) {
            final AttributeOverride ao = getEntityClass().getAnnotation(AttributeOverride.class);

            if(ao.name().equals("id")){
                return ao.column().name();
            }
        }
        return "id";
    }

    /**
     * This method inserts or updates an entity object. It is compatible
     * with the single table inheritance strategy.
     * @param entity the instance to be inserted or updated
     * @param <S> is a subtype of the managed entities' type {@link E}
     *           i.e. a sub-entity type in a single table inheritance strategy
     * @return the inserted or the updated instance of the entity
     */
    @Transactional
    default <S extends E> S save(S entity){
        if(!existsById(entity.getId())){ // Add new entity (INSERT)
            getEntityManager().persist(entity);
            return entity;
        }
        return getEntityManager().merge(entity); // Update existing entity (UPDATE)
    }

    /**
     * This method uses the method {@link #save(S)} to insert or to update a collection or an array of
     * instances of an entity defined by the subtype {@link S}.
     * @param entities a collection or an array of instances of the entity to be inserted or updated
     * @param <S> is a subtype of the managed entities' type {@link E}
     *      i.e. a sub-entity type in a single table inheritance strategy
     * @return a collection or an array of inserted or updated instances of the entity
     */
    @Transactional
    default  <S extends E> Iterable<S> saveAll(Iterable<S> entities){
        entities.forEach(this::save); //The method reference `this::save` is used instead of the lambda `e -> save(e)`. i.e. Apply the method save() on each element of the collection or the Array.
        return entities;
    }

    /**
     * This method updates partially (i.e. some attributes) an instance
     * of the entity with the given id.
     * @param id The identifier of the entity instance to be updated.
     * @param updateFewAttributes The consumer (i.e. lambda expression or method reference) which will update
     *                            partially the associated instance with the given id.
     * @return the updated instance of the entity
     */
    @Transactional
    default E edit(ID id, Consumer<E> updateFewAttributes){
        E entity = getEntityManager().find(getEntityClass(),id);
        if(entity == null){
            throw new IllegalArgumentException("Unknown entity with Id: " + id);
        }
        updateFewAttributes.accept(entity);
        getEntityManager().flush();
        return entity;
    }

    /**
     * This method deletes the given instance of the entity
     * @param entity The instance to be deleted
     */
    @Transactional
    default void delete(E entity){
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    /**
     * This method uses {@link #delete(E)} to delete all elements of the given collection or array.
     * @param entities The collection or array of instances to be deleted
     */
    @Transactional
    default void deleteAll(Iterable<? extends E> entities){
        entities.forEach(this::delete);
    }

    /**
     * This method deletes the entity instance with the given id. This method is safe: it only deletes
     * the entity instance if it exists.
     * @param id The id of the entity instance to be deleted.
     */
    @Transactional
    default void deleteById(ID id){
        findById(id).ifPresent(this::delete);
    }

    /**
     * This method fetches the entity instance associated with the given id.
     * @param id The id of the entity instance to be fetched.
     * @return an optional(i.e. possibly null) of the fetched entity instance.
     */
    default Optional<E> findById(ID id){
        return Optional.ofNullable(getEntityManager().find(getEntityClass(),id));
    }

    default Optional<E> findById(ID id, String entityGraphName){
        EntityGraph<?> entityGraph = getEntityManager().getEntityGraph(entityGraphName);
        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.fetchgraph", entityGraph);
        return Optional.ofNullable(getEntityManager().find(getEntityClass(),id,properties));
    }

    /**
     * This method fetches all the instances of the entity from the database.
     * @return a stream of the entities (i.e. instances of the entity)
     */
    default List<E> findAll(){ //A stream is a sequence of objects that supports various methods which can be pipelined to produce the desired result.
        CriteriaQuery<E> cq = getEntityManager().getCriteriaBuilder().createQuery(getEntityClass());
        cq.select(cq.from(getEntityClass()));
        return getEntityManager().createQuery(cq).getResultList();
    }

    /**
     * This method fetches all the instances of the entity from the database.
     * @return a stream of the entities (i.e. instances of the entity)
     */
    default List<E> findAll(String entityGraphName){ //A stream is a sequence of objects that supports various methods which can be pipelined to produce the desired result.
        CriteriaQuery<E> cq = getEntityManager().getCriteriaBuilder().createQuery(getEntityClass());
        cq.select(cq.from(getEntityClass()));
        EntityGraph<?> entityGraph = getEntityManager().getEntityGraph(entityGraphName);
        return getEntityManager().createQuery(cq).setHint("jakarta.persistence.loadgraph", entityGraph).getResultList();
    }

    /**
     * This method fetches all the instances of the entity in the given range.
     * This method is useful when we want to retrieve instances of the entity by ranges (e.g. when displaying
     * instances by pages in a web based GUI).
     * @param first The first element index in the range
     * @param last The last element index in the range
     * @return the instances of the entity in the given range
     */
    default Stream<E> findRange(int first,int last){
        CriteriaQuery<E> cq = getEntityManager().getCriteriaBuilder().createQuery(getEntityClass());
        cq.select(cq.from(getEntityClass()));
        TypedQuery<E> q = getEntityManager().createQuery(cq);
        q.setMaxResults(last - first + 1);
        q.setFirstResult(first);
        return q.getResultStream();
    }

    /**
     * This method counts the number of instances of the entity in the database.
     * @return the number of instances of the entity in the database
     */
    default long count(){
        final CriteriaQuery<Long> cq = getEntityManager().getCriteriaBuilder().createQuery(Long.class);
        Root<E> root = cq.from(getEntityClass());
        cq.select(getEntityManager().getCriteriaBuilder().count(root));
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult();
    }
}
