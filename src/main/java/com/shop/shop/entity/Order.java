package com.shop.shop.entity;

import com.shop.shop.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Getter
@Setter
public class Order extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name="order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) //주문이 여러개가 될 수 있음 order가 주인(양방향매핑), 영속성 설정
    private List<OrderItem> orderItems = new ArrayList<>();

    /*private LocalDateTime regTime;
    private LocalDateTime updateTime;*/

    //주문서 주문아이템 리스트에 주문아이템 추가
    //주문 아이템에 주문서 추가
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
        /*
        주문서 생성
        현재 로그인된 멤버 주문서에 추가
        주문아이템 리스트를 반복문을 통해서 주문서엪 추가
        상태는 주문으로 세팅
        주문 시간은 현재시간
        주문서 리턴
         */
        public static Order createOrder(Member member, List<OrderItem> orderItemList){
            Order order = new Order();
            order.setMember(member);
            for(OrderItem orderItem : orderItemList){
                order.addOrderItem(orderItem);
            }
            order.setOrderStatus(OrderStatus.ORDER);
            order.setOrderDate(LocalDateTime.now());
            return order;
        }
        /*
        주문서에 있는 주문 아이템 리스트를 반복
        주문 아이템마다 총 가격을 totalPrice에 추가
         */
        public int getTotalPrice(){
            int totalPrice=0;
            for(OrderItem orderItem:orderItems){
                totalPrice += orderItem.getTotalPrice(); //주문서에 있는 모든 제품에 대한 totalPrice
            }
            return totalPrice;
        }

        public void cancelOrder(){
            this.orderStatus = OrderStatus.CANCEL;

            for(OrderItem orderItem: orderItems){
                orderItem.cancel();
            }
        }

}
