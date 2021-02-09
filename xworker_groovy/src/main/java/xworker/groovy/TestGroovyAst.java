package xworker.groovy;

import java.util.List;
import java.util.Map;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.MethodPointerExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.SpreadMapExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.UnaryMinusExpression;
import org.codehaus.groovy.ast.expr.UnaryPlusExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.xmeta.Thing;
import org.xmeta.World;

public class TestGroovyAst implements  GroovyCodeVisitor{
	public void test() {
		try {
			World world = World.getInstance();
			world.init(null);
			
			Thing thing = world.getThing("xworker.ide.worldexplorer.swt.SimpleExplorer/@shell1/@scripts/@openDataObject");
			String code = thing.getString("code");
			
			AstBuilder ac = new AstBuilder();
			List<ASTNode> nodes = ac.buildFromString(code);
			for(ASTNode node : nodes) {
				node.visit(this);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		TestGroovyAst test = new TestGroovyAst();
		test.test();
	}

	@Override
	public void visitBlockStatement(BlockStatement statement) {
		System.out.println("visitBlockStatement, " + statement);
		System.out.println(statement.getVariableScope().getDeclaredVariables());
		for(Statement st : statement.getStatements()) {
			System.out.println(statement.getVariableScope().getDeclaredVariables());
			st.visit(this);
		}
	}

	@Override
	public void visitForLoop(ForStatement forLoop) {
		System.out.println("visitForLoop");
	}

	@Override
	public void visitWhileLoop(WhileStatement loop) {
		System.out.println("visitWhileLoop");
	}

	@Override
	public void visitDoWhileLoop(DoWhileStatement loop) {
		System.out.println("visitDoWhileLoop");
	}

	@Override
	public void visitIfElse(IfStatement ifElse) {
		System.out.println("visitIfElse");
		BlockStatement ifBlok = (BlockStatement) ifElse.getIfBlock();
		Map<String, Variable> variables = ifBlok.getVariableScope().getDeclaredVariables(); 
		for(String key : variables.keySet()) {
			Variable v = variables.get(key);
			if(v != null) {
				Expression exp = v.getInitialExpression();
				if(exp != null) {
					System.out.println(key + ": " + v.getType() + " ," + v.getInitialExpression().getLineNumber() + ", " + 
								v.getInitialExpression().getColumnNumber());
				}else {
					System.out.println(key + ": " + v.getType());
				}
			}else {
				System.out.println(key + ": is null");
			}
		}
		for(Statement st : ifBlok.getStatements()) {
			
			st.visit(this);
		}
	}

	@Override
	public void visitExpressionStatement(ExpressionStatement statement) {
		System.out.println("visitExpressionStatement");
	}

	@Override
	public void visitReturnStatement(ReturnStatement statement) {
		System.out.println("visitReturnStatement");
	}

	@Override
	public void visitAssertStatement(AssertStatement statement) {
		System.out.println("visitAssertStatement");
	}

	@Override
	public void visitTryCatchFinally(TryCatchStatement finally1) {
		System.out.println("visitTryCatchFinally");
	}

	@Override
	public void visitSwitch(SwitchStatement statement) {
		System.out.println("visitSwitch");
	}

	@Override
	public void visitCaseStatement(CaseStatement statement) {
		System.out.println("visitCaseStatement");
	}

	@Override
	public void visitBreakStatement(BreakStatement statement) {
		System.out.println("visitBreakStatement");
	}

	@Override
	public void visitContinueStatement(ContinueStatement statement) {
		System.out.println("visitContinueStatement");
	}

	@Override
	public void visitThrowStatement(ThrowStatement statement) {
		System.out.println("visitThrowStatement");
	}

	@Override
	public void visitSynchronizedStatement(SynchronizedStatement statement) {
		System.out.println("visitSynchronizedStatement");
	}

	@Override
	public void visitCatchStatement(CatchStatement statement) {
		System.out.println("visitCatchStatement");
	}

	@Override
	public void visitMethodCallExpression(MethodCallExpression call) {
		System.out.println("visitMethodCallExpression");
	}

	@Override
	public void visitStaticMethodCallExpression(StaticMethodCallExpression expression) {
		System.out.println("visitStaticMethodCallExpression");
	}

	@Override
	public void visitConstructorCallExpression(ConstructorCallExpression expression) {
		System.out.println("visitConstructorCallExpression");
	}

	@Override
	public void visitTernaryExpression(TernaryExpression expression) {
		System.out.println("visitTernaryExpression");
	}

	@Override
	public void visitShortTernaryExpression(ElvisOperatorExpression expression) {
		System.out.println("visitBlockStatement");
	}

	@Override
	public void visitBinaryExpression(BinaryExpression expression) {
		System.out.println("visitBlockStatement");
	}

	@Override
	public void visitPrefixExpression(PrefixExpression expression) {
		System.out.println("visitBlockStatement");
	}

	@Override
	public void visitPostfixExpression(PostfixExpression expression) {
		System.out.println("visitBlockStatement");
	}

	@Override
	public void visitBooleanExpression(BooleanExpression expression) {
		System.out.println("visitBlockStatement");
	}

	@Override
	public void visitClosureExpression(ClosureExpression expression) {
		System.out.println("visitBlockStatement");
	}

	@Override
	public void visitTupleExpression(TupleExpression expression) {
		System.out.println("visitTupleExpression");
	}

	@Override
	public void visitMapExpression(MapExpression expression) {
		System.out.println("visitMapExpression");
	}

	@Override
	public void visitMapEntryExpression(MapEntryExpression expression) {
		System.out.println("visitMapEntryExpression");
	}

	@Override
	public void visitListExpression(ListExpression expression) {
		System.out.println("visitListExpression");
	}

	@Override
	public void visitRangeExpression(RangeExpression expression) {
		System.out.println("visitRangeExpression");
	}

	@Override
	public void visitPropertyExpression(PropertyExpression expression) {
		System.out.println("visitPropertyExpression");
	}

	@Override
	public void visitAttributeExpression(AttributeExpression attributeExpression) {
		System.out.println("visitAttributeExpression");
	}

	@Override
	public void visitFieldExpression(FieldExpression expression) {
		System.out.println("visitFieldExpression");
	}

	@Override
	public void visitMethodPointerExpression(MethodPointerExpression expression) {
		System.out.println("visitMethodPointerExpression");
	}

	@Override
	public void visitConstantExpression(ConstantExpression expression) {
		System.out.println("visitConstantExpression");		
	}

	@Override
	public void visitClassExpression(ClassExpression expression) {
		System.out.println("visitClassExpression");
	}

	@Override
	public void visitVariableExpression(VariableExpression expression) {
		System.out.println("visitVariableExpression");
	}

	@Override
	public void visitDeclarationExpression(DeclarationExpression expression) {
		System.out.println("visitDeclarationExpression");
	}

	@Override
	public void visitGStringExpression(GStringExpression expression) {
		System.out.println("visitGStringExpression");
	}

	@Override
	public void visitArrayExpression(ArrayExpression expression) {
		System.out.println("visitArrayExpression");
	}

	@Override
	public void visitSpreadExpression(SpreadExpression expression) {
		System.out.println("visitSpreadExpression");
	}

	@Override
	public void visitSpreadMapExpression(SpreadMapExpression expression) {
		System.out.println("visitSpreadMapExpression");
	}

	@Override
	public void visitNotExpression(NotExpression expression) {
		System.out.println("visitNotExpression");
	}

	@Override
	public void visitUnaryMinusExpression(UnaryMinusExpression expression) {
		System.out.println("visitUnaryMinusExpression");
	}

	@Override
	public void visitUnaryPlusExpression(UnaryPlusExpression expression) {
		System.out.println("visitUnaryPlusExpression");
	}

	@Override
	public void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
		System.out.println("visitBitwiseNegationExpression");
	}

	@Override
	public void visitCastExpression(CastExpression expression) {
		System.out.println("visitCastExpression");
	}

	@Override
	public void visitArgumentlistExpression(ArgumentListExpression expression) {
		System.out.println("visitArgumentlistExpression");
	}

	@Override
	public void visitClosureListExpression(ClosureListExpression closureListExpression) {
		System.out.println("visitClosureListExpression");
	}

	@Override
	public void visitBytecodeExpression(BytecodeExpression expression) {
		System.out.println("visitBytecodeExpression");
	}
}
