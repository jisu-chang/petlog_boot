package com.example.PetLog.ItemUser;

import com.example.PetLog.Item.ItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ItemUserController {

    @Autowired
    ItemUserService itemUserService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemUserRepository itemUserRepository;

    @GetMapping(value = "/ItemUser/ItemOutUser")
    public String itemlist() {

        return "";
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

        return "ItemUser/ItemOutUser";
    }
}
