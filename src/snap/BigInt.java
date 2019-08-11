package snap;

import java.util.ArrayList;
import java.util.Scanner;

public class BigInt implements Comparable<BigInt> {

    private static final char MINUS_CHAR = '-';
    private static final char PLUS_CHAR = '+';

    // Saves the digits of the number - last element represents the smallest unit of the number
    private ArrayList<Integer> numberDigits = new ArrayList<>();

    // Indication if the number is negative
    private boolean negative;

    // String representation as given by the user
    private String stringNumber;

    BigInt(String number){

        if (number.equals("")){
            stringNumber = "0";
            numberDigits.add(0);
        }
        else {
            // Dealing with the positive/negative signs
            char firstChar = number.charAt(0);
            if (firstChar == MINUS_CHAR || firstChar == PLUS_CHAR) {
                if (firstChar == MINUS_CHAR)
                    negative = true;

                number = number.substring(1);
            }

            // Regex to remove zeros at the beginning of the number
            number = number.replaceFirst("^0+(?!$)", "");
            stringNumber = number;

            // Saving the digits
            for (int index = 0; index < number.length(); index++) {
                int curDigNumericVal = Character.getNumericValue(number.charAt(index));

                // Current char is not a valid digit
                if (curDigNumericVal == -1)
                    throw new IllegalArgumentException();

                numberDigits.add(curDigNumericVal);
            }
        }
    }

    private boolean isNegative() {
        return negative;
    }

    private void flipNegativity() {
        if (stringNumber == "0")
            return;

        negative = !negative;
        if (stringNumber.charAt(0) == MINUS_CHAR){
            stringNumber = stringNumber.substring(1);
        } else {
            stringNumber = MINUS_CHAR + stringNumber;
        }
    }

    // Adding another bigInt number to the current bigInt number
    BigInt plus(BigInt otherNumber) {

        // current is negative, other is positive - subtract current from the other
        if (negative && !otherNumber.isNegative()) {
            return otherNumber.minus(new BigInt(stringNumber));
        }

        // other is negative - subtract its value
        if (otherNumber.isNegative()) {
            return minus(new BigInt(otherNumber.toString()));
        }

        // Setting the longer number of the two numbers
        ArrayList<Integer> longerNumber, shorterNumber;
        if (numberDigits.size() >= otherNumber.numberDigits.size()) {
            longerNumber = numberDigits;
            shorterNumber = otherNumber.numberDigits;
        }
        else {
            longerNumber = otherNumber.numberDigits;
            shorterNumber = numberDigits;
        }

        int lengthsDifferences = longerNumber.size() - shorterNumber.size();


        StringBuilder resultString = new StringBuilder();

        // Initializing a carry for every addition
        int carry = 0;


        // Iterating from smallest unit digit to the biggest
        for (int index = shorterNumber.size() - 1; index >= 0; index--) {
            int shorterNumberDigit = shorterNumber.get(index);
            int longerNumberDigit = longerNumber.get(index + lengthsDifferences);

            int newDigit = shorterNumberDigit + longerNumberDigit + carry;

            // Calculating the carry and the new digit
            carry = newDigit / 10;
            newDigit = newDigit % 10;

            resultString.append(newDigit);
        }

        // Adding digits of longer number
        for (int index = lengthsDifferences - 1; index >= 0; index--) {
            int currDig = longerNumber.get(index);

            // Check if need to add carry
            if (currDig + carry == 10) {
                resultString.append(0);
                carry = 1;
            } else {
                resultString.append(currDig + carry);
                carry = 0;
            }
        }

        // Check if there is carry on last digit
        if (carry > 0)
            resultString.append(carry);

        return new BigInt(resultString.reverse().toString());
    }

    BigInt minus(BigInt otherNumber){

        // If the other number is negative, add its value
        if (otherNumber.isNegative()) {
            return plus(new BigInt(otherNumber.stringNumber));
        }

        // subtract a bigger number than the current
        if (this.compareTo(otherNumber) < 0) {
            BigInt result = otherNumber.minus(this);
            result.flipNegativity();
            return result;
        }

        // Other number is positive and not greater than current:
        int lengthsDifferences = numberDigits.size() - otherNumber.numberDigits.size();

        StringBuilder resultString = new StringBuilder();

        int carry = 0;

        for (int index = otherNumber.numberDigits.size() - 1; index >=0 ; index--) {
            int biggerNumDig = numberDigits.get(index + lengthsDifferences) - carry;
            int smallerNumDig = otherNumber.numberDigits.get(index);

            carry = 0;

            if (biggerNumDig < smallerNumDig){
                carry = 1;
                biggerNumDig += 10;
            }

            resultString.append(biggerNumDig - smallerNumDig);
        }

        for (int index = lengthsDifferences - 1; index >= 0; index--) {
            int currDig = numberDigits.get(index);

            // Check if carry is needed
            if (carry > currDig){
                resultString.append(currDig + 10 - carry);
                carry = 1;
            } else {
                resultString.append(currDig - carry);
                carry = 0;
            }
        }

        return new BigInt(resultString.reverse().toString());
    }

    // Multiply bigInt
    BigInt multiply(BigInt otherNumber){

        BigInt finalResult = new BigInt("0");
        BigInt currentUnit = new BigInt("1");

        for (int otherNumIndex = otherNumber.numberDigits.size() - 1; otherNumIndex >=0; otherNumIndex--){
            int currentOtherNumDigit = otherNumber.numberDigits.get(otherNumIndex);

            // Holds the result of multiplication of the number by the current digit of the other number

            BigInt currentResult = new BigInt("0");
            BigInt currentDigitUnit = new BigInt(currentUnit.toString());

            for (int index = numberDigits.size() - 1; index >=0; index--) {
                int currentDigit = numberDigits.get(index);
                int digitsMultiplication = currentDigit * currentOtherNumDigit;

                currentResult = currentDigitUnit.MultiplyUnit(digitsMultiplication);
                currentDigitUnit.multiplyByTen();
            }

            currentUnit.multiplyByTen();
            finalResult = finalResult.plus(currentResult);
        }

        // Check if need to flip negativity
        if (otherNumber.isNegative() && !isNegative() || isNegative() && !otherNumber.isNegative())
            finalResult.flipNegativity();

        return finalResult;
    }

    BigInt divide(BigInt otherNumber) {

        if (isBigIntZero(otherNumber))
            throw new ArithmeticException();

        // Handling the case where the current number is positive and the other is negative
        if (otherNumber.isNegative() && !isNegative()) {
            BigInt result = divide(new BigInt(otherNumber.stringNumber));
            result.flipNegativity();
            return result;

            // Handling the case where the current number is negative and the other is positive
        } else if (!otherNumber.isNegative() && isNegative()) {
            BigInt result = new BigInt(stringNumber).divide(otherNumber);
            result.flipNegativity();
            return result;
        }

        int compareResult = this.compareTo(otherNumber);
        if (compareResult == 0)
            return new BigInt("1");
        else if (compareResult < 0)
            return new BigInt("0");

        BigInt result = new BigInt("0");
        BigInt tempNumber = new BigInt("0");

        while (tempNumber.compareTo(this) < 0) {
            tempNumber = tempNumber.plus(otherNumber);
            result = result.plus(new BigInt("1"));
        }

        return result;

    }

    private boolean isBigIntZero(BigInt number) {
        return number.stringNumber.replace("0", "").equals("");

    }

    // Multiply a unit of BigInt with an integer. Example: 1000000000000000000 * 54
    private BigInt MultiplyUnit(int majorUnits){

        // Setting the string representation
        String majorUnitsString = String.valueOf(majorUnits);
        String newNumber = majorUnitsString + stringNumber.substring(1);

        return new BigInt(newNumber);
    }

    private void multiplyByTen() {
        this.numberDigits.add(0);
        stringNumber += '0';
    }

    @Override
    public int compareTo(BigInt other) {

        // Current is negative, other is positive
        if (isNegative() && !other.isNegative())
            return -1;

            // Current is positive, other is negative
        else if (!isNegative() && other.isNegative()){
            return 1;
        }

        // Both are negative
        else if (isNegative()){
            // Current is negative and has more digits - therefore it is smaller
            if (numberDigits.size() > other.numberDigits.size())
                return -1;
                // Current is negative and has less digits - Therefore it is bigger
            else if (numberDigits.size() < other.numberDigits.size())
                return 1;

                // Both have same number of digits - need to iterate them
            else
                for (int index = 0; index < numberDigits.size(); index++) {

                    // Current has bigger negative digit - therefore it is smaller
                    if (numberDigits.get(index) > other.numberDigits.get(index))
                        return -1;

                        // Current has smaller negative digit - therefore it is smaller
                    else if (numberDigits.get(index) < other.numberDigits.get(index))
                        return 1;
                }

            // If we have reached here, the numbers are completely identical
            return 0;
        }

        // If we have reached here, both numbers are positive

        // Current is positive and has more digits - Therefore it is bigger
        if (numberDigits.size() > other.numberDigits.size()) {
            return 1;
        }

        // Current is positive and has less digits - Therefore it is smaller
        else if (numberDigits.size() < other.numberDigits.size())
            return -1;

            // Both have same number of digits - need to iterate them
        else
            for (int index = 0; index < numberDigits.size(); index++) {

                // Current has bigger positive digit - therefore it is bigger
                if (numberDigits.get(index) > other.numberDigits.get(index))
                    return 1;

                    // Current has smaller positive digit - therefore it is smaller
                else if (numberDigits.get(index) < other.numberDigits.get(index))
                    return -1;
            }

        // If we have reached here, the numbers are completely identical
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        // self check
        if (this == o)
            return true;

        // null check
        if (o == null)
            return false;

        // type check and cast
        if (getClass() != o.getClass())
            return false;

        BigInt other = (BigInt) o;
        // field comparison

        return other.toString().equals(stringNumber);
    }

    @Override
    public String toString() {
        return stringNumber;
    }


    public static void main(String[] args) {
        BigInt firstNumber;
        BigInt secondNumber;

        System.out.println("Enter first number: ");
        firstNumber = inputBigIntNumber();

        System.out.println("Enter second number: ");
        secondNumber = inputBigIntNumber();

        System.out.println("The result of plus is: " + firstNumber.plus(secondNumber));
        System.out.println("The result of minus is: " + firstNumber.minus(secondNumber));
        System.out.println("The result of multiply is: " + firstNumber.multiply(secondNumber));

        try {
            System.out.println("The result of divide is: " + firstNumber.divide(secondNumber));
        } catch (ArithmeticException ex){
            System.out.println("Can not divide by zero");
        }

    }

    // Taking a valid integer input from the user (greater than 0)
    private static BigInt inputBigIntNumber(){
        Scanner scanner = new Scanner(System.in);

        String str = scanner.nextLine();

        while (true) {
            try {
                return new BigInt(str);
            }
            catch (IllegalArgumentException ex) {
                System.out.println("Invalid number, please try again: ");
            }
        }
    }
}
