package com.imsoftware.students.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.imsoftware.students.repository.StudentRepository;
import org.springframework.stereotype.Service;

import com.imsoftware.students.domain.StudentDTO;
import com.imsoftware.students.entity.Student;
import com.imsoftware.students.entity.Subject;
import com.imsoftware.students.service.IStudentService;

@Service
public class StudentServiceImpl implements IStudentService {

	private final StudentRepository studentRepository;

	public StudentServiceImpl(StudentRepository studentRepository) {
		super();
		this.studentRepository = studentRepository;
	}

	@Override
	public Collection<StudentDTO> findAll() {
		return studentRepository.findAll().stream().map(new Function<Student, StudentDTO>() {
			@Override
			public StudentDTO apply(Student student) {
				List<String> programmingLanguagesKnowAbout = student.getSubjects().stream()
						.map(pl -> new String(pl.getName())).collect(Collectors.toList());
				return new StudentDTO(student.getName(), programmingLanguagesKnowAbout);
			}

		}).collect(Collectors.toList());
		
	}

	@Override
	public Collection<StudentDTO> findAllAndShowIfHaveAPopularSubject() {
		// TODO Obtener la lista de todos los estudiantes e indicar la materia más concurrida existentes en la BD e
		// indicar si el estudiante cursa o no la materia más concurrida registrado en la BD.
	    String mostPopularSubject = studentRepository.findAll().stream()
	            .flatMap(student -> student.getSubjects().stream())
	            .collect(Collectors.groupingBy(Subject::getName, Collectors.counting()))
	            .entrySet()
	            .stream()
	            .max(Map.Entry.comparingByValue())
	            .map(Map.Entry::getKey)
	            .orElse(null);

	    return studentRepository.findAll().stream()
	            .map(student -> {
	                List<String> subjects = student.getSubjects().stream()
	                        .map(Subject::getName)
	                        .collect(Collectors.toList());
	                boolean isEnrolledInMostPopularSubject = subjects.contains(mostPopularSubject);
	                return new StudentDTO(student.getName(), subjects, mostPopularSubject, isEnrolledInMostPopularSubject);
	            })
	            .collect(Collectors.toList());
	}

}
