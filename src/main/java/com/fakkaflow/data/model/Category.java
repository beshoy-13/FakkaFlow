package com.fakkaflow.data.model;
/**
 * Represents a transaction category.
 *
 * Used to classify transactions such as Food, Transport, etc.
 */
public class Category {
    private int categoryId;
    private String name;
    /**
     * Default constructor.
     */
    public Category() {}

    /**
     * Creates a category with ID and name.
     *
     * @param categoryId category ID
     * @param name category name
     */
    public Category(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    /**
     * Returns the category name as string representation.
     *
     * @return category name
     */
    @Override
    public String toString() { return name; }
}
