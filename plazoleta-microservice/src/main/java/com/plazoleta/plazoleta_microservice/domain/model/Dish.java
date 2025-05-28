package com.plazoleta.plazoleta_microservice.domain.model;

import com.plazoleta.plazoleta_microservice.domain.validator.DishValidator;

public class Dish {
    private final Long id;
    private final String name;
    private final Double price;
    private final String description;
    private final String imageUrl;
    private final Category category;
    private final boolean active;
    private final Long restaurantId;

    private Dish(Builder builder) {
        DishValidator.validate(builder.name, builder.price, builder.description, builder.imageUrl, builder.category, builder.restaurantId);

        this.id = builder.id;
        this.name = builder.name;
        this.price = builder.price;
        this.description = builder.description;
        this.imageUrl = builder.imageUrl;
        this.category = builder.category;
        this.active = builder.active;
        this.restaurantId = builder.restaurantId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private Double price;
        private String description;
        private String imageUrl;
        private Category category;
        private boolean active = true;
        private Long restaurantId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder price(Double price) {
            this.price = price;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Builder restaurantId(Long restaurantId) {
            this.restaurantId = restaurantId;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Dish build() {
            return new Dish(this);
        }
    }

    public Dish activate() {
        if (this.active) return this;
        return Dish.builder()
                .id(this.id)
                .name(this.name)
                .price(this.price)
                .description(this.description)
                .imageUrl(this.imageUrl)
                .category(this.category)
                .restaurantId(this.restaurantId)
                .active(true)
                .build();
    }

    public Dish deactivate() {
        if (!this.active) return this;
        return Dish.builder()
                .id(this.id)
                .name(this.name)
                .price(this.price)
                .description(this.description)
                .imageUrl(this.imageUrl)
                .category(this.category)
                .restaurantId(this.restaurantId)
                .active(false)
                .build();
    }

    public boolean isActive() {
        return active;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }
}
