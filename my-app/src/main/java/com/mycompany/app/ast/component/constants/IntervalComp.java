package com.mycompany.app.ast.component.constants;

import com.mycompany.app.ast.type.IntT;

import lombok.Getter;

/* Interval Constant */
@Getter
public class IntervalComp extends CompConst {
    private IntT interval;

    IntervalComp(int lineNumber, IntT interval) {
        super(lineNumber);
        this.interval = interval;
    }
}
