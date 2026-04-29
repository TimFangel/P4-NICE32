package frontend.abstract_syntax.component.constants;

import frontend.abstract_syntax.component.constants.component_types.DirectionType;
import lombok.Getter;
import lombok.ToString;

/* Direction Constant */
@ToString
@Getter
public class DirectionComp extends CompConst {
    private DirectionType direction;

    DirectionComp(int lineNumber, DirectionType direction) {
        super(lineNumber);
        this.direction = direction;
    }
    
}
