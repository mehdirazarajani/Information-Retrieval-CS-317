/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkgboolean.ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

/**
 *
 * @author Mehdi Raza Rajani
 */
public class inffixToPostfix {
    
    static int precedence (String st){
        switch (st){
            case "!":
                return 3;
            case "&&":
                return 2;
            case "||":
                return 1;
        }
        return -1;
    }
    
    static String infixToPostfix(String exp){
        String result = new String();
        Stack<String> stack = new Stack<>();
        ArrayList<String> expList = new ArrayList(Arrays.asList(exp.split("#")));
        expList.remove(0);
//        System.out.println(expList);
        for (String s : expList){
            //if any word
            if (!"!".equals(s) && !"&&".equals(s) && !"||".equals(s) && !"(".equals(s) && !")".equals(s))
                result += (s + "#");
            else if ("(".equals(s)){
                stack.push(s);
//                System.out.println("abc%"+s);
            }
            else if (")".equals(s)){
                while (!stack.isEmpty() && !"(".equals(stack.peek()))
                {
//                    System.out.println("wer%"+stack.peek());
                    result += (stack.pop() + "#");
                }
                if (!stack.isEmpty() && !"(".equals(stack.peek()))
                    return "INVALID EXPRESSION";
                else 
                    stack.pop();
            }
            else {
                //operator
                while (!stack.isEmpty() && precedence(s) <= precedence(stack.peek())) 
                    result += (stack.pop() + "#"); 
                stack.push(s); 
            }
        }
        
        while (!stack.isEmpty()){
//            System.out.println("er%"+stack.peek());
            result += (stack.pop() + "#"); 
        }
//        System.out.println(result);
        return result;
    }
    
}
