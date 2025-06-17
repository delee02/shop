package com.shop.shop.repository;

import com.shop.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

}
