package leetcode;

/*****************************************************************************
 *      Demonstrates Comparable and Comparator interfaces
 *
 *****************************************************************************/
import java.util.*;

public class SortDemo2
{
    public static void main(String[] args)
    {
        //sort an array of cards by using the Comparable interface
        String[] suits = {"Diamonds", "Hearts", "Spades", "Clubs"};
        Card[] hand = new Card[5];
        Random rand = new Random();;

        for (int i = 0; i < 5; i++)
            hand[i] = new Card(suits[rand.nextInt(4)], rand.nextInt(12));

        System.out.println("(suit, value):  ");
        System.out.println(Arrays.toString(hand));

        System.out.println("\nsort by value");
        Arrays.sort(hand);
        System.out.println(Arrays.toString(hand));

        //sort an array of cards by using the Comparator
        Arrays.sort(hand, new SuitSort());
        System.out.println("\nsort by suit");
        System.out.println(Arrays.toString(hand));

        /*  TREESET  */
        for (int i = 0; i < 5; i++)
            hand[i] = new Card(suits[rand.nextInt(4)], rand.nextInt(12));

        System.out.println("(suit, value):  ");
        System.out.println(Arrays.toString(hand));
        System.out.println();

        //cards with the same value collide
        TreeSet<Card> tree1 = new TreeSet<Card>();
        for (int i = 0; i < 5; i++) tree1.add( hand[i] );
        System.out.println(tree1);
        System.out.println();

        //cards with the same suit collide
        TreeSet<Card> tree2 = new TreeSet<Card>( new SuitSort() );
        for (int i = 0; i < 5; i++) tree2.add( hand[i] );
        System.out.println(tree2);
        System.out.println();

        //no collisions
        TreeSet<Card> tree3 = new TreeSet<Card>( new SuitValue() );
        for (int i = 0; i < 5; i++) tree3.add( hand[i] );
        System.out.println(tree3);
        System.out.println();
    }
}

class Card implements Comparable<Card>
{
    private String suit;
    private int value;

    public Card(String suit, int value)
    {
        this.suit = suit;
        this.value = value;
    }
    public int getValue()
    {
        return value;
    }
    public String getSuit()
    {
        return suit;
    }
    public String toString()
    {
        return "(" + suit + "," + value +")";
    }
    public int compareTo(Card x)
    {
        return getValue() - x.getValue();
    }
}

class SuitSort implements Comparator<Card>
{
    public int compare(Card x, Card y)
    {
        return x.getSuit().compareTo( y.getSuit() );
    }
}

class SuitValue implements Comparator<Card>
{
    public int compare(Card x, Card y)
    {
        int n = x.getSuit().compareTo( y.getSuit() );
        if( n != 0 )
            return n;
        else
            return x.getValue() - y.getValue();
    }
}
