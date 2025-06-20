package com.shop.shop.controller;

import com.shop.shop.dto.OrderDto;
import com.shop.shop.dto.OrderHisDto;
import com.shop.shop.dto.OrderItemDto;
import com.shop.shop.entity.OrderItem;
import com.shop.shop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    //ajax -> 화면전환이 없고 새로운게 들어갈 때 쓰임 (댓글) //부른곳에서 다시
    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult, Principal principal){
        //String a= "abc"+"def";
        //StringBuilder a;
        //a.append("abc")'
        //a.append("def");
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for(FieldError fieldError: fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        //로그인정보=> Spring security
        //principal.getName() 혀ㅑㄴ재 로그인된 정보
        String email = principal.getName();
        Long orderId;
        try {
            orderId = orderService.order(orderDto, email);
        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return  new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page")Optional<Integer> page, Principal principal, Model model){
        Pageable pageable = PageRequest.of(page.isPresent()? page.get() : 0, 5);

        Page<OrderHisDto> orderHisDtoList = orderService.getOrderList(principal.getName(), pageable);
        for(OrderHisDto orderItem : orderHisDtoList){
            for(OrderItemDto orderItemDto : orderItem.getOrderItemDtoList()){
                System.out.println("////////////////////////////////////////////dddddd"+orderItemDto.getOrderPrice());
            }
        }
        model.addAttribute("orders", orderHisDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);
        return "order/orderHist";
    }

    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Principal principal){
        if(!orderService.validateOrder(orderId, principal.getName())){
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        orderService.cancelOrder(orderId);
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

}
