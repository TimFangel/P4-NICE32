package com.mycompany.app.ast.component.constants;

import com.mycompany.app.ast.value.Num;

import lombok.Getter;
import lombok.ToString;

/* Interval Constant */
@ToString
@Getter
public class IntervalComp extends CompConst {
    private Num interval;

    IntervalComp(int lineNumber, Num interval) {
        super(lineNumber);
        this.interval = interval;
    }
}
