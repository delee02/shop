package com.shop.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.shop.constant.ItemSellStatus;
import com.shop.shop.dto.ItemSearchDto;
import com.shop.shop.dto.MainItemDto;
import com.shop.shop.dto.QMainItemDto;
import com.shop.shop.entity.Item;
import com.shop.shop.entity.QItem;
import com.shop.shop.entity.QItemImg;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{
    private JPAQueryFactory queryFactory; //동적쿼리를 사용하기 위해 jPaQueryFactory 변수 선언

    public ItemRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em); //JpaQueryFactory 실질적인 객체가 만들어진다.
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus ==null? null : QItem.item.itemSellStatus.eq(searchSellStatus);
        //ItemSellSataus null이면 null리턴 아니면 SELL SOLD중에 리턴
    }

    private BooleanExpression regDtsAfter(String searchDatetype){ //all 1s 1w 1m 6m
        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDatetype) || searchDatetype == null){
            return null;
        }else if(StringUtils.equals("1d",searchDatetype)){
            dateTime = dateTime.minusDays(1);
        }else if(StringUtils.equals("1w",searchDatetype)){
            dateTime = dateTime.minusWeeks(1);
        }else if(StringUtils.equals("1m", searchDatetype)){
            dateTime = dateTime.minusMonths(1);
        }else if(StringUtils.equals("6m", searchDatetype)){
            dateTime = dateTime.minusMonths(6);
        }
        return QItem.item.regTime.after(dateTime);
        //dateTime을 시간에 맞게 세팅후 시간에 맞는 등록된 상품이 조회하도록 조건값 반환
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if(StringUtils.equals("itemNm",searchBy)){ //상품명
            return QItem.item.itemNM.like("%"+searchQuery+"%");
        }else if(StringUtils.equals("createdBy", searchBy)){ //작성자
            return QItem.item.createdBy.like("%"+searchQuery+"%");
        }
        return null;
    }

  private BooleanExpression itemNmLike(String searchQuery) {
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNM.like("%" + searchQuery + "%");
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QueryResults<Item> results = queryFactory.selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();
        List<Item> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;
        //select i.id, id.itemNm, i.itemDetail, im.itemImg, i.price from item i, itemimg im join i.id = im.itemid
        //where im.repImgYn="Y" and i.itemNm like %searchQuery% order by i.id desc
        //QMainItemDto @QueryProjection을 허용하면 DTO 바로 조회가능
        QueryResults<MainItemDto> results = queryFactory.select(new QMainItemDto(item.id, item.itemNM, item.itemDetail,
                                                itemImg.imgUrl,item.price))
                .from(itemImg).join(itemImg.item, item).where(itemImg.repImgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc()).offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();
        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }



}
