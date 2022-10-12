package se.iths.labborationer.labb2;

import java.math.BigDecimal;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InventoryBalance {
    private List<Product> inventory;

    public InventoryBalance() {
        this.inventory = new ArrayList<>();
    }

    public InventoryBalance(ArrayList<Product> list) {
        this.inventory = new ArrayList<>(list);
    }

    public InventoryBalance(InventoryBalance list) {
        this.inventory = new ArrayList<>(list.getInventory());
    }

    public InventoryBalance(List<Product> list) {
        this.inventory = new ArrayList<>(list);
    }

    public void add(Product product) {
        this.inventory.add(product);
    }

    public void remove(int i) {
        this.inventory.remove(this.getProduct(i));
    }

    public void remove(Product product) {
        this.inventory.remove(product);
    }

    public void remove(String name) {
        this.inventory.removeIf(product -> product.product().equals(name));
    }

    public boolean contains(Product product) {
        return this.inventory.stream()
                .anyMatch(p -> p.equals(product));
    }

    public List<Product> listToRemove(String name) {
        return this.inventory.stream().filter(p -> p.product().equals(name)).toList();
    }

    public List<Product> listToRemove(String name, long limit) {
        return this.inventory.stream().limit(limit).filter(p -> p.product().equals(name)).toList();
    }

    public List<Product> getInventory() {
        return inventory;
    }


    public Product getProduct(int i) {
        return this.inventory.get(i);
    }


    public ProductCategory getCategory(int i) {
        return this.inventory.get(i).category();
    }

    public List<Product> copy(InventoryBalance inventory) {
        return this.inventory = new ArrayList<>(inventory.getInventory());

    }

    public void printProductWithCategory(ProductCategory category) {
        //admin4
        this.inventory.stream()
                .filter(p -> productMatch(p.category(), category))
                .distinct()
                .sorted(Comparator.comparing(compareCategoryOrder())
                        .thenComparing(Product::product))
                .forEach(this::printProductSaldo);
    }

    public List<Product> getListWithChosenCategory(ProductCategory category) {
        return this.inventory.stream()
                .filter(p -> productMatch(p.category(), category))
                .distinct()
                .sorted(Comparator.comparing(compareCategoryOrder())
                        .thenComparing(Product::product))
                .toList();
    }

    public long nrOfProducts(Product number) {
        return this.inventory.stream()
                .filter(product -> product.matchingEanCode(number.productNumber()))
                .count();
    }


    public boolean productMatch(ProductCategory p, ProductCategory o) {
        return p.category().equals(o.category());
    }

    public void printBetweenPrices(BigDecimal lowestInputPrice, BigDecimal highestInputPrice) {
        //admin5

        BigDecimal lowestPrice = lowestInputPrice;
        BigDecimal highestPrice = highestInputPrice;
        this.inventory.stream().filter(p -> isLowerThan(highestPrice, p))
                .filter(p -> isHigherThan(lowestPrice, p))
                .distinct()
                .sorted(Comparator.comparing(compareCategoryOrder())
                        .thenComparing(Product::product))
                .forEach(this::printProductSaldo);
    }

    public static Function<Product, String> compareCategoryOrder() {
        return product -> product.category().category();
    }

    private static boolean isLowerThan(BigDecimal highestPrice, Product p) {
        return p.price().compareTo(highestPrice) <= 0;
    }

    private static boolean isHigherThan(BigDecimal lowestPrice, Product p) {
        return p.price().compareTo(lowestPrice) >= 0;
    }

    public void printBalance() {
        this.inventory.forEach(System.out::println);
    }

    public void printbalancetest() {
        //admin3
        inventory.stream()
                .distinct()
                .sorted(Comparator.comparing(compareCategoryOrder())
                        .thenComparing(Product::product))
                .forEach(p -> printProductSaldo(p));

    }

    private void printProductSaldo(Product p) {
        System.out.println(p + " " + nrOfProducts(p) + " st i lager");
    }

    public List<Product> getDistinctProducts(List<Product> listIn) {
        return listIn
                .stream()
                .sorted(Comparator.comparing(compareCategoryOrder())
                        .thenComparing(Product::product))
                .distinct()
                .toList();
    }

    public String printBalance(int i) {
        return this.inventory.get(i).category() + ", "
                + this.inventory.get(i).product() + ", "
                + this.inventory.get(i).price() + ", "
                + this.inventory.get(i).productNumber();
    }

    private boolean categoryMatch(ProductCategory category, int i) {
        return this.inventory.get(i).category().equals(category);
    }

    public int size() {
        return inventory.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryBalance that = (InventoryBalance) o;
        return Objects.equals(inventory, that.inventory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventory);
    }


    public void getLongestCategory() {
        int i = this.inventory.stream()
                .map(Product::category)
                .map(ProductCategory::category)
                .collect(Collectors.summarizingInt(String::length))
                .getMax();
    }
}


//    public List<String> getAllCategories(){
//        var list = new ArrayList<String>();
//
//        for (int i = 0; i < this.inventory.size(); i++) {
//            String category = String.valueOf(this.inventory.get(i).category());
//
//            if (!(list.contains(category)))
//                list.add(category);
//        }
//
//        return list;
//    }
