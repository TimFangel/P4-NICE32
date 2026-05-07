package frontend.abstract_syntax.expression;

import frontend.abstract_syntax.Node;
import frontend.abstract_syntax.expression.arith_expression.ArithExpr;
import frontend.abstract_syntax.expression.bool_expression.BoolExpr;
import lombok.Getter;
import lombok.ToString;

/* Expression */
@ToString
@Getter
public abstract sealed class Expr extends Node
        permits Cast, FuncCall, MemberAccess, Operand, VarExpr, ArithExpr, BoolExpr {
    protected Expr(int lineNumber) {
        super(lineNumber);
    }
}
