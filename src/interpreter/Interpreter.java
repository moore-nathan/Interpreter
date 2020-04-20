//First part of the semster project
package interpreter;

/**
 *
 * @author Nathan Moore
 */
import java.util.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
//import org.apache.commons.lang3.StringUtils;
public class Interpreter{
    public static final String FILE = "data/test.txt";
    public ArrayList<Variable> varArr;
   // var
    int m, n;
    String[] prefixes = {"+", "-", "~", "*", "/", "//", "%", "&", "|", "!", 
        "==", "!=", "<", ">", "<=", ">="};
    String[] typesList = {"int", "double", "string", "char", "boolean"};
    public Interpreter() {
        Random rnd = new Random();
        varArr = new ArrayList();
        parse();
    }//constructor
    
    private void read(Scanner in){
        Scanner keyboard = new Scanner(System.in);
        String parse, msg = "";
        parse = in.next();
        
        if(parse.startsWith("\"")){
            parse = parse.substring(1, parse.length());
            //System.out.print(parse.substring(1, parse.length() -2) + ":")            parse = in.next();
            while(parse.endsWith("\"") == false){
                msg += " " + parse;
                parse = in.next();
            }
            msg += " " + parse.substring(0, parse.length()-1);
            
            //System.out.print(msg.trim() + ":");
        }
        parse = in.next();
        while(parse.equals(";") == false){
            System.out.print(msg.trim());
            varArr.get(find(parse)).value = keyboard.nextLine();
            parse = in.next();
        }
//        in.next();
    }//read
    
    private int find(String target){
        for(int i = 0; i < varArr.size(); i++){
            if(varArr.get(i).name.equals(target)){
                return i;
            }
        }
        return -1;
    }//find
    
    private void inputVar(Scanner in){
        while(in.hasNext("endvar") == false){
            String type = in.next();
            while(in.hasNext(";") == false){
                varArr.add(new Variable(in.next(), type));
            }
            in.next(); //skip over the ";"
        }
        in.next(); //skip over the "endvar"
    }//inputVar
    
    public void assign(Scanner in){
        String var = in.next();
        
        String type = "";
        String finalExpression = "";
        String finalExpType;
        boolean coercions = false;
        //varArr.get(find(var)).value = in.next();
        
        String exp = in.next();
        for (String typeList : typesList) {//pretty cool for loop
            if (exp.equals(typeList)) {
                type = typeList;
                coercions = true;
                exp = in.next();
            }
        }
        int findVar = find(var);
        if(find(exp) >= 0){
            finalExpression = varArr.get(find(exp)).value;
        }
        else if(isPrefix(exp)){
            if(isBinaryOp(exp))
                finalExpression = Double.toString(preValue(in, exp));
            else if(isLogicOp(exp))
                finalExpression = Boolean.toString(logic(in, exp, in.next()));
            else
                finalExpression = Boolean.toString(compare(in, exp, in.next()));
        }else{
            finalExpression = literal(exp, in);
        }
        
        finalExpType = valueType(finalExpression);
        
        if(coercions){//actions taken if a explict coercion is happening
            if(type.equals("int")){
                if(finalExpType.equals("int")){
                    varArr.get(findVar).value = finalExpression;
                }else if(finalExpType.equals("double")){
                    Integer x = Double.valueOf(finalExpression).intValue();
                    varArr.get(findVar).value = x.toString();
                }else if(finalExpType.equals("String")){
                    try{
                        varArr.get(findVar).value = Integer.valueOf(finalExpression).toString();
                    }catch(Exception e){
                        System.out.println("STRING CANNOT BE CONVERTED");
                    }
                }else if(finalExpType.equals("char")){
                    Integer x = (int)finalExpression.charAt(0);//changed from 0 to 1
                    varArr.get(findVar).value = x.toString();
                }else{//boolean
                    if(finalExpression.equals("false"))
                        varArr.get(findVar).value = "0";
                    else
                        varArr.get(findVar).value = "1";
                }//type.equals("int")
            }else if(type.equals("double")){
                if(finalExpType.equals("int")){
                    Double x = Integer.valueOf(finalExpression).doubleValue();
                    varArr.get(findVar).value = x.toString();
                }else if(finalExpType.equals("double")){
                    varArr.get(findVar).value = finalExpression;
                }else if(finalExpType.equals("String")){
                    try{
                        varArr.get(findVar).value = Double.valueOf(finalExpression).toString();
                    }catch(Exception e){
                        System.out.println("STRING CANNOT BE CONVERTED");
                    }
                }else if(finalExpType.equals("char")){
                    Double x = (double)finalExpression.charAt(0);//changed from 1 to 0
                    varArr.get(findVar).value = x.toString();
                }else{//boolean
                    if(finalExpression.equals("false"))
                        varArr.get(findVar).value = "0";
                    else
                        varArr.get(findVar).value = "1.0";
                }//type.equals("double")
            }else if(type.equals("string")){
                //code
                varArr.get(findVar).value = finalExpression;
                //type.equals("string")
            }else if(type.equals("char")){
                if(finalExpType.equals("int")){
                    Integer x = Integer.valueOf(finalExpression);
                    varArr.get(findVar).value = Integer.toString(x % 256);
                }else if(finalExpType.equals("double")){
                    Integer x = Double.valueOf(finalExpression).intValue();
                    varArr.get(findVar).value = Integer.toString(x % 256);
                }else if(finalExpType.equals("String")){
                    if(isChar(finalExpression)){
                        varArr.get(findVar).value = finalExpression.substring(1,1);
                    }else{
                        System.out.println("STRING CANNOT BE CONVERTED");
                    }
                }else if(finalExpType.equals("char")){
                    varArr.get(findVar).value = finalExpression;
                }else{//boolean
                    System.out.println("BAD");
                }//type.equals("char")
            }else{//boolean
               if(finalExpType.equals("int")){
                   if(Integer.valueOf(finalExpression) == 0)
                        varArr.get(findVar).value = "true";
                   else
                       varArr.get(findVar).value = "false";
                }else if(finalExpType.equals("double")){
                    if(Double.valueOf(finalExpression) == 0)
                        varArr.get(findVar).value = "true";
                   else
                       varArr.get(findVar).value = "false";
                }else if(finalExpType.equals("String")){
                    System.out.println("STRING CANNOT BE CONVERTED");
                }else if(finalExpType.equals("char")){
                    System.out.println("ILLEGAL");
                }else{//boolean
                    varArr.get(findVar).value = finalExpression;
                }//type.equals("char") 
            }
        }else{
            //varArr.get(find(var)).value = finalExpression;
            varArr.get(findVar).value = finalExpression;
        }
    }//assign
//    public void assign(Scanner in, String var, String type){
//        String exp = in.next();
//        
//        if(find(exp) >= 0){
//            varArr.get(find(var)).value = varArr.get(find(exp)).value;
//        }
//        else if(isPrefix(exp)){
//            if(isBinaryOp(exp))
//                varArr.get(find(var)).value = Double.toString(preValue(in, exp));
//            else if(isLogicOp(exp))
//                varArr.get(find(var)).value = Boolean.toString(logic(in, exp, in.next()));
//            else
//                varArr.get(find(var)).value = Boolean.toString(compare(in, exp, in.next()));
//        }else{
//            varArr.get(find(var)).value = literal(exp, in);
//        }
//    }
    
    public String literal(String var, Scanner in){
        String output = "";
        
        if(var.startsWith("\"")){
            var = var.substring(1, var.length());
            //System.out.print(parse.substring(1, parse.length() -2) + ":")            parse = in.next();
            while(var.endsWith("\"") == false){
                output += var + " ";
                var = in.next();
            }
            output += var.substring(0, var.length()-1);
            return output;
        }else if(var.startsWith("'")){
            return var.substring(1,2);
        }else{
            return var;
        }  
    }//literal
    
    public boolean isPrefix(String var){
        for(int i = 0; i < prefixes.length; i++)
            if(prefixes[i].equals(var))
                return true;
        return false;
    }//isPrefix
    
    public boolean isBinaryOp(String var){
        //checks if it 
        for(int i = 0; i < 7; i++)
            if(prefixes[i].equals(var))
                return true;
        return false;
    }//isBinaryOp
    
    public boolean isLogicOp(String var){
        for(int i = 7; i < 12; i++)
            if(prefixes[i].equals(var))
                return true;
        return false;
    }//isLogicOp
    
    public boolean isCompareOp(String var){
        for(int i = 12; i < prefixes.length; i++)
            if(prefixes[i].equals(var))
                return true;
        return false;
    }//isCompareOp
    
    private boolean isInt(String var){
        try{
            Integer.valueOf(var);
            return true;
        }catch(Exception e){
            return false;
        }
    }//isInt
    
    private boolean isDouble(String var){
         try{
            Double.valueOf(var);
            return true;
        }catch(Exception e){
            return false;
        }
    }//isDouble
    
    private boolean isBoolean(String var){
        if(var.equals("true") || var.equals("false"))
            return true;
        else
            return false;
    }
    
    private boolean isChar(String var){
//        int x = var.length();
         return var.length() == 3 || var.length() == 1;
    }//isChar
    
    private String valueType(String var){
        if(isInt(var))
            return "int";
        else if(isDouble(var))
            return "double";
        else if(isChar(var))
            return "char";
        else if(isBoolean(var))
            return "boolean";
        else
            return "String";
    }
    
    
    public boolean isExplictCor(String var){
         for (String typeList : typesList) {//pretty cool for loop
            if (var.equals(typeList)) {
                return true;
            }
        }
         return false;
    }
    
    public String preValueCor(Scanner in, String type){
        String next = in.next();
        String finalExpression;
        if(isPrefix(next)){
            finalExpression = Double.toString(preValue(in, in.next()));
        }else if(isExplictCor(next)){
            finalExpression = preValueCor(in, next);
        }else{
            finalExpression = next;
        }
        
        String finalExpType = valueType(finalExpression);
        
        if(type.equals("int")){
            if(finalExpType.equals("int")){
                return finalExpression;
            }else if(finalExpType.equals("double")){
                Integer x = Double.valueOf(finalExpression).intValue();
                return x.toString();
            }else if(finalExpType.equals("String")){
                try{
                    return Integer.valueOf(finalExpression).toString();
                }catch(Exception e){
                    System.out.println("STRING CANNOT BE CONVERTED");
                }
            }else if(finalExpType.equals("char")){
                Integer x = (int)finalExpression.charAt(1);
                return x.toString();
            }else{//boolean
                if(finalExpression.equals("false"))
                    return "0";
                else
                    return "1";
            }//type.equals("int")
        }else if(type.equals("double")){
            if(finalExpType.equals("int")){
                Double x = Integer.valueOf(finalExpression).doubleValue();
                return x.toString();
            }else if(finalExpType.equals("double")){
                return finalExpression;
            }else if(finalExpType.equals("String")){
                try{
                    return Double.valueOf(finalExpression).toString();
                }catch(Exception e){
                    System.out.println("STRING CANNOT BE CONVERTED");
                }
            }else if(finalExpType.equals("char")){
                Double x = (double)finalExpression.charAt(1);
                return x.toString();
            }else{//boolean
                if(finalExpression.equals("false"))
                    return "0";
                else
                    return "1.0";
            }//type.equals("double")
        }else if(type.equals("string")){
            //code
            return finalExpression;
            //type.equals("string")
        }else if(type.equals("char")){
            if(finalExpType.equals("int")){
                Integer x = Integer.valueOf(finalExpression);
                return Integer.toString(x % 256);
            }else if(finalExpType.equals("double")){
                Integer x = Double.valueOf(finalExpression).intValue();
                return Integer.toString(x % 256);
            }else if(finalExpType.equals("String")){
                if(isChar(finalExpression)){
                    return finalExpression.substring(1,1);
                }else{
                    System.out.println("STRING CANNOT BE CONVERTED");
                }
            }else if(finalExpType.equals("char")){
                return finalExpression;
            }else{//boolean
                System.out.println("BAD");
            }//type.equals("char")
        }else{//boolean
           if(finalExpType.equals("int")){
               if(Integer.valueOf(finalExpression) == 0)
                    return "true";
               else
                   return "false";
            }else if(finalExpType.equals("double")){
                if(Double.valueOf(finalExpression) == 0)
                    return "false";
               else
                   return "true";
            }else if(finalExpType.equals("String")){
                System.out.println("STRING CANNOT BE CONVERTED");
            }else if(finalExpType.equals("char")){
                System.out.println("ILLEGAL");
            }else{//boolean
                return finalExpression;
            }//type.equals("char") 
        }
        return "";
    }
    
    public double preValue(Scanner in, String var){
        if(isPrefix(var) == false){
            if(isExplictCor(var)){
                return Double.valueOf(preValueCor(in, var));
            }
            if(find(var) >= 0)
                return Double.valueOf(varArr.get(find(var)).value);
            else
                return Double.valueOf(var);
        }else{
            if(var.equals("+"))
                return preValue(in, in.next()) + preValue(in, in.next());
            else if(var.equals("-"))
                return preValue(in, in.next()) - preValue(in, in.next());
            else if(var.equals("*"))
                return preValue(in, in.next()) * preValue(in, in.next());
            else if(var.equals("/"))
                return preValue(in, in.next()) / preValue(in, in.next());
            else if(var.equals("//"))
                return Math.floor(preValue(in, in.next()) + preValue(in, in.next()));
            else if(var.equals("%"))
                return preValue(in, in.next()) % preValue(in, in.next());
            else if(var.equals("~"))
                return -1 *  preValue(in, in.next());
        }
        return 0;//has to be here
    }//preValue
    
    public boolean logic(Scanner in, String prev, String var){
        //double and int version
        if((isLogicOp(var) == false || isLogicOp(prev) == false) && isCompareOp(var) == false){
            if(isExplictCor(var)){
                logic(in, prev, preValueCor(in, var));
            }else{
                if(prev.equals("&")){
                        return Boolean.valueOf(in.next()) && Boolean.valueOf(in.next());
                }else if(prev.equals("|")){
                        return Boolean.valueOf(in.next()) || Boolean.valueOf(in.next());
                }else if(prev.equals("!")){
                        return !Boolean.valueOf(in.next());
                }else if(prev.equals("==")){
                    if(isBinaryOp(var) || isDouble(var))
                        return preValue(in,var) == preValue(in,in.next());
                    else
                        return literal(var,in).equals(literal(in.next(),in));
                }else if(prev.equals("!=")){
                    if(isBinaryOp(var) || isDouble(var))
                        return preValue(in,var) != preValue(in,in.next());
                    else
                        return var.equals(in.next()) == false;
                }else if(isCompareOp(prev)){
                    return compare(in, prev, var);
                }
            }
        }else{//If any problems occur it is probably because of the change of the 2nd vars being changed to in.next()
            if(prev.equals("&")){
                boolean log1 = logic(in, var, in.next());
                boolean log2 = logic(in, in.next(), in.next());
                return log1 && log2;
            }
            else if(prev.equals("|")){
                boolean log1 = logic(in, var, in.next());
                boolean log2 = logic(in, in.next(), in.next());
                return log1 || log2;
            }
            else if(prev.equals("!")){
                return !logic(in, var, in.next());
            }
            else if(prev.equals("==")){
                return logic(in, var, in.next()) == logic(in, in.next(), in.next());
            }
            else if(prev.equals("!=")){
                return logic(in, var, in.next()) != logic(in, in.next(), in.next());
            }
        }
        return false;//has to be here
    }//logic
    
    public boolean compare(Scanner in, String var, String exp1){
        String exp2 = in.next();
        if(find(exp1) >= 0)
            exp1 = varArr.get(find(exp1)).value;
        if(find(exp2) >= 0)
            exp2 = varArr.get(find(exp2)).value;
                
        if(var.equals("<")){
            if(isBinaryOp(exp1) || isDouble(exp1))
                return preValue(in, exp1) < preValue(in, exp2);
            else if(isChar(exp1))
                return exp1.charAt(1) < exp2.charAt(1);
        }else if(var.equals(">")){
            if(isBinaryOp(exp1) || isDouble(exp1))
                return preValue(in, exp1) > preValue(in, exp2);
            else if(isChar(exp1))
                return exp1.charAt(1) > exp2.charAt(1);
        }else if(var.equals("<=")){
            if(isBinaryOp(exp1) || isDouble(exp1))
                return preValue(in, exp1) <= preValue(in, exp2);
            else if(isChar(exp1))
                return exp1.charAt(1) <= exp2.charAt(1);
        }else if(var.equals(">=")){
            if(isBinaryOp(exp1) || isDouble(exp1))
                return preValue(in, exp1) >= preValue(in, exp2);
            else if(isChar(exp1))
                return exp1.charAt(1) >= exp2.charAt(1);
        }
        return false;//has to be here
    }//compare
    
    public void ifStatement(Scanner in){
        String var = in.next();
        boolean guard = false;
        while(var.equals("fi") == false){
            if(var.equals("{") && guard == false){
                var = in.next();
                if(isLogicOp(var))
                    guard = logic(in, var, in.next());
                else
                    guard = compare(in, var, in.next());
                if (guard) {
                    in.next(); //for ":"
//                    if (in.hasNext("var")) {
//                        in.next(); //skip "var"
//                        inputVar(in);
//                    } else 
                    if (in.hasNext("read")) {
                        in.next(); //skip "read"
                        //System.out.println("read");
                        read(in);
                    } else if (in.hasNext("=")) {
                        in.next(); //skip "="
                        assign(in);
                    } else if (in.hasNext("print")) {
                        in.next(); //skip "print"
                        print(in);
                    }else if(in.hasNext("if")){
                        in.next();
                        ifStatement(in);
                    }
                } else {
                    var = in.nextLine();
                }
            }//if("{")
            var = in.next();
        }
    }
    
    public void print(Scanner in){
        while(in.hasNext(";") == false){
            String var = in.next();
            if(find(var) >= 0){
                if(varArr.get(find(var)).type.equals("int"))
                    System.out.print(Double.valueOf(varArr.get(find(var)).value).intValue());
                else
                    System.out.print(varArr.get(find(var)).value);
            }else if(isExplictCor(var)){
                System.out.print(preValueCor(in, var));
            }
            else if(var.equals(",")){
                System.out.print(" ");
            }else if(var.equals(",,")){
                System.out.println();
            }
            else if(isPrefix(var)){
                if(isBinaryOp(var))
                    System.out.print(preValue(in, var));
                else if(isLogicOp(var))
                    System.out.print(logic(in, var, in.next()));
                else
                    System.out.print(compare(in, var, in.next()));
            }else if(var.equals("null")){
                //do nothing
            }else{
                System.out.print(literal(var, in));
            }
            //System.out.println(varArr.get(find(in.next())).value);
        }
        System.out.println();
        in.next();
    }//print
    
    public void parse(){
        try{
            Scanner in = new Scanner(new FileInputStream(FILE));
             while (in.hasNext()) {
                if (in.hasNext("var")) {
                    in.next(); //skip "var"
                    inputVar(in);
                } else if (in.hasNext("read")) {
                    in.next(); //skip "read"
                    //System.out.println("read");
                    read(in);
                } else if (in.hasNext("=")) {
                    in.next(); //skip "="
                    assign(in);
                } else if (in.hasNext("print")) {
                    in.next(); //skip "print"
                    print(in);
                } else if (in.hasNext("if")) {
                    in.next();
                    ifStatement(in);
                    //System.out.println(ifStatement(in));
                    //Don't know if will do it this way or another
                }
            }
        }catch(FileNotFoundException e){
            System.out.println("File Not Found");
        } 
    }//parse
    
    public static void main(String[] args){
        //System.out.println(s.getTop());
//        System.out.println(!true);
//System.out.println(Boolean.valueOf("true") && Boolean.valueOf("false"));
        Interpreter p = new Interpreter();
//        System.out.println(p.varArr.size());
//        System.out.println(p.varArr.get(3).name);
//        System.out.println(p.varArr.get(2).value + " " + p.varArr.get(5).value);
    }
}