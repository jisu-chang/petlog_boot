package com.example.PetLog.Item;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
public class ItemController {
    @Autowired
    ItemService itemService;

    String path = System.getProperty("user.dir") + "/src/main/resources/static/image";

    @GetMapping(value = "/Item/ItemInput")
    public String input() {

        return "Item/ItemInput";
    }

    @PostMapping(value = "/ItemSave")
    public String save(ItemDTO dto, Principal principal) throws IOException {

        dto.setItemStatus("판매중");

        MultipartFile file = dto.getItemImage();
        if (file != null && !file.isEmpty()) {
            String filename = file.getOriginalFilename();

            String path = System.getProperty("user.dir") + "/src/main/resources/static/image";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs(); // 폴더 없으면 생성
            }
            file.transferTo(new File(dir, filename));
            dto.setItemImageName(filename);
        }

        ItemEntity itemEntity = dto.entity();
        itemService.save(itemEntity);

        return "redirect:/Item/ItemInput";
    }

    @GetMapping(value = "/Item/ItemOut")
    public String out(Model mo, Principal principal) {

        List<ItemDTO> list = itemService.out();
        mo.addAttribute("list",list);

        return "Item/ItemOut";
    }

    @GetMapping(value = "/Item/ItemUpdate")
    public String up(@RequestParam("itemId") Long itemId, Model mo, Principal principal) {

        ItemEntity itemEntity = itemService.up(itemId);

        if (itemEntity == null) {
            // 아이템이 없을 경우 오류 페이지나 리다이렉트 처리
            return "error/itemNotFound";
        }

        ItemDTO dto = new ItemDTO();
        dto.setItemId(itemEntity.getItemId());
        dto.setItemName(itemEntity.getItemName());
        dto.setItemCost(itemEntity.getItemCost());
        dto.setItemCategory(itemEntity.getItemCategory());
        dto.setItemImageName(itemEntity.getItemImage());
        dto.setItemStatus(itemEntity.getItemStatus());

        mo.addAttribute("dto",dto);
        return "Item/ItemUpdate";

    }

    @PostMapping(value = "/ItemUpdateSave")
    public String upsave(@ModelAttribute ItemDTO dto, @RequestParam("himage") String himage, HttpSession hs, Principal pri) throws IOException {

        MultipartFile mf = dto.getItemImage();
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        if (mf != null && !mf.isEmpty()) {
            String filename = mf.getOriginalFilename();
            mf.transferTo(new File(path + File.separator + filename));
            dto.setItemImageName(filename);
        } else {
            dto.setItemImageName(himage);
        }

        ItemEntity entity = dto.entity();
        itemService.update(entity);

        return "redirect:/Item/ItemOut";
    }

    @PostMapping(value = "/Item/ItemStopped")
    public String stop(Model mo) { //판매완료된 아이템 보여주는 페이지

        List<ItemDTO> list = itemService.itemStopped();
        mo.addAttribute("list", list);

        return "Item/ItemStopped";
    }

    @PostMapping(value = "/Item/ItemDelete")
    public String del(HttpSession hs,@RequestParam("delete")Long itemId) { //판매중->판매완료

        itemService.changeStatus(itemId);

        return "redirect:/Item/ItemOut";
    }

    @PostMapping(value = "/itemRestore")
    public String restore(@RequestParam("itemId") Long itemId, HttpSession session) { //판매완료->판매중
        itemService.changeStatusSell(itemId);
        return "redirect:/Item/ItemOut";
    }
}
