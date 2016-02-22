package com.oracle.truffle.api.dsl.test.newlayout;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.TypeSystem;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.dsl.internal.DSLOptions;
import com.oracle.truffle.api.dsl.internal.DSLOptions.DSLGenerator;
import com.oracle.truffle.api.dsl.test.examples.ExampleNode;
import com.oracle.truffle.api.dsl.test.newlayout.TestNode.MyTypeSystem;

@TypeSystemReference(MyTypeSystem.class)
public class TestNode extends ExampleNode {

    // test caching
    @Specialization(guards = {"a == cachedA", "b == cachedB"})
    protected static int doIntCached(int a, int b, @Cached("a") int cachedA, @Cached("b") int cachedB) {
        return cachedA + cachedB;
    }

    // test rewrite exceptions
    @Specialization(rewriteOn = ArithmeticException.class, contains = "doIntCached")
    protected static int doIntRewrite(int a, int b) {
        return a + b;
    }

    // testing fallthough from int
    @Specialization
    protected static double doIntNoRewrite(int a, int b) {
        return a + b;
    }

    // testing implicit casts
    @Specialization
    protected static double doDouble(double a, double b) {
        return a + b;
    }

    // testing fallback
    @Fallback
    protected static Object doFallback(Object a, Object b) {
        return null;
    }

    @TypeSystem({})
    @DSLOptions(defaultGenerator = DSLGenerator.DEFAULT)
    public static class MyTypeSystem {

        @ImplicitCast
        public static double toDouble(int value) {
            return value;
        }

    }

}
