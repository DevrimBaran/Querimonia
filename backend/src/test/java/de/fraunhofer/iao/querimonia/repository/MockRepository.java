package de.fraunhofer.iao.querimonia.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import tec.uom.lib.common.function.Identifiable;

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MockRepository<T extends Identifiable<Long>> implements
    PagingAndSortingRepository<T, Long> {

  private final HashMap<Long, T> mockDatabase = new HashMap<>();
  private long counter = 1;

  @Override
  public <S extends T> S save(S entity) {
    long id = entity.getId();
    if (id <= 0) {
      // new object
      id = counter;
      counter++;
    }
    mockDatabase.put(id, entity);
    return entity;
  }

  @Override
  public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
    return StreamSupport.stream(entities.spliterator(), false)
        .map(this::save)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<T> findById(Long aLong) {
    return Optional.ofNullable(mockDatabase.getOrDefault(aLong, null));
  }

  @Override
  public boolean existsById(Long aLong) {
    return mockDatabase.containsKey(aLong);
  }

  @Override
  public Iterable<T> findAll() {
    return mockDatabase.values();
  }

  @Override
  public Iterable<T> findAllById(Iterable<Long> longs) {
    return StreamSupport.stream(longs.spliterator(), false)
        .map(this::findById)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  @Override
  public long count() {
    return mockDatabase.size();
  }

  @Override
  public void deleteById(Long aLong) {
    mockDatabase.remove(aLong);
  }

  @Override
  public void delete(T entity) {
    mockDatabase.remove(entity.getId(), entity);
  }

  @Override
  public void deleteAll(Iterable<? extends T> entities) {
    entities.forEach(this::delete);
  }

  @Override
  public void deleteAll() {
    mockDatabase.clear();
  }

  @Override
  public Iterable<T> findAll(Sort sort) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public Page<T> findAll(Pageable pageable) {
    throw new RuntimeException("Not implemented");
  }
}