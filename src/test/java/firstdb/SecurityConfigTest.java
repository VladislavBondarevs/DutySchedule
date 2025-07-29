//package firstdb;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class SecurityConfigTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    public void shouldAllowAccessToPublicUrls() throws Exception {
//        mockMvc.perform(get("/login"))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(get("/register"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    public void shouldAllowAccessToAdminPageForAdmin() throws Exception {
//        mockMvc.perform(get("/admin_dashboard"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void shouldRedirectUnauthenticatedUsers() throws Exception {
//        mockMvc.perform(get("/dashboard"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrlPattern("**/login"));
//    }
//}
//
