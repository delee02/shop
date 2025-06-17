package com.shop.shop.entity;

import com.shop.shop.constant.ItemSellStatus;
import com.shop.shop.dto.ItemFormDto;
import com.shop.shop.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
@Entity
@Table(name="item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity {
    @Id
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 50)
    private String itemNM; //상품명

    @Column(name="price" , nullable = false)
    private int price;

    @Column(nullable = false)
    private int stockNumber; //수량

    @Lob //큰 데이터
    @Column(nullable = false)
    private String itemDetail;

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; //상품판매 상태

   /* private LocalDateTime regTime;
    private LocalDateTime updateTime;*/

    public void updateItem(ItemFormDto itemFormDto){
        this.itemNM = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    //주문이 들어오면 가지고있는 수량보다 적으면 예외처리
    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;
        if(restStock < 0){
            throw new OutOfStockException("상품의 재고가 부족합니다. (현재 재고 수량: "+this.stockNumber+")");
        }
        this.stockNumber = restStock;
    }

    public void addStock(int stockNumber){this.stockNumber += stockNumber;}
}
