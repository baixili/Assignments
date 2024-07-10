import java.util.*;
import java.util.regex.*;

/**
 * See assignment handout for the grammar.
 * You need to implement the parse(..) method and all the rest of the parser.
 * There are several methods provided for you:
 * - several utility methods to help with the parsing
 * See also the TestParser class for testing your code.
 */
public class Parser {

    // Useful Patterns
    static final Pattern NUMPAT = Pattern.compile("-?[1-9][0-9]*|0"); 
    static final Pattern OPENPAREN = Pattern.compile("\\(");
    static final Pattern CLOSEPAREN = Pattern.compile("\\)");
    static final Pattern OPENBRACE = Pattern.compile("\\{");
    static final Pattern CLOSEBRACE = Pattern.compile("\\}");

    //----------------------------------------------------------------
    /**
     * The top of the parser, which is handed a scanner containing
     * the text of the program to parse.
     * Returns the parse tree.
     */
    ProgramNode parse(Scanner s) {
        // Set the delimiter for the scanner.
        s.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");
        // THE PARSER GOES HERE
        // Call the parseProg method for the first grammar rule (PROG) and return the node
        ProgramNode node = parseProg(s);
        return node;
    }



    //----------------------------------------------------------------
    // utility methods for the parser
    // - fail(..) reports a failure and throws exception
    // - require(..) consumes and returns the next token as long as it matches the pattern
    // - requireInt(..) consumes and returns the next token as an int as long as it matches the pattern
    // - checkFor(..) peeks at the next token and only consumes it if it matches the pattern

    /**
     * Report a failure in the parser.
     */
    static void fail(String message, Scanner s) {
        String msg = message + "\n   @ ...";
        for (int i = 0; i < 5 && s.hasNext(); i++) {
            msg += " " + s.next();
        }
        throw new ParserFailureException(msg + "...");
    }

    /**
     * Requires that the next token matches a pattern if it matches, it consumes
     * and returns the token, if not, it throws an exception with an error
     * message
     */
    static String require(String p, String message, Scanner s) {
        if (s.hasNext(p)) {return s.next();}
        fail(message, s);
        return null;
    }

    static String require(Pattern p, String message, Scanner s) {
        if (s.hasNext(p)) {return s.next();}
        fail(message, s);
        return null;
    }

    /**
     * Requires that the next token matches a pattern (which should only match a
     * number) if it matches, it consumes and returns the token as an integer
     * if not, it throws an exception with an error message
     */
    static int requireInt(String p, String message, Scanner s) {
        if (s.hasNext(p) && s.hasNextInt()) {return s.nextInt();}
        fail(message, s);
        return -1;
    }

    static int requireInt(Pattern p, String message, Scanner s) {
        if (s.hasNext(p) && s.hasNextInt()) {return s.nextInt();}
        fail(message, s);
        return -1;
    }

    /**
     * Checks whether the next token in the scanner matches the specified
     * pattern, if so, consumes the token and return true. Otherwise returns
     * false without consuming anything.
     */
    static boolean checkFor(String p, Scanner s) {
        if (s.hasNext(p)) {s.next(); return true;}
        return false;
    }

    static boolean checkFor(Pattern p, Scanner s) {
        if (s.hasNext(p)) {s.next(); return true;} 
        return false;
    }

    // creating the pat
    public static final Pattern MOVE_PAT = Pattern.compile("move");
    public static final Pattern TURNL_PAT = Pattern.compile("turnL");
    public static final Pattern TURNR_PAT = Pattern.compile("turnR");
    public static final Pattern TAKEFUEL_PAT = Pattern.compile("takeFuel");
    public static final Pattern WAIT_PAT = Pattern.compile("wait");
    public static final Pattern SHIELD_ON_PAT = Pattern.compile("shieldOn");
    public static final Pattern SHIELD_OFF_PAT = Pattern.compile("shieldOff");
    public static final Pattern TURN_AROUND_PAT = Pattern.compile("turnAround");
    public static final Pattern ACT_PAT = Pattern.compile("move|turnL|turnR|turnAround|shieldOn|shieldOff|takeFuel|wait");
    public static final Pattern SEMICOLON_PAT = Pattern.compile(";");
    public static final Pattern LOOP_PAT = Pattern.compile("loop");
    public static final Pattern BRACEL_PAT = Pattern.compile("\\{");
    public static final Pattern BRACER_PAT = Pattern.compile("\\}");
    public static final Pattern fUEL_LEFT_PAT = Pattern.compile("fuelLeft");
    public static final Pattern OPP_LR_PAT = Pattern.compile("oppLR");
    public static final Pattern OPP_FB_PAT = Pattern.compile("oppFB");
    public static final Pattern NUM_BARRELS_PAT = Pattern.compile("numBarrels");
    public static final Pattern BARREL_LR_PAT = Pattern.compile("barrelLR");
    public static final Pattern BARREL_FB_PAT = Pattern.compile("barrelFB");
    public static final Pattern WALL_DIST_PAT = Pattern.compile("wallDist");
    public static final Pattern IT_PAT = Pattern.compile("lt");
    public static final Pattern GT_PAT = Pattern.compile("gt");
    public static final Pattern EQ_PAT = Pattern.compile("eq");
    public static final Pattern RELOP_PAT = Pattern.compile("lt|gt|eq");
    public static final Pattern OP_PAT = Pattern.compile("add|sub|mul|div");
    public static final Pattern SENS_PAT = Pattern.compile("fuelLeft|oppLR|oppFB|numBarrels|barrelLR|barrelFB|wallDist");
    public static final Pattern AND_PAT = Pattern.compile("and");
    public static final Pattern OR_PAT = Pattern.compile("or");
    public static final Pattern NOT_PAT = Pattern.compile("not");
    public static final Pattern COLUMN_PAT = Pattern.compile(",");
    public static final Pattern IF_PAT = Pattern.compile("if");
    public static final Pattern ELSE_PAT = Pattern.compile("else");
    public static final Pattern WHILE_PAT = Pattern.compile("while");
    private static final Pattern ELIF_PAT = Pattern.compile("elif");
    private static final Pattern VARPAT = Pattern.compile("\\$[A-Za-z][A-Za-z0-9]*");
    private static final Pattern EQUALPAT = Pattern.compile("=");
    private Map<String, Integer> varMap = new HashMap<>(); // store the variable as key and corresponding value
    List<BooleanNode> elifCondList = new ArrayList<>(); // store all the elif conditions
    List<ProgramNode> elifBlockList = new ArrayList<>(); // store all the elif bodies

    /** "move" [ "(" EXPR ")" ] */
    public ProgramNode parseMove(Scanner scanner){
        // "move" is required here
        Parser.require(MOVE_PAT, "expecting 'move'", scanner);
        // initialise the number of move to be 1
        IntNode num = new NumNode(1);
        // if there is "(", check how many move we need to do
        if(scanner.hasNext(this.OPENPAREN)){
            // skip the "("
            scanner.next();
            // getting how many time there is move
            num = parseExpr(scanner);
            // ")" is required
            require(this.CLOSEPAREN, "Expecting ')'", scanner);
        }
        // return the move node
        return new MoveNode(num);
    }

    /** turn left */
    public ProgramNode parseTurnL(Scanner scanner){
        // the pat is required
        Parser.require(TURNL_PAT, "expecting 'turn left'", scanner);
        // if there is next token here and the token is not semicolon, fail to parse.
        if(!scanner.hasNext(this.SEMICOLON_PAT) && scanner.hasNext()){
            fail("Nothing should go over turnL excepting ';'", scanner);
            return null;
        }
        return new TurnLNode();
    }

    /** turn right */
    public ProgramNode parseTurnR(Scanner scanner){
        Parser.require(TURNR_PAT, "expecting 'turn right'", scanner);
        if(!scanner.hasNext(this.SEMICOLON_PAT) && scanner.hasNext()){
            fail("Nothing should go over turnR excepting ';'", scanner);
            return null;
        }
        return new TurnRNode();
    }

    /** take fuel */
    public ProgramNode parseTakeFuel(Scanner scanner){
        Parser.require(TAKEFUEL_PAT, "expecting 'take fuel'", scanner);
        if(!scanner.hasNext(this.SEMICOLON_PAT) && scanner.hasNext()){
            fail("Nothing should go over takeFuel excepting ';'", scanner);
            return null;
        }
        return new TakeFuelNode();
    }

    /** "wait" [ "(" EXPR ")"] */
    public ProgramNode parseWait(Scanner scanner){
        Parser.require(WAIT_PAT, "expecting 'wait'", scanner);
        IntNode num = new NumNode(1);
        if(scanner.hasNext(this.OPENPAREN)){
            scanner.next();
            num = parseExpr(scanner);
            require(this.CLOSEPAREN, "Expecting ')'", scanner);
        }
        return new WaitNode(num);
    }

    /** turn around */
    public ProgramNode parseTurnAround(Scanner scanner){
        require(TURN_AROUND_PAT, "expecting 'turn around'", scanner);
        if(!scanner.hasNext(this.SEMICOLON_PAT) && scanner.hasNext()){
            fail("Nothing should go over turnAround excepting ';'", scanner);
            return null;
        }
        return new TurnAroundNode();
    }

    /** shield on */
    public ProgramNode parseShieldOn(Scanner scanner){
        require(SHIELD_ON_PAT, "expecting 'shield on'", scanner);
        if(!scanner.hasNext(this.SEMICOLON_PAT) && scanner.hasNext()){
            fail("Nothing should go over shieldOn excepting ';'", scanner);
            return null;
        }
        return new ShieldOnNode();
    }

    /** shield off */
    public ProgramNode parseShieldOff(Scanner scanner){
        require(SHIELD_OFF_PAT, "expecting 'shield off'", scanner);
        if(!scanner.hasNext(this.SEMICOLON_PAT) && scanner.hasNext()){
            fail("Nothing should go over shieldOff excepting ';'", scanner);
            return null;
        }
        return new ShieldOffNode();
    }

    /** "move" [ "(" EXPR ")" ] | "turnL" | "turnR" | "turnAround" | 
    "shieldOn" | "shieldOff" | "takeFuel" | "wait" [ "(" EXPR ")" ] */
    public ProgramNode parseAct(Scanner scanner){
        // if the next token is "move", parse move
        if(scanner.hasNext(MOVE_PAT)){
            return parseMove(scanner);
        }
        // if the next token is "turnL", parse turn left
        if(scanner.hasNext(TURNL_PAT)){
            return parseTurnL(scanner);
        }
        // if the next token is "turnR", parse turn right
        if(scanner.hasNext(TURNR_PAT)){
            return parseTurnR(scanner);
        }
        // if the next token is "takeFuel", parse take fuel
        if(scanner.hasNext(TAKEFUEL_PAT)){
            return parseTakeFuel(scanner);
        }
        // if the next token is "wait", parse wait
        if(scanner.hasNext(WAIT_PAT)){
            return parseWait(scanner);
        }

        // if the next token is "turnAround", parse turnAround
        if(scanner.hasNext(TURN_AROUND_PAT)){
            return parseTurnAround(scanner);
        }

        // if the next token is "shieldOn", parse shieldOn
        if(scanner.hasNext(SHIELD_ON_PAT)){
            return parseShieldOn(scanner);
        }

        // if the next token is "shieldOff", parse shieldOff
        if(scanner.hasNext(SHIELD_OFF_PAT)){
            return parseShieldOff(scanner);
        }
        // the expected pats are not here
        fail("Expecting 'move' or 'turnL' or 'turnR', or 'wait' or 'turnAround' or 'shieldOn' or 'shieldOff', 'takeFuel'", scanner);
        return null;
    }

    

    /** STMT  ::= ACT ";" | LOOP | IF | WHILE | ASSGN ";" */
    public ProgramNode parseStmt(Scanner scanner){
        // if the next token is any of the ACT action, parseAct will take us to the corresponding [ACT] node
        if(scanner.hasNext(ACT_PAT)){
            // bring us to parseAct and then parseMove, it has scanner.next(), returning the next token
            ProgramNode node = parseAct(scanner);
            // check if there is semicolon
            Parser.require(SEMICOLON_PAT, "expecting semicolon", scanner);
            return node;
        }

        // if the next token is "loop", parse loop.
        if(scanner.hasNext(LOOP_PAT)){
            return parseLoop(scanner);
        }

        // if the next token is "if", parse if
        if(scanner.hasNext(IF_PAT)){
            return parseIf(scanner);
        }

        // if the next token is "while", parse while
        if(scanner.hasNext(WHILE_PAT)){
            return parseWhile(scanner);
        }
        
        if(scanner.hasNext(this.VARPAT)){
            ProgramNode assgnNode = parseAssgn(scanner);
            require(SEMICOLON_PAT, "expecting semicolon", scanner);
            return assgnNode;
        }
        // the expected statment is not here
        fail("Expecting statment:'ACT PAT' or 'loop' or 'while' or 'if'", scanner);
        return null;
    }

    /** LOOP  ::= "loop" BLOCK */
    public ProgramNode parseLoop(Scanner scanner){
        // a word "loop" is expected
        require(LOOP_PAT, "expecting 'loop'", scanner);
        // store the parseBlock result to the ProgramNode called block
        ProgramNode block = parseBlock(scanner);
        // creating the loop node
        return new LoopNode(block);
    }

    

    /** BLOCK ::= "{" STMT+ "}" */
    public ProgramNode parseBlock(Scanner scanner){

        // creating a list used to store all the stmts
        List<ProgramNode> acts = new ArrayList<>();
        // "{" is required
        require(BRACEL_PAT, "expecting '{'", scanner);
        // add at least one statement to the list, then checking the condition to see if break or continue
        do{
            acts.add(parseStmt(scanner));
            // if the next token is not required pat, it has to be "}"
            if(!scanner.hasNext(ACT_PAT) && !scanner.hasNext(LOOP_PAT) && !scanner.hasNext(IF_PAT) && !scanner.hasNext(WHILE_PAT)
            && !scanner.hasNext(VARPAT)){
                require(BRACER_PAT, "expecting '}'", scanner);
                // return the block node, including the acts, break the while
                return new BlockNode(acts);
            }
        }
        // while there is next token, keep parsing the statement and add to the acts.
        while(scanner.hasNext());

        // } expected
        require(BRACER_PAT, "expecting '}'", scanner);        
        return new BlockNode(acts);
    }

    /** PROG  ::= [ STMT ]* */
    public ProgramNode parseProg(Scanner scanner){
        // list used to store all the statments
        List<ProgramNode> stmts = new ArrayList<>();
        // while there is statement, add the statemens to the list
        while(scanner.hasNext()){
            // add the statements to be list
            stmts.add(parseStmt(scanner));
        }
        return new ProgNode(stmts);
    }


    /** fuelLeft */
    public IntNode parseFuelLeft(Scanner scanner){
        require(fUEL_LEFT_PAT, "expecting 'fuelLeft'", scanner);
        return new FuelLeftNode();
    }

    /** oppLR */
    public IntNode parseOppLR(Scanner scanner){
        require(OPP_LR_PAT, "expecting 'oppLR'", scanner);
        return new OppLRNode();
    }

    /** oppFB */
    public IntNode parseOppFB(Scanner scanner){
        require(OPP_FB_PAT, "expecting 'oppFB'", scanner);
        return new OppFBNode();
    }

    /** numBarrels */
    public IntNode parseNumBarrels(Scanner scanner){
        require(NUM_BARRELS_PAT,"expecting 'numBarrels'", scanner);
        return new NumBarrelsNode();
    }

    /** barrelLR  [ "(" EXPR ")" ] */
    public IntNode parseBarrelLRNode(Scanner scanner){
        IntNode expr = null;
        require(BARREL_LR_PAT, "expecting 'barrelLR'", scanner);
        if(scanner.hasNext(this.OPENPAREN)){
            scanner.next();
            expr = parseExpr(scanner);
            require(this.CLOSEPAREN, "expecting ')'", scanner);
        }
        return new BarrelLRNode(expr);
    }

    /** barrelFB  [ "(" EXPR ")" ]*/
    public IntNode parseBarrelFBNode(Scanner scanner){
        IntNode expr = null;
        require(BARREL_FB_PAT, "expecting 'barrelFB'", scanner);
        if(scanner.hasNext(this.OPENPAREN)){
            scanner.next();
            expr = parseExpr(scanner);
            require(this.CLOSEPAREN, "expecting ')'", scanner);
        }
        return new BarrelFBNode(expr);
    }

    /** wallDistance */
    public IntNode parseWallDistanceNode(Scanner scanner){
        require(WALL_DIST_PAT, "expecting 'wallDistance'", scanner);
        return new WallDistNode();
    }

    /** SENS  ::= "fuelLeft" | "oppLR" | "oppFB" | "numBarrels" |
          "barrelLR" [ "(" EXPR ")" ] | "barrelFB" [ "(" EXPR ")" ] | "wallDist" */
    public IntNode parseSens(Scanner scanner){
        // if the token is fuelLeft, return the fuelLeftNode
        if(scanner.hasNext(fUEL_LEFT_PAT)){
            return parseFuelLeft(scanner);
        }
        // if the token is oppLR, return the oppLRNode
        if(scanner.hasNext(OPP_LR_PAT)){
            return parseOppLR(scanner);
        }
        // if the token is oppFB, return the oppFBNode
        if(scanner.hasNext(OPP_FB_PAT)){
            return parseOppFB(scanner);
        }
        // if the token is numBarrerls, return the numBarrelsNode
        if(scanner.hasNext(NUM_BARRELS_PAT)){
            return parseNumBarrels(scanner);
        }
        if(scanner.hasNext(BARREL_LR_PAT)){
            return parseBarrelLRNode(scanner);
        }
        // if the token is barrelFB, return the BarrelFBNode
        if(scanner.hasNext(BARREL_FB_PAT)){
            return parseBarrelFBNode(scanner);
        }
        // if the token is wallDist, return the WallDistNode
        if(scanner.hasNext(WALL_DIST_PAT)){
            return parseWallDistanceNode(scanner);
        }
        // the expected sens is not here
        fail("Expecting SENS: fuelLeft|oppLR|oppFB|numBarrels|barrelLR|barrelFB|wallDist", scanner);
        return null;
    }

   
    /** RELOP ::= "lt" | "gt" | "eq" **/
    public String parseRelop(Scanner scanner){
        // check if the token want to compare less than, equal or greater to
        String string = require(RELOP_PAT, "expecting lt or gt or eq", scanner);
        return string;
    }

    /** OP::= "add" | "sub" | "mul" | "div" */
    
    public String parseOp(Scanner scanner){
        // getting the operator
        String operator = require(OP_PAT, "expecting add or sub or mul or div", scanner);
        return operator;
    }

    /** COND  ::= "and" "(" COND "," COND ")" | "or" "(" COND "," COND ")" | "not" "(" COND ")"  | 
    RELOP "(" EXPR "," EXPR ") */
    public BooleanNode parseCond(Scanner scanner){
        // add is required here
        if(checkFor(AND_PAT, scanner)){
            //(
            require(this.OPENPAREN, "Expecting '('", scanner);
            // store the result of parseCond to cond1
            BooleanNode cond1 = parseCond(scanner);
            require(COLUMN_PAT, "Expecting ','", scanner);
            BooleanNode cond2 = parseCond(scanner);
            require(this.CLOSEPAREN, "Expecting ')'", scanner);
            // the AndNode contains cond1 and cond2 because I can check the logic(True or False) inside the AndNode class.
            return new AndNode(cond1, cond2);
        }
        // or is required here
        if(checkFor(OR_PAT, scanner)){
            require(this.OPENPAREN, "Expecting '('", scanner);
            BooleanNode cond1 = parseCond(scanner);
            require(COLUMN_PAT, "Expecting ','", scanner);
            BooleanNode cond2 = parseCond(scanner);
            require(this.CLOSEPAREN, "Expecting ')'", scanner);
            return new OrNode(cond1, cond2);
        }
        // not is required here
        if(checkFor(NOT_PAT, scanner)){
            require(this.OPENPAREN, "Expecting '('", scanner);
            BooleanNode cond1 = parseCond(scanner);
            require(this.CLOSEPAREN, "Expecting ')'", scanner);
            return new NotNode(cond1);
        }
        // the relop pat is required here
        if(scanner.hasNext(RELOP_PAT)){
            String string = scanner.next();
            // "(" required
            require(OPENPAREN, "expecting '('", scanner);
            IntNode e1 = parseExpr(scanner);
            require(COLUMN_PAT, "expecting ','", scanner);
            IntNode e2 = parseExpr(scanner);
            require(this.CLOSEPAREN, "expecting ')'", scanner);
            return new CondNode(string, e1, e2);
        }
        // required cond pat is not found
        fail("Expecting COND: 'add' or 'or' or 'not' or 'lt|gt|eq'", scanner);
        return null;
    }
    
    
    /** IF    ::= "if" "(" COND ")" BLOCK [ "elif"  "(" COND ")"  BLOCK ]* [ "else" BLOCK ] */
    public ProgramNode parseIf(Scanner scanner){
        List<BooleanNode> elifCondList = new ArrayList<>();
        List<ProgramNode> elifBlockList = new ArrayList<>();
        ProgramNode elseBlockNode = null;
        
        // expected if pat and return the next token
        require(IF_PAT, "expecting 'if'", scanner);
        // expected ( and return the next token, Cond pat
        require(OPENPAREN, "expecting '('", scanner);

        // paseCond to see if the next token is Cond pat
        BooleanNode condNode = parseCond(scanner);
        require(CLOSEPAREN, "expecting ')'", scanner);
        // parseBlock to see if the next token is block pat
        ProgramNode blockNode = parseBlock(scanner);
        // as long as the next token is elif pat, we keep parsing it
        while(scanner.hasNext(ELIF_PAT)){
            scanner.next();
            require(this.OPENPAREN, "expecting '(' after elif", scanner);
            // the condition of elif
            BooleanNode elifCondNode = parseCond(scanner);
            require(this.CLOSEPAREN, "expecting ')' after COND", scanner);
            // the body of the if statement
            ProgramNode elifBlockNode = parseBlock(scanner);
            // add the condition and the body to the list
            elifCondList.add(elifCondNode);
            elifBlockList.add(elifBlockNode);
            
        }
        if(scanner.hasNext(ELSE_PAT)){
            // skip the "else"
            scanner.next();
            // for the next token, { is required
            if(!scanner.hasNext(this.BRACEL_PAT)){
                fail("Expecting '{' after 'else'", scanner);
            }
            // the else{...} contains the information in BLOCK
            elseBlockNode = parseBlock(scanner);
            // pass condNode, blockNode and elseBlockNode so we can check the condition, execute the block node
            // print the grammer inside IfNode
            
        }
        // if there is no else, make the blockNode for else to be null
        return new IfNode(condNode, blockNode, elseBlockNode, elifCondList, elifBlockList);

    }

    /** WHILE ::= "while" "(" COND ")" BLOCK */
   
    public ProgramNode parseWhile(Scanner scanner){
        require(WHILE_PAT, "expecting 'while'", scanner);
        require(OPENPAREN, "expecting '('", scanner);
        // paseCond checking if the next token is Cond pat
        // store the information to the condNode
        BooleanNode condNode = parseCond(scanner);
        require(CLOSEPAREN, "expecting ')'", scanner);
        // parseBlock to see if the next token is block pat
        ProgramNode blockNode = parseBlock(scanner);
        return new WhileNode(condNode, blockNode);
    }

    /** EXPR   ::= NUM | SENS | VAR | OP "(" EXPR "," EXPR ")" */
    public IntNode parseExpr(Scanner scanner){
        // if the next token is num, numNode
        if(scanner.hasNext(NUMPAT)){
            return new NumNode(scanner.nextInt());
        }
        // if the next token is sens, parseSens to get the node
        if(scanner.hasNext(SENS_PAT)){
            return parseSens(scanner);
        }
        
        if(scanner.hasNext(VARPAT)){
            String next = scanner.next();
            // if the map does not contain the variable, it is a new variable, pass 0 as value
            if(!varMap.containsKey(next)){
                varMap.put(next, 0);
            }
            // if there is no equal pat, that means we use this variable but not assign it a value
            if(!scanner.hasNext(this.EQUALPAT)){
                return new VarNode(next, varMap);
            }
            fail("Unknown commend", scanner);
        }
        
        // if the next is one of the operator
        if(scanner.hasNext(OP_PAT)){
            // get the operator
            String operator = parseOp(scanner);
            require(this.OPENPAREN, "Expecting '('", scanner);
            // parse the first expression
            IntNode e1 = parseExpr(scanner);
            require(COLUMN_PAT, "Expecting ';'", scanner);
            // parse the second expression
            IntNode e2 = parseExpr(scanner);
            require(this.CLOSEPAREN, "Expecting ')'", scanner);
            return new OPNode(operator, e1, e2);
        }
        
        // the expected expr is not found here.
        
        fail("Expecting EXPR:  'number' or 'sens' or 'add|div|sub|mul'", scanner);
        return null;
    }

    /** ASSGN ::= VAR "=" EXPR */
    public ProgramNode parseAssgn(Scanner scanner){
        // if the next token is varpat
        if(scanner.hasNext(VARPAT)){
            // getting the variable
            String variable = scanner.next();
            require(EQUALPAT, "expecting '='", scanner);
            // we parse the expression so we know the value the variable will be
            IntNode expr = parseExpr(scanner);
            return new AssgnNode(variable, expr, varMap);
        }
        fail("expecting VAR pat", scanner);
        return null;
    }
}

// You could add the node classes here or as separate java files.
// (if added here, they must not be declared public or private)
// For example:
//  class BlockNode implements ProgramNode {.....
//     with fields, a toString() method and an execute() method
//


/**
 * all the class implements this interface need to have this method to return a value as integer
 *
 * @author (your name)
 * @version (a version number or a date)
 */
interface IntNode
{
    public int value(Robot robot);
    
}


/**
 * All the class implements this interface have to have isTrue method to check if the condition is correct
 *
 * @author (your name)
 * @version (a version number or a date)
 */
interface BooleanNode
{
    public boolean isTrue(Robot robot);
}



/**
 * execute the robot to move
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class MoveNode implements ProgramNode
{
    private IntNode num;
    public MoveNode(IntNode num)
    {
        this.num = num;
    }
    
    public String toString(){ return "move(" + num.toString() + ");\n" ;}
    
    public void execute(Robot robot){
        int times = num.value(robot);
        for(int i = 0; i < times; i++){
            robot.move();
            
        }
        
    }
    
}

/**
 * TurnRNode, executing the robot to turn right
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class TurnRNode implements ProgramNode
{
    public TurnRNode(){};
    
    public String toString(){ return "turnR;\n";}
    
    public void execute(Robot robot){
        robot.turnRight();
    }
}

/**
 * Write a description of class WaitNode here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class WaitNode implements ProgramNode
{
    private IntNode num;
    public WaitNode(IntNode num)
    {
        this.num = num;
    }
    
    public String toString(){ return "wait(" + num.toString() + ");\n";}
    
    public void execute(Robot robot){
        int times = num.value(robot);
        for(int i = 0; i < times; i++){
            robot.idleWait();
        }
        
    }
}

/**
 * executing the robot to turn left
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class TurnLNode implements ProgramNode
{
    public TurnLNode(){};
    
    public String toString(){ return "turnL;\n";}
    
    public void execute(Robot robot){
        robot.turnLeft();
    }
}

/**
 * executing the robot to take the fuel
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class TakeFuelNode implements ProgramNode
{
    public TakeFuelNode(){};
    
    public String toString(){ return "takeFuel;\n";}
    
    public void execute(Robot robot){
        robot.takeFuel();
    }
}

/**
 * checking which condition is true and executing the corresponding body of the statement
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class IfNode implements ProgramNode
{
    BooleanNode condNode;
    ProgramNode ifNode;
    ProgramNode elseNode;
    List<BooleanNode> elifCond;
    List<ProgramNode> elifBlock;
    Map<BooleanNode, ProgramNode> elifCondtion;
    IfNode(BooleanNode condNode, ProgramNode ifNode, ProgramNode elseNode, List<BooleanNode> elifCond, List<ProgramNode> elifBlock){
        this.condNode = condNode;
        this.ifNode = ifNode;
        this.elseNode = elseNode;
        this.elifCond = elifCond;
        this.elifBlock = elifBlock;
    }
    public void execute(Robot robot){
        Boolean ifBoolean = condNode.isTrue(robot);
        Boolean elifTrue = false;
        // executing the if body if if statment is true
        if(ifBoolean){
            ifNode.execute(robot);
            return;
        }
        // if one of the elif condition is true, executing the corresponding body
        for(int i = 0; i < elifCond.size(); i++){
            Boolean elifBoolean = elifCond.get(i).isTrue(robot);
            if(elifBoolean){
                elifTrue = true;
                elifBlock.get(i).execute(robot);
                return;
            }
            
        }
        // if none of them are correct, execute the else body
        if(elseNode != null && !ifBoolean && !elifTrue){
            elseNode.execute(robot);
        }
        
    }
    public String toString(){
        String ans = "if(" + condNode.toString() + ")" + ifNode.toString();
        for(int i = 0; i < elifCond.size(); i++){
            ans += "elif(" + elifCond.get(i).toString() + ")" + elifBlock.get(i).toString();
        }
        if(elseNode != null){
            ans += "\nelse" + elseNode.toString();
        }
        return ans;
    }
    
}


/**
 * keeping executing the body of while as long as the contion is true
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class WhileNode implements ProgramNode
{
    BooleanNode condNode;
    ProgramNode block;
    WhileNode(BooleanNode condNode, ProgramNode block){
        this.condNode = condNode;
        this.block = block;
    }
    public void execute(Robot robot){
        // execut if the condition is true
        while(condNode.isTrue(robot)){
            block.execute(robot);
        }
    }

    public String toString(){
        String ans = "while(" + condNode.toString() + ")" + block.toString();
        return ans;
    }
}

/**
 * executing the statement one by one
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class ProgNode implements ProgramNode
{
    public List<ProgramNode> stmts = new ArrayList<>();
    
    ProgNode(List<ProgramNode> stmts){
        this.stmts = stmts;
    }
    
    public void execute(Robot robot){
        for(int i = 0; i < stmts.size(); i++){
            stmts.get(i).execute(robot);
        }
    }
    
    public String toString(){
        String ans = "";
        for(int i = 0; i < stmts.size(); i++){
            ans += stmts.get(i).toString();
        }
        return ans;
    }
}

/**
 * keep looping the body of the "loop"
 *
 * @author (your name)
 * @version (a version number or a date)
 */

// LOOP  ::= "loop" BLOCK
class LoopNode implements ProgramNode
{
    // private ProgramNode block = new BlockNode(new ArrayList<ProgramNode>());
    private ProgramNode block;
    public LoopNode(ProgramNode block){
        this.block = block;
    }
    public void execute(Robot robot){
        // keep looping and executing the body of block
        while(true){
            block.execute(robot);
        }
        
    }
    
    public String toString(){
        return "loop" + block.toString();
    }
}



/**
 * getting the statment and executing it one by one
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class BlockNode implements ProgramNode
{
    
    private List<ProgramNode> acts = new ArrayList<>();
    BlockNode(List<ProgramNode> acts){
        this.acts = acts;
    }
    
    public void execute(Robot robot){
        for(ProgramNode node: acts){
            node.execute(robot);
            
        }
    }
    public String toString(){
        String ans = "{\n";
        for(int i = 0; i < acts.size(); i++){
            ans += acts.get(i).toString();
        }
        return ans + "\n}";
    }
}


/**
 * OppFBNode, getting an integer
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class OppFBNode implements IntNode
{
    private int distance;
    public int value(Robot robot){
        distance = robot.getOpponentFB();
        return distance;
    }
    
    
    
    public String toString(){
        return "oppFB";
    }
}


/**
 * OppLRNode, getting the integer
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class OppLRNode implements IntNode
{
    private int distance;
    public int value(Robot robot){
        distance = robot.getOpponentLR();
        return distance;
    }
    
    public String toString(){
        return "OppLR";
    }
    
    
}


/**
 * getting the distance from the closest barrelFB or the specific barrelFB
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class BarrelFBNode implements IntNode
{
    private int distance;
    private IntNode num;
    
    public BarrelFBNode(IntNode num){
        this.num = num;
    }
    public int value(Robot robot){
        if(num != null){
            distance = robot.getBarrelFB(num.value(robot));
        }
        else{
            distance = robot.getClosestBarrelFB();
        }
        return distance;
    }
    
    public String toString(){
        if(num != null){
            return "BarrelFB(" + num.toString() + ")";
        }
        return "BarrelFB";
    }
}


/**
 * getting the distance from the robot to the closest barrelLR or the specific one.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class BarrelLRNode implements IntNode
{
    private int distance;
    private IntNode num;
    public BarrelLRNode(IntNode num){
        this.num = num;
    }
    public int value(Robot robot){
        if(num != null){
            distance = robot.getBarrelLR(num.value(robot));
        }
        else{
             distance = robot.getClosestBarrelLR();
        }
       
        return distance;
    }
    
    
    public String toString(){
        if(num != null){
            return "BarrelLR(" + num.toString() + ")";
        }
        return "BarrelLR";
    }
}


/**
 * ShieldOnNode, do the execution
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class ShieldOnNode implements ProgramNode
{
    public ShieldOnNode(){};
    
    public void execute(Robot robot){
        robot.setShield(true);
    }
    
    public String toString(){
        return "shieldOn;";
    }
}


/**
 * getting the remain fuel as an integer
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class FuelLeftNode implements IntNode
{
    private int remain = 0;
    public int value(Robot robot){
        remain = robot.getFuel();
        return remain;
    }
    
    
    
    public String toString(){
        return "FuelLeft";
    }
    
}


/**
 * turnAroundNode, executing the robot to turn around
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class TurnAroundNode implements ProgramNode
{
    TurnAroundNode(){};
    public void execute(Robot robot){
        robot.turnAround();
    }
    public String toString(){
        return "turnAround;";
    }
}


/**
 * WallDistNode, getting the distance as an integer.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class WallDistNode implements IntNode
{
    private int distance;
    public int value(Robot robot){
        distance = robot.getDistanceToWall();
        return distance;
    }

    public String toString(){
        return "wallDist";
    }
}


/**
 * executing the robot to sheildOff
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class ShieldOffNode implements ProgramNode
{
    ShieldOffNode(){};
    public void execute(Robot robot){
        robot.setShield(false);
    }
    public String toString(){
        return "sheildOff;";
    }
}


/**
 * getting the number of barrels of the robot
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class NumBarrelsNode implements IntNode
{
    private int number;
    public int value(Robot robot){
        number = robot.numBarrels();
        return number;
    }
    
    
    
    public String toString(){
        return "numBarrels";
    }
}


/**
 * creating the num node for the normal number, getting an integer
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class NumNode implements IntNode
{
    private int num;
    NumNode(int num){
        this.num = num;
    }
    public int value(Robot robot){
        return num;
    }
    
    public String toString(){
        return "" + num;
    }
}


/**
 * compare the size of two integer, getting the boolean of the condition
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class CondNode implements BooleanNode
{
    private IntNode e1;
    private IntNode e2;
    private String sign = "";
    CondNode(String sign, IntNode e1, IntNode e2){
        this.sign = sign;
        this.e1 = e1;
        this.e2 = e2;
    }
    public boolean isTrue(Robot robot){
        int value1 = e1.value(robot);
        int value2 = e2.value(robot);
        if(sign.equals("lt")){
            return value1 < value2;
        }
        else if(sign.equals("gt")){
            return value1 > value2;
        }
        return value1 == value2;
    }
    
    public String toString(){
        return sign + "(" + e1.toString() + ", " + e2.toString() + ")";
    }
    
}






/**
 * add/sub/mul/div two integer to get a new value
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class OPNode implements IntNode
{
    private IntNode num1;
    private IntNode num2;
    private String operator;
    
    OPNode(String operator, IntNode num1, IntNode num2){
        this.operator = operator;
        this.num1 = num1;
        this.num2 = num2;
    }
    public int value(Robot robot){
        int value1 = num1.value(robot);
        int value2 = num2.value(robot);
        if(operator.equals("add")){
            
            return value1 + value2;
        }
        else if(operator.equals("sub")){
            return value1 - value2;
        }
        else if(operator.equals("mul")){
            return value1 * value2;
        }
        return value1 / value2;
    }
    
    public String toString(){
        return operator + "(" + num1.toString() + ", " + num2.toString() + ")";
    }
    
}


/**
 * checking if both the conditions are correct
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class AndNode implements BooleanNode
{
    private BooleanNode cond1;
    private BooleanNode cond2;
    
    public AndNode(BooleanNode cond1, BooleanNode cond2){
        this.cond1 = cond1;
        this.cond2 = cond2;
    }
    
    public boolean isTrue(Robot robot){
        return (cond1.isTrue(robot) && cond2.isTrue(robot));
    }
    
    public String toString(){
        return "and(" + cond1.toString() + ", " + cond2.toString() + ")";
    }
}


/**
 * checking the either condition is true or not
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class OrNode implements BooleanNode
{
    private BooleanNode cond1;
    private BooleanNode cond2;
    
    public OrNode(BooleanNode cond1, BooleanNode cond2){
        this.cond1 = cond1;
        this.cond2 = cond2;
    }
    
    public boolean isTrue(Robot robot){
        return (cond1.isTrue(robot) || cond2.isTrue(robot));
    }
    
    public String toString(){
        return "or(" + cond1.toString() + ", " + cond2.toString() + ")";
    }
}


/**
 * negate the condition
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class NotNode implements BooleanNode
{
    private BooleanNode cond1;
    
    public NotNode(BooleanNode cond1){
        this.cond1 = cond1;
    }
    
    public boolean isTrue(Robot robot){
        return !(cond1.isTrue(robot));
    }
    
    public String toString(){
        return "not(" + cond1.toString() + ")";
    }
}



/**
 * The node for variable, getting the integer value of it.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class VarNode implements IntNode
{
    private Map<String, Integer> map;
    private String string;
    VarNode(String string, Map<String, Integer> map){
        this.string = string;
        this.map = map;
    }
    public int value(Robot robot){
        // getting the value of the variable
        return map.get(string);
    }
    
    public String toString(){
        return string;
    }
}



/**
 * The AssgnNode, update the value of the variable when trying to assign a value to it
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class AssgnNode implements ProgramNode{
    private String variable;
    private IntNode expr;
    private Map<String, Integer> map;
    public AssgnNode(String variable, IntNode expr, Map<String, Integer> map){
        this.variable = variable;
        this.expr = expr;
        this.map = map;
    }
    public void execute(Robot robot){
        // update the value of the variable when creating the AssgnNode object and execute the execute method
        map.put(variable, expr.value(robot));
    }
    public String toString(){
        return variable + " = " + expr.toString();
    }
    public IntNode getExpr(){
        return expr;
    }
}

















