package com.shop.shop.dto;

import com.shop.shop.constant.ItemSellStatus;
import com.shop.shop.entity.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItemFormDto {
    private Long id;
    @NotBlank(message = "상품명은 필수 입력값입니다.")
    private String itemNm;

    @NotNull(message = "가격은 필수 입력값입니다.")
    private Integer price;

    @NotBlank(message = "설명은 필수 입력값입니다.")
    private String itemDetail;

    @NotNull(message = "재고는 필수 입력값입니다.")
    private String stockNumber;

    private ItemSellStatus itemSellStatus;

    //------------------------------------------------
    //itemImg
    private List<ItemImgDto> itemImgDtoList = new ArrayList<>(); //상품 이미지 정보
    private List<Long> itemImgIds = new ArrayList<>();

    //========================================================
    //model mapper
    private static ModelMapper modelMapper = new ModelMapper();

    public Item createItem(){
        //ItemFormDto -> Item 연결
        return modelMapper.map(this, Item.class);
    }

    public static ItemFormDto of(Item item){
        //Item -> ItemFormDTO 연결
        return modelMapper.map(item, ItemFormDto.class);
    }
}
