package com.example.employees;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc
public class EmployeeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private EmployeeService employeeService;

	@Test
	public void testGetAllEmployees() throws Exception {
		Employee employee1 = new Employee("Тестовое ФИО", "Тестовая должность", 5, "Тестовая компания", 5000);
		Employee employee2 = new Employee("Таргашин Альбедо Екатеринович", "Супер пупер разработчик", 3, "Супер пупер компания", 900000);
		List<Employee> employees = Arrays.asList(employee1, employee2);

		when(employeeService.getAllEmployees()).thenReturn(employees);

		mockMvc.perform(get("/employees")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json("[{}, {}]"));
	}

	@Test
	public void testGetEmployeeById() throws Exception {
		Long id = 1L;
		Employee employee = new Employee("Тестовое ФИО", "Тестовая должность", 5, "Тестовая компания", 5000);
		when(employeeService.getEmployeeById(id)).thenReturn(Optional.of(employee));

		mockMvc.perform(get("/employees/{id}", id)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.fio").value("Тестовое ФИО"));
	}

	@Test
	public void testAddEmployee() throws Exception {
		Employee employee = new Employee("Тестовое ФИО", "Тестовая должность", 5, "Тестовая компания", 5000);

		mockMvc.perform(post("/employees")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{ \"fio\": \"Тестовое ФИО\", \"post\": \"Тестовая должность\", \"experience\": 5, \"workplace\": \"Тестовая компания\", \"salary\": 5000 }"))
				.andExpect(status().isOk());

		verify(employeeService, times(1)).addEmployee(any(Employee.class));
	}

	@Test
	public void testUpdateEmployee() throws Exception {
		Long id = 1L;
		Employee updatedEmployee = new Employee("Тестовое ФИО", "Тестовая должность", 5, "Тестовая компания", 6000);

		mockMvc.perform(put("/employees/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{ \"fio\": \"Тестовое ФИО\", \"post\": \"Тестовая должность\", \"experience\": 5, \"workplace\": \"Тестовая компания\", \"salary\": 6000 }"))
				.andExpect(status().isOk());

		verify(employeeService, times(1)).updateEmployee(any(Employee.class));
	}

	@Test
	public void testDeleteEmployee() throws Exception {
		Long id = 1L;

		mockMvc.perform(delete("/employees/{id}", id)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verify(employeeService, times(1)).deleteEmployee(id);
	}

	@Test
	public void testFindEmployeesByWorkplaceIgnoreCase() throws Exception {
		Employee employee1 = new Employee("Тестовое ФИО", "Тестовая должность", 5, "Тестовая компания", 5000);
		Employee employee2 = new Employee("Таргашин Альбедо Екатеринович", "Супер пупер разработчик", 3, "Супер пупер компания", 900000);
		List<Employee> employees = Arrays.asList(employee1, employee2);

		when(employeeService.findEmployeesByWorkplaceIgnoreCase("Тестовая компания")).thenReturn(employees);

		mockMvc.perform(get("/employees/getWorkplace?workplace=office%20a")
						.contentType(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("<h1>Сотрудники работающие в Тестовая компания</h1>")));
	}

	@Test
	public void testGetEmployeesSortedBySalary() throws Exception {
		Employee employee1 = new Employee("Тестовое ФИО", "Тестовая должность", 5, "Тестовая компания", 5000);
		Employee employee2 = new Employee("Таргашин Альбедо Екатеринович", "Супер пупер разработчик", 3, "Супер пупер компания", 900000);
		List<Employee> employees = Arrays.asList(employee1, employee2);

		when(employeeService.getEmployeesSortedBySalary()).thenReturn(employees);

		mockMvc.perform(get("/employees/sorted")
						.contentType(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Тестовая должность")))
				.andExpect(content().string(containsString("Супер пупер разработчик")));
	}

	@Test
	public void testGetEmployeesWithExperienceGreaterThan() throws Exception {
		Employee employee = new Employee("Тестовое ФИО", "Тестовая должность", 5, "Тестовая компания", 5000);
		List<Employee> employees = Arrays.asList(employee);

		when(employeeService.getEmployeesWithExperienceGreaterThan(4)).thenReturn(employees);

		mockMvc.perform(get("/employees/experience>4")
						.contentType(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Тестовое ФИО")));
	}
}