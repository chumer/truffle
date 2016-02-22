package com.oracle.truffle.api.dsl.test.newlayout;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.NodeCost;

public class Sharable {

    public static int specializeImplicitDouble(Object value) {
        if (value instanceof Integer) {
            return 0b01;
        } else if (value instanceof Double) {
            return 0b10;
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        throw new AssertionError();
    }

    public static boolean isImplicitDouble(int state, Object value) {
        if ((state & 0b01) != 0 && value instanceof Integer) {
            return true;
        }
        if ((state & 0b10) != 0 && value instanceof Double) {
            return true;
        }
        return false;
    }

    public static double asImplicitDouble(int state, Object value) {
        if ((state & 0b01) != 0 && value instanceof Integer) {
            return MyTypeSystemGen.toDouble((int) value);
        }
        if ((state & 0b10) != 0 && value instanceof Double) {
            return (double) value;
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        throw new AssertionError();
    }

    public static boolean singleState(int state) {
        return state != 0L && (state & (state - 1L)) == 0L;
    }

    public static NodeCost calculateCost(long state) {
        if (state == 0) {
            return NodeCost.UNINITIALIZED;
        } else if (state != 0L && (state & (state - 1L)) == 0L) { // is power of two
            return NodeCost.MONOMORPHIC;
        } else {
            return NodeCost.POLYMORPHIC;
        }
    }

    public static NodeCost calculateCost(int state) {
        if (state == 0) {
            return NodeCost.UNINITIALIZED;
        } else if (state != 0 && (state & (state - 1)) == 0) { // is power of two
            return NodeCost.MONOMORPHIC;
        } else {
            return NodeCost.POLYMORPHIC;
        }
    }

}
