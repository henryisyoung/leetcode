//package leetcode;
//import java.util.*;
//public class NestedIterator implements Iterator<Integer> {
//    Stack<NestedInteger> stack = new Stack<NestedInteger>();;
//    public NestedIterator(List<NestedInteger> nestedList) {
//        if(nestedList == null){
//        	return;
//        }
//        for(int i = nestedList.size() - 1; i >= 0; i--){
//        	stack.push(nestedList.get(i));
//        }
//    }
//
//
//
//	@Override
//    public Integer next() {
//        return stack.pop().getInteger();
//    }
//
//    @Override
//    public boolean hasNext() {
//    	while(!stack.isEmpty()){
//        	NestedInteger n = stack.peek();
//        	if(n.isInteger()){
//        		return true;
//        	}else{
//        		stack.pop();
//        		for(int i = n.getList().size() - 1; i >= 0; i--){
//        			stack.push(n.getList().get(i));
//        		}
//        	}
//    	}
//    	return false;
//    }
//}
