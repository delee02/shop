package com.shop.shop.service;

import com.shop.shop.dto.CartItemDto;
import com.shop.shop.entity.Cart;
import com.shop.shop.entity.CartItem;
import com.shop.shop.entity.Item;
import com.shop.shop.entity.Member;
import com.shop.shop.repository.CartItemRepository;
import com.shop.shop.repository.CartRepository;
import com.shop.shop.repository.ItemRepository;
import com.shop.shop.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public Long addCart(CartItemDto cartItemDto, String email){
        Item item = itemRepository.findById(cartItemDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);

        Cart cart = cartRepository.findByMemberId(member.getId());
        if(cart==null){
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }


        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());
        if(savedCartItem !=null){ //같은 제품을 담았을 때 추가가 아니고 count만 올라감
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        }else{
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }
}
