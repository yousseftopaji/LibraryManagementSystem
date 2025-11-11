

import java.util.*;

public class PubSubBroker {

    // topic -> list of subscribers
    private final Map<String, List<Subscriber>> subscribers = new HashMap<>();

    // subscribe to a topic
    public void subscribe(String topic, Subscriber subscriber) {
        subscribers.computeIfAbsent(topic, t -> new ArrayList<>()).add(subscriber);
    }

    // publish an event to all subscribers
    public void publish(Event event) {
        List<Subscriber> subs = subscribers.get(event.getType());
        if (subs == null) {
            System.out.println("No subscribers for topic: " + event.getType());
            return;
        }

        for (Subscriber s : subs) {
            try {
                s.onEvent(event);
            } catch (Exception e) {
                // if one subscriber fails, others still get the event
                System.out.println("[WARN] Subscriber failed: " + e.getMessage());
            }
        }
    }

    // simple functional interface for subscribers
    public interface Subscriber {
        void onEvent(Event event);
    }
}
