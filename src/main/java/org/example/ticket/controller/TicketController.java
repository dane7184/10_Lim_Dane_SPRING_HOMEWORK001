package org.example.ticket.controller;

import org.example.ticket.enums.TicketStatus;
import org.example.ticket.model.Ticket;
import org.example.ticket.model.TicketPayment;
import org.example.ticket.model.TicketRequest;
import org.example.ticket.response.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("api/v1/tickets")
public class TicketController {
    private final List<Ticket> TICKET_LIST = new ArrayList<>();
    private final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(6);

    public TicketController() {
        TICKET_LIST.add(new Ticket(1, "Dane", LocalDate.now(), "PP", "SR", 100.00, true, TicketStatus.BOOKED, "A"));
        TICKET_LIST.add(new Ticket(2, "Sean Sean", LocalDate.now(), "PP", "SR", 100.00, true, TicketStatus.CANCELLED, "A"));
        TICKET_LIST.add(new Ticket(3, "Tepy", LocalDate.now(), "PP", "SR", 100.00, true, TicketStatus.COMPLETED, "A"));
        TICKET_LIST.add(new Ticket(4, "Thearin", LocalDate.now(), "PP", "SR", 100.00, true, TicketStatus.BOOKED, "A"));
        TICKET_LIST.add(new Ticket(5, "Lyza", LocalDate.now(), "PP", "SR", 100.00, true, TicketStatus.COMPLETED, "A"));
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<Ticket>>> getAllTickets(
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size) {

        int firstPage = (page - 1) * size;
        int lastPage = Math.min(firstPage + size, TICKET_LIST.size());

        List<Ticket> ticketList = TICKET_LIST.subList(firstPage, lastPage);

        return ResponseEntity.ok(
                new APIResponse<>(
                        true,
                        "Tickets retrieved successfully",
                        HttpStatus.OK,
                        ticketList,
                        LocalDateTime.now()
                )
        );
    }

    @GetMapping("/{ticket-id}")
    public ResponseEntity<APIResponse<List<Ticket>>> getTicketById(@PathVariable("ticket-id") Integer id) {

        var searchById = TICKET_LIST.stream().filter(ticket -> ticket.getTicketID().equals(id)).toList();

        return ResponseEntity.ok(
                new APIResponse<>(
                        true,
                        "Ticket found",
                        HttpStatus.OK,
                        searchById,
                        LocalDateTime.now()
                )
        );
    }

    @GetMapping("/ticket-name")
    public ResponseEntity<APIResponse<List<Ticket>>> getTicketByName(@RequestParam String ticketName) {

        var search = TICKET_LIST.stream()
                .filter(ticket -> ticket.getPassengerName().contains(ticketName)).toList();

        return ResponseEntity.ok(
                new APIResponse<>(
                        true,
                        "Ticket Not Found",
                        HttpStatus.OK,
                        search,
                        LocalDateTime.now()
                )
        );
    }

    @GetMapping("/travel-date")
    public ResponseEntity<APIResponse<List<Ticket>>> getTicketFilter(@RequestParam TicketStatus status, @RequestParam LocalDate date){

        var filter = TICKET_LIST
                .stream().filter(ticket -> ticket.getTicketStatus().equals(status)).toList()
                .stream().filter(ticket -> ticket.getTravelDate().equals(date)).toList();

        return ResponseEntity.ok(
                new APIResponse<>(
                        true,
                        "Ticket Found",
                        HttpStatus.OK,
                        filter,
                        LocalDateTime.now()
                )
        );
    }

    @PostMapping
    public ResponseEntity<APIResponse<Ticket>> addTicket(@RequestBody TicketRequest ticketRequest) {

        Ticket ticket = new Ticket(
                ATOMIC_INTEGER.getAndIncrement(),
                ticketRequest.getPassengerName(),
                ticketRequest.getTravelDate(),
                ticketRequest.getSourceStation(),
                ticketRequest.getDestinationStation(),
                ticketRequest.getPrice(),
                ticketRequest.getPaymentStatus(),
                TicketStatus.BOOKED,
                ticketRequest.getSeatNumber());
        TICKET_LIST.add(ticket);

        return ResponseEntity.ok(new APIResponse<>(
                true,
                "Add Successfully",
                HttpStatus.CONTINUE,
                ticket,
                LocalDateTime.now()
        ));
    }

    @PostMapping("/bulk")
    public ResponseEntity<APIResponse<List<Ticket>>> addMultiTicket(
            @RequestBody List<TicketRequest> ticketRequestList) {
        List<Ticket> tickets = new ArrayList<>();

        for (TicketRequest req : ticketRequestList) {
            Ticket ticket = new Ticket(
                    ATOMIC_INTEGER.getAndIncrement(),
                    req.getPassengerName(),
                    req.getTravelDate(),
                    req.getSourceStation(),
                    req.getDestinationStation(),
                    req.getPrice(),
                    req.getPaymentStatus(),
                    TicketStatus.BOOKED,
                    req.getSeatNumber()
            );

            tickets.add(ticket);
        }

        TICKET_LIST.addAll(tickets);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new APIResponse<>(
                        true,
                        "Tickets added successfully",
                        HttpStatus.CREATED,
                        tickets,
                        LocalDateTime.now()
                )
        );
    }

    @PutMapping("/{ticket-id}")
    public ResponseEntity<APIResponse<TicketRequest>> updateTicketById(@PathVariable("ticket-id") Integer id, @RequestBody TicketRequest ticketRequest) {
        for (Ticket ticket : TICKET_LIST) {
            if (ticket.getTicketID().equals(id)){
                ticket.setPassengerName(ticketRequest.getPassengerName());
                ticket.setTravelDate(ticketRequest.getTravelDate());
                ticket.setSourceStation(ticketRequest.getSourceStation());
                ticket.setDestinationStation(ticketRequest.getDestinationStation());
                ticket.setPrice(ticketRequest.getPrice());
                ticket.setPaymentStatus(ticketRequest.getPaymentStatus());
                ticket.setTicketStatus(ticketRequest.getTicketStatus());
                ticket.setSeatNumber(ticketRequest.getSeatNumber());
            }
            break;
        }
        return ResponseEntity.ok(
                new APIResponse<>(
                        true,
                        "Update Successfully",
                        HttpStatus.CONTINUE,
                        ticketRequest,
                        LocalDateTime.now()
                )
        );
    }

    @PutMapping("/bulk")
    public ResponseEntity<APIResponse<List<Ticket>>> updateTicketPaymentStatus(@RequestBody TicketPayment ticketPayment) {

        List<Ticket> updated = new ArrayList<>();

        for (Ticket ticket : TICKET_LIST) {
            if (ticketPayment.getTicketId().contains(ticket.getTicketID())) {
                ticket.setPaymentStatus(ticketPayment.getPaymentStatus());
                updated.add(ticket);
            }
        }

        return ResponseEntity.ok(
                new APIResponse<>(
                        true,
                        "Payment Update Successfully",
                        HttpStatus.CONTINUE,
                        updated,
                        LocalDateTime.now()
                )
        );
    }

    @DeleteMapping("/{ticket-id}")
    public ResponseEntity<APIResponse<Ticket>> deleteTicketById(@PathVariable("ticket-id") Integer id) {

        Optional<Ticket> ticketOptional = TICKET_LIST.stream()
                .filter(ticket -> ticket.getTicketID().equals(id))
                .findFirst();

        if (ticketOptional.isPresent()) {
            TICKET_LIST.remove(ticketOptional.get());
            return ResponseEntity.ok(new APIResponse<>(
                    true,
                    "Delete Successfully",
                    HttpStatus.OK,
                    null,
                    LocalDateTime.now()
            ));
        }
        return ResponseEntity.ok(new APIResponse<>(
                true,
                "Ticket Id is already delete",
                HttpStatus.OK,
                null,
                LocalDateTime.now()
        ));
    }
}
