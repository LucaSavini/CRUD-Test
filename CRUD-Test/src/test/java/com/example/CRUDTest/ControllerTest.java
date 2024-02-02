package com.example.CRUDTest;

import com.example.CRUDTest.Controllers.StudentController;
import com.example.CRUDTest.Entities.Student;
import com.example.CRUDTest.Service.StudentService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private StudentController studentController;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private StudentService studentService;
    @Autowired
    private ObjectMapper objectMapper;


    //creazione studente fisico
    private Student createAStudent() throws Exception {
        Student student = new Student();
        student.setWorking(true);
        student.setName("Luca");
        student.setSurname("Rossi");
        return this.createAStudent(student);
    }

    private Student createAStudent(Student student) throws Exception {
        MvcResult result = createStudentRequest(student);
        Student responseStudent = this.objectMapper.readValue(result.getResponse().getContentAsString(), Student.class);
        return responseStudent;
    }

    private MvcResult createStudentRequest() throws Exception {
        Student student = new Student();
        student.setWorking(true);
        student.setName("Luca");
        student.setSurname("Rossi");

        return createStudentRequest(student);

    }

    private MvcResult createStudentRequest(Student student) throws Exception {
        if (student == null) return null;

        String studentJSON = objectMapper.writeValueAsString(student);

        return this.mockMvc.perform(post("/students/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJSON))
                .andExpect(status().isOk()).andDo(print())
                .andReturn();
    }

    private Student getStudentFromId(Long id) throws Exception {
        MvcResult result = this.mockMvc.perform(get("/students/getStudent/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        if (result.getResponse().getContentAsString().isEmpty()) {
            return null;
        }

        return objectMapper.readValue(result.getResponse().getContentAsString(), Student.class);
    }

    @Test
    void createAStudentTest() throws Exception {

        Student studentResponse = createAStudent();
        assertThat(studentResponse.getId()).isNotNull();
    }

    @Test
    void readList() throws Exception {
        createAStudentTest();
        createAStudentTest();
        createAStudentTest();
        createAStudentTest();
        MvcResult result = this.mockMvc.perform(get("/students/getList"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<Student> responseStudent = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
        System.out.println("User in database are: " + responseStudent.size());
        assertThat(responseStudent.size()).isNotZero();
    }

    @Test
    void readSingleStudent() throws Exception {
        Student student = createAStudent();
        assertThat(student.getId()).isNotNull();

        Student responseStudent = getStudentFromId(student.getId());

        assertThat(responseStudent.getId()).isEqualTo(student.getId());
    }

    @Test
    void updateStudent() throws Exception {
        // Creazione di uno studente
        Student student = createAStudent();
        assertThat(student.getId()).isNotNull();

        // Aggiornamento del nome dello studente
        String newName = "Andrea";
        student.setName(newName);

        // Converti lo studente in formato JSON
        String studentJSON = objectMapper.writeValueAsString(student);

        // Esegui la richiesta PUT per aggiornare lo studente
        MvcResult result = this.mockMvc.perform(put("/students/updateStudent/" + student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // Verifica della risposta dopo l'aggiornamento
        Student responseStudent = objectMapper.readValue(result.getResponse().getContentAsString(), Student.class);
        assertThat(responseStudent.getId()).isEqualTo(student.getId());
        assertThat(responseStudent.getName()).isEqualTo(newName);

        // Verifica aggiuntiva usando una richiesta GET
        Student responseStudentGet = getStudentFromId(student.getId());
        assertThat(responseStudentGet.getId()).isEqualTo(student.getId());
        assertThat(responseStudentGet.getName()).isEqualTo(newName);
    }


    @Test
    void deleteUser()throws Exception{
        Student student = createAStudent();
        assertThat(student.getId()).isNotNull();

        this.mockMvc.perform(delete("/students/deleteStudent/"+student.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        Student responseStudent = getStudentFromId(student.getId());

        assertThat(responseStudent).isNull();

    }

}
