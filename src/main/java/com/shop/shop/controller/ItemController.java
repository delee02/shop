package com.shop.shop.controller;

import com.shop.shop.dto.ItemFormDto;
import com.shop.shop.dto.ItemSearchDto;
import com.shop.shop.entity.Item;
import com.shop.shop.service.ItemService;
import jakarta.persistence.Column;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    //상품등록 페이지 보여주기
    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

    //상품등록
    @PostMapping(value = "admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                          @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {
        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번쩨 상품이미지는 필수 입력값입니다.");
            return "item/itemForm";
        }
        try {
            itemService.savedItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }

    //상품디테일, 상품 수정페이지 보여주기
    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model) {
        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }
        return "item/itemForm";
    }

    //상품 수정
    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFilsList){
        if (bindingResult.hasErrors()){
            return "item/itemForm";
        }
        if(itemImgFilsList.get(0).isEmpty() && itemFormDto.getId()==null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력값입니다.");
            return "item/itemForm";
        }
        try {
            itemService.updateItem(itemFormDto, itemImgFilsList);
        }catch (Exception e){
            model.addAttribute("errorMessage","상품 수정 중에 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }

    //item list (paging, after searching, list)
    /*
    value가 두개인 이유
    1.네비게이션에서 상품관리 클릭하면 나오는거
    2. 상품관리 안에서 페이지 이동할 때 받는거
     */
    @GetMapping(value = {"/admin/items", "admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page")Optional<Integer> page, Model model){
        /*
        page.isPresent() -> page값 있음?
        ㅇㅇ page.get() / ㄴㄴ 0
        페이지당 사이즈5 6부터는 다음페이지
         */
        Pageable pageable = PageRequest.of(page.isPresent()? page.get() : 0 , 5);

        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        return "item/itemMng";
    }

}
