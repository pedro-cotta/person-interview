package com.example.Person.repository;

import com.example.Person.model.Person;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class PersonRepository {

    private final Map<Long, Person> people = new HashMap<>();

    @PostConstruct
    public void init() {
        people.put(1L, new Person(1L, "José da Silva", LocalDate.of(2000, 4, 6), LocalDate.of(2020, 5, 10)));
        people.put(2L, new Person(2L, "Maria Oliveira", LocalDate.of(1995, 6, 15), LocalDate.of(2018, 7, 20)));
        people.put(3L, new Person(3L, "Carlos Pereira", LocalDate.of(1990, 1, 25), LocalDate.of(2015, 3, 30)));
    }

    public List<Person> findAll() {
        return people.values().stream()
                .sorted(Comparator.comparing(Person::getName))
                .collect(Collectors.toList());
    }

    public Person findById(Long id) {
        return people.get(id);
    }

    public void save(Person person) {
        people.put(person.getId(), person);
    }

    public void delete(Long id) {
        people.remove(id);
    }

    public boolean existsById(Long id) {
        return people.containsKey(id);
    }

    public Long generateId() {
        return people.keySet().stream().max(Long::compare).orElse(0L) + 1;
    }
}
