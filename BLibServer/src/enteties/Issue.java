package enteties;

import java.io.Serializable;
/**
 * Represents an issue associated with a book return.
 * Implements Serializable for object serialization.
 */
public class Issue implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The type of issue (e.g., "Lost", "Damaged"). */
    private String type;

    /**
     * Constructs an Issue with the specified type.
     *
     * @param type The type of the issue.
     */
    public Issue(String type) {
        this.type = type;
    }

    /**
     * Retrieves the type of the issue.
     *
     * @return The issue type.
     */
    public String getType() {
        return type;
    }
}
