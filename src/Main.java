import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//Main method which takes input from the user.
        XMLFile x=new XMLFile();

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the starting date (YYYY-MM-DD):");
        String startDate = scan.nextLine();

        System.out.println("Enter the ending date (YYYY-MM-DD):");
        String endDate = scan.nextLine();

        System.out.println("Enter the name of file:");
        String file = scan.nextLine();

        x.sqlQuery(startDate, endDate);

        String str = x.printString(x.sqlQuery(startDate, endDate), startDate, endDate);

        x.Writexml(str, file);
        if (x.Writexml(str, file) == true) {
            System.out.println("XML file generated");
        } else {
            System.out.println("Unsuccessful");
        }
    }
}