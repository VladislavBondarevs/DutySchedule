//package firstdb;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
// class TicketServiceTest {
//
//    @Mock
//    private TicketRepository ticketRepository;
//
//    @InjectMocks
//    private TicketServiceImpl ticketService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testGetAllTickets() {
//        // Arrange
//        Ticket ticket1 = new Ticket();
//        Ticket ticket2 = new Ticket();
//        when(ticketRepository.findAll()).thenReturn(Arrays.asList(ticket1, ticket2));
//
//        // Act
//        List<Ticket> tickets = ticketService.getAllTickets();
//
//        // Assert
//        assertNotNull(tickets);
//        assertEquals(2, tickets.size());
//        verify(ticketRepository, times(1)).findAll();
//    }
//
//    @Test
//    void testGetTicketById() {
//        // Arrange
//        Long ticketId = 1L;
//        Ticket ticket = new Ticket();
//        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
//
//        // Act
//        Ticket foundTicket = ticketService.getTicketById(ticketId);
//
//        // Assert
//        assertNotNull(foundTicket);
//        verify(ticketRepository, times(1)).findById(eq(ticketId));
//    }
//
//    @Test
//    void testSaveTicket() {
//        // Arrange
//        Ticket ticket = new Ticket();
//        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
//
//        // Act
//        ticketService.saveTicket(ticket);
//
//        // Assert
//        verify(ticketRepository, times(1)).save(eq(ticket));
//    }
//
//    @Test
//    void testDeleteTicketById() {
//        // Arrange
//        Long ticketId = 1L;
//        doNothing().when(ticketRepository).deleteById(ticketId);
//
//        // Act
//        ticketService.deleteTicketById(ticketId);
//
//        // Assert
//        verify(ticketRepository, times(1)).deleteById(eq(ticketId));
//    }
//}
