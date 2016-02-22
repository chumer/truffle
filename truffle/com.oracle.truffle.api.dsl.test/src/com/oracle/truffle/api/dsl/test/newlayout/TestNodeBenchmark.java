package com.oracle.truffle.api.dsl.test.newlayout;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import com.oracle.truffle.api.dsl.test.examples.ExampleNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

@Warmup(iterations = 6)
@Measurement(iterations = 6)
@Fork(1)
@State(Scope.Thread)
public class TestNodeBenchmark {

    static class PolymorphicNode extends ExampleNode {
        private final int intValue = 12;
        private final double doubleValue = 32;

        private int count = 0;
        private final int compare;

        public PolymorphicNode(int compare) {
            this.compare = compare;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            count++;
            if (count % 2 == compare) {
                return intValue;
            } else {
                return doubleValue;
            }
        }
    }

    static class IntNode extends ExampleNode {

        private int value;

        public IntNode(int value) {
            this.value = value;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            return value++;
        }

        @Override
        public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
            return value++;
        }

    }

    static class DoubleNode extends ExampleNode {

        private double value;

        public DoubleNode(double value) {
            this.value = value;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            return value++;
        }

        @Override
        public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
            return value++;
        }

    }

    ExampleNode[] intChildren = new ExampleNode[]{new IntNode(42), new IntNode(45)};
    ExampleNode[] doubleChildren = new ExampleNode[]{new DoubleNode(42.0d), new DoubleNode(45.0d)};
    ExampleNode[] mixedChildren = new ExampleNode[]{new IntNode(42), new DoubleNode(45.0d)};
    ExampleNode[] polymorphicChildren = new ExampleNode[]{new PolymorphicNode(0), new PolymorphicNode(1)};
    ExampleNode escapedNode;

    @Param({"OLD_LAYOUT", "NEW_LAYOUT"}) public String factoryName;

    ExampleNodeFactory factory;
    ExampleNode intNode;
    ExampleNode doubleNode;
    ExampleNode mixedNode;
    ExampleNode polymorphicNode;

    public static final ExampleNodeFactory OLD_LAYOUT = new ExampleNodeFactory() {
        public TestNode create(ExampleNode[] children) {
            return TestNodeGenOld.create(children);
        }
    };

    public static final ExampleNodeFactory NEW_LAYOUT = new ExampleNodeFactory() {
        public TestNode create(ExampleNode[] children) {
            return TestNodeGenNew.create(children);
        }
    };

    @Setup
    public void setup() throws Exception {
        factory = (ExampleNodeFactory) TestNodeBenchmark.class.getField(factoryName).get(null);
        intNode = factory.create(intChildren);
        intNode.adoptChildren();
        doubleNode = factory.create(doubleChildren);
        doubleNode.adoptChildren();
        mixedNode = factory.create(mixedChildren);
        mixedNode.adoptChildren();
        polymorphicNode = factory.create(polymorphicChildren);
        polymorphicNode.adoptChildren();
    }

    @Benchmark
    public Object benchmark_create() {
        return factory.create(intChildren);
    }

    @Benchmark
    public Object benchmark_create_and_execute_monomorph() {
        ExampleNode node = factory.create(intChildren);
        node.adoptChildren();
        node.execute(null);
        return node;
    }

    @Benchmark
    public Object benchmark_execute_often_object() {
        return intNode.execute(null);
    }

    @Benchmark
    public int benchmark_execute_often_int() throws UnexpectedResultException {
        return intNode.executeInt(null);
    }

    @Benchmark
    public double benchmark_execute_often_double() throws UnexpectedResultException {
        return doubleNode.executeDouble(null);
    }

    @Benchmark
    public double benchmark_execute_often_mixed() throws UnexpectedResultException {
        return mixedNode.executeDouble(null);
    }

    @Benchmark
    public Object benchmark_execute_often_polymorph() {
        return polymorphicNode.execute(null);
    }

    public static void main(String[] args) throws Exception {
        TestNodeBenchmark bench = new TestNodeBenchmark();
        bench.factoryName = "OLD_LAYOUT";
        bench.setup();
    }

    private interface ExampleNodeFactory {

        ExampleNode create(ExampleNode[] children);

    }

}
