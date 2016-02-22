package com.oracle.truffle.api.dsl.test.newlayout;

import com.oracle.truffle.api.dsl.test.examples.ExampleNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public final class ConstantNode extends ExampleNode {

    private final int intValue;
    private final double doubleValue;

    public ConstantNode(Object value) {
        if (value instanceof Integer) {
            this.intValue = (int) value;
            this.doubleValue = 0.0;
        } else if (value instanceof Double) {
            this.doubleValue = (double) value;
            this.intValue = 0;
        } else {
            this.intValue = 0;
            this.doubleValue = 0;
        }
    }

    @Override
    public Object execute(VirtualFrame frame) {
        if (intValue != 0) {
            return intValue;
        } else {
            return doubleValue;
        }
    }

    @Override
    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        return intValue;
    }

    @Override
    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        return doubleValue;
    }

}
