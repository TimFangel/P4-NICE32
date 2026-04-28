package frontend.abstract_syntax.function;

import frontend.abstract_syntax.Node;

import lombok.Getter;
import lombok.ToString;

/* Function Composition Node */
@ToString
@Getter
public class CompFunc extends Node implements Func  {
    private FuncDecl func1;
    private FuncDecl func2;

    CompFunc(int lineNumber, FuncDecl func1, FuncDecl func2) {
        super(lineNumber);
        this.func1 = func1;
        this.func2 = func2;
    }
}
