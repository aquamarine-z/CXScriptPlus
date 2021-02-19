// Generated from I:/Eclipse Program/CXScriptPlus/src/scriptexecutor\Script.g4 by ANTLR 4.9.1
package scriptexecutor;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ScriptParser}.
 */
public interface ScriptListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ScriptParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(ScriptParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(ScriptParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code printExpr}
	 * labeled alternative in {@link ScriptParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterPrintExpr(ScriptParser.PrintExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code printExpr}
	 * labeled alternative in {@link ScriptParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitPrintExpr(ScriptParser.PrintExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assign}
	 * labeled alternative in {@link ScriptParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterAssign(ScriptParser.AssignContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assign}
	 * labeled alternative in {@link ScriptParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitAssign(ScriptParser.AssignContext ctx);
	/**
	 * Enter a parse tree produced by the {@code blank}
	 * labeled alternative in {@link ScriptParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterBlank(ScriptParser.BlankContext ctx);
	/**
	 * Exit a parse tree produced by the {@code blank}
	 * labeled alternative in {@link ScriptParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitBlank(ScriptParser.BlankContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parens}
	 * labeled alternative in {@link ScriptParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParens(ScriptParser.ParensContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parens}
	 * labeled alternative in {@link ScriptParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParens(ScriptParser.ParensContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MulDiv}
	 * labeled alternative in {@link ScriptParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMulDiv(ScriptParser.MulDivContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MulDiv}
	 * labeled alternative in {@link ScriptParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMulDiv(ScriptParser.MulDivContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link ScriptParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAddSub(ScriptParser.AddSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link ScriptParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAddSub(ScriptParser.AddSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code id}
	 * labeled alternative in {@link ScriptParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterId(ScriptParser.IdContext ctx);
	/**
	 * Exit a parse tree produced by the {@code id}
	 * labeled alternative in {@link ScriptParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitId(ScriptParser.IdContext ctx);
	/**
	 * Enter a parse tree produced by the {@code int}
	 * labeled alternative in {@link ScriptParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterInt(ScriptParser.IntContext ctx);
	/**
	 * Exit a parse tree produced by the {@code int}
	 * labeled alternative in {@link ScriptParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitInt(ScriptParser.IntContext ctx);
}