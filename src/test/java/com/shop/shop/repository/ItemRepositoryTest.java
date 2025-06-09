package com.shop.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.QTuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.shop.constant.ItemSellStatus;
import com.shop.shop.entity.Item;
import com.shop.shop.entity.QItem;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemRepositoryTest  {

    @Autowired //자동 줄 (연결)
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;


    @Test
    @DisplayName("상품저장 테스트")
    public void createItemTest(){
        Item item = new Item();
        item.setItemNM("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());

        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());
    }

    public void createItemListTest(){
        for(int i=0;i<10;i++) {
            Item item = new Item();
            item.setItemNM("테스트 상품"+i);
            item.setPrice(10000+i);
            item.setItemDetail("테스트 상품 상세 설명"+i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());

            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품면 조회 테스트")
    public void findByItemNmTest(){
        this.createItemTest();
        List<Item> itemList = itemRepository.findByItemNM("테스트 상품1");
        for(Item item: itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 테스트")
    public void findByItemNMOrItemDetail(){
        this.createItemListTest();
        List<Item> itemList =
                itemRepository.findByItemNMOrItemDetail("테스트 상품1","테스트 상품 상세 설명5");
        for(Item item: itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 lessthan(미만) text")
    public void findByPriceLessThanTest(){
        this.createItemListTest();
        List<Item> itemList =itemRepository.findByPriceLessThan(10005);
        for(Item item: itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 내림차순 테스트")
    public void findByPriceLessThanOrderByPriceDesc(){
        this.createItemListTest();
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회")
    public void findByItemDetail(){
        this.createItemListTest();
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for(Item item: itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("nativeQuery를 이용한 상품 조회")
    public void findByItemDetailNative(){
        this.createItemListTest();
        List<Item> itemList = itemRepository.findByItemDetailNative("테스트 상품 상세 설명");
        for(Item item: itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("queryDsl  테스트")
    public void queryDslTest(){
        this.createItemListTest();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%"+"테스트 상품 상세 설명"+"%"))
                .orderBy(qItem.price.desc());
        List<Item> itemList = query.fetch();
        for(Item item: itemList){
            System.out.println(item.toString());
        }
    }

    public void createItemList2(){
        for(int i=0;i<=5;i++){
            Item item = new Item();
            item.setItemNM("테스트 상품"+i);
            item.setPrice(10000+i);
            item.setItemDetail("테스트 상품 상세 설명"+i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());

            itemRepository.save(item);
        }
        for(int i=6;i<=10;i++){
            Item item = new Item();
            item.setItemNM("테스트 상품"+i);
            item.setPrice(10000+i);
            item.setItemDetail("테스트 상품 상세 설명"+i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());

            itemRepository.save(item);
        }
    }
    @Test
    @DisplayName("상품 querydsl 조회 테스트2")
    public void queryDslTest2(){
        this.createItemList2();
        //쿼리문 조건을 취합해서 사용하게 해주는 클래스
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QItem item = QItem.item;

        String itemDetail = "테스트 상품 상세 설명";
        int price=10003;
        String itemSellStat ="SELL";

        booleanBuilder.and(item.itemDetail.like("%"+itemDetail+"%"));
        booleanBuilder.and(item.price.gt(price)); //price 10003보다 큰 경우

        if(StringUtils.equals(itemSellStat, ItemSellStatus.SELL)){
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        Pageable pageable = PageRequest.of(0,5);
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);

        System.out.println("total element: "+ itemPagingResult.getTotalElements());

        List<Item> resultItemList = itemPagingResult.getContent();
        for (Item resulItem : resultItemList){
            System.out.println(resulItem.toString());
        }
    }

}