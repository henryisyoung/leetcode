package contest;

public class FindWinnerofArrayGame {
    public static int getWinner(int[] arr, int k) {
        int winner= arr[0];
        int count=0;
        for(int i=1; i<arr.length; i++) {
            if(winner>arr[i])
                count++;
            else {
                winner= arr[i];
                count=1;
            }
            if(count==k)
                return winner;
        }
        return winner;
    }

    public static void main(String[] args) {
        int[] arr = {2,1,3,5,4,6,7};
        int[] arr2 = {3,2,1};
        int[] arr3 = {1,9,8,2,3,7,6,4,5};
        int k = 2,k2=10,k3=7;

        System.out.println(getWinner(arr, k));
        System.out.println(getWinner(arr2, k2));
        System.out.println(getWinner(arr3, k3));
    }
}
