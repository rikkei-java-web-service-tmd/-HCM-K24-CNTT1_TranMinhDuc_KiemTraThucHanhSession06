package com.re.studentmanage.repository;

import com.re.studentmanage.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
