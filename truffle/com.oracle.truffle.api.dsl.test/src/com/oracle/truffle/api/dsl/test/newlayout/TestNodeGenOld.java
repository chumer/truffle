// CheckStyle: start generated
package com.oracle.truffle.api.dsl.test.newlayout;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.dsl.internal.SpecializationNode;
import com.oracle.truffle.api.dsl.internal.SpecializedNode;
import com.oracle.truffle.api.dsl.internal.SuppressFBWarnings;
import com.oracle.truffle.api.dsl.test.examples.ExampleNode;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

@GeneratedBy(TestNode.class)
@SuppressFBWarnings("SA_LOCAL_SELF_COMPARISON")
public final class TestNodeGenOld extends TestNode implements SpecializedNode {

    @Child private ExampleNode args0_;
    @Child private ExampleNode args1_;
    @CompilationFinal private Class<?> args0Type_;
    @CompilationFinal private Class<?> args1Type_;
    @CompilationFinal private boolean excludeIntCached_;
    @CompilationFinal private boolean excludeIntRewrite_;
    @Child private BaseNode_ specialization_;

    private TestNodeGenOld(ExampleNode[] args) {
        this.args0_ = args != null && 0 < args.length ? args[0] : null;
        this.args1_ = args != null && 1 < args.length ? args[1] : null;
        this.specialization_ = UninitializedNode_.create(this);
    }

    @Override
    public NodeCost getCost() {
        return specialization_.getNodeCost();
    }

    @Override
    public Object execute(VirtualFrame frameValue) {
        return specialization_.execute(frameValue);
    }

    @Override
    public double executeDouble(VirtualFrame frameValue) throws UnexpectedResultException {
        return specialization_.executeDouble(frameValue);
    }

    @Override
    public int executeInt(VirtualFrame frameValue) throws UnexpectedResultException {
        return specialization_.executeInt(frameValue);
    }

    @Override
    public SpecializationNode getSpecializationNode() {
        return specialization_;
    }

    @Override
    public Node deepCopy() {
        return SpecializationNode.updateRoot(super.deepCopy());
    }

    private static double expectDouble(Object value) throws UnexpectedResultException {
        if (value instanceof Double) {
            return (double) value;
        }
        throw new UnexpectedResultException(value);
    }

    private static int expectInteger(Object value) throws UnexpectedResultException {
        if (value instanceof Integer) {
            return (int) value;
        }
        throw new UnexpectedResultException(value);
    }

    public static TestNode create(ExampleNode[] args) {
        return new TestNodeGenOld(args);
    }

    @GeneratedBy(TestNode.class)
    private abstract static class BaseNode_ extends SpecializationNode {

        @CompilationFinal protected TestNodeGenOld root;

        BaseNode_(TestNodeGenOld root, int index) {
            super(index);
            this.root = root;
        }

        @Override
        protected final void setRoot(Node root) {
            this.root = (TestNodeGenOld) root;
        }

        @Override
        protected final Node[] getSuppliedChildren() {
            return new Node[] {root.args0_, root.args1_};
        }

        @Override
        public final Object acceptAndExecute(Frame frameValue, Object args0Value, Object args1Value) {
            return this.execute_((VirtualFrame) frameValue, args0Value, args1Value);
        }

        public abstract Object execute_(VirtualFrame frameValue, Object args0Value, Object args1Value);

        public Object execute(VirtualFrame frameValue) {
            Object args0Value_ = executeArgs0_(frameValue);
            Object args1Value_ = executeArgs1_(frameValue);
            return execute_(frameValue, args0Value_, args1Value_);
        }

        public double executeDouble(VirtualFrame frameValue) throws UnexpectedResultException {
            return expectDouble(execute(frameValue));
        }

        public int executeInt(VirtualFrame frameValue) throws UnexpectedResultException {
            return expectInteger(execute(frameValue));
        }

        @Override
        protected final SpecializationNode createNext(Frame frameValue, Object args0Value, Object args1Value) {
            if (args0Value instanceof Integer && args1Value instanceof Integer) {
                int args0Value_ = (int) args0Value;
                int args1Value_ = (int) args1Value;
                int cachedA1 = (args0Value_);
                int cachedB1 = (args1Value_);
                if ((args0Value_ == cachedA1) && (args1Value_ == cachedB1)) {
                    if (!root.excludeIntCached_) {
                        SpecializationNode s = IntCachedNode_.create(root, cachedA1, cachedB1);
                        if (countSame(s) < (3)) {
                            return s;
                        }
                    }
                }
                if (!root.excludeIntRewrite_) {
                    root.excludeIntCached_ = true;
                    return IntRewriteNode_.create(root);
                }
                return IntNoRewriteNode_.create(root);
            }
            if (MyTypeSystemGen.isImplicitDouble(args0Value) && MyTypeSystemGen.isImplicitDouble(args1Value)) {
                return DoubleNode_.create(root, args0Value, args1Value);
            }
            return null;
        }

        @Override
        protected final SpecializationNode createFallback() {
            return FallbackNode_.create(root);
        }

        @Override
        protected final SpecializationNode createPolymorphic() {
            return PolymorphicNode_.create(root);
        }

        protected final BaseNode_ getNext() {
            return (BaseNode_) this.next;
        }

        protected final Object executeArgs0_(Frame frameValue) {
            Class<?> args0Type_ = root.args0Type_;
            try {
                if (args0Type_ == double.class) {
                    return root.args0_.executeDouble((VirtualFrame) frameValue);
                } else if (args0Type_ == int.class) {
                    return root.args0_.executeInt((VirtualFrame) frameValue);
                } else if (args0Type_ == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    Class<?> _type = Object.class;
                    try {
                        Object _value = root.args0_.execute((VirtualFrame) frameValue);
                        if (_value instanceof Double) {
                            _type = double.class;
                        } else if (_value instanceof Integer) {
                            _type = int.class;
                        } else {
                            _type = Object.class;
                        }
                        return _value;
                    } finally {
                        root.args0Type_ = _type;
                    }
                } else {
                    return root.args0_.execute((VirtualFrame) frameValue);
                }
            } catch (UnexpectedResultException ex) {
                root.args0Type_ = Object.class;
                return ex.getResult();
            }
        }

        protected final Object executeArgs1_(Frame frameValue) {
            Class<?> args1Type_ = root.args1Type_;
            try {
                if (args1Type_ == double.class) {
                    return root.args1_.executeDouble((VirtualFrame) frameValue);
                } else if (args1Type_ == int.class) {
                    return root.args1_.executeInt((VirtualFrame) frameValue);
                } else if (args1Type_ == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    Class<?> _type = Object.class;
                    try {
                        Object _value = root.args1_.execute((VirtualFrame) frameValue);
                        if (_value instanceof Double) {
                            _type = double.class;
                        } else if (_value instanceof Integer) {
                            _type = int.class;
                        } else {
                            _type = Object.class;
                        }
                        return _value;
                    } finally {
                        root.args1Type_ = _type;
                    }
                } else {
                    return root.args1_.execute((VirtualFrame) frameValue);
                }
            } catch (UnexpectedResultException ex) {
                root.args1Type_ = Object.class;
                return ex.getResult();
            }
        }

    }
    @GeneratedBy(TestNode.class)
    private static final class UninitializedNode_ extends BaseNode_ {

        UninitializedNode_(TestNodeGenOld root) {
            super(root, 2147483647);
        }

        @Override
        public Object execute_(VirtualFrame frameValue, Object args0Value, Object args1Value) {
            return uninitialized(frameValue, args0Value, args1Value);
        }

        static BaseNode_ create(TestNodeGenOld root) {
            return new UninitializedNode_(root);
        }

    }
    @GeneratedBy(TestNode.class)
    private static final class PolymorphicNode_ extends BaseNode_ {

        PolymorphicNode_(TestNodeGenOld root) {
            super(root, 0);
        }

        @Override
        public SpecializationNode merge(SpecializationNode newNode, Frame frameValue, Object args0Value, Object args1Value) {
            return polymorphicMerge(newNode, super.merge(newNode, frameValue, args0Value, args1Value));
        }

        @Override
        public Object execute_(VirtualFrame frameValue, Object args0Value, Object args1Value) {
            return getNext().execute_(frameValue, args0Value, args1Value);
        }

        static BaseNode_ create(TestNodeGenOld root) {
            return new PolymorphicNode_(root);
        }

    }
    @GeneratedBy(methodName = "doIntCached(int, int, int, int)", value = TestNode.class)
    private static final class IntCachedNode_ extends BaseNode_ {

        private final int cachedA;
        private final int cachedB;

        IntCachedNode_(TestNodeGenOld root, int cachedA, int cachedB) {
            super(root, 1);
            this.cachedA = cachedA;
            this.cachedB = cachedB;
        }

        @Override
        public SpecializationNode merge(SpecializationNode newNode, Frame frameValue, Object args0Value, Object args1Value) {
            if (newNode.getClass() == IntRewriteNode_.class) {
                removeSame("Contained by doIntRewrite(int, int)");
            }
            return super.merge(newNode, frameValue, args0Value, args1Value);
        }

        @Override
        public boolean isIdentical(SpecializationNode other, Frame frameValue, Object args0Value, Object args1Value) {
            if (args0Value instanceof Integer && args1Value instanceof Integer) {
                int args0Value_ = (int) args0Value;
                int args1Value_ = (int) args1Value;
                if ((args0Value_ == this.cachedA) && (args1Value_ == this.cachedB)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Object execute(VirtualFrame frameValue) {
            try {
                return executeInt(frameValue);
            } catch (UnexpectedResultException ex) {
                return ex.getResult();
            }
        }

        @Override
        public int executeInt(VirtualFrame frameValue) throws UnexpectedResultException {
            int args0Value_;
            try {
                args0Value_ = root.args0_.executeInt(frameValue);
            } catch (UnexpectedResultException ex) {
                Object args1Value = executeArgs1_(frameValue);
                return expectInteger(getNext().execute_(frameValue, ex.getResult(), args1Value));
            }
            int args1Value_;
            try {
                args1Value_ = root.args1_.executeInt(frameValue);
            } catch (UnexpectedResultException ex) {
                return expectInteger(getNext().execute_(frameValue, args0Value_, ex.getResult()));
            }
            if ((args0Value_ == this.cachedA) && (args1Value_ == this.cachedB)) {
                return TestNode.doIntCached(args0Value_, args1Value_, this.cachedA, this.cachedB);
            }
            return expectInteger(getNext().execute_(frameValue, args0Value_, args1Value_));
        }

        @Override
        public Object execute_(VirtualFrame frameValue, Object args0Value, Object args1Value) {
            if (args0Value instanceof Integer && args1Value instanceof Integer) {
                int args0Value_ = (int) args0Value;
                int args1Value_ = (int) args1Value;
                if ((args0Value_ == this.cachedA) && (args1Value_ == this.cachedB)) {
                    return TestNode.doIntCached(args0Value_, args1Value_, this.cachedA, this.cachedB);
                }
            }
            return getNext().execute_(frameValue, args0Value, args1Value);
        }

        static BaseNode_ create(TestNodeGenOld root, int cachedA, int cachedB) {
            return new IntCachedNode_(root, cachedA, cachedB);
        }

    }
    @GeneratedBy(methodName = "doIntRewrite(int, int)", value = TestNode.class)
    private static final class IntRewriteNode_ extends BaseNode_ {

        IntRewriteNode_(TestNodeGenOld root) {
            super(root, 2);
        }

        @Override
        public Object execute(VirtualFrame frameValue) {
            try {
                return executeInt(frameValue);
            } catch (UnexpectedResultException ex) {
                return ex.getResult();
            }
        }

        @Override
        public int executeInt(VirtualFrame frameValue) throws UnexpectedResultException {
            int args0Value_;
            try {
                args0Value_ = root.args0_.executeInt(frameValue);
            } catch (UnexpectedResultException ex) {
                Object args1Value = executeArgs1_(frameValue);
                return expectInteger(getNext().execute_(frameValue, ex.getResult(), args1Value));
            }
            int args1Value_;
            try {
                args1Value_ = root.args1_.executeInt(frameValue);
            } catch (UnexpectedResultException ex) {
                return expectInteger(getNext().execute_(frameValue, args0Value_, ex.getResult()));
            }
            try {
                return TestNode.doIntRewrite(args0Value_, args1Value_);
            } catch (ArithmeticException ex) {
                root.excludeIntRewrite_ = true;
                return expectInteger(remove("threw rewrite exception", frameValue, args0Value_, args1Value_));
            }
        }

        @Override
        public Object execute_(VirtualFrame frameValue, Object args0Value, Object args1Value) {
            if (args0Value instanceof Integer && args1Value instanceof Integer) {
                int args0Value_ = (int) args0Value;
                int args1Value_ = (int) args1Value;
                try {
                    return TestNode.doIntRewrite(args0Value_, args1Value_);
                } catch (ArithmeticException ex) {
                    root.excludeIntRewrite_ = true;
                    return remove("threw rewrite exception", frameValue, args0Value_, args1Value_);
                }
            }
            return getNext().execute_(frameValue, args0Value, args1Value);
        }

        static BaseNode_ create(TestNodeGenOld root) {
            return new IntRewriteNode_(root);
        }

    }
    @GeneratedBy(methodName = "doIntNoRewrite(int, int)", value = TestNode.class)
    private static final class IntNoRewriteNode_ extends BaseNode_ {

        IntNoRewriteNode_(TestNodeGenOld root) {
            super(root, 3);
        }

        @Override
        public Object execute(VirtualFrame frameValue) {
            try {
                return executeDouble(frameValue);
            } catch (UnexpectedResultException ex) {
                return ex.getResult();
            }
        }

        @Override
        public double executeDouble(VirtualFrame frameValue) throws UnexpectedResultException {
            int args0Value_;
            try {
                args0Value_ = root.args0_.executeInt(frameValue);
            } catch (UnexpectedResultException ex) {
                Object args1Value = executeArgs1_(frameValue);
                return expectDouble(getNext().execute_(frameValue, ex.getResult(), args1Value));
            }
            int args1Value_;
            try {
                args1Value_ = root.args1_.executeInt(frameValue);
            } catch (UnexpectedResultException ex) {
                return expectDouble(getNext().execute_(frameValue, args0Value_, ex.getResult()));
            }
            return TestNode.doIntNoRewrite(args0Value_, args1Value_);
        }

        @Override
        public Object execute_(VirtualFrame frameValue, Object args0Value, Object args1Value) {
            if (args0Value instanceof Integer && args1Value instanceof Integer) {
                int args0Value_ = (int) args0Value;
                int args1Value_ = (int) args1Value;
                return TestNode.doIntNoRewrite(args0Value_, args1Value_);
            }
            return getNext().execute_(frameValue, args0Value, args1Value);
        }

        static BaseNode_ create(TestNodeGenOld root) {
            return new IntNoRewriteNode_(root);
        }

    }
    @GeneratedBy(methodName = "doDouble(double, double)", value = TestNode.class)
    private static final class DoubleNode_ extends BaseNode_ {

        private final Class<?> args0ImplicitType;
        private final Class<?> args1ImplicitType;

        DoubleNode_(TestNodeGenOld root, Object args0Value, Object args1Value) {
            super(root, 4);
            this.args0ImplicitType = MyTypeSystemGen.getImplicitDoubleClass(args0Value);
            this.args1ImplicitType = MyTypeSystemGen.getImplicitDoubleClass(args1Value);
        }

        @Override
        public boolean isSame(SpecializationNode other) {
            return super.isSame(other) && this.args0ImplicitType == ((DoubleNode_) other).args0ImplicitType && this.args1ImplicitType == ((DoubleNode_) other).args1ImplicitType;
        }

        @Override
        public Object execute(VirtualFrame frameValue) {
            try {
                return executeDouble(frameValue);
            } catch (UnexpectedResultException ex) {
                return ex.getResult();
            }
        }

        @Override
        public double executeDouble(VirtualFrame frameValue) throws UnexpectedResultException {
            double args0Value_;
            try {
                if (args0ImplicitType == int.class) {
                    args0Value_ = MyTypeSystem.toDouble(root.args0_.executeInt(frameValue));
                } else if (args0ImplicitType == double.class) {
                    args0Value_ = root.args0_.executeDouble(frameValue);
                } else {
                    Object args0Value__ = executeArgs0_(frameValue);
                    throw new UnexpectedResultException(args0Value__);
                }
            } catch (UnexpectedResultException ex) {
                Object args1Value = executeArgs1_(frameValue);
                return expectDouble(getNext().execute_(frameValue, ex.getResult(), args1Value));
            }
            double args1Value_;
            try {
                if (args1ImplicitType == int.class) {
                    args1Value_ = MyTypeSystem.toDouble(root.args1_.executeInt(frameValue));
                } else if (args1ImplicitType == double.class) {
                    args1Value_ = root.args1_.executeDouble(frameValue);
                } else {
                    Object args1Value__ = executeArgs1_(frameValue);
                    throw new UnexpectedResultException(args1Value__);
                }
            } catch (UnexpectedResultException ex) {
                return expectDouble(getNext().execute_(frameValue, args0Value_, ex.getResult()));
            }
            return TestNode.doDouble(args0Value_, args1Value_);
        }

        @Override
        public Object execute_(VirtualFrame frameValue, Object args0Value, Object args1Value) {
            if (MyTypeSystemGen.isImplicitDouble(args0Value, args0ImplicitType) && MyTypeSystemGen.isImplicitDouble(args1Value, args1ImplicitType)) {
                double args0Value_ = MyTypeSystemGen.asImplicitDouble(args0Value, args0ImplicitType);
                double args1Value_ = MyTypeSystemGen.asImplicitDouble(args1Value, args1ImplicitType);
                return TestNode.doDouble(args0Value_, args1Value_);
            }
            return getNext().execute_(frameValue, args0Value, args1Value);
        }

        static BaseNode_ create(TestNodeGenOld root, Object args0Value, Object args1Value) {
            return new DoubleNode_(root, args0Value, args1Value);
        }

    }
    @GeneratedBy(methodName = "doFallback(Object, Object)", value = TestNode.class)
    private static final class FallbackNode_ extends BaseNode_ {

        FallbackNode_(TestNodeGenOld root) {
            super(root, 2147483646);
        }

        @TruffleBoundary
        private boolean guardFallback(Object args0Value, Object args1Value) {
            return createNext(null, args0Value, args1Value) == null;
        }

        @Override
        public Object execute_(VirtualFrame frameValue, Object args0Value, Object args1Value) {
            if (guardFallback(args0Value, args1Value)) {
                return TestNode.doFallback(args0Value, args1Value);
            }
            return getNext().execute_(frameValue, args0Value, args1Value);
        }

        static BaseNode_ create(TestNodeGenOld root) {
            return new FallbackNode_(root);
        }

    }
}
