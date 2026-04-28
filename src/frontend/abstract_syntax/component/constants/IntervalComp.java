package frontend.abstract_syntax.component.constants;

import frontend.abstract_syntax.value.Num;

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
