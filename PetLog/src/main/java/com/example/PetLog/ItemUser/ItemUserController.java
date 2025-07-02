package com.example.PetLog.ItemUser;

import com.example.PetLog.Item.ItemDTO;
import com.example.PetLog.Item.ItemEntity;
import com.example.PetLog.Item.ItemRepository;
import com.example.PetLog.Item.ItemService;
import com.example.PetLog.User.UserEntity;
import com.example.PetLog.User.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @Autowired
    ItemService itemService;

    @GetMapping("/ItemUser/ItemOutUser")
    public String showItemShop(Model model,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "5") int size) {
        int start = (page - 1) * size + 1;
        int end = page * size;

        List<ItemEntity> pagedItems = itemRepository.findByItemStatusWithPaging("판매중", start, end);
        int totalItems = itemRepository.countByItemStatus("판매중");

        int totalPages = (int) Math.ceil((double) totalItems / size);

        System.out.println("현재 페이지: " + page);
        System.out.println("총 아이템 수: " + totalItems);
        System.out.println("총 페이지 수: " + totalPages);

        model.addAttribute("list", pagedItems);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "ItemUser/ItemOutUser";
    }

        @GetMapping(value = "/ItemUser/ItemDetail")
    public String showItemDetail(@RequestParam("itemId") Long itemId, Model model) {
        ItemEntity item = itemRepository.findById(itemId).orElse(null);

        if (item == null) {
            // 아이템이 없을 경우 예외처리 (선택)
            return "redirect:/ItemUser/ItemOutUser"; // 목록 페이지로 돌려보냄
        }

        model.addAttribute("dto", item);
        return "ItemUser/ItemDetail";
    }

    @PostMapping(value = "/ItemUser/ItemBought")
    // RedirectAttributes를 파라미터로 추가
    public String buyItem(@RequestParam("itemId") Long itemId, HttpSession session, RedirectAttributes redirectAttributes) {

        // 1. 세션에서 로그인된 user_id 꺼내기
        Long userId = (Long) session.getAttribute("userId");

        // 로그인되지 않은 경우 로그인 페이지로 리디렉트
        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        // 2. 아이템 조회
        ItemEntity item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            redirectAttributes.addFlashAttribute("message", "아이템을 찾을 수 없습니다.");
            return "redirect:/ItemUser/ItemOutUser";
        }

        // 3. 유저 조회
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "사용자 정보를 찾을 수 없습니다.");
            return "redirect:/login";
        }

        // 4. 포도알 확인
        if (user.getGrapeCount() < item.getItemCost()) {
            redirectAttributes.addFlashAttribute("message", "포도알이 부족합니다.");
            return "redirect:/ItemUser/ItemDetail?itemId=" + itemId; // itemId는 그대로 전달 (현재 아이템 상세 페이지)
        }

        ItemUserEntity existing = itemUserRepository.findByUserIdAndItem_ItemId(userId, itemId);
        if (existing != null) {
            redirectAttributes.addFlashAttribute("message", "이미 구매한 아이템입니다.");
            return "redirect:/ItemUser/ItemDetail?itemId=" + itemId;
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

        // 구매 성공 시 메시지 추가
        redirectAttributes.addFlashAttribute("message", item.getItemName() + " 구매가 완료되었습니다!");
        return "redirect:/ItemUser/ItemBought"; // 내 아이템 페이지로 이동
    }

    @GetMapping(value = "/ItemUser/ItemBought")
    public String showMyItems(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        System.out.println("세션에서 가져온 userId: " + userId);

        if (userId == null) {
            System.out.println("세션에 userId가 없습니다. 로그인 페이지로 리디렉션됩니다.");
            return "redirect:/login";
        }

        List<ItemUserEntity> myItems = itemUserRepository.findByUserId(userId);

        System.out.println("조회된 내 아이템 개수: " + myItems.size());
        myItems.forEach(iue -> {
        });

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

    @GetMapping(value = "/ItemUser/ItemPuton")
    public String showPutOnItemPage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        if (userId == null || userLoginId == null) {
            return "redirect:/login?error=login_required";
        }

        List<ItemDTO> list = itemUserService.findFrameItemsByUserId(userId); // 프레임 아이템만 조회
        model.addAttribute("list", list);

        return "ItemUser/ItemPuton"; // templates/ItemUser/ItemPuton.html
    }


    @PostMapping(value = "/ItemUser/ItemPuton")
    public String putOnItem(@RequestParam("itemId") Long itemId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login?error=login_required";
        }

        // 기존 착용 아이템이 있다면 모두 해제
        itemUserRepository.clearEquippedItems(userId, "프레임");

        // 착용할 아이템을 찾아서 상태 변경
        ItemUserEntity itemUser = itemUserRepository.findByUserIdAndItem_ItemId(userId, itemId);
        if (itemUser != null) {
            itemUser.setUsertemEquip("Y"); // 착용 상태로 변경
            itemUserRepository.save(itemUser);
        }

        return "redirect:/ItemUser/ItemPuton"; // 위에서 만든 HTML 파일로 이동
    }

    @PostMapping("/ItemDeleteUser")
    public String deleteUserItem(@RequestParam("delete") Long itemId,
                                 @RequestParam("dfimage") String itemImageName,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        try {
            ItemUserEntity itemUser = itemUserRepository.findByUserIdAndItem_ItemId(userId, itemId);

            if (itemUser != null) {
                itemUserRepository.delete(itemUser);
                redirectAttributes.addFlashAttribute("message", "아이템이 성공적으로 삭제되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("error", "해당 아이템을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "아이템 삭제 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
            System.out.println("ERROR: 아이템 삭제 중 예외 발생 - " + e.getMessage());
        }
        return "redirect:/ItemUser/ItemBought";
    }

    // 프로필 프레임 착용 처리
    @PostMapping("/ItemOn")
    public String wearFrame(@RequestParam("itemId") Long itemId, HttpSession session) {

        Long rawUserId = (Long) session.getAttribute("userId");
        Long userId = (rawUserId != null) ? rawUserId.longValue() : null;

        Long itemIdLong = (long) itemId;

        if (userId == null) {
            return "redirect:/login?error=login_required";
        }

        itemUserService.frame_wearing(userId, itemIdLong);

        return "redirect:/ItemUser/ItemPuton";
    }
}