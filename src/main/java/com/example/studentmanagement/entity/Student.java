package com.example.studentmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "students")
@Data
public class Student {

    @Id
    @Column(name = "student_id")
    private String studentId;

    @NotBlank
    private String name;

    private LocalDate dateOfBirth;

    private String gender;

    private String phone;

    @DecimalMin("0.0")
    @DecimalMax("4.0")
    private Double gpa;
}
