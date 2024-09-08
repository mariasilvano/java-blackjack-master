 public class Chip {
    private final int value;

    private Chip(int value) {
        super();
        this.value = value;
    }

    public static Chip one() {
        return new Chip(1);
    }

    public static Chip five() {
        return new Chip(5);
    }

    public static Chip ten() {
        return new Chip(10);
    }

    public static Chip twentyFive() {
        return new Chip(25);
    }

    public static Chip hundred() {
        return new Chip(100);
    }

    public int getValue() {
        return value;
    }
}
