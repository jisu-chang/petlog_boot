package com.example.PetLog.Pet;

import com.example.PetLog.User.UserEntity;
import com.example.PetLog.User.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class PetController {

    @Autowired
    PetService petService;

    @Autowired
    UserService userService;

    String path = new File("src/main/resources/static/image").getAbsolutePath();

    @GetMapping("/Pet/PetInput")
    public String input(@ModelAttribute PetDTO petDTO, HttpSession session) {
        UserEntity loginUser = (UserEntity) session.getAttribute("loginUser");

        if (loginUser == null) return "redirect:/login";

        petDTO.setUserId(loginUser.getUserId());
        return "Pet/PetInput";
    }

    @PostMapping("/PetSave")
    public String save(@ModelAttribute PetDTO petDTO,
                       @RequestParam("petImg") MultipartFile mf,
                       HttpSession session) throws IOException {

        UserEntity loginUser = (UserEntity) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        petDTO.setUserId(loginUser.getUserId());

        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        if (!mf.isEmpty()) {
            String fileName = mf.getOriginalFilename();
            mf.transferTo(new File(path + File.separator + fileName));
            petDTO.setPetImgName(fileName);
        }

        petDTO.setPetImg(mf);
        UserEntity user = userService.findById(petDTO.getUserId());
        petService.save(petDTO.entity(user));

        return "redirect:/main";
    }

    @GetMapping("/Pet/PetOut")
    public String out(Model model, HttpSession session) {
        UserEntity loginUser = (UserEntity) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        List<PetEntity> list = petService.petOut(loginUser); // userEntity 넘겨줌
        model.addAttribute("list", list);
        return "Pet/PetOut";
    }

    @GetMapping("/Pet/PetDetail")
    public String detail(Model model, @RequestParam("petId") long petId) {
        PetEntity petEntity = petService.detail(petId);
        model.addAttribute("petEntity", petEntity);
        return "Pet/PetDetail";
    }

    @GetMapping("/Pet/PetUpdate")
    public String update(@RequestParam("petId") Long petId, Model model, HttpSession session) {
        UserEntity loginUser = (UserEntity) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        PetEntity petEntity = petService.detail(petId);
        PetDTO dto = new PetDTO();
        dto.setPetId(petEntity.getPetId());
        dto.setPetName(petEntity.getPetName());
        dto.setPetBog(petEntity.getPetBog());
        dto.setPetHbd(petEntity.getPetHbd());
        dto.setPetNeuter(petEntity.getPetNeuter());
        dto.setPetImgName(petEntity.getPetImg());
        dto.setUserId(petEntity.getUser().getUserId());

        model.addAttribute("dto", dto);
        model.addAttribute("petEntity", petEntity);
        return "Pet/PetUpdate";
    }

    @PostMapping("/PetUpdateSave")
    public String updateSave(@ModelAttribute PetDTO petDTO,
                             @RequestParam("himage") String himage,
                             HttpSession session) throws IOException {

        UserEntity loginUser = (UserEntity) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        petDTO.setUserId(loginUser.getUserId());

        MultipartFile mf = petDTO.getPetImg();
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        if (mf != null && !mf.isEmpty()) {
            String filename = mf.getOriginalFilename();
            mf.transferTo(new File(path + File.separator + filename));
            petDTO.setPetImgName(filename);
        } else {
            petDTO.setPetImgName(himage);
        }

        UserEntity user = userService.findById(petDTO.getUserId());
        petService.update(petDTO.entity(user));

        return "redirect:/Pet/PetDetail?petId=" + petDTO.getPetId();
    }

    @GetMapping("/Pet/PetDelete")
    public String deleteAndConfirm(@RequestParam("delete") Long petId,
                                   @RequestParam("dfimage") String imageName,
                                   HttpSession session) {

        UserEntity loginUser = (UserEntity) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        petService.delete(petId);

        File file = new File(path, imageName);
        if (file.exists()) {
            file.delete();
        }

        return "redirect:/Pet/PetOut";
    }

}
