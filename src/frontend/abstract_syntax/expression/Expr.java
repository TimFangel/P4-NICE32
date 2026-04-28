package frontend.abstract_syntax.expression;

import frontend.abstract_syntax.Node;

import lombok.Getter;
import lombok.ToString;

/* Expression */
@ToString
@Getter
public abstract class Expr extends Node {
    protected Expr(int lineNumber) {
        super(lineNumber);
    }
}
