package leetcode.solution;
import java.util.*;
public class Solution68 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public List<String> fullJustify(String[] words, int maxWidth) {
        if(words == null) return null;
        List<String> res = new ArrayList<String>();
        if(words.length == 0) return res;


        int i = 0;
        while(i < words.length){
            int count = 0;
            int sum = 0;
            String s = "";

            while(i < words.length && sum <= maxWidth + 1){
                if(sum + words[i].length() > maxWidth){ // no need to add.
                    break;
                }else{ // add one word and a space.
                    count++;
                    sum += words[i].length();
                    sum++; //the last step add an extra space. 
                    i++;
                }
            }
           // meet the end or only have one word--> Left justification.
                if(count == 1 || i > words.length - 1){ 
                    for(int k = i - count; k < i; k++){
                        s += words[k];
                        if(k == i - 1){break;} // last word
                        s += " ";
                    }
                    while(s.length() < maxWidth){
                        s += " "; 
                    }
                //middle justification
                }else{
                    int avespace = (maxWidth - sum + 1) / (count - 1);
                    int reminder = (maxWidth - sum + 1) % (count - 1);

                    for(int k = i - count; k < i; k++){
                        s += words[k];
                        if(k == i - 1){break;} // last word

                        //Extra spaces between words should be distributed as evenly as possible. 
                        // add one regular space and #average space.
                        for(int kk = 0; kk <= avespace; kk++){ 
                            s += " ";
                        }
                        //If the number of spaces on a line do not divide evenly between words, the empty slots on the left will be assigned more spaces than the slots on the right. 
                        if(k < i - count + reminder){ // a
                            s += " ";
                        }
                    }
                }//
            res.add(s);
        }
        return res;
    }
}
