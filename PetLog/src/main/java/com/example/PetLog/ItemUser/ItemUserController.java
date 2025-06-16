package com.example.PetLog.ItemUser;

import com.example.PetLog.Item.ItemDTO;
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
        // "판매중"인 아이템만 조회
        List<ItemEntity> items = itemRepository.findByItemStatus("판매중");
        model.addAttribute("list", items);
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
        Long userId = (Long) session.getAttribute("userId");
        System.out.println("세션에서 가져온 userId: " + userId);  // userId가 제대로 나오는지 확인

        // 로그인되지 않은 경우 로그인 페이지로 리디렉트
        if (userId == null) {
            System.out.println("세션에 userId가 없습니다. 로그인 페이지로 리디렉션됩니다.");
            return "redirect:/login";
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
                .item(item)
                .userId(userId)
                .usertemEquip("N")
                .build();
        itemUserRepository.save(itemUser);

        return "ItemUser/ItemBought"; // 내 아이템 페이지로 이동
    }

    @GetMapping("/ItemUser/ItemBought")
    public String showMyItems(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        System.out.println("세션에서 가져온 userId: " + userId);

        if (userId == null) {
            System.out.println("세션에 userId가 없습니다. 로그인 페이지로 리디렉션됩니다.");
            return "redirect:/login";
        }

        List<ItemUserEntity> myItems = itemUserRepository.findByUserId(userId);

        // ✅ 로그 출력 (forEach는 여기까지만!)
        System.out.println("조회된 내 아이템 개수: " + myItems.size());
        myItems.forEach(iue -> {
            System.out.println("🎯 usertemId = " + iue.getUsertemId());
            System.out.println("➡ itemId = " + iue.getItemId());
            System.out.println("➡ 연관된 item = " + iue.getItem());
        });

        // ✅ DTO 변환은 forEach 바깥에서
        List<ItemUserDTO> dtoList = myItems.stream().map(iue -> {
            ItemEntity item = iue.getItem(); // 연관된 item 객체

            return ItemUserDTO.builder()
                    .itemId(item != null ? item.getItemId() : null)
                    .itemName(item != null ? item.getItemName() : "이름 없음")
                    .itemCategory(item != null ? item.getItemCategory() : "카테고리 없음")
                    .itemImageName(item != null ? item.getItemImage() : "default.png")
                    .usertemEquip(iue.getUsertemEquip())
                    .build();
        }).toList();

        model.addAttribute("list", dtoList);
        return "ItemUser/ItemBought";
    }
}