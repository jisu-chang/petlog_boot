package com.example.PetLog.Item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public String up(Model mo, Principal principal) {



        return "Item/ItemUpdate";

    }

    @PostMapping(value = "/ItemUpdateSave")
    public String updave() {

        return "redirect:/Item/ItemOut";
    }

    @GetMapping(value = "/Item/ItemDelete")
    public String del(Model mo, Principal principal) {

        return "redirect:/Item/ItemOut";
    }
}
