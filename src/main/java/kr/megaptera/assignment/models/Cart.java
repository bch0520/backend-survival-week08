package kr.megaptera.assignment.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "carts")
public class Cart {
    @EmbeddedId
    private CartId cartId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cart_id")
    @OrderBy("id")
    private List<LineItem> lineItems = new ArrayList<>();
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private Cart() {
    }

    public Cart(CartId cartId) {
        this.cartId = cartId;
    }

    public Cart(CartId cartId, List<LineItem> lineItems) {
        this.cartId = cartId;
        this.lineItems = lineItems;
    }

    public static Cart create() {
        return new Cart(CartId.generate());
    }

    public void addProduct(Product product, int quantity) {
        if (quantity <= 0) {
            return;
        }
        Optional<LineItem> found = findLineItem(product.id());

        if (found.isPresent()) {
            LineItem lineItem = found.get();
            lineItem.increaseQuantity(quantity);
            return;
        }

        LineItem lineItem = LineItem.create(product, quantity);

        lineItems.add(lineItem);
    }

    public void changeLineItemQuantity(LineItemId lineItemId, int quantity) {
        LineItem lineItem = findLineItem(lineItemId).get();

        if (quantity <= 0) {
            lineItems.remove(lineItem);
            return;
        }

        lineItem.changeQuantity(quantity);
    }

    public int lineItemsSize() {
        return lineItems.size();
    }

    public LineItem lineItem(int index) {
        return lineItems.get(index);
    }

    public Optional<LineItem> findLineItem(ProductId productId) {
        return lineItems.stream()
                .filter(item -> item.sameProduct(productId))
                .findFirst();
    }

    public Optional<LineItem> findLineItem(LineItemId lineItemId) {
        return lineItems.stream()
                .filter(item -> item.id().equals(lineItemId))
                .findFirst();
    }
}
