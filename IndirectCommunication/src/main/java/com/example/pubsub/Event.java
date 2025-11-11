

public class Event {
    private final String type;
    private final String message;

    public Event(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() { return type; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return "Event[type=" + type + ", message=" + message + "]";
    }
}
