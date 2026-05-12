package ir.util;

public enum IrOperator {
    ADD, SUB, MUL, DIV, MOD,
    ASS, LT, GT, LEQ, GEQ, EQ,
    NEQ, GOTO, LABEL, IF_FALSE,
    NEG, AND, OR, NOT, RET,
    INT_TO_FLOAT, FLOAT_TO_INT, CALL,
    ALLOC, SET_FIELD, GET_FIELD,
    SETUP
}
