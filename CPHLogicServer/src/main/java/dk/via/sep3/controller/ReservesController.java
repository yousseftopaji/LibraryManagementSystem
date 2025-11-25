package dk.via.sep3.controller;

import dk.via.sep3.model.reservations.ReserveService;
import dk.via.sep3.shared.reserve.CreateReserveDTO;
import dk.via.sep3.shared.reserve.ReserveDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reserves")
public class ReservesController {
    private final ReserveService reserveService;

    public ReservesController(ReserveService reserveService) {
        this.reserveService = reserveService;
    }

    @PostMapping
    public ResponseEntity<ReserveDTO> createReserve(@RequestBody CreateReserveDTO request) {
        try {
            ReserveDTO reserveDTO = reserveService.createReserve(request);

            return new ResponseEntity<>(reserveDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Validation failed (invalid user, book not found, invalid dates)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            // Book not available
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            // Unexpected error
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
