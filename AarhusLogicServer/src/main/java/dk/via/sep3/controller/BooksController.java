package dk.via.sep3.controller;

import dk.via.sep3.grpcConnection.GrpcConnectionInterface;
import dk.via.sep3.model.entities.BookDTO;
import dk.via.sep3.model.entities.LoanDTO;
import dk.via.sep3.model.entities.ReservationDTO;
import dk.via.sep3.model.entities.CreateLoanRequest;
import dk.via.sep3.model.entities.CreateReservationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@Validated
public class BooksController {

    private final GrpcConnectionInterface grpc;

    public BooksController(GrpcConnectionInterface grpc) {
        this.grpc = grpc;
    }

    // GET /api/books/{isbn}
    @GetMapping("/books/{isbn}")
    public ResponseEntity<BookDTO> getBook(@PathVariable String isbn) {
        BookDTO dto = grpc.getBookByIsbn(isbn);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    // POST /api/loans
    @PostMapping("/loans")
    public ResponseEntity<LoanDTO> createLoan( @RequestBody CreateLoanRequest req) {
        LoanDTO loan = grpc.createLoan(req.getUsername(), req.getIsbn());
        return ResponseEntity.status(HttpStatus.CREATED).body(loan);
    }

    // POST /api/reservations
    @PostMapping("/reservations")
    public ResponseEntity<ReservationDTO> reserve( @RequestBody CreateReservationRequest req) {
        ReservationDTO res = grpc.reserveBook(req.getUsername(), req.getIsbn());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}
