package io.github.kglowins.shifts.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.kglowins.shifts.db.EmployeeDTO;
import io.github.kglowins.shifts.db.Repository;
import java.util.List;

@Singleton
public class EmployeeService {
    private final Repository repository;

    @Inject
    public EmployeeService(Repository repository) {
        this.repository = repository;
    }

    public List<EmployeeDTO> getEmployees() {
        return repository.getEmployees();
    }

    public EmployeeDTO getEmployees(Long id) {
        return repository.getEmployee(id);
    }

    public void addEmployee(EmployeeDTO employeeDTO) {
        repository.insertEmployee(employeeDTO);
    }

    public void removeEmployee(Long id) {
        repository.deleteEmployee(id);
    }
}
