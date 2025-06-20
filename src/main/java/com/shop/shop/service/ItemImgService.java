package com.shop.shop.service;

import com.shop.shop.entity.ItemImg;
import com.shop.shop.repository.ItemImgRepository;
import jakarta.persistence.EntityExistsException;
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

    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception{
        if(!itemImgFile.isEmpty()){ //상품의 이미지를 수정한 경우 상품이미지 업데이트
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId).orElseThrow(EntityExistsException::new); //기존 엔티티 조회
            //기존 등록된 상품 이미지 파일이 있으면 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName())){
                fileService.deleteFile(itemImgLocation+"/"+savedItemImg.getImgName());
            }
            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes()); //파일 업러드
            String imgUrl = "/images/item/"+imgName;
            /*
            변경된 상품 이미지 정보를 세팅
            상품등록을 하는 경우에는 ItemImgRepository.save() 로직을 호출하지만
            호출을 하지 않음
            savedItemImg 엔티티는 현재 영속성 상태이다
            그래서 데이터를 변경하는 것만으로 변경을 감지기능이 동작
            트랜잭션이 끝날 떄 update 쿼리가 실행된다
            !!!!영속성 상태여야 사용가능!!!!!
             */
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }
}
