package com.shop.shop.service;

import com.shop.shop.entity.ItemImg;
import com.shop.shop.repository.ItemImgRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {
    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;
    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception{
        String oriImgName = itemImgFile.getOriginalFilename();//오리지말 이미지 경로
        String imgName="";
        String imgUrl="";
        System.out.println(oriImgName);

        //파일업로드
        if(!StringUtils.isEmpty(oriImgName)){ // oriImgName 문자열로 비어잇지 않으면 실행
            System.out.println("*********");
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            System.out.println(imgName);
            imgUrl = "/images/item/"+imgName;
        }
        System.out.println("1111");

        //상품이미지 정보 저장
        //imgName 실제 로컹에 저장된 상품 이미지 파일의 이름
        //oriImgName 상품 이미지 파일의 원래 이름
        //imgUrl 로컬에 저장된 상품 이미지 파일을 불러오는 경로
        itemImg.updateItemImg(oriImgName,imgName,imgUrl);
        System.out.println("(((((");
        itemImgRepository.save(itemImg);
    }
}
