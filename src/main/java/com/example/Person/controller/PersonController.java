package com.example.Person.controller;

import com.example.Person.model.Person;
import com.example.Person.repository.PersonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Person> addPerson(@RequestBody Person person) {
        if (person.getId() == null) {
            person.setId(personRepository.generateId());
        } else if (personRepository.existsById(person.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        personRepository.save(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(person);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        if (!personRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        personRepository.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable Long id, @RequestBody Person updatedPerson) {
        if (!personRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        updatedPerson.setId(id);
        personRepository.save(updatedPerson);
        return ResponseEntity.ok(updatedPerson);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Person> patchPerson(@PathVariable Long id, @RequestBody Person partialPerson) {
        Person existingPerson = personRepository.findById(id);
        if (existingPerson == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (partialPerson.getName() != null) existingPerson.setName(partialPerson.getName());
        if (partialPerson.getBirthDate() != null) existingPerson.setBirthDate(partialPerson.getBirthDate());
        if (partialPerson.getAdmissionDate() != null) existingPerson.setAdmissionDate(partialPerson.getAdmissionDate());

        personRepository.save(existingPerson);
        return ResponseEntity.ok(existingPerson);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        Person person = personRepository.findById(id);
        if (person == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(person);
    }

    @GetMapping("/{id}/age")
    public ResponseEntity<?> getAge(@PathVariable Long id, @RequestParam String output) {
        Person person = personRepository.findById(id);
        if (person == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Period period = Period.between(person.getBirthDate(), LocalDate.now());
        int age;
        switch (output) {
            case "days":
                age = period.getYears() * 365 + period.getMonths() * 30 + period.getDays();
                break;
            case "months":
                age = period.getYears() * 12 + period.getMonths();
                break;
            case "years":
                age = period.getYears();
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Formato de saída inválido");
        }

        return ResponseEntity.ok(age);
    }

    @GetMapping("/{id}/salary")
    public ResponseEntity<?> getSalary(@PathVariable Long id, @RequestParam String output) {
        Person person = personRepository.findById(id);
        if (person == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        LocalDate currentDate = LocalDate.now();
        int yearsWorked = Period.between(person.getAdmissionDate(), currentDate).getYears();
        double salary = 1558.00 * Math.pow(1.18, yearsWorked) + 500 * yearsWorked;
        double minSalaryEquivalent = salary / 1302.00;

        double result;
        switch (output) {
            case "full":
                result = salary;
                break;
            case "min":
                result = minSalaryEquivalent;
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Formato de saída inválido");
        }

        return ResponseEntity.ok(String.format("%.2f", result));
    }
}
