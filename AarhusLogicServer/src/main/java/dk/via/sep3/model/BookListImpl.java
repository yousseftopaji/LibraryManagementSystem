package dk.via.sep3.model;

import dk.via.sep3.DTOBook;
import dk.via.sep3.grpcConnection.GrpcConnectionInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookListImpl implements BookList {

    private final GrpcConnectionInterface grpcConnection;

    public BookListImpl(GrpcConnectionInterface grpcConnection) {
        this.grpcConnection = grpcConnection;
    }

    @Override
    public List<DTOBook> getAllBooks() {
        return grpcConnection.getAllBooks();
    }
}

