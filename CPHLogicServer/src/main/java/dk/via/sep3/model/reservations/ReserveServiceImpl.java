package dk.via.sep3.model.reservations;

import dk.via.sep3.DTOBook;
import dk.via.sep3.DTOReserve;
import dk.via.sep3.DTOUser;
import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.grpcConnection.reserveGrpcService.ReserveGrpcService;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.shared.reserve.CreateReserveDTO;
import dk.via.sep3.shared.reserve.ReserveDTO;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class ReserveServiceImpl implements ReserveService {

    private final BookGrpcService bookGrpcService;
    private final UserGrpcService userGrpcService;
    private final ReserveGrpcService reserveGrpcService;

    public ReserveServiceImpl(BookGrpcService bookGrpcService, UserGrpcService userGrpcService, ReserveGrpcService reserveGrpcService) {
        this.bookGrpcService = bookGrpcService;
        this.userGrpcService = userGrpcService;
        this.reserveGrpcService = reserveGrpcService;
    }

    @Override
    public ReserveDTO createReserve(CreateReserveDTO createReserveDTO) {
        validateUser(createReserveDTO.getUsername());

        List<DTOBook> books = bookGrpcService.getBooksByIsbn(createReserveDTO.getBookISBN());

        if (books.isEmpty()) {
            throw new IllegalArgumentException("No book with this ISBN exists");
        }
        validateBookExists(books);

        ensureNoAvailableCopies(books);

        int queueNumber = getNextQueueNumber(createReserveDTO.getBookISBN());

        DTOReserve grpcReserve = createGrpcReserve(createReserveDTO, queueNumber);

        return convertToReserveDTO(grpcReserve);
    }

    private void validateUser(String username) {
        DTOUser user = userGrpcService.getUserByUsername(username);
        if (user == null || !user.getUsername().equals(username)) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }
    }

    private void validateBookExists(List<DTOBook> books) {
        if (books == null || books.isEmpty()) {
            throw new IllegalArgumentException("No book with this ISBN exists.");
        }
    }

    private void ensureNoAvailableCopies(List<DTOBook> books) {
        boolean isAnyAvailable = books.stream()
                .anyMatch(b -> b.getState().equalsIgnoreCase("AVAILABLE"));

        if (isAnyAvailable) {
            throw new IllegalStateException("Book is available → borrowing should happen instead.");
        }
    }

    private int getNextQueueNumber(String isbn) {
        List<DTOReserve> reservations = reserveGrpcService.getReservationsByIsbn(isbn);
        return reservations.size() + 1;  // First reservation = queue number 1
    }

    private void updateBookStatus(DTOBook book) {
        bookGrpcService.updateBookStatus(String.valueOf(book.getId()), "Reserved");
    }

    private DTOReserve createGrpcReserve(CreateReserveDTO dto, int queueNumber) {
        String reserveDate = new Date(System.currentTimeMillis()).toString();
        DTOBook book = bookGrpcService.getBooksByIsbn(dto.getBookISBN()).get(0);
        return reserveGrpcService.createReserve(
                dto.getUsername(),
                String.valueOf(book.getId()),
                queueNumber,
                reserveDate
        );
    }

    private ReserveDTO convertToReserveDTO(DTOReserve grpcReservation) {
        return new ReserveDTO(
                String.valueOf(grpcReservation.getId()),

                grpcReservation.getUsername(),
                String.valueOf(grpcReservation.getBookId()),
                grpcReservation.getReserveDate(),
                grpcReservation.getQueueNumber()
        );
    }

}
