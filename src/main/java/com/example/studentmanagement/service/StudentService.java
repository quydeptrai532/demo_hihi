
package com.example.studentmanagement.service;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository repository;

    public Student create(Student student) {
        if (repository.existsById(student.getStudentId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Student already exists"
            );
        }
        return repository.save(student);
    }

    public Page<Student> getAll(
            String keyword,
            int page,
            int size,
            String sortBy,
            String direction) {

        Set<String> allowedFields = Set.of(
                "studentId", "name", "dateOfBirth",
                "gender", "phone", "gpa"
        );

        if (!allowedFields.contains(sortBy)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid sort field"
            );
        }

        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid sort direction"
            );
        }

        Pageable pageable = PageRequest.of(
                page, size, Sort.by(sortDirection, sortBy)
        );

        Specification<Student> spec = (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("studentId")), pattern)
            );
        };

        return repository.findAll(spec, pageable);
    }

    public Student getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student not found"
                ));
    }

    public Student update(String id, Student data) {
        Student student = getById(id);

        student.setName(data.getName());
        student.setDateOfBirth(data.getDateOfBirth());
        student.setGender(data.getGender());
        student.setPhone(data.getPhone());
        student.setGpa(data.getGpa());

        return repository.save(student);
    }

    public void delete(String id) {
        Student student = getById(id);
        repository.delete(student);
    }
}
