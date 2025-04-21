import java.util.Arrays;
import java.util.Random;

public class tesArea {
    public static void main(String[] args) {
        slotMechanism();
    }
    private static int money = 1000;
    private static int multiplier = 2;
    private static int betMoney = 100;
    private static int[] betNumber = {1, 5, 3};
    private static void slotMechanism() {
        Random rand = new Random();
        String result = "| ";
        int[] winning = new int[3];
        int chance = rand.nextInt(100) + 1; //out of 4 - 25%
        boolean isWin = false;
        if (chance <= 50) {
            isWin = true;
            for (int i = 0; i < 3; i++) {
                winning[i] = betNumber[i];
            }
        } else {
            for (int i = 0; i < 3; i++) {
                winning[i] = rand.nextInt(9) + 1;
            }
            for (int i = 0; i < 3; i++) {
                int tmp;
                do {
                    tmp = rand.nextInt(9) + 1;
                } while (tmp == winning[i]);
                result += tmp + " | ";
            }
        }

        System.out.println("bet number: " + Arrays.toString(betNumber));
        System.out.print("Result: | ");
        for (int i = 0; i < winning.length; i++) {
            System.out.print(winning[i]);
            if (i < winning.length - 1) System.out.print(" | ");
        }
        System.out.println(" | :: ChanceNum -> " + chance);
        System.out.println("isWin? " + isWin + "\n");
        System.out.println("Before:\n money: "+ money + "\n bet: " + betMoney + "\n multiplier:" + multiplier);
        if (isWin == true){
            money += betMoney * multiplier;
            multiplier++;
        } else {
            money -= betMoney;
            multiplier = 2;
        }
        System.out.println("After:\n money: "+ money + "\n bet: " + betMoney + "\n multiplier:" + multiplier);


    }
}
