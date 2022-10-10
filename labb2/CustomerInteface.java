package se.iths.labborationer.labb2;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import static java.math.BigDecimal.valueOf;

public class CustomerInteface{
    private InventoryBalance balance;
    private List<ProductCategory> categories;
    private Scanner scanner;
    private int count;
    private Register register;

    public CustomerInteface(InventoryBalance balance, List<ProductCategory> categories, Scanner scanner) {
        this.balance = balance;
        this.categories = categories;
        this.scanner = scanner;
        this.register = new Register();

    }

    public void start(boolean loop) {
        startupkategorier();
        while (loop) {

            costumerMenuGreeting();
            var input = scanner.next();

            System.out.println("====================");
            switch (input) {
                case "1" -> loopThroughCategories(register);
                case "2" -> getSpecificCategor(register);
                case "3" -> listAllProducts(register);
                case "4" -> register.printRegister();
                case "5" -> checkOut(register);
                case "e" -> loop = false;
            }
            System.out.println("====================");
        }
    }

    private void checkOut(Register register) {
        var distinctRegister = new Register(register.getDistinctRegister(register.register));
        var priceToPay = valueOf(0);
            System.out.println("Produkt\t\t á-pris\t\t\tst\t\ttotalpris");

        for (int i = 0; i < distinctRegister.size(); i++) {

            System.out.println(distinctRegister.getProduct(i).product()
                 + "\t\t\t" + distinctRegister.getProduct(i).price()
                 + "\t\t\t" + nrOfProducts(distinctRegister.getProduct(i))
                 + "\t\t\t" + totalPrice(register, distinctRegister.getProduct(i)));

         priceToPay = priceToPay.add(totalPrice(register, distinctRegister.getProduct(i)));

        }
        System.out.println();

        BigDecimal discountedPrice = priceToPay;

        if(priceToPay.compareTo(valueOf(2000)) > 0)
            discountedPrice = applyDiscount(new TwentyPercent(), priceToPay);
        else if(priceToPay.compareTo(valueOf(1000)) > 0)
            discountedPrice = applyDiscount(new TenPercent(), priceToPay);

        System.out.println("Totalt \t\t\t\t\t\t\t\t" + priceToPay + " kr");
        printDiscountValue(priceToPay);
        System.out.println("\t\t\t\t\t\t\t\t\t__________");
        System.out.println("Att betala \t\t\t\t\t\t\t\t" + discountedPrice + " kr");
        System.out.println(priceToPay);



        /*
        Produkt      á-pris     st      totalpris
        morot        1.79       4            7.16
        kyckling     2.99       2            5.98
        Bröd         1          2            2.40

        Totalt                              72.39
        Discount                            -9.60
                                       ----------
        Att betala                          65.69

        Payment                              70.00
        Your Change                    4.31

        Thank you. Come Again!*/

    }

    private static void printDiscountValue(BigDecimal priceToPay) {
       var discount = valueOf(0);
        if(priceToPay.compareTo(valueOf(2000)) > 0)
             discount = priceToPay.multiply(valueOf(0.2));
        else if(priceToPay.compareTo(valueOf(1000)) > 0)
             discount = priceToPay.multiply(valueOf(0.1));

        if(discount.compareTo(valueOf(1)) > 0)
            System.out.println("Rabatt \t\t\t\t\t\t\t\t\t" + discount + " kr");
    }

    public BigDecimal applyDiscount(Discounter discount, BigDecimal amount){
        return discount.apply(amount);

    }

    private BigDecimal totalPrice(Register register, Product product) {
        var nrOfProducts = valueOf(nrOfProducts(product));
        return product.price().multiply(nrOfProducts);

    }

    private int nrOfProducts(Product product){
        return (int) register.sameProductsInRegister(product);
    }



    private void loopThroughCategories(Register register) {
        for (int i = 0; i < this.categories.size(); i++) {

            var category = this.categories.get(i);
            var list = new InventoryBalance(this.balance.getListWithChosenCategory(category));
            System.out.println("0 Nästa kategori");
            printProductsInStore(register, list);
            int choice = getProductOrQuit();

            if(choice == 0)
                continue;

            askAndAddProduct(register, list, choice);
        }
    }


    private void addToRegister(Register register, InventoryBalance list) {
        printProductsInStore(register, list);
        int choice = getProductOrQuit();
        askAndAddProduct(register, list, choice);
    }

    private int getProductOrQuit() {
        int choice = getChoice("Skriv siffran på produkten du vill ha eller gå tillbaka.");
        if (choice == count)
             start(true);
        return choice;
    }

    private void askAndAddProduct(Register register, InventoryBalance list, int choice) {
        long nrOfProducts = getNrOfProducts("Hur många vill du köpa?");

        if(isProductAvailable(register, list, choice, nrOfProducts))
            addNrOfProductsToRegister(register, list, choice, nrOfProducts);
        else
            System.out.println("Finns inte tillräckligt med varor");
    }

    private ProductCategory getCategoryAtIndex(int i) {
        return this.categories.get(i);
    }

    private void getSpecificCategor(Register register) {
        var categoryChoice = getUserCategoryChoice(1);

        var list = new InventoryBalance(this.balance.getListWithChosenCategory(categoryChoice));

            addToRegister(register, list);

    }
    private ProductCategory getUserCategoryChoice(int number) {
        printCategoriesInOrder(number);
        return this.categories.get(scanner.nextInt() - 1);
    }
    private void printCategoriesInOrder(int number) {
        if (this.categories.size() >= 1) {
            printCategoriesFormated(number);
        }
    }
    private void printCategoriesFormated(int number) {
        for (int i = 0; i < this.categories.size(); i++) {
            System.out.println((i + number) + ". " + getCategoryAtIndex(i) + ".");
        }
    }

    public void searchByCategory() {

    }
    private void costumerMenuGreeting() {
        System.out.println("1. Vill gå igenom alla kategorier en i taget?");
        System.out.println("2. Välj en specifik kategori");
        System.out.println("3. Printa ut alla produkter");
        System.out.println("4. print your register");
        System.out.println("e. Gå till kassan och betala");
    }

    private void printCategory(ProductCategory category) {
        this.balance.printProductWithCategory(category);
    }



    private void listAllProducts(Register register) {

        var list = new InventoryBalance(this.balance.getDistinctProducts(this.balance.getInventory()));
           while(true)
                addToRegister(register, list);

    }


    private void printProductsInStore(Register register, InventoryBalance list) {
        this.count = 1;
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + " " + list.getProduct(i) + " " + actualProductsInStore(list, register, i));
            count++;
        }
        System.out.println(count + " För att gå tillbaka");
    }

    private boolean isProductAvailable(Register register, InventoryBalance list, int choice, long nrOfProducts) {
        return nrOfProducts <= actualProductsInStore(list, register, (choice - 1)) && actualProductsInStore(list, register, (choice - 1)) >= 0;
    }

    private long getNrOfProducts(String x) {
        System.out.println(x);
        long nrOfProducts = scanner.nextInt();
        return nrOfProducts;
    }

    private int getChoice(String x) {
        System.out.println(x);
        int choice = scanner.nextInt();
        return choice;
    }

    private static void addNrOfProductsToRegister(Register register, InventoryBalance list, int choice, long nrOfProducts) {
        for (int i = 0; i < nrOfProducts; i++) {
            register.add(list.getProduct(choice - 1));
        }
    }

    private int actualProductsInStore(InventoryBalance list, Register register, int i ){
        int produtsInStore = (int) this.balance.nrOfProducts(list.getProduct(i));

        for (int j = 0; j < register.sameProductsInRegister(list.getProduct(i)) ; j++) {
            produtsInStore--;
        }
        return produtsInStore;
    }


    private void printProducts() {
        this.balance.printbalancetest(this.balance.getInventory());
    }


    public void startupkategorier() {
        this.balance.add(new Product(new ProductCategory("Dairy"), "Milk", valueOf(199), 10938));
        this.balance.add(new Product(new ProductCategory("Dairy"), "Milk", valueOf(199), 10938));
        this.balance.add(new Product(new ProductCategory("Dairy"), "Milk", valueOf(199), 10938));
        this.balance.add(new Product(new ProductCategory("Dairy"), "Milk", valueOf(199), 10938));
        this.balance.add(new Product(new ProductCategory("Dairy"), "Milk", valueOf(199), 10938));
        this.balance.add(new Product(new ProductCategory("Dairy"), "Cream", valueOf(11), 1098));
        this.balance.add(new Product(new ProductCategory("Meat"), "Chicken", valueOf(12), 109));
        this.balance.add(new Product(new ProductCategory("Meat"), "Chicken", valueOf(12), 109));
        this.balance.add(new Product(new ProductCategory("Meat"), "Chicken", valueOf(12), 109));
        this.balance.add(new Product(new ProductCategory("Meat"), "Beef", valueOf(1), 10));
        this.balance.add(new Product(new ProductCategory("Fruit"), "Apples", valueOf(11), 1038));
        this.balance.add(new Product(new ProductCategory("Fruit"), "Apples", valueOf(11), 1038));
        this.balance.add(new Product(new ProductCategory("Fruit"), "Apples", valueOf(11), 1038));
        this.balance.add(new Product(new ProductCategory("Vegetable"), "Carrot", valueOf(17), 938));
        this.balance.add(new Product(new ProductCategory("Vegetable"), "Carrot", valueOf(17), 938));
        this.balance.add(new Product(new ProductCategory("Fruit"), "Banana", valueOf(14), 18));
        this.balance.add(new Product(new ProductCategory("Fruit"), "Banana", valueOf(14), 18));

        for (int i = 0; i < this.balance.size(); i++) {
            if (!(this.categories.contains(this.balance.getCategory(i))))
                this.categories.add(this.balance.getCategory(i));
        }
    }
}

