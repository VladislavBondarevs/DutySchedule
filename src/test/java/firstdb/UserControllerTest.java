//package firstdb;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//@WebMvcTest(UserController.class)
//public class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UserService userService;
//
//    @MockBean
//    private TicketServiceTest ticketService;
//
//    @InjectMocks
//    private UserController userController;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
//    }
//
//    @Test
//    public void testRegisterSave_NewUser() throws Exception {
//        UserDto userDto = new UserDto("newUser", "password","","","");
//        when(userService.findByUsername(userDto.getUsername())).thenReturn(null);
//
//        ResultActions resultActions = mockMvc.perform(post("/register")
//                .flashAttr("user", userDto));
//
//        resultActions.andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/register?success"));
//    }
//
//    @Test
//    public void testCreateTicket() throws Exception {
//        Ticket ticket = new Ticket();
//        ResultActions resultActions = mockMvc.perform(post("/create-ticket")
//                .flashAttr("ticket", ticket));
//
//        resultActions.andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/home"));
//    }
//
//
//}