package com.shop.shop.service;

import com.shop.shop.dto.OrderDto;
import com.shop.shop.dto.OrderHisDto;
import com.shop.shop.dto.OrderItemDto;
import com.shop.shop.entity.*;
import com.shop.shop.repository.ItemImgRepository;
import com.shop.shop.repository.ItemRepository;
import com.shop.shop.repository.MemberRepository;
import com.shop.shop.repository.OrderRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;

    public Long order(OrderDto orderDto, String email){
        Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityExistsException::new);
        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);
        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHisDto> getOrderList(String email, Pageable pageable){
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);
        List<OrderHisDto> orderHisDtos = new ArrayList<>();
        //Order -> OrderHisDto
        // OrderItem -> OrderItemDto
        for(Order order : orders) {
            OrderHisDto orderHisDto = new OrderHisDto(order);
            List<OrderItem> orderItems = order.getOrderItems();// order에서 OrderItemList 추출
            //OrderItemList -> OrderItem을 빼고 없을 때까지 반복
            for (OrderItem orderItem : orderItems) {
                //OrderItem을 이용해서 ItemImg 추출시 대표이미지만
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");
                //OrderItemDto 객체 생성 -> 생성시 OrderItem과 ItemImgUrl 정보를 매개변수로 제공
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                //OrderItemDto를  OrderHisDto에 추가
                orderHisDto.addOrderItemDto(orderItemDto);
            }
            //OrderHIsDto를 list에 추가
            orderHisDtos.add(orderHisDto);
        }
        return new PageImpl<OrderHisDto>(orderHisDtos, pageable, totalCount);
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }
        return true;
    }

    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }
}
