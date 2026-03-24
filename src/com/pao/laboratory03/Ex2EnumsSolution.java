package com.pao.laboratory03;

/**
 * Soluție Exercițiul 2 — Enum-uri cu câmpuri și metode abstracte
 * Rulează direct acest main. Priority este definit ca inner enum.
 */
public class Ex2EnumsSolution {

    // --- Enum definit ca inner type (în proiect ar fi fișier separat) ---
    private enum Priority {
        LOW(1, "green") {
            @Override public String getEmoji() { return "🟢"; }
        },
        MEDIUM(2, "yellow") {
            @Override public String getEmoji() { return "🟡"; }
        },
        HIGH(3, "orange") {
            @Override public String getEmoji() { return "🟠"; }
        },
        CRITICAL(4, "red") {
            @Override public String getEmoji() { return "🔴"; }
        };

        private final int level;
        private final String color;

        Priority(int level, String color) {
            this.level = level;
            this.color = color;
        }

        public int getLevel() { return level; }
        public String getColor() { return color; }
        public abstract String getEmoji();
    }

    public static void main(String[] args) {
        // a) Toate prioritățile
        System.out.println("=== Toate prioritățile ===");
        for (Priority p : Priority.values()) {
            System.out.println(p.getEmoji() + " " + p.name()
                    + " (level=" + p.getLevel() + ", color=" + p.getColor() + ")");
        }

        // b) Switch
        System.out.println("\n=== Switch pe prioritate ===");
        Priority current = Priority.HIGH;
        switch (current) {
            case LOW:      System.out.println("Totul e OK."); break;
            case MEDIUM:   System.out.println("De monitorizat."); break;
            case HIGH:     System.out.println("⚠️ Atenție! Prioritate ridicată!"); break;
            case CRITICAL: System.out.println("🚨 URGENT!"); break;
        }

        // c) valueOf
        System.out.println("\n=== valueOf ===");
        Priority fromString = Priority.valueOf("HIGH");
        System.out.println("Priority.valueOf(\"HIGH\") = " + fromString);

        // d) Comparare
        System.out.println("\n=== Comparare enum ===");
        System.out.println("HIGH == HIGH? " + (Priority.HIGH == fromString));
        System.out.println("HIGH == LOW? " + (Priority.HIGH == Priority.LOW));

        // e) name() și ordinal()
        System.out.println("\n=== name() și ordinal() ===");
        for (Priority p : Priority.values()) {
            System.out.println(p.name() + ": name=" + p.name() + ", ordinal=" + p.ordinal());
        }
    }
}

