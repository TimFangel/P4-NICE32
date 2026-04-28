package frontend.abstract_syntax.expression;
import frontend.abstract_syntax.Node;

import lombok.Getter;
import lombok.ToString;

/* Operator */
@ToString
@Getter
public abstract class Operator extends Node {
    Operator(int lineNumber) {
        super(lineNumber);
    }
}
