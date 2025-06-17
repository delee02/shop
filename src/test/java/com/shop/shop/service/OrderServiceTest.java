package com.shop.shop.service;

import com.shop.shop.constant.ItemSellStatus;
import com.shop.shop.dto.OrderDto;
import com.shop.shop.entity.Item;
import com.shop.shop.entity.Member;
import com.shop.shop.entity.Order;
import com.shop.shop.entity.OrderItem;
import com.shop.shop.repository.ItemRepository;
import com.shop.shop.repository.MemberRepository;
import com.shop.shop.repository.OrderRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderServiceTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    public Item saveItem(){
        Item item = new Item();
        item.setItemNM("테스트상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    public Member savedMember(){
        Member member = new Member();
        member.setEmail("test@test.com");
        return memberRepository.save(member);
    }

    @Test
    @DisplayName("주문 테스트")
    public void order(){
        Item item =saveItem();
        Member member = savedMember();

        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10);
        orderDto.setItemId(item.getId());

        Long orderId = orderService.order(orderDto, member.getEmail());

        Order order =orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        List<OrderItem> orderItemList = order.getOrderItems();
        int totalPrice = orderDto.getCount()*item.getPrice();

        assertEquals(totalPrice, order.getTotalPrice());

    }

}