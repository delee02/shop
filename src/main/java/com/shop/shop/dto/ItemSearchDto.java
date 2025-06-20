package com.shop.shop.dto;

import com.shop.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {
    private String searchDateType; //조회날짜

    private ItemSellStatus searchSellStatus; //상태

    private String searchBy; //조회유형

    private String searchQuery= ""; //검색단어
}
