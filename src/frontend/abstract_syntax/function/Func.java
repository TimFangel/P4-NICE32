package frontend.abstract_syntax.function;

import frontend.abstract_syntax.statement.Stmt;

import lombok.Getter;
import lombok.ToString;

/* Function */
@ToString
@Getter
public abstract class Func extends Stmt {
    Func(int lineNumber) {
        super(lineNumber);
    }
}
