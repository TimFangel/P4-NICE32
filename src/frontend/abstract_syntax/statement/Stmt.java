package frontend.abstract_syntax.statement;

import frontend.abstract_syntax.Node;

import lombok.Getter;
import lombok.ToString;

/* Statement Node */
@ToString
@Getter
public abstract class Stmt extends Node {
    protected Stmt(int lineNumber) {
        super(lineNumber);
    }
}
