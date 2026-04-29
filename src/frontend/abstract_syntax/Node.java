package frontend.abstract_syntax;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/* Node Superclass */
@ToString
@Getter
@AllArgsConstructor
public abstract class Node {
    protected final int lineNumber;
}
