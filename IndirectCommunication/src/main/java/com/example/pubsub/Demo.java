

public class Demo {
    public static void main(String[] args) {
        PubSubBroker broker = new PubSubBroker();

        // Subscribe two listeners to "news" topic
        broker.subscribe("news", event -> System.out.println("Subscriber A got: " + event));
        broker.subscribe("news", event -> System.out.println("Subscriber B got: " + event));

        // One faulty subscriber
        broker.subscribe("news", event -> { throw new RuntimeException("Subscriber C crashed!"); });

        // Subscribe to another topic
        broker.subscribe("sports", event -> System.out.println("Sports subscriber got: " + event));

        // Publish some events
        broker.publish(new Event("news", "Breaking News: Simple Pub/Sub works!"));
        broker.publish(new Event("sports", "Football match starts at 6PM!"));
        broker.publish(new Event("weather", "Sunny day")); // No subscribers
    }
}
