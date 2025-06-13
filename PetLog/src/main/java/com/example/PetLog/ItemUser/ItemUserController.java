package com.example.PetLog.ItemUser;

import com.example.PetLog.Item.ItemEntity;
import com.example.PetLog.Item.ItemRepository;
import com.example.PetLog.Item.ItemService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ItemUserController {

    @Autowired
    ItemUserService itemUserService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemUserRepository itemUserRepository;

    @GetMapping(value = "/ItemUser/ItemOutUser")
    public String itemlist(Model mo) {
        List<ItemEntity> list = itemRepository.findAll();
        mo.addAttribute("list",list);

        return "ItemUser/ItemOutUser";
    }

    @RequestMapping(value = "/ItemUser/ItemDetail", method = {RequestMethod.GET, RequestMethod.POST})
    public String detail(@RequestParam("itemId")Long itemId, Model mo) {

        System.out.println("✅ [디버깅] itemId = " + itemId);

        ItemEntity item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            return "redirect:/ItemUser/ItemOutUser";
        }
        mo.addAttribute("dto", item);

        return "ItemUser/ItemDetail";
    }

    @PostMapping(value = "/ItemUser/ItemBuy")
    public String buy(@RequestParam("itemId") Long itemId, HttpSession hs, Model mo) {

        Long userId = (Long) hs.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        ItemUserEntity itemUserEntity = new ItemUserEntity();
        itemUserEntity.setUserId(userId);
        itemUserEntity.setItemId(itemId);
        itemUserEntity.setUsertemEquip("N");

        itemUserRepository.save(itemUserEntity);

        return "ItemUser/ItemBuy";
    }



}
