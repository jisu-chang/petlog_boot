package com.example.PetLog.ItemUser;

import com.example.PetLog.Item.ItemEntity;
import com.example.PetLog.Item.ItemRepository;
import com.example.PetLog.User.UserEntity;
import com.example.PetLog.User.UserRepository;
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
    @Autowired
    UserRepository userRepository;

    @GetMapping("/ItemUser/ItemOutUser")
    public String showItemShop(Model model) {
        List<ItemEntity> items = itemRepository.findAll(); // 전체 아이템 조회
        model.addAttribute("list", items); // HTML로 전달
        return "ItemUser/ItemOutUser";
    }

    @GetMapping("/ItemUser/ItemDetail")
    public String showItemDetail(@RequestParam("itemId") Long itemId, Model model) {
        ItemEntity item = itemRepository.findById(itemId).orElse(null);

        if (item == null) {
            // 아이템이 없을 경우 예외처리 (선택)
            return "redirect:/ItemUser/ItemOutUser"; // 목록 페이지로 돌려보냄
        }

        model.addAttribute("dto", item);
        return "ItemUser/ItemDetail";
    }

    @PostMapping("/ItemUser/ItemBought")
    public String buyItem(@RequestParam("itemId") Long itemId, HttpSession session) {
        System.out.println("🧨 구매 요청 들어옴: itemId = " + itemId);

        // 1. 세션에서 로그인된 user_id 꺼내기
        Object sessionValue = session.getAttribute("user_id");
        Long userId = null;

        if (sessionValue instanceof Integer) {
            userId = ((Integer) sessionValue).longValue();
        } else if (sessionValue instanceof Long) {
            userId = (Long) sessionValue;
        }

        if (userId == null) {
            return "redirect:/login?error=login_required";
        }

        // 2. 아이템 조회
        ItemEntity item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            return "redirect:/ItemUser/ItemOutUser?error=item_not_found";
        }

        // 3. 유저 조회
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "redirect:/login?error=user_not_found";
        }

        // 4. 포도알 확인
        if (user.getGrapeCount() < item.getItemCost()) {
            return "redirect:/ItemUser/ItemDetail?itemId=" + itemId + "&error=not_enough_grapes";
        }

        // 5. 포도알 차감
        long userGrapes = user.getGrapeCount();
        long itemCost = item.getItemCost();
        user.setGrapeCount((int) (userGrapes - itemCost));
        userRepository.save(user);

        // 6. 아이템 구매 기록 저장
        ItemUserEntity itemUser = ItemUserEntity.builder()
                .itemId(itemId)
                .userId(userId)
                .usertemEquip("N")
                .build();
        itemUserRepository.save(itemUser);

        return "ItemUser/ItemBought"; // 내 아이템 페이지로 이동
    }

    @GetMapping("/ItemUser/ItemBought")
    public String showMyItems(HttpSession session, Model model) {
        Object sessionValue = session.getAttribute("user_id");
        Long userId = null;

        if (sessionValue instanceof Integer) {
            userId = ((Integer) sessionValue).longValue();
        } else if (sessionValue instanceof Long) {
            userId = (Long) sessionValue;
        }

        if (userId == null) {
            return "redirect:/login?error=login_required";
        }

        List<ItemUserEntity> myItems = itemUserRepository.findByUserId(userId);
        model.addAttribute("list", myItems);

        return "ItemUser/ItemBought";
    }




}